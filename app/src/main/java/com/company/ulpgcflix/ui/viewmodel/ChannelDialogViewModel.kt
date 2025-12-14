package com.company.ulpgcflix.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.ulpgcflix.ui.model.ChannelUiState
import com.company.ulpgcflix.domain.model.Message
import com.company.ulpgcflix.ui.servicios.ChannelDialogService // Para mensajes
import com.company.ulpgcflix.ui.servicios.ChannelProfileService // 💡 Nuevo: Para detalles del canal
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import java.lang.Exception
import com.company.ulpgcflix.domain.model.Group
import com.company.ulpgcflix.domain.model.GroupMember


class ChannelDialogViewModel(
    private val dialogService: ChannelDialogService, // 💡 Renombrado para claridad
    private val profileService: ChannelProfileService, // 💡 Servicio para cargar detalles
): ViewModel(){

    private val _uiState = MutableStateFlow<ChannelUiState>(ChannelUiState.Loading)
    val uiState: StateFlow<ChannelUiState> = _uiState.asStateFlow()

    private val _channelId = MutableStateFlow<String?>(null)

    val messages: StateFlow<List<Message>> = _channelId
        .flatMapLatest { channelId ->
            if (channelId != null) {
                // Usamos el servicio de diálogos para recibir mensajes
                dialogService.receiveMessages(channelId)
            } else {
                emptyFlow()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Expone el ID del usuario actualmente logueado.
     */
    fun getUserCurrently(): String {
        // Usamos el servicio de diálogos, o el de perfil si ambos comparten esta lógica
        return try {
            dialogService.getUserCurrently() // Asumo que esta función está en dialogService
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Carga el Group y los miembros del canal usando el ChannelProfileService.
     */
    fun loadChannelData(channelId:String){
        viewModelScope.launch {
            _uiState.value = ChannelUiState.Loading
            try {
                // 1. CARGA PARALELA de Group y Miembros, usando profileService
                val groupDeferred = async { profileService.getChannelDetails(channelId) } // 💡 Uso de profileService
                val membersDeferred = async { profileService.getGroupMembers(channelId) } // 💡 Uso de profileService

                val groupDetails = groupDeferred.await()
                val membersList = membersDeferred.await()

                // 2. Pasar Group y List<GroupMember> al estado
                _uiState.value = ChannelUiState.Success(
                    group = groupDetails,
                    members = membersList
                )

                _channelId.value = channelId

            } catch (e: Exception) {
                _uiState.value = ChannelUiState.Error("Error al cargar el canal: ${e.message}")
                _channelId.value = null
            }
        }
    }

    fun sendMessage(channelId: String, text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            try {
                // Usamos el servicio de diálogos para enviar el mensaje
                dialogService.sendMessage(channelId, text)
            } catch (e: Exception) {
                println("Error al enviar mensaje: ${e.message}")
            }
        }
    }
}