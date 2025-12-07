package com.company.ulpgcflix.model

data class FavoriteContentMetadata(
    // Campos existentes (con valores predeterminados para el mapeo de Firestore)
    val contentId: String = "",
    val title: String = "",
    val image: String = "",
    val kind: String = "",
    val assessment: Double = 0.0,

    // Campos Añadidos:
    val categoryIds: List<Category> = emptyList(), // Guarda los IDs de categorías (números)
    val isAdult: Boolean = false,          // Contenido para adultos

    // Campo de gestión
    val lastFetched: Long = 0L // Usamos 0L como valor predeterminado en lugar de System.currentTimeMillis()
)