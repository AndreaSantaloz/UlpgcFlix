package com.company.ulpgcflix.ui.servicios

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.company.ulpgcflix.ui.model.CategoryUi
import com.company.ulpgcflix.ui.interfaces.CategoryRepository

class CategoryServices: CategoryRepository {

    private val movieCategories = listOf(
        CategoryUi("28", "Acción", Icons.Default.LocalFireDepartment),
        CategoryUi("12", "Aventura", Icons.Default.Explore),
        CategoryUi("16", "Animación", Icons.Default.Animation),
        CategoryUi("35", "Comedia", Icons.Default.EmojiEmotions),
        CategoryUi("80", "Crimen", Icons.Default.Gavel),
        CategoryUi("99", "Documental", Icons.Default.Book),
        CategoryUi("18", "Drama", Icons.Default.TheaterComedy),
        CategoryUi("10751", "Familiar", Icons.Default.ChildFriendly),
        CategoryUi("14", "Fantasía", Icons.Default.AutoAwesome),
        CategoryUi("36", "Historia", Icons.Default.Museum),
        CategoryUi("27", "Terror", Icons.Default.MoodBad),
        CategoryUi("10402", "Música", Icons.Default.MusicNote),
        CategoryUi("9648", "Misterio", Icons.Default.QuestionMark),
        CategoryUi("10749", "Romance", Icons.Default.Favorite),
        CategoryUi("878", "Ciencia Ficción", Icons.Default.RocketLaunch),
        CategoryUi("10770", "Película de TV", Icons.Default.Tv),
        CategoryUi("53", "Thriller", Icons.Default.FlashOn),
        CategoryUi("10752", "Guerra", Icons.Default.LocalPolice),
        CategoryUi("37", "Western", Icons.Default.Grain)
    )

    override fun getCategories(): List<CategoryUi> {
        return movieCategories
    }


    fun getMovieCategories(): List<CategoryUi> {
        return movieCategories
    }
}