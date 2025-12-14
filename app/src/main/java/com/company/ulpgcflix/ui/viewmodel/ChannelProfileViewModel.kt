package com.company.ulpgcflix.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.ulpgcflix.ui.model.ChannelUiState
import com.company.ulpgcflix.ui.servicios.ChannelProfileService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlinx.coroutines.async // Para carga paralela
import android.util.Log // Importar Log para debug
import com.company.ulpgcflix.domain.model.Group // Necesario para la copia

class ChannelProfileViewModel(
    private val channelService: ChannelProfileService,
): ViewModel(){

    private val _uiState = MutableStateFlow<ChannelUiState>(ChannelUiState.Loading)
    val uiState: StateFlow<ChannelUiState> = _uiState.asStateFlow()


    /**
     * Carga la información completa del canal, incluyendo la descripción editable.
     */
    fun getInformationChannel(channelId: String){
        if (channelId.isBlank()) return

        viewModelScope.launch {
            _uiState.value = ChannelUiState.Loading
            try {
                // 💡 CARGA PARALELA de los tres conjuntos de datos
                val groupDeferred = async { channelService.getChannelDetails(channelId) }
                val membersDeferred = async { channelService.getGroupMembers(channelId) }
                // Cargar la descripción editable de la colección 'groupProfiles'
                val editableDescDeferred = async { channelService.getGroupProfileDescription(channelId) }

                val groupDetails = groupDeferred.await()
                val membersList = membersDeferred.await()
                val editableDescription = editableDescDeferred.await()


                // Determinar la descripción final: Usar la editable si existe, sino la original.
                val finalDescription = if (editableDescription.isNotBlank()) {
                    editableDescription
                } else {
                    groupDetails.getDescription
                }

                // Creamos una copia del grupo (Group) para el UI State con la descripción correcta.
                // Usamos los datos originales (nombre, imagen) de groupDetails, que ya incluyen la última edición guardada
                // en la colección 'channels' (asumiendo que las funciones updateName/updateImage lo hacen).
                val groupForUi = Group(
                    id = groupDetails.getId,
                    name = groupDetails.getName,
                    image = groupDetails.getImage,
                    ownerId = groupDetails.getIdOwner,
                    description = finalDescription, // Usamos la descripción editada/refrescada
                    isPublic = groupDetails.getIsPublic
                )


                // 💡 EMITIR: Emitimos el Group con la descripción correcta y la lista de miembros
                _uiState.value = ChannelUiState.Success(
                    group = groupForUi,
                    members = membersList
                )

            } catch (e: Exception) {
                Log.e("ChannelProfileVM", "Error al cargar el canal $channelId: ${e.message}", e)
                _uiState.value = ChannelUiState.Error("Error al cargar la información del canal: ${e.message}")
            }
        }
    }

    /**
     * Actualiza la descripción del grupo en Firestore y refresca la vista.
     */
    fun updateDescription(channelId: String, newDescription: String) {
        if (channelId.isBlank()) return

        viewModelScope.launch {
            val result = channelService.updateGroupDescription(channelId, newDescription)

            if (result.isSuccess) {
                Log.d("ChannelProfileVM", "Descripción actualizada con éxito. Refrescando UI.")
                getInformationChannel(channelId)
            } else {
                val errorMsg = result.exceptionOrNull()?.localizedMessage ?: "Error al guardar la descripción."
                Log.e("ChannelProfileVM", "Fallo al guardar descripción: $errorMsg")
                // Si falla, recargar para mostrar el estado actual o un mensaje de error si la recarga falla.
                getInformationChannel(channelId)
            }
        }
    }

    /**
     * ✅ NUEVO: Actualiza el nombre del grupo en Firestore y refresca la vista.
     */
    fun updateName(channelId: String, newName: String) {
        if (channelId.isBlank() || newName.isBlank()) return

        viewModelScope.launch {
            val result = channelService.updateGroupName(channelId, newName)

            if (result.isSuccess) {
                Log.d("ChannelProfileVM", "Nombre actualizado con éxito. Refrescando UI.")
                getInformationChannel(channelId)
            } else {
                val errorMsg = result.exceptionOrNull()?.localizedMessage ?: "Error al guardar el nombre."
                Log.e("ChannelProfileVM", "Fallo al guardar nombre: $errorMsg")
                getInformationChannel(channelId)
            }
        }
    }

    /**
     * ✅ NUEVO: Actualiza la imagen del grupo en Firestore y refresca la vista.
     */
    fun updateImage(channelId: String, newImageUrl: String) {
        if (channelId.isBlank() || newImageUrl.isBlank()) return

        viewModelScope.launch {
            val result = channelService.updateGroupImage(channelId, newImageUrl)

            if (result.isSuccess) {
                Log.d("ChannelProfileVM", "Imagen actualizada con éxito. Refrescando UI.")
                getInformationChannel(channelId)
            } else {
                val errorMsg = result.exceptionOrNull()?.localizedMessage ?: "Error al guardar la imagen."
                Log.e("ChannelProfileVM", "Fallo al guardar imagen: $errorMsg")
                getInformationChannel(channelId)
            }
        }
    }


    /**
     * Expone el ID del usuario actualmente logueado.
     */
    fun getCurrentUserId(): String {
        return try {
            channelService.getCurrentUserId()
        } catch (e: Exception) {
            // Maneja el caso de que no haya usuario autenticado.
            ""
        }
    }
}