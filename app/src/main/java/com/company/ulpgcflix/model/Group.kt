package com.company.ulpgcflix.model

//Si quiere crear un grupo
data class Group(
    private final val id: String,
    private final val name: String,
    private final val idowner:String,
    private  val description:String,
    private val isPublic:Boolean=false,
)
