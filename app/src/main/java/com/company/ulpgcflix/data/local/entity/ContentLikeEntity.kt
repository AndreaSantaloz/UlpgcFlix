package com.company.ulpgcflix.data.local.entity


data class ContentLikeEntity(
    val userId: String,
    var contentId: String,
    var likedAt: Long = System.currentTimeMillis()

)