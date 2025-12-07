package com.company.ulpgcflix.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.ulpgcflix.ui.servicios.VisualContentService
import com.company.ulpgcflix.ui.servicios.CategoryServices
import com.company.ulpgcflix.ui.servicios.UserCategoriesService // <-- IMPORTANTE
import com.company.ulpgcflix.model.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.company.ulpgcflix.ui.interfaces.ApiService
import com.company.ulpgcflix.ui.servicios.FavoritesService

class VisualContentViewModel(
    private val visual: VisualContentService,
    private val categoryServices: CategoryServices,
    apiService: ApiService, // Se mantiene para compatibilidad con Factory
    private val favoritesService: FavoritesService,
    private val userCategoriesService: UserCategoriesService // <-- AÑADIDO EL SERVICIO DE USUARIO
) : ViewModel() {

    private val _contentList = mutableStateOf<List<VisualContent>>(emptyList())
    val contentList: State<List<VisualContent>> = _contentList

    // Lista de favoritos (añadida en la revisión anterior)
    private val _favoriteList = mutableStateOf<List<VisualContent>>(emptyList())
    val favoriteList: State<List<VisualContent>> = _favoriteList

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private var currentPage = 1
    // Almacena la lista de géneros del usuario para la paginación
    private var currentUserGenreIds: String = ""

    // Mapeo solo de categorías de Películas
    private val categoryMap: Map<String, Category> = categoryServices.getCategories()
        .associateBy { it.categoryId }

    /**
     * Mapea los IDs de género de TMDB a objetos Category.
     */
    private fun mapGenreIdsToCategories(genreIds: List<Int>): List<Category> {
        val defaultCategory = Category("0", "Otros", Icons.Default.Circle)
        return genreIds.mapNotNull { id ->
            categoryMap[id.toString()]
        }.distinct().let {
            if (it.isEmpty()) listOf(defaultCategory) else it
        }
    }

    /**
     * Mapea un objeto JSON a un objeto VisualContent (siempre tipo MOVIE).
     */
    private fun mapJsonToVisualContent(itemObject: JSONObject): VisualContent {
        val genreIdsArray = itemObject.optJSONArray("genre_ids")
        val genreIds = mutableListOf<Int>()
        if (genreIdsArray != null) {
            for (i in 0 until genreIdsArray.length()) {
                genreIds.add(genreIdsArray.optInt(i, 0))
            }
        }
        val categories = mapGenreIdsToCategories(genreIds)

        return VisualContent(
            id = itemObject.optInt("id", 0).toString(),
            title = itemObject.optString("title", "Título Desconocido"),
            overview = itemObject.optString("overview", "Sin descripción"),
            image = itemObject.optString("poster_path", ""),
            assessment = itemObject.optDouble("vote_average", 0.0),
            kind = kindVisualContent.MOVIE,
            category = categories,
            isAdult = itemObject.optBoolean("adult", false)
        )
    }

    // --- NUEVO MÉTODO DE CARGA BASADO EN PREFERENCIAS DEL USUARIO ---

    /**
     * Carga el contenido basado en las categorías guardadas por el usuario.
     * ESTE DEBE SER EL MÉTODO LLAMADO DESDE LA VISTA INICIAL.
     * @param userId El ID del usuario actual para buscar sus categorías.
     * @param append Si es 'true', añade a la lista existente; si es 'false', la reemplaza.
     */
    fun loadContentForUser(userId: String, append: Boolean = false) {
        viewModelScope.launch {
            if (!append) currentPage = 1 // Resetea página para la primera carga

            try {
                if (!append || currentUserGenreIds.isEmpty()) {
                    // 1. Obtener las categorías guardadas del usuario
                    val selectedCategories = userCategoriesService.getUserCategories(userId)

                    // 2. Convertir a IDs de TMDB separados por comas
                    currentUserGenreIds = selectedCategories.joinToString(",") { it.categoryId }
                }

                if (currentUserGenreIds.isEmpty()) {
                    _error.value = "No se han seleccionado categorías. Por favor, elige algunas."
                    _contentList.value = emptyList()
                    return@launch
                }

                // 3. Llamar al cargador de la API con los géneros obtenidos
                loadContentFromApi(currentUserGenreIds, append)

            } catch (e: Exception) {
                _error.value = "Error al obtener categorías del usuario: ${e.message}"
                if (!append) {
                    _contentList.value = emptyList()
                }
            }
        }
    }

    /**
     * Método interno que realiza la llamada a la API y actualiza el estado.
     */
    private fun loadContentFromApi(filmGenres: String, append: Boolean) {
        viewModelScope.launch {
            try {
                val movieJsonString: String = visual.getFilmsFromApi(filmGenres, currentPage)

                val movieResultsArray: JSONArray = JSONObject(movieJsonString).getJSONArray("results")

                val newContent = mutableListOf<VisualContent>()
                for (i in 0 until movieResultsArray.length()) {
                    val itemObject = movieResultsArray.getJSONObject(i)
                    newContent.add(mapJsonToVisualContent(itemObject))
                }

                val combinedContent = newContent.shuffled()
                if (append) {
                    _contentList.value = _contentList.value + combinedContent
                } else {
                    _contentList.value = combinedContent
                }

                _error.value = null
                currentPage++

            } catch (e: Exception) {
                _error.value = "Error al cargar contenido (películas): ${e.message}"
                if (!append) {
                    _contentList.value = emptyList()
                }
            }
        }
    }

    // --- Métodos de Favoritos (Añadidos en la revisión anterior) ---

    fun saveFavorite(content: VisualContent) {
        viewModelScope.launch {
            try {
                favoritesService.addFavorite(content)
                loadFavorites()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "No se pudo guardar la película favorita: ${e.message}"
            }
        }
    }

    fun removeFavorite(content: VisualContent) {
        viewModelScope.launch {
            try {
                favoritesService.removeFavorite(content)
                loadFavorites()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "No se pudo eliminar la película favorita: ${e.message}"
            }
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            try {
                _favoriteList.value = favoritesService.getFavorites()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al cargar la lista de favoritos: ${e.message}"
                _favoriteList.value = emptyList()
            }
        }
    }
}