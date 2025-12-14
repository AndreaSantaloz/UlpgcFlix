package com.company.ulpgcflix.ui.servicios

import com.company.ulpgcflix.ui.interfaces.ApiService
import com.company.ulpgcflix.BuildConfig

class VisualContentService(
    private val apiService: ApiService
) {
    suspend fun getFilmsFromApi(genreIds: String, page: Int): String {
        val response = apiService.getFilms(
            apiKey = BuildConfig.TMDB_API_KEY,
            genreIds = genreIds,
            page = page
        )

        if (response.isSuccessful && response.body() != null) {
            return response.body()!!.string()
        } else {
            throw Exception("Error al cargar películas: ${response.code()}")
        }
    }


}