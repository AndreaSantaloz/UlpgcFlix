package com.company.ulpgcflix.ui.vistas.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.company.ulpgcflix.ui.vistas.comienzo.OnboardingScreen
import com.company.ulpgcflix.ui.vistas.filtro.ElegirGustosScreen
import com.company.ulpgcflix.ui.vistas.listOfFilms.PeliculasScreen
import com.company.ulpgcflix.ui.vistas.login.LoginScreen
import com.company.ulpgcflix.ui.vistas.registro.RegistroScreen
import com.company.ulpgcflix.ui.vistas.profile.PerfilScreen
import com.company.ulpgcflix.ui.vistas.favList.ListaFavoritosScreen

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Onboarding.route) {

        // ONBOARDING -> LOGIN (permite volver atrás)
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onContinueClick = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        // LOGIN -> LISTA (NO permite volver al login)
        // LOGIN -> REGISTRO (sí permite volver atrás)
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.ListOfFilms.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // REGISTRO-> FILTRO (permite volver atrás)
        composable(Screen.Register.route) {
            RegistroScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Filtro.route)
                }
            )
        }

        // FILTRO ->LISTA (NO permite volver al filtro)
        composable(Screen.Filtro.route) {
            ElegirGustosScreen(
                onConfirmar = {
                    navController.navigate(Screen.ListOfFilms.route) {
                        popUpTo(Screen.Filtro.route) { inclusive = true }
                    }
                }
            )
        }

        // PELÍCULAS (back funciona normal)
        // PERFIL (permite volver)
        composable(Screen.ListOfFilms.route) {
            PeliculasScreen(
                setingSucess = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        // PERFIL -> FAVORITOS (permite volver)
        composable(Screen.Profile.route) {
            PerfilScreen(
                FavSuccess = {
                    navController.navigate(Screen.FavList.route)
                }
            )
        }

        // FAVORITOS → (solo vuelve atrás)
        composable(Screen.FavList.route) {
            ListaFavoritosScreen()
        }
    }
}
