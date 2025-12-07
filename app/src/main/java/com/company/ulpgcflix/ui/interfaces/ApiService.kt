package com.company.ulpgcflix.ui.interfaces

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

interface ApiService {

    @GET("discover/movie")
    suspend fun getFilms(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genreIds: String,
        @Query("page") page: Int
    ): Response<okhttp3.ResponseBody>


}