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

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Onboarding.route) {

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onContinueClick = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.Login.route){
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.ListOfFilms.route)
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegistroScreen(
                onRegisterSuccess = { navController.navigate(Screen.Filtro.route) }
            )
        }

        composable(Screen.Filtro.route){
            ElegirGustosScreen(
                onConfirmar = {
                    navController.navigate(Screen.ListOfFilms.route)
                }
            )
        }

        composable(Screen.ListOfFilms.route){
            PeliculasScreen()
        }
    }
}