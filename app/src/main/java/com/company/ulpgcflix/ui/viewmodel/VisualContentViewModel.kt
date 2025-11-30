package com.company.ulpgcflix.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.ulpgcflix.ui.servicios.VisualContentService
import com.company.ulpgcflix.ui.servicios.CategoryServices
import com.company.ulpgcflix.ui.servicios.FavoritesService // <-- IMPORTADO
import com.company.ulpgcflix.model.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.company.ulpgcflix.ui.interfaces.ApiService

class VisualContentViewModel(
    private val visual: VisualContentService,
    private val categoryServices: CategoryServices,
    apiService: ApiService,
    private val favoritesService: FavoritesService // <-- NUEVA DEPENDENCIA
) : ViewModel() {

    private val _contentList = mutableStateOf<List<VisualContent>>(emptyList())
    val contentList: State<List<VisualContent>> = _contentList

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private var currentPage = 1

    private val categoryMap: Map<String, Category> = categoryServices.getCategories()
        .associateBy { it.categoryId }

    private fun mapGenreIdsToCategories(genreIds: List<Int>): List<Category> {
        val defaultCategory = Category("0", "Desconocido", Icons.Default.Circle)
        return genreIds.mapNotNull { id ->
            categoryMap[id.toString()] ?: defaultCategory
        }.distinct()
    }

    private fun mapJsonToVisualContent(
        itemObject: JSONObject,
        contentKind: kindVisualContent
    ): VisualContent {
        val genreIdsArray = itemObject.getJSONArray("genre_ids")
        val genreIds = mutableListOf<Int>()
        for (i in 0 until genreIdsArray.length()) {
            genreIds.add(genreIdsArray.optInt(i, 0))
        }
        val categories = mapGenreIdsToCategories(genreIds)
        val titleKey = if (contentKind == kindVisualContent.SERIES) "name" else "title"

        return VisualContent(
            id = itemObject.optInt("id", 0).toString(),
            title = itemObject.optString(titleKey, "Título Desconocido"),
            overview = itemObject.optString("overview", "Sin descripción"),
            image = itemObject.optString("poster_path", ""),
            assessment = itemObject.optDouble("vote_average", 0.0),
            kind = contentKind,
            category = categories,
            isAdult = itemObject.optBoolean("adult", false)
        )
    }


    fun loadContent(filmGenres: String, seriesGenres: String, append: Boolean = false) {
        viewModelScope.launch {
            try {
                val movieJsonString: String = visual.getFilmsFromApi(filmGenres, currentPage)
                val seriesJsonString: String = visual.getSeriesFromApi(seriesGenres, currentPage)

                val movieResultsArray: JSONArray = JSONObject(movieJsonString).getJSONArray("results")
                val seriesResultsArray: JSONArray = JSONObject(seriesJsonString).getJSONArray("results")

                val newContent = mutableListOf<VisualContent>()

                for (i in 0 until movieResultsArray.length()) {
                    val itemObject = movieResultsArray.getJSONObject(i)
                    newContent.add(mapJsonToVisualContent(itemObject, kindVisualContent.MOVIE))
                }

                for (i in 0 until seriesResultsArray.length()) {
                    val itemObject = seriesResultsArray.getJSONObject(i)
                    newContent.add(mapJsonToVisualContent(itemObject, kindVisualContent.SERIES))
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
                _error.value = "Error al cargar contenido: ${e.message}"
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
                _error.value = null
            } catch (e: Exception) {
                _error.value = "No se pudo guardar: ${e.message}"
            }
        }
    }
}