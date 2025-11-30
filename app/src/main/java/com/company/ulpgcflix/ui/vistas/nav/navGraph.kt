package com.company.ulpgcflix.ui.vistas.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.company.ulpgcflix.ui.vistas.home.OnboardingScreen
import com.company.ulpgcflix.ui.vistas.Categories.Categories
import com.company.ulpgcflix.ui.vistas.FavouriteVisualContent.FavouriteVisualContent
import com.company.ulpgcflix.ui.vistas.VisualContent.VisualContent
import com.company.ulpgcflix.ui.vistas.login.LoginScreen
import com.company.ulpgcflix.ui.vistas.registro.RegistroScreen
import com.company.ulpgcflix.ui.vistas.profile.ProfileScreen
import com.company.ulpgcflix.ui.vistas.Setting.Setting
import com.company.ulpgcflix.ui.vistas.SocialMedia.SocialMedia
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavigationGraph(
    onToggleDarkMode: (Boolean) -> Unit,
    isDarkModeEnabled: Boolean
) {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    NavHost(navController = navController, startDestination = Screen.Onboarding.route) {

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onContinueClick = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }


        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.VisualContent.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegistroScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Categories.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Categories.route) {
            Categories(
                onCategoriesSelected = {
                    navController.navigate(Screen.VisualContent.route) {
                        popUpTo(Screen.Categories.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.VisualContent.route) {
            VisualContent(
                setingSucess = {
                    navController.navigate(Screen.Profile.route)
                },
                onSocialMedia = {
                    navController.navigate(Screen.SocialMedia.route)
                }


            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen (

                onSettings={
                    navController.navigate(Screen.Setting.route)
                },onVisualContent={
                    navController.popBackStack()
                },onGoToFavorites={
                    navController.navigate(Screen.FavouriteVisualContent.route)
                },
            )
        }

        composable(Screen.Setting.route) {
            Setting(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEditProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onEditPreferences = {
                    navController.navigate(Screen.Categories.route)
                },
                onToggleDarkMode = onToggleDarkMode,
                isDarkModeEnabled = isDarkModeEnabled,

                onLogout = {
                    auth.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.FavouriteVisualContent.route) {
            FavouriteVisualContent (
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.SocialMedia.route){
            SocialMedia (
                onSocialMedia = {
                    navController.popBackStack()
                }
            )
        }


    }
}