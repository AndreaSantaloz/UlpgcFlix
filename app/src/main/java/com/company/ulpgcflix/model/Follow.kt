package com.company.ulpgcflix.model

//tabla de seguidores
data class Follow(
    private final val id: String,
    private final val followerId: String,    // quien SIGUE (el seguidor)
    private final val followingId: String,   // a quien SIGUE (el seguido)
    private final val followedAt: Long = System.currentTimeMillis(),
    private val status:Status,
)
