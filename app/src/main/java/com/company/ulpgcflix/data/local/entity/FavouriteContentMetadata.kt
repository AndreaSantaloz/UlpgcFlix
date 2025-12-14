package com.company.ulpgcflix.data.local.entity

data class FavoriteContentMetadata(
    val contentId: String = "",
    val title: String = "",
    val image: String = "",
    val kind: String = "",
    val assessment: Double = 0.0,
    val categoryIds: List<String> = emptyList(),
    val isAdult: Boolean = false,
    val lastFetched: Long = 0L


)