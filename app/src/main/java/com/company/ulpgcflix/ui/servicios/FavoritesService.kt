package com.company.ulpgcflix.ui.servicios

import com.company.ulpgcflix.domain.model.VisualContent
import com.company.ulpgcflix.domain.model.ContentLike
import com.company.ulpgcflix.data.local.entity.FavoriteContentMetadata
import com.company.ulpgcflix.domain.model.enums.kindVisualContent
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
        val contentApiId = content.getId


        val metadata = FavoriteContentMetadata(
            contentId = contentApiId,
            title = content.getTitle,
            image = content.getImage,
            kind = content.getKind.name,
            assessment = content.getAssessment,
            categoryIds = content.getCategory.map { it.categoryId },
            isAdult = content.isAdultContent
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


    suspend fun getFavorites(): List<VisualContent> {
        val userId = getUserId()

        val likesSnapshot = firestore.collection(CONTENT_LIKES_PATH)
            .whereEqualTo("userId", userId)
            .get()
            .await()

        val contentApiIds = likesSnapshot.documents
            .mapNotNull { it.getString("contentId") }
            .distinct()

        if (contentApiIds.isEmpty()) return emptyList()

        val favoritesList = mutableListOf<VisualContent>()

        contentApiIds.chunked(10).forEach { batchIds ->
            val metadataSnapshot = firestore.collection(CONTENT_METADATA_PATH)
                .whereIn("contentId", batchIds)
                .get()
                .await()

            for (doc in metadataSnapshot.documents) {
                val metadata = doc.toObject(FavoriteContentMetadata::class.java)
                if (metadata != null) {
                    favoritesList.add(
                        VisualContent(
                            id = metadata.contentId,
                            title = metadata.title,
                            overview =  "",
                            image = metadata.image,
                            assessment = metadata.assessment,
                            kind = enumValueOf<kindVisualContent>(metadata.kind),
                            category = emptyList(),
                            isAdult = metadata.isAdult
                        )
                    )
                }
            }
        }

        return favoritesList
    }


    suspend fun removeFavorite(content: VisualContent) {
        val userId = getUserId()
        val contentApiId = content.getId
        val documentId = "${userId}_$contentApiId"

        firestore.collection(CONTENT_LIKES_PATH)
            .document(documentId)
            .delete()
            .await()
    }
}