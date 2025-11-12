// FiltroServiceImpl.kt
package com.example.ulpgcflix.data.service

import com.example.ulpgcflix.ui.interfaces.film
import com.example.ulpgcflix.ui.interfaces.Gusto
import kotlinx.coroutines.delay
interface FiltroService {
    suspend fun guardarFiltros(gustos: List<Gusto>)
    suspend fun obtenerPeliculasFiltradas(): List<film>
}

object FiltroServiceImpl : FiltroService {

    private var filtrosSeleccionados: List<Gusto> = emptyList()

    // Base de datos simulada
    private val peliculas = listOf(
        film("Avengers", "Acción", 9, "https://example.com/avengers.jpg"),
        film("Toy Story", "Animación", 8, "https://example.com/toystory.jpg"),
        film("Titanic", "Romance", 9, "https://example.com/titanic.jpg"),
        film("It", "Terror", 7, "https://example.com/it.jpg"),
        film("Interstellar", "Ciencia Ficción", 10, "https://example.com/interstellar.jpg"),
        film("La La Land", "Comedia", 8, "https://example.com/lalaland.jpg")
    )

    override suspend fun guardarFiltros(gustos: List<Gusto>) {
        filtrosSeleccionados = gustos
        delay(300) // Simular pequeña latencia
    }

    override suspend fun obtenerPeliculasFiltradas(): List<film> {
        delay(800) // Simular carga
        if (filtrosSeleccionados.isEmpty()) return peliculas
        return peliculas.filter { film ->
            filtrosSeleccionados.any { gusto ->
                film.category.equals(gusto.nombre, ignoreCase = true)
            }
        }
    }
}
