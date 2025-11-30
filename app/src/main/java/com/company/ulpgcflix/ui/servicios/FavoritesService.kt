package com.company.ulpgcflix.ui.servicios

import com.company.ulpgcflix.model.VisualContent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await

class FavoritesService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val favoritesCollection = firestore.collection("favorites")

    private fun getUserId(): String {
        return auth.currentUser?.uid ?: throw Exception("Usuario no autenticado. Por favor, inicie sesión.")
    }


    suspend fun addFavorite(content: VisualContent) {
        val userId = getUserId()
        val contentApiId = content.getId
        val favoriteData = mapOf(
            "titulo" to content.getTitle,
            "poster_path" to content.getImage,
            "tipo" to content.getKind.toString(),
            "assessment" to content.getAssessment
        )
        val existingDocSnapshot = favoritesCollection
            .whereEqualTo("user_id", userId)
            .limit(1)
            .get()
            .await()

        if (existingDocSnapshot.documents.isNotEmpty()) {
            val docId = existingDocSnapshot.documents.first().id

            favoritesCollection.document(docId).update(
                "elementos_favoritos.$contentApiId",
                favoriteData
            ).await()

        } else {

            favoritesCollection.add(
                mapOf(
                    "user_id" to userId,
                    "elementos_favoritos" to mapOf(
                        contentApiId to favoriteData
                    )
                )
            ).await()
        }
    }


    suspend fun removeFavorite(contentApiId: String) {
        val userId = getUserId()
        val existingDocSnapshot = favoritesCollection
            .whereEqualTo("user_id", userId)
            .limit(1)
            .get()
            .await()

        if (existingDocSnapshot.documents.isNotEmpty()) {
            val docId = existingDocSnapshot.documents.first().id

            favoritesCollection.document(docId).update(
                "elementos_favoritos.$contentApiId",
                FieldValue.delete()
            ).await()
        } else {
            throw Exception("No se encontró el documento de favoritos para el usuario.")
        }
    }

    suspend fun getFavoritesMap(): Map<String, Map<String, Any>> {
        val userId = getUserId()
        val existingDocSnapshot = favoritesCollection
            .whereEqualTo("user_id", userId)
            .limit(1)
            .get()
            .await()

        if (existingDocSnapshot.documents.isNotEmpty()) {
            val data = existingDocSnapshot.documents.first().data
            @Suppress("UNCHECKED_CAST")
            return data?.get("elementos_favoritos") as? Map<String, Map<String, Any>> ?: emptyMap()
        }

        return emptyMap()
    }
}