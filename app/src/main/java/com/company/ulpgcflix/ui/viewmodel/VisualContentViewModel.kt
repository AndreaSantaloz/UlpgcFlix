package com.company.ulpgcflix.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.ulpgcflix.ui.servicios.VisualContentService
import com.company.ulpgcflix.ui.servicios.CategoryServices
import com.company.ulpgcflix.ui.servicios.UserCategoriesService // <-- IMPORTANTE
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.company.ulpgcflix.domain.model.VisualContent
import com.company.ulpgcflix.domain.model.enums.kindVisualContent
import com.company.ulpgcflix.ui.interfaces.ApiService
import com.company.ulpgcflix.ui.model.CategoryUi
import com.company.ulpgcflix.ui.servicios.FavoritesService

class VisualContentViewModel(
    private val visual: VisualContentService,
    private val categoryServices: CategoryServices,
    apiService: ApiService,
    private val favoritesService: FavoritesService,
    private val userCategoriesService: UserCategoriesService
) : ViewModel() {

    private val _contentList = mutableStateOf<List<VisualContent>>(emptyList())
    val contentList: State<List<VisualContent>> = _contentList

    private val _favoriteList = mutableStateOf<List<VisualContent>>(emptyList())
    val favoriteList: State<List<VisualContent>> = _favoriteList

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private var currentPage = 1
    private var currentUserGenreIds: String = ""

    private val categoryMap: Map<String, CategoryUi> = categoryServices.getCategories()
        .associateBy { it.categoryId }

    private fun mapGenreIdsToCategories(genreIds: List<Int>): List<CategoryUi> {
        val defaultCategory = CategoryUi("0", "Otros", Icons.Default.Circle)
        return genreIds.mapNotNull { id ->
            categoryMap[id.toString()]
        }.distinct().let {
            if (it.isEmpty()) listOf(defaultCategory) else it
        }
    }


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



    fun loadContentForUser(userId: String, append: Boolean = false) {
        viewModelScope.launch {
            if (!append) currentPage = 1

            try {
                if (!append || currentUserGenreIds.isEmpty()) {
                    val selectedCategories = userCategoriesService.getUserCategories(userId)
                    currentUserGenreIds = selectedCategories.joinToString(",") { it.categoryId }
                }

                if (currentUserGenreIds.isEmpty()) {
                    _error.value = "No se han seleccionado categorías. Por favor, elige algunas."
                    _contentList.value = emptyList()
                    return@launch
                }

                loadContentFromApi(currentUserGenreIds, append)

            } catch (e: Exception) {
                _error.value = "Error al obtener categorías del usuario: ${e.message}"
                if (!append) {
                    _contentList.value = emptyList()
                }
            }
        }
    }


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