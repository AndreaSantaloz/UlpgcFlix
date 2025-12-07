package com.company.ulpgcflix.ui.vistas.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewmodel.compose.viewModel
import com.company.ulpgcflix.ui.vistas.Home.OnboardingScreen
import com.company.ulpgcflix.ui.vistas.Categories.Categories
import com.company.ulpgcflix.ui.vistas.FavouriteVisualContent.FavouriteVisualContent
import com.company.ulpgcflix.ui.vistas.VisualContent.VisualContent
import com.company.ulpgcflix.ui.vistas.Login.LoginScreen
import com.company.ulpgcflix.ui.vistas.Register.RegistroScreen
import com.company.ulpgcflix.ui.vistas.Profile.ProfileScreen
import com.company.ulpgcflix.ui.vistas.Setting.Setting
import com.company.ulpgcflix.ui.vistas.SocialMedia.SocialMedia
import com.company.ulpgcflix.ui.vistas.SocialMedia.NewChannelDialog
import com.company.ulpgcflix.ui.viewmodel.SocialMediaViewModelFactory
import com.company.ulpgcflix.ui.servicios.SocialMediaService
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavigationGraph(
    onToggleDarkMode: (Boolean) -> Unit,
    isDarkModeEnabled: Boolean
) {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val socialMediaService = remember {
        SocialMediaService(
            firebaseService = com.company.ulpgcflix.firebase.FirebaseFirestore(),
            auth = auth
        )
    }

    // ESTADO ELEVADO: Controla si el perfil está en modo edición.
    var isEditingProfile by remember { mutableStateOf(false) }

    NavHost(navController = navController, startDestination = Screen.Onboarding.route) {

        composable(Screen.Onboarding.route) {
            OnboardingScreen(onContinueClick = { navController.navigate(Screen.Login.route) })
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Screen.VisualContent.route) { popUpTo(Screen.Login.route) { inclusive = true } } },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Register.route) {
            RegistroScreen(
                onRegisterSuccess = { navController.navigate(Screen.Categories.route) { popUpTo(Screen.Register.route) { inclusive = true } } }
            )
        }

        // DESTINO DE CATEGORÍAS (Manejo de Modo Edición vs. Modo Registro)
        composable(Screen.Categories.route) {
            // Determinamos si es Modo Edición comprobando si la ruta anterior NO fue la de Registro.
            val isEditMode = navController.previousBackStackEntry?.destination?.route != Screen.Register.route

            Categories(
                isEditMode = isEditMode,
                // El botón de retroceso (visible en modo edición) simplemente vuelve a la pantalla anterior.
                onBack = { navController.popBackStack() },
                // La acción principal al seleccionar las categorías
                onCategoriesSelected = {
                    if (isEditMode) {
                        // Si se está editando, solo vuelve a la pantalla anterior (Setting)
                        navController.popBackStack()
                    } else {
                        // Si es el registro inicial, navega a la pantalla principal y limpia el stack.
                        navController.navigate(Screen.VisualContent.route) {
                            popUpTo(Screen.Categories.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.VisualContent.route) {
            VisualContent(
                setingSucess = { navController.navigate(Screen.Profile.route) },
                onSocialMedia = { navController.navigate(Screen.SocialMedia.route) }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen (
                onSettings = { navController.navigate(Screen.Setting.route) },
                // Al volver a VisualContent, aseguramos que el modo edición se desactive.
                onVisualContent = {
                    isEditingProfile = false
                    navController.popBackStack()
                },
                onGoToFavorites = { navController.navigate(Screen.FavouriteVisualContent.route) },
                // PASAR ESTADO Y SETTER
                isEditing = isEditingProfile,
                onSetEditing = { isEditingProfile = it }
            )
        }

        composable(Screen.Setting.route) {
            Setting(
                onNavigateBack = {
                    // Limpia el estado de edición al retroceder si el destino era Profile
                    if (navController.previousBackStackEntry?.destination?.route == Screen.Profile.route) {
                        isEditingProfile = false
                    }
                    navController.popBackStack()
                },
                // Navega a ProfileScreen en modo edición.
                onEditProfile = {
                    isEditingProfile = true
                    navController.navigate(Screen.Profile.route)
                },
                // Navega a Categories en modo edición.
                onEditPreferences = { navController.navigate(Screen.Categories.route) },
                onToggleDarkMode = onToggleDarkMode,
                isDarkModeEnabled = isDarkModeEnabled,
                onLogout = {
                    auth.signOut()
                    navController.navigate(Screen.Login.route) { popUpTo(navController.graph.id) { inclusive = true } }
                }
            )
        }

        composable(Screen.FavouriteVisualContent.route) {
            FavouriteVisualContent (
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SocialMedia.route){
            val socialMediaViewModelFactory = remember { SocialMediaViewModelFactory(socialMediaService) }

            SocialMedia (
                viewModel = viewModel(factory = socialMediaViewModelFactory),
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreateChannel = { navController.navigate(Screen.NewChannelDialog.route) },
            )

        }

        composable(Screen.NewChannelDialog.route){
            val socialMediaViewModelFactory = remember { SocialMediaViewModelFactory(socialMediaService) }

            NewChannelDialog(
                viewModel = viewModel(factory = socialMediaViewModelFactory),
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}