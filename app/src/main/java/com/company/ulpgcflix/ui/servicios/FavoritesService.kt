// package com.company.ulpgcflix.ui.servicios

import com.company.ulpgcflix.model.VisualContent
import com.company.ulpgcflix.model.ContentLike
import com.company.ulpgcflix.model.FavoriteContentMetadata
import com.company.ulpgcflix.model.kindVisualContent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

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

    // ===================================================
    // 1. ADD FAVORITE: Escritura Dual
    // ===================================================

    suspend fun addFavorite(content: VisualContent) {
        val userId = getUserId()
        val contentApiId = content.getId // ID de TMDb

        // --- A. GUARDAR METADATOS (Colección content_metadata) ---
        // Usamos SET con merge para no sobrescribir si el contenido ya existe.
        val metadata = FavoriteContentMetadata(
            contentId = contentApiId,
            title = content.getTitle,
            image = content.getImage,
            kind = content.getKind.name,
            assessment = content.getAssessment
        )
        // El ID del documento será el ID de TMDb
        firestore.collection(CONTENT_METADATA_PATH)
            .document(contentApiId)
            .set(metadata, SetOptions.merge()) // Sobrescribe solo si es necesario, sin borrar otros campos
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
     * Esto reemplaza por completo la función getFavoritesMap().
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

        // 2. Usar 'whereIn' para leer todos los metadatos de una vez (máx. 10 IDs por consulta)
        // Si tienes más de 10 IDs, necesitas hacer múltiples consultas.

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
                            id =metadata.contentId,
                            title =metadata.title,
                            overview = "",
                            image =metadata.image,
                            assessment = metadata.assessment,
                            kind = enumValueOf<kindVisualContent>(metadata.kind),
                            category = metadata.categoryIds,
                            isAdult = metadata.isAdult,


                        )
                    )
                }
            }
        }

        return favoritesList
    }

    // Y también necesitas un removeFavorite actualizado para eliminar la relación (ContentLike)
    suspend fun removeFavorite(contentApiId: String) {
        val userId = getUserId()
        val documentId = "${userId}_$contentApiId"

        firestore.collection(CONTENT_LIKES_PATH)
            .document(documentId)
            .delete()
            .await()

        // NOTA: No borramos los metadatos, ya que otros usuarios pueden tenerlo como favorito.
    }
}