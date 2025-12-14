package com.company.ulpgcflix.domain.model

import com.company.ulpgcflix.domain.model.enums.Status

data class Message (
    private val id: String,
    private val idUser: String,
    private var text: String,
    private var status: Status,
)