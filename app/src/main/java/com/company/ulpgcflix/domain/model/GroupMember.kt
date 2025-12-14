package com.company.ulpgcflix.domain.model

import com.company.ulpgcflix.domain.model.enums.GroupRole

data class GroupMember(
    private final val id:String,
    private final val idGroup:String,
    private final val idMember: String,
    private final val rolMember: GroupRole,
)