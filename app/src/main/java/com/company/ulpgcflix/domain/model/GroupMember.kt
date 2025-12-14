package com.company.ulpgcflix.domain.model

import com.company.ulpgcflix.domain.model.enums.GroupRole

data class GroupMember(
    private val id: String,
    private val idGroup: String,
    private val idMember: String,
    private val rolMember: GroupRole,
    // 💡 NUEVOS CAMPOS AÑADIDOS para la carga eficiente del Servicio
    private val name: String,
    private val profileImageUrl: String? = null
) {
    /**
     * Getter para la propiedad 'id'
     */
    fun getId(): String {
        return id
    }

    /**
     * Getter para la propiedad 'idGroup'
     */
    fun getIdGroup(): String {
        return idGroup
    }

    /**
     * Getter para la propiedad 'idMember' (el UID)
     */
    fun getIdMember(): String {
        return idMember
    }

    /**
     * Getter para la propiedad 'rolMember'
     */
    fun getRolMember(): GroupRole {
        return rolMember
    }

    // 💡 Getter para la propiedad 'name'
    fun getName(): String {
        return name
    }

    // 💡 Getter para la propiedad 'profileImageUrl'
    fun getProfileImageUrl(): String? {
        return profileImageUrl
    }
}