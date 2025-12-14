package com.company.ulpgcflix.domain.model

import com.company.ulpgcflix.domain.model.enums.Status

//tabla de seguidores
data class Follow(
    private  val id: String,
    private  val followerId: String,    // quien SIGUE (el seguidor)
    private  val followingId: String,   // a quien SIGUE (el seguido)
    private  val followedAt: Long = System.currentTimeMillis(),
    private val status: Status,
)