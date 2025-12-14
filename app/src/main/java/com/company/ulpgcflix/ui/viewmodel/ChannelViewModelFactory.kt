package com.company.ulpgcflix.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.company.ulpgcflix.ui.servicios.ChannelDialogService
import com.company.ulpgcflix.ui.servicios.ChannelProfileService // 💡 IMPORTACIÓN NECESARIA

class ChannelViewModelFactory(
    // 💡 Aceptar ambos servicios
    private val dialogService: ChannelDialogService,
    private val profileService: ChannelProfileService
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(ChannelDialogViewModel::class.java)) {
            return ChannelDialogViewModel(
                dialogService = dialogService,
                profileService = profileService
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}