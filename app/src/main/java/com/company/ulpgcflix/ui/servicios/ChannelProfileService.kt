package com.company.ulpgcflix.ui.servicios

import android.util.Log
import com.company.ulpgcflix.domain.model.Group
import com.company.ulpgcflix.domain.model.GroupMember
import com.company.ulpgcflix.domain.model.enums.GroupRole
import com.company.ulpgcflix.firebase.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Servicio encargado de la lógica de negocio para obtener y actualizar la información
 * de perfil o detalles de un canal específico desde Firestore, manteniendo la
 * funcionalidad de gestión de canales.
 */
@Singleton
class ChannelProfileService @Inject constructor(
    private val service: FirestoreRepository,
    private val auth: FirebaseAuth
) {
    // 💡 Colecciones de Firestore
    private val PROFILES_PATH = "profiles"
    private val CHANNELS_COLLECTION = "channels"
    private val MEMBERS_COLLECTION = "channel_members"
    private val GROUP_PROFILES_COLLECTION = "groupProfiles"

    // Se eliminan las colecciones de seguimiento de usuarios (FOLLOW_REQUESTS, FOLLOWING, FOLLOWERS)

    // --- 1. Obtención de Detalles del Canal ---
    suspend fun getChannelDetails(id: String): Group {
        // Ejecutar las lecturas de ambos documentos en paralelo para eficiencia
        val (channelData, groupProfileData) = coroutineScope {
            val channel = async { service.readDocumentSuspended(CHANNELS_COLLECTION, id) }
            val profile = async { service.readDocumentSuspended(GROUP_PROFILES_COLLECTION, id) }
            Pair(channel.await(), profile.await())
        }

        if (channelData == null) {
            throw Exception("Documento del canal con ID '$id' no encontrado.")
        }

        try {
            // 1. Usar datos de 'channels' como base (ownerId, isPublic, etc.)
            val baseName = (channelData["name"] as? String) ?: "Canal sin nombre"
            val baseImage = (channelData["image"] as? String) ?: ""
            val baseDescription = (channelData["description"] as? String) ?: ""

            // 2. Sobrescribir con datos de 'groupProfiles' si existen y son más recientes (editados)
            val editedName = (groupProfileData?.get("name") as? String) ?: baseName
            val editedImage = (groupProfileData?.get("image") as? String) ?: baseImage
            val editedDescription = (groupProfileData?.get("description") as? String) ?: baseDescription

            val groupInstance = Group(
                id = id,
                // Usamos los valores editados (o base si no hay edición)
                name = editedName,
                image = editedImage,
                description = editedDescription,
                // Estos campos se obtienen del documento principal 'channels'
                ownerId = (channelData["ownerId"] as? String) ?: "",
                isPublic = (channelData["isPublic"] as? Boolean) ?: false
            )

            return groupInstance

        } catch (e: Exception) {
            throw Exception("Error al mapear los datos del canal: ${e.message}")
        }
    }


    // --- 2. Funcionalidad de Edición del Perfil del Grupo (Utilitarios) ---

    /**
     * Función utilitaria para actualizar el campo en una colección con callbacks de FirestoreRepository.
     */
    private suspend fun updateDocumentSuspended(
        collectionPath: String,
        documentId: String,
        updates: Map<String, Any>
    ): Result<Unit> = suspendCancellableCoroutine { continuation ->
        service.updateDocument(
            collectionPath = collectionPath,
            documentId = documentId,
            updates = updates,
            onSuccess = { continuation.resume(Result.success(Unit)) },
            onFailure = { continuation.resume(Result.failure(it)) }
        )
    }

    /**
     * Intenta actualizar un documento en GROUP_PROFILES_COLLECTION. Si falla, intenta crearlo
     * con datos mínimos del documento principal 'channels' como fallback.
     */
    private suspend fun updateOrCreateGroupProfile(
        groupId: String,
        updates: Map<String, Any>
    ): Result<Unit> {
        val updateResult = updateDocumentSuspended(GROUP_PROFILES_COLLECTION, groupId, updates)

        // Usamos when para garantizar la compatibilidad si las extensiones fallan
        return when {
            // Si el resultado tiene un valor (es success)
            updateResult.getOrNull() != null -> updateResult
            else -> {
                // Si la actualización falla, intentamos crearlo.
                val currentChannelData = service.readDocumentSuspended(CHANNELS_COLLECTION, groupId)

                // Usamos los datos actuales de 'channels' como fallback para la creación
                val initialData = mutableMapOf<String, Any>(
                    "id" to groupId, // Usar "id" para consistencia con tu modelo
                    "ownerId" to (currentChannelData?.get("ownerId") as? String ?: getCurrentUserId()),
                    "name" to (currentChannelData?.get("name") as? String ?: "Canal nuevo"),
                    "image" to (currentChannelData?.get("image") as? String ?: ""),
                    "description" to (currentChannelData?.get("description") as? String ?: ""),
                )
                // Sobrescribir con las nuevas actualizaciones
                initialData.putAll(updates)

                return try {
                    // Crear el documento con los datos combinados
                    service.createDocumentSuspended(GROUP_PROFILES_COLLECTION, initialData, groupId)
                    Result.success(Unit)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
        }
    }


    // --- 3. Métodos de Edición Específicos (Todos apuntan a GROUP_PROFILES_COLLECTION) ---

    /**
     * Actualiza el campo 'description' en la colección 'groupProfiles'.
     */
    suspend fun updateGroupDescription(groupId: String, newDescription: String): Result<Unit> {
        val updateData = mapOf("description" to newDescription)
        Log.d("ChannelService", "Intentando actualizar descripción en $GROUP_PROFILES_COLLECTION/$groupId")
        return updateOrCreateGroupProfile(groupId, updateData)
    }

    /**
     * Actualiza el campo 'name' en la colección 'groupProfiles'.
     */
    suspend fun updateGroupName(groupId: String, newName: String): Result<Unit> {
        val updateData = mapOf("name" to newName)
        Log.d("ChannelService", "Intentando actualizar nombre en $GROUP_PROFILES_COLLECTION/$groupId")
        return updateOrCreateGroupProfile(groupId, updateData)
    }

    /**
     * Actualiza el campo 'image' en la colección 'groupProfiles'.
     */
    suspend fun updateGroupImage(groupId: String, newImageUrl: String): Result<Unit> {
        val updateData = mapOf("image" to newImageUrl)
        Log.d("ChannelService", "Intentando actualizar imagen en $GROUP_PROFILES_COLLECTION/$groupId")
        return updateOrCreateGroupProfile(groupId, updateData)
    }

    /**
     * Obtiene la descripción de la colección 'groupProfiles' (función de lectura directa).
     */
    suspend fun getGroupProfileDescription(groupId: String): String {
        val documentData = service.readDocumentSuspended(GROUP_PROFILES_COLLECTION, groupId)
        return (documentData?.get("description") as? String) ?: ""
    }

    // --- 4. Funciones de Gestión de Miembros y Usuarios ---

    /**
     * Obtiene el nombre y la URL de imagen de un usuario a partir de su perfil.
     */
    suspend fun getUserProfileDetails(userId: String): Pair<String, String?> {
        val userData = service.readDocumentSuspended(
            collectionPath = PROFILES_PATH,
            documentId = userId
        )
        val name = (userData?.get("name") as? String) ?: "Usuario Desconocido"
        val imageUrl = (userData?.get("urlImagen") as? String).takeIf { !it.isNullOrBlank() }

        return Pair(name, imageUrl)
    }

    /**
     * Obtiene la lista de miembros de un canal específico, resolviendo los detalles del perfil
     * del usuario en paralelo.
     */
    suspend fun getGroupMembers(channelId: String): List<GroupMember> = coroutineScope {
        // Lee los documentos de membresía por el ID del canal
        val memberDocuments = service.readDocumentsByFieldSuspended(MEMBERS_COLLECTION, "channelId", channelId)

        if (memberDocuments.isEmpty()) {
            return@coroutineScope emptyList()
        }

        // Asíncronamente obtiene los detalles de perfil para cada miembro
        val deferredMembers = memberDocuments.mapNotNull { docData ->
            val idMember = docData["userId"] as? String

            if (!idMember.isNullOrBlank()) {
                async {
                    val (userName, imageUrl) = getUserProfileDetails(idMember)
                    Triple(docData, userName, imageUrl)
                }
            } else {
                null
            }
        }

        val results = deferredMembers.awaitAll()
        val membersList = mutableListOf<GroupMember>()

        for ((docData, userName, imageUrl) in results) {
            try {
                // Mapeo de datos del documento de membresía
                val id = (docData["id"] as? String) ?: continue
                val idMember = docData["userId"] as? String ?: continue
                val idGroup = docData["channelId"] as? String ?: continue
                val rolString = (docData["role"] as? String)?.uppercase() ?: "MEMBER"

                val finalName = if (userName == "Usuario Desconocido") {
                    "Usuario ${idMember.take(8)}..." // Fallback de nombre si no se encuentra
                } else {
                    userName
                }

                val rolMember = try {
                    GroupRole.valueOf(rolString)
                } catch (e: IllegalArgumentException) {
                    GroupRole.MEMBER
                }

                membersList.add(
                    GroupMember(
                        id = id,
                        idGroup = idGroup,
                        idMember = idMember,
                        rolMember = rolMember,
                        name = finalName,
                        profileImageUrl = imageUrl
                    )
                )
            } catch (e: Exception) {
                System.err.println("Error al mapear miembro: ${e.message}")
                continue
            }
        }

        return@coroutineScope membersList
    }

    /**
     * Obtiene el ID del usuario actualmente autenticado.
     */
    fun getCurrentUserId(): String {
        return auth.currentUser?.uid
            ?: throw Exception("Usuario no autenticado. Inicie sesión para ver perfiles.")
    }

    /**
     * Función para eliminar un miembro de un grupo.
     */
    suspend fun removeMemberFromGroup(memberDocumentId: String): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            service.deleteDocument(
                collectionPath = MEMBERS_COLLECTION,
                documentId = memberDocumentId,
                onSuccess = { continuation.resume(Result.success(Unit)) },
                onFailure = { continuation.resume(Result.failure(it)) }
            )
        }
    }

}