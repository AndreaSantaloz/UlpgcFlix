package com.company.ulpgcflix.ui.servicios

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.company.ulpgcflix.model.Category
import com.company.ulpgcflix.ui.interfaces.CategoryRepository

class CategoryServices: CategoryRepository {

    private val movieCategories = listOf(
        Category("28", "Acción", Icons.Default.LocalFireDepartment),
        Category("12", "Aventura", Icons.Default.Explore),
        Category("16", "Animación", Icons.Default.Animation),
        Category("35", "Comedia", Icons.Default.EmojiEmotions),
        Category("80", "Crimen", Icons.Default.Gavel),
        Category("99", "Documental", Icons.Default.Book),
        Category("18", "Drama", Icons.Default.TheaterComedy),
        Category("10751", "Familiar", Icons.Default.ChildFriendly),
        Category("14", "Fantasía", Icons.Default.AutoAwesome),
        Category("36", "Historia", Icons.Default.Museum),
        Category("27", "Terror", Icons.Default.MoodBad),
        Category("10402", "Música", Icons.Default.MusicNote),
        Category("9648", "Misterio", Icons.Default.QuestionMark),
        Category("10749", "Romance", Icons.Default.Favorite),
        Category("878", "Ciencia Ficción", Icons.Default.RocketLaunch),
        Category("10770", "Película de TV", Icons.Default.Tv),
        Category("53", "Thriller", Icons.Default.FlashOn),
        Category("10752", "Guerra", Icons.Default.LocalPolice),
        Category("37", "Western", Icons.Default.Grain)
    )

    /**
     * Implementa el método de la interfaz CategoryRepository.
     * Devuelve la lista completa de categorías de Películas (la única soportada ahora).
     */
    override fun getCategories(): List<Category> {
        return movieCategories
    }

    /**
     * Método explícito para obtener solo categorías de películas.
     */
    fun getMovieCategories(): List<Category> {
        return movieCategories
    }
}