package com.company.ulpgcflix.ui.model

import com.company.ulpgcflix.domain.model.Group
import com.company.ulpgcflix.domain.model.GroupMember

sealed interface ChannelUiState {
    data object Loading : ChannelUiState
    data class Success(
        val group: Group,
        val members: List<GroupMember>
    ) : ChannelUiState
    data class Error(val message: String) : ChannelUiState
}