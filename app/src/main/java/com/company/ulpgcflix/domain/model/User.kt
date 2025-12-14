package com.company.ulpgcflix.domain.model

import com.company.ulpgcflix.domain.model.enums.Permission

data class User(
    private final val id: String,
    private final val name: String,
    private final val email:String,
    private final val password: String,
    private val isPublic:Boolean=false,
    private final val permission: Permission
)