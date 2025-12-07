package com.company.ulpgcflix.model

data class ContentLike(
    val userId: String,
    val contentId: String,
    val likedAt: Long = System.currentTimeMillis()
)