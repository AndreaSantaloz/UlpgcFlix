package com.company.ulpgcflix.model

//tabla de contenido visual
data class ContentLike(
    val userId: String,      // ID del usuario que dio like
    val contentId: String,   // ID del contenido que le gustó
    val likedAt: Long = System.currentTimeMillis()
)