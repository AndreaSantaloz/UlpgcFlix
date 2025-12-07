package com.company.ulpgcflix.ui.viewmodel

import FavoritesService
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.company.ulpgcflix.model.VisualContent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class FavoritesViewModelFactory(
    private val favoritesService: FavoritesService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(favoritesService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


class FavoritesViewModel(
    private val favoritesService: FavoritesService
) : ViewModel() {

    private val _allFavorites = mutableStateOf<List<VisualContent>>(emptyList())

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _visibleFavorites = mutableStateOf<List<VisualContent>>(emptyList())
    val visibleFavorites: State<List<VisualContent>> = _visibleFavorites

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private var searchJob: Job? = null

    init {
        loadFavorites()
        viewModelScope.launch {
            searchText.collect { text ->
                filterFavorites(text)
            }
        }
    }

    /**
     * Carga los favoritos utilizando el nuevo método del servicio.
     * El mapeo a VisualContent se realiza ahora en la capa Service.
     */
    fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Llama al servicio que lee de Firestore y devuelve List<VisualContent>
                val favoritesList = favoritesService.getFavorites()

                // Ya no necesitamos la lógica compleja de mapeo manual
                // que existía antes para 'getFavoritesMap()'.

                _allFavorites.value = favoritesList
                filterFavorites(_searchText.value)

            } catch (e: Exception) {
                _error.value = "Error al cargar favoritos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun onSearchTextChanged(text: String) {
        _searchText.value = text
    }

    private fun filterFavorites(query: String) {
        if (query.isBlank()) {
            _visibleFavorites.value = _allFavorites.value
            return
        }

        val filteredList = _allFavorites.value.filter { content ->
            // Se asume que content.getTitle es la forma correcta de acceder al título.
            content.getTitle.contains(query, ignoreCase = true)
        }
        _visibleFavorites.value = filteredList
    }


    fun removeFavorite(content: VisualContent) {
        viewModelScope.launch {
            try {
                // removeFavorite sigue funcionando igual
                favoritesService.removeFavorite(content.getId)
                val updatedList = _allFavorites.value.filter { it.getId != content.getId }
                _allFavorites.value = updatedList
                filterFavorites(_searchText.value)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al eliminar favorito: ${e.message}"
            }
        }
    }

}