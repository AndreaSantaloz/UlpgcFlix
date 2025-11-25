package com.company.ulpgcflix.ui.servicios

import com.company.ulpgcflix.ui.interfaces.film
interface FavService{

    suspend fun addFav(film: film)
    suspend fun removeFav(film: film)
    suspend fun fav(): MutableList<film>
}

object FavServiceImpl : FavService {

    private var films: MutableList<film> = mutableListOf()
    override suspend fun fav(): MutableList<film> {
        return films
    }

    override suspend fun addFav(film: film) {
        if (!films.contains(film)) {
            films.add(film)
        }
    }

    override suspend fun removeFav(film: film) {
        films.remove(film)
    }

}
