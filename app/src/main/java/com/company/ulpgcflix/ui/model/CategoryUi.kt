package com.company.ulpgcflix.ui.model

import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryUi(
    private  val id:String,
    private  val name: String,
    private  val icon: ImageVector,
){

    val categoryName: String get() = name
    val categoryIcon: ImageVector get() = icon
    val categoryId: String get() = id
}