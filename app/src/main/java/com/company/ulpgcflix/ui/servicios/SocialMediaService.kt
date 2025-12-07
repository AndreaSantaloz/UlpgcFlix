package com.company.ulpgcflix.ui.servicios

import com.company.ulpgcflix.firebase.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SocialMediaService(
    private val firebaseService: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    private val CHANNELS_PATH = "channels"
    private val CHANNEL_MEMBERS_PATH = "channel_members"


    private fun getUserId(): String {
        return auth.currentUser?.uid ?: throw Exception("Usuario no autenticado. Inicie sesión.")
    }

    // ===================================================
    // 1. GESTIÓN DE CANALES: CREAR, ELIMINAR, ACTUALIZAR, VER
    // ===================================================

    /**
     * ➕ CREAR CANAL: Crea un nuevo canal y retorna su ID.
     */
    suspend fun createChannel(name: String, description: String, isPublic: Boolean): String =
        suspendCancellableCoroutine { continuation ->
            val userId = getUserId()
            val newChannel = mapOf(
                "name" to name,
                "description" to description,
                "ownerId" to userId,
                "isPublic" to isPublic,
                "createdAt" to System.currentTimeMillis()
            )

            firebaseService.createDocument(
                collectionPath = CHANNELS_PATH,
                data = newChannel,
                onSuccess = { docRef: DocumentReference ->
                    continuation.resume(docRef.id)
                },
                onFailure = { exception ->
                    continuation.resumeWithException(exception)
                }
            )
        }

    /**
     * ❌ ELIMINAR CANAL: Elimina un canal por su ID (ID de documento de Firebase).
     */
    suspend fun deleteChannel(channelId: String) {
        suspendCancellableCoroutine { continuation ->
            firebaseService.deleteDocument(
                collectionPath = CHANNELS_PATH,
                documentId = channelId,        // <-- Usa el ID de Firebase proporcionado
                onSuccess = { continuation.resume(Unit) },
                onFailure = { continuation.resumeWithException(it) }
            )
        }
    }

    /**
     * 📝 ACTUALIZAR CANAL: Actualiza campos específicos de un canal usando su ID de Firebase.
     */
    suspend fun updateChannel(channelId: String, updates: Map<String, Any>) {
        suspendCancellableCoroutine { continuation ->
            firebaseService.updateDocument(
                collectionPath = CHANNELS_PATH,
                documentId = channelId,        // <-- Usa el ID de Firebase proporcionado
                updates = updates,
                onSuccess = { continuation.resume(Unit) },
                onFailure = { continuation.resumeWithException(it) }
            )
        }
    }

    /**
     * 👀 VER CANAL (individual): Obtiene los datos de un canal específico.
     */
    suspend fun getChannel(channelId: String): Map<String, Any>? =
        suspendCancellableCoroutine { continuation ->
            firebaseService.readDocument(
                collectionPath = CHANNELS_PATH,
                documentId = channelId,
                onSuccess = { data ->
                    continuation.resume(data)
                },
                onFailure = { exception ->
                    continuation.resumeWithException(exception)
                }
            )
        }

    /**
     * 🔭 VER CANALES (todos): Obtiene todos los canales, INCLUYENDO EL ID DE FIREBASE.
     */
    suspend fun getChannels(): List<Map<String, Any>> =
        suspendCancellableCoroutine { continuation ->
            firebaseService.readCollection(
                collectionPath = CHANNELS_PATH,
                onSuccess = { querySnapshot ->
                    // Mapeo crucial: Añade el ID del documento ('it.id') al mapa de datos con la clave "id"
                    val channelsList = querySnapshot.documents.mapNotNull {
                        it.data?.plus("id" to it.id)
                    }
                    continuation.resume(channelsList)
                },
                onFailure = { exception ->
                    continuation.resumeWithException(exception)
                }
            )
        }

    /**
     * 🔎 BUSCAR CANAL: Obtiene todos los canales y realiza un filtrado básico en memoria.
     */
    suspend fun searchChannels(query: String): List<Map<String, Any>> {
        val allChannels = getChannels()
        if (query.isBlank()) return allChannels

        return allChannels.filter { channel ->
            val name = channel["name"] as? String ?: ""
            name.contains(query, ignoreCase = true)
        }
    }

    // ===================================================
    // 2. SEGUIMIENTO DE CANALES: SEGUIR, DEJAR DE SEGUIR Y OBTENER SEGUIDOS
    // ===================================================


    suspend fun getFollowedChannelIds(): Set<String> =
        suspendCancellableCoroutine { continuation ->
            val userId = getUserId()
            firebaseService.queryCollection(
                collectionPath = CHANNEL_MEMBERS_PATH,
                query = { collectionRef -> collectionRef.whereEqualTo("userId", userId) },
                onSuccess = { querySnapshot ->
                    val followedIds = querySnapshot.documents
                        .mapNotNull { it.getString("channelId") }
                        .toSet()

                    continuation.resume(followedIds)
                },
                onFailure = { exception ->
                    continuation.resumeWithException(exception)
                }
            )
        }

    /**
     * ✅ SEGUIR CANAL: Crea un documento de membresía con un ID compuesto ("userId_channelId").
     */
    suspend fun followChannel(channelId: String) {
        val userId = getUserId()
        val membershipId = "${userId}_$channelId"
        val membershipData = mapOf(
            "userId" to userId,
            "channelId" to channelId,
            "followedAt" to System.currentTimeMillis()
        )

        suspendCancellableCoroutine { continuation ->
            firebaseService.createDocument(
                collectionPath = CHANNEL_MEMBERS_PATH,
                data = membershipData,
                documentId = membershipId, // Fuerza un ID para facilitar el seguimiento/dejar de seguir
                onSuccess = { continuation.resume(Unit) },
                onFailure = { continuation.resumeWithException(it) }
            )
        }
    }

    /**
     * 💔 DEJAR DE SEGUIR CANAL: Elimina el documento de membresía.
     */
    suspend fun unfollowChannel(channelId: String) {
        val userId = getUserId()
        val membershipId = "${userId}_$channelId" // Usamos el ID compuesto para la eliminación

        suspendCancellableCoroutine { continuation ->
            firebaseService.deleteDocument(
                collectionPath = CHANNEL_MEMBERS_PATH,
                documentId = membershipId,
                onSuccess = { continuation.resume(Unit) },
                onFailure = { continuation.resumeWithException(it) }
            )
        }
    }
}