package com.company.ulpgcflix.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.company.ulpgcflix.model.Group
import com.company.ulpgcflix.ui.servicios.SocialMediaService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SocialMediaViewModelFactory(
    private val socialMediaService: SocialMediaService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SocialMediaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SocialMediaViewModel(socialMediaService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


class SocialMediaViewModel(
    private val SocialMediaService: SocialMediaService
) : ViewModel() {

    // --- ESTADOS OBSERVABLES ---
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _visibleChannels = mutableStateOf<List<Group>>(emptyList())
    val visibleChannels: State<List<Group>> = _visibleChannels

    private val _allChannels = mutableStateOf<List<Group>>(emptyList())

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    // 🟢 NUEVO ESTADO: IDs de los canales que el usuario sigue (Para persistencia)
    private val _followedChannelIds = MutableStateFlow<Set<String>>(emptySet())
    val followedChannelIds: StateFlow<Set<String>> = _followedChannelIds.asStateFlow()

    // --- INICIALIZACIÓN ---
    init {
        loadChannels()
        loadFollowedChannelIds() // 🟢 Cargamos los canales seguidos al iniciar
        viewModelScope.launch {
            searchText.collect { text ->
                filterChannels(text)
            }
        }
    }

    // ===================================================
    // 1. CARGA, BÚSQUEDA Y FILTRADO
    // ===================================================

    /**
     * Actualiza el texto de búsqueda.
     */
    fun onSearchTextChanged(text: String) {
        _searchText.value = text
    }

    /**
     * Carga todos los canales desde el servicio de Firebase.
     */
    fun loadChannels() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val channelMaps = SocialMediaService.getChannels()

                val channels = channelMaps.map { dataMap ->
                    // Mapeo SEGURO
                    Group(
                        id = dataMap["id"] as? String ?: throw IllegalStateException("Canal sin ID de Firebase"),
                        name = dataMap["name"] as? String ?: "Nombre Desconocido",
                        description = dataMap["description"] as? String ?: "Sin descripción",
                        // ⚠️ Usamos 'ownerId' del mapa para 'idowner'
                        idowner = dataMap["ownerId"] as? String ?: "Desconocido",
                        image = dataMap["image"] as? String ?: "",
                        isPublic = dataMap["isPublic"] as? Boolean ?: true,
                    )
                }

                _allChannels.value = channels
                filterChannels(_searchText.value) // Filtra después de cargar

            } catch (e: Exception) {
                _error.value = "Error al cargar canales: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 🟢 NUEVA FUNCIÓN: Carga los IDs de los canales que el usuario actual sigue.
     */
    fun loadFollowedChannelIds() {
        viewModelScope.launch {
            try {
                // ⚠️ Se asume que SocialMediaService.getFollowedChannelIds() existe e interactúa con Firebase
                // y devuelve una lista/set de IDs de String.
                val followedIds = SocialMediaService.getFollowedChannelIds()
                _followedChannelIds.value = followedIds.toSet()
            } catch (e: Exception) {
                _error.value = "Error al cargar canales seguidos: ${e.message}"
            }
        }
    }


    /**
     * Filtra la lista de canales basándose en el query de búsqueda por nombre.
     */
    private fun filterChannels(query: String) {
        if (query.isBlank()) {
            _visibleChannels.value = _allChannels.value
            return
        }

        val filteredList = _allChannels.value.filter { group ->
            group.getName.contains(query, ignoreCase = true)
        }
        _visibleChannels.value = filteredList
    }

    // ===================================================
    // 2. OPERACIONES CRUD DE CANALES
    // ===================================================

    /**
     * Crea un nuevo canal en Firebase.
     */
    fun createChannel(name: String, description: String, isPublic: Boolean, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val newChannelId = SocialMediaService.createChannel(name, description, isPublic)
                loadChannels()
                onComplete()

            } catch (e: Exception) {
                _error.value = "Error al crear canal: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Elimina un canal y actualiza la lista localmente.
     */
    fun deleteChannel(group: Group) {
        val channelId = group.getId
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                SocialMediaService.deleteChannel(channelId)

                val updatedList = _allChannels.value.filter { it.getId != channelId }
                _allChannels.value = updatedList
                filterChannels(_searchText.value)

            } catch (e: Exception) {
                _error.value = "Error al eliminar canal: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Actualiza la información de un canal.
     */
    fun updateChannel(group: Group, updates: Map<String, Any>) {
        val channelId = group.getId
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                SocialMediaService.updateChannel(channelId, updates)
                loadChannels()
            } catch (e: Exception) {
                _error.value = "Error al actualizar canal: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    // ===================================================
    // 3. SEGUIMIENTO (FOLLOW/UNFOLLOW)
    // ===================================================

    /**
     * Permite al usuario seguir un canal y actualiza el estado local.
     */
    fun followChannel(channelId: String) {
        viewModelScope.launch {
            _error.value = null
            try {
                SocialMediaService.followChannel(channelId)
                // 🟢 Actualiza el estado observable de IDs seguidos
                _followedChannelIds.value = _followedChannelIds.value + channelId

            } catch (e: Exception) {
                _error.value = "Error al seguir canal: ${e.message}"
            }
        }
    }

    /**
     * Permite al usuario dejar de seguir un canal y actualiza el estado local.
     */
    fun unfollowChannel(channelId: String) {
        viewModelScope.launch {
            _error.value = null
            try {
                SocialMediaService.unfollowChannel(channelId)
                // 🟢 Actualiza el estado observable de IDs seguidos
                _followedChannelIds.value = _followedChannelIds.value - channelId

            } catch (e: Exception) {
                _error.value = "Error al dejar de seguir canal: ${e.message}"
            }
        }
    }
}