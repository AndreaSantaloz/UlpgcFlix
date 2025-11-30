package com.company.ulpgcflix.ui.servicios

import com.company.ulpgcflix.model.Category
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class UserCategoriesService {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun saveUserCategories(selectedCategories: Set<Category>, userId: String) {
        val categoriesCollection = db.collection("categories")
        val existingDocSnapshot = categoriesCollection
            .whereEqualTo("usuario", userId)
            .limit(1)
            .get()
            .await()
        val tmdbCategoriesMap = selectedCategories.associate { category ->
            val tmdbIdAsLong = category.categoryId.toLongOrNull() ?: 0L
            category.categoryName to tmdbIdAsLong
        }

        val dataToSave = hashMapOf(
            "usuario" to userId,
            "category" to tmdbCategoriesMap
        )

        try {
            if (existingDocSnapshot.documents.isNotEmpty()) {
                val docId = existingDocSnapshot.documents.first().id
                categoriesCollection.document(docId)
                    .set(dataToSave, SetOptions.merge())
                    .await()
            } else {
                categoriesCollection
                    .add(dataToSave)
                    .await()

            }
        } catch (e: Exception) {
            println("❌ Error saving categories map to Firebase: ${e.message}")
            throw e
        }
    }
}