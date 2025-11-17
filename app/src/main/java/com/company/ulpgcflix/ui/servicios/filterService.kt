// FiltroServiceImpl.kt
package com.example.ulpgcflix.data.service

import com.company.ulpgcflix.ui.interfaces.film
import com.company.ulpgcflix.ui.interfaces.Gusto

interface FiltroService {
    suspend fun guardarFiltros(gustos: List<Gusto>)
    suspend fun obtenerPeliculasFiltradas(): List<film>
}

object FiltroServiceImpl : FiltroService {

    private var filtrosSeleccionados: List<Gusto> = emptyList()

    // Base de datos simulada
    private val peliculas = listOf(
        film("Avengers", "Acción", 9, "https://static.wikia.nocookie.net/marvelcinematicuniverse/images/2/2b/The_Avengers_Poster.png"),
        film("Toy Story", "Animación", 8, "https://lumiere-a.akamaihd.net/v1/images/poster_toy_story_1995_605b75ac.jpeg"),
        film("Titanic", "Romance", 9, "https://upload.wikimedia.org/wikipedia/commons/3/38/Titanic_Poster.jpg"),
        film("It", "Terror", 7, "https://m.media-amazon.com/images/M/MV5BMjEyNTA4MDk4Nl5BMl5BanBnXkFtZTcwMDg0ODg3OA@@._V1_SY1000_CR0,0,674,1000_AL_.jpg"),
        film("Interstellar", "Ciencia Ficción", 10, "https://upload.wikimedia.org/wikipedia/en/b/bc/Interstellar_film_poster.jpg"),
        film("La La Land", "Comedia", 8, "https://upload.wikimedia.org/wikipedia/en/a/ab/La_La_Land_%28film%29_poster.jpg")

    )

    override suspend fun guardarFiltros(gustos: List<Gusto>) {
        filtrosSeleccionados = gustos
    }

    override suspend fun obtenerPeliculasFiltradas(): List<film> {
        if (filtrosSeleccionados.isEmpty()) return peliculas
        return peliculas.filter { film ->
            filtrosSeleccionados.any { gusto ->
                film.category.equals(gusto.nombre, ignoreCase = true)
            }
        }
    }
}
