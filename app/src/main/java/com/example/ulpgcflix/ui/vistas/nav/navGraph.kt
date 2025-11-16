package com.example.ulpgcflix.ui.vistas.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ulpgcflix.ui.vistas.comienzo.OnboardingScreen
import com.example.ulpgcflix.ui.vistas.login.LoginScreen
import com.example.ulpgcflix.ui.vistas.registro.RegistroScreen
import com.example.ulpgcflix.ui.vistas.filtro.ElegirGustosScreen
import com.example.ulpgcflix.ui.vistas.listOfFilms.PeliculasScreen

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Onboarding.route) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onContinueClick = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = { navController.navigate(Screen.Home.route) }
            )
        }

        composable(Screen.Register.route) {
            RegistroScreen(
                onRegisterSuccess = { navController.navigate(Screen.Filtro.route) },
                //onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.Filtro.route){
            ElegirGustosScreen(
                onConfirmar= {navController.navigate(Screen.ListOfFilms.route)
                }
            )
        }
        composable(Screen.ListOfFilms.route){
            PeliculasScreen()
        }
    }
}