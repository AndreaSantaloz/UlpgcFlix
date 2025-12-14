package com.company.ulpgcflix.domain.model


// Clase que mapea la nueva colección 'groupProfiles'
data class GroupProfile(
    val groupId: String = "",       // El ID del grupo al que pertenece (Tu channelId)
    val ownerId: String = "",       // El ID del propietario (para seguridad)
    val description: String = "",   // Descripción editable
    val imageUrl: String = ""       // URL de la imagen editable
)