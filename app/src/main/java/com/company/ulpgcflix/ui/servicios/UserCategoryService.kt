package com.company.ulpgcflix.ui.servicios

import com.company.ulpgcflix.model.Category
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserCategoriesService {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    // Usamos CategoryServices para obtener las categorías de referencia
    private val categoryService = CategoryServices()


    suspend fun saveUserCategories(userId: String, selectedCategories: Set<Category>) {
        val categoriesCollection = db.collection("categories")
        val existingDocSnapshot = categoriesCollection
            .whereEqualTo("usuario", userId)
            .limit(1)
            .get()
            .await()

        // Mapea a [Nombre -> ID de TMDB (Long)]
        val categoryDataMap: Map<String, Long> = selectedCategories.associate { category ->
            val tmdbIdAsLong = category.categoryId.toLongOrNull() ?: 0L
            category.categoryName to tmdbIdAsLong
        }

        val dataToUpdate = hashMapOf<String, Any>(
            "tmdbCategoriesMap" to categoryDataMap
        )

        try {
            if (existingDocSnapshot.documents.isNotEmpty()) {
                // Si el documento existe, lo actualiza
                val docId = existingDocSnapshot.documents.first().id
                categoriesCollection.document(docId)
                    .update(dataToUpdate)
                    .await()
                println("✅ Categorías de Películas actualizadas (reemplazadas) para el usuario: $userId")
            } else {
                // Si el documento no existe, lo crea
                val dataToCreate = hashMapOf<String, Any>(
                    "usuario" to userId,
                    "tmdbCategoriesMap" to categoryDataMap
                )
                categoriesCollection
                    .add(dataToCreate)
                    .await()
                println("✅ Nuevo documento de categorías de Películas creado para el usuario: $userId")
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

                // Usamos el método que devuelve todas las categorías de referencia (películas)
                val movieCategoriesReference = categoryService.getCategories()
                val savedCategoryNames = savedCategoriesMap.keys

                // Filtramos las referencias por los nombres de categoría guardados
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