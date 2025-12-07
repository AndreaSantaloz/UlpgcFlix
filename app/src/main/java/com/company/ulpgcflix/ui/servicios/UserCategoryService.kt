package com.company.ulpgcflix.ui.servicios

import com.company.ulpgcflix.model.Category
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserCategoriesService {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val categoryService = CategoryServices()


    suspend fun saveUserCategories(userId: String, selectedCategories: Set<Category>) {
        val categoriesCollection = db.collection("categories")
        val existingDocSnapshot = categoriesCollection
            .whereEqualTo("usuario", userId)
            .limit(1)
            .get()
            .await()

        val categoryDataMap: Map<String, Long> = selectedCategories.associate { category ->
            val tmdbIdAsLong = category.categoryId.toLongOrNull() ?: 0L
            category.categoryName to tmdbIdAsLong
        }

        val dataToUpdate = hashMapOf<String, Any>(
            "tmdbCategoriesMap" to categoryDataMap
        )

        try {
            if (existingDocSnapshot.documents.isNotEmpty()) {
                val docId = existingDocSnapshot.documents.first().id
                categoriesCollection.document(docId)
                    .update(dataToUpdate)
                    .await()

            } else {
                val dataToCreate = hashMapOf<String, Any>(
                    "usuario" to userId,
                    "tmdbCategoriesMap" to categoryDataMap
                )
                categoriesCollection
                    .add(dataToCreate)
                    .await()
            }
        } catch (e: Exception) {
            println("❌ Error saving categories map to Firebase: ${e.message}")
            throw e
        }
    }

    suspend fun getUserCategories(userId: String): Set<Category> {
        return try {
            val categoriesCollection = db.collection("categories")
            val existingDocSnapshot = categoriesCollection
                .whereEqualTo("usuario", userId)
                .limit(1)
                .get()
                .await()

            if (existingDocSnapshot.documents.isNotEmpty()) {
                val document = existingDocSnapshot.documents.first()

                @Suppress("UNCHECKED_CAST")
                val savedCategoriesMap = document["tmdbCategoriesMap"] as? Map<String, Any> ?: emptyMap()
                if (savedCategoriesMap.isEmpty()) {
                    return emptySet()
                }

                val movieCategoriesReference = categoryService.getCategories()
                val savedCategoryNames = savedCategoriesMap.keys

                val selectedMovieCategories = movieCategoriesReference.filter { category ->
                    savedCategoryNames.contains(category.categoryName)
                }.toSet()

                selectedMovieCategories

            } else {
                emptySet()
            }
        } catch (e: Exception) {
            println("❌ Error retrieving categories from Firebase: ${e.message}")
            emptySet()
        }
    }
}