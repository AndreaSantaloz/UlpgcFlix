package com.company.ulpgcflix.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.company.ulpgcflix.ui.servicios.ChannelProfileService

/**
 * Factory personalizada para crear instancias de ChannelProfileViewModel,
 * inyectando ChannelProfileService manualmente.
 */
class ChannelProfileViewModelFactory(
    private val channelProfileService: ChannelProfileService
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(ChannelProfileViewModel::class.java)) {
            return ChannelProfileViewModel(channelProfileService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}