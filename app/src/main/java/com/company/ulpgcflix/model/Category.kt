package com.company.ulpgcflix.model

import androidx.compose.ui.graphics.vector.ImageVector


data class Category(
    private final val id:String,
    private final val name: String,
    private final val icon: ImageVector,
){

    val categoryName: String get() = name
    val categoryIcon: ImageVector get() = icon
    val categoryId: String get() = id
}
