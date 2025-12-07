package com.company.ulpgcflix.model


data class User(
    private final val id: String,
    private final val name: String,
    private final val email:String,
    private final val password: String,
    private val isPublic:Boolean=false,
)
