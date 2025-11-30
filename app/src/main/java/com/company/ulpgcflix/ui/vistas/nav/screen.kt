package com.company.ulpgcflix.ui.vistas.nav

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Categories: Screen("categories")
    object  VisualContent: Screen("visualContent")
    object Profile: Screen("profile")
    object FavouriteVisualContent: Screen("favouriteVisualContent")
    object Setting: Screen("Setting")

    object SocialMedia: Screen("SocialMedia")
}