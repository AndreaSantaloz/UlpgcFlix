package com.company.ulpgcflix.domain.model


data class Category(
    private  val id:String,
    private  val name: String,
){

    val categoryName: String get() = name
    val categoryId: String get() = id
}