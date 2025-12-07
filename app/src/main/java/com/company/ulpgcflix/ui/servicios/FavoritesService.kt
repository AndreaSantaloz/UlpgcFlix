package com.company.ulpgcflix.ui.servicios

import com.company.ulpgcflix.model.VisualContent
import com.company.ulpgcflix.model.ContentLike
import com.company.ulpgcflix.model.FavoriteContentMetadata
import com.company.ulpgcflix.model.kindVisualContent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.lang.Exception // Importado para la excepción en getUserId

class FavoritesService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    // Nuevas colecciones:
    private val CONTENT_LIKES_PATH = "content_likes"
    private val CONTENT_METADATA_PATH = "content_metadata"

    private fun getUserId(): String {
        return auth.currentUser?.uid
            ?: throw Exception("Usuario no autenticado. Por favor, inicie sesión.")
    }

    suspend fun addFavorite(content: VisualContent) {
        val userId = getUserId()
        // CORRECCIÓN: Usar content.id
        val contentApiId = content.getId


        val metadata = FavoriteContentMetadata(
            contentId = contentApiId,
            title = content.getTitle,
            image = content.getImage,
            kind = content.getKind.name,
            assessment = content.getAssessment,
            categoryIds = content.getCategory.map { it.categoryId }, // Asumiendo que categoryIds es List<String>
            isAdult = content.isAdultContent // Asumiendo que existe
        )
        firestore.collection(CONTENT_METADATA_PATH)
            .document(contentApiId)
            .set(metadata, SetOptions.merge())
            .await()


        val documentId = "${userId}_$contentApiId"
        val like = ContentLike(userId = userId, contentId = contentApiId)

        firestore.collection(CONTENT_LIKES_PATH)
            .document(documentId)
            .set(like)
            .await()
    }

    // ===================================================
    // 2. GET FAVORITES: Lectura Dual
    // ===================================================

    /**
     * Obtiene los favoritos leyendo las relaciones y luego los metadatos.
     */
    suspend fun getFavorites(): List<VisualContent> {
        val userId = getUserId()

        // 1. Leer las relaciones (content_likes)
        val likesSnapshot = firestore.collection(CONTENT_LIKES_PATH)
            .whereEqualTo("userId", userId)
            .get()
            .await()

        val contentApiIds = likesSnapshot.documents
            .mapNotNull { it.getString("contentId") }
            .distinct()

        if (contentApiIds.isEmpty()) return emptyList()

        val favoritesList = mutableListOf<VisualContent>()

        // Divide los IDs en lotes de 10
        contentApiIds.chunked(10).forEach { batchIds ->
            val metadataSnapshot = firestore.collection(CONTENT_METADATA_PATH)
                .whereIn("contentId", batchIds)
                .get()
                .await()

            // 3. Mapear los metadatos recuperados a VisualContent
            for (doc in metadataSnapshot.documents) {
                val metadata = doc.toObject(FavoriteContentMetadata::class.java)
                if (metadata != null) {
                    favoritesList.add(
                        // Mapea la información de FavoriteContentMetadata de vuelta a VisualContent
                        VisualContent(
                            id = metadata.contentId,
                            title = metadata.title,
                            overview =  "", // Asumiendo que el campo overview existe y puede ser nulo en la DB
                            image = metadata.image,
                            assessment = metadata.assessment,
                            kind = enumValueOf<kindVisualContent>(metadata.kind),
                            // Esto es una asunción, ya que categoryIds es List<String> y category es List<Category>
                            // Si necesitas los nombres, debes inyectar CategoryServices aquí para buscar los objetos Category
                            category = emptyList(),
                            isAdult = metadata.isAdult
                        )
                    )
                }
            }
        }

        return favoritesList
    }

    /**
     * Elimina una película favorita eliminando la relación (ContentLike).
     * @param content El objeto VisualContent a eliminar.
     */
    suspend fun removeFavorite(content: VisualContent) {
        val userId = getUserId()

        // CORRECCIÓN CLAVE: Obtener el ID de la API del objeto VisualContent
        val contentApiId = content.getId

        // Reconstruir el documentId exactamente como fue guardado en addFavorite
        val documentId = "${userId}_$contentApiId"

        firestore.collection(CONTENT_LIKES_PATH)
            .document(documentId)
            .delete()
            .await()
    }
}