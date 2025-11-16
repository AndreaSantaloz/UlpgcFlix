package com.example.ulpgcflix.ui.vistas.nav

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Filtro: Screen("filtro")

    object  ListOfFilms: Screen("films")
}