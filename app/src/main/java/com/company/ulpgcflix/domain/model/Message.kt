package com.company.ulpgcflix.domain.model



data class Message (
    private val id: String,
    private val idUser: String,
    private val text: String,
    val timestamp: Long = System.currentTimeMillis()
){

    fun getId(): String {
        return id
    }

    fun getIdUser(): String {
        return idUser
    }

    fun getText(): String {
        return text
    }

}