package com.company.ulpgcflix.ui.vistas.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
// ... (otras importaciones) ...
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.company.ulpgcflix.ui.vistas.Home.OnboardingScreen
import com.company.ulpgcflix.ui.vistas.Categories.Categories
import com.company.ulpgcflix.ui.vistas.FavouriteVisualContent.FavouriteVisualContent
import com.company.ulpgcflix.ui.vistas.VisualContent.VisualContent
import com.company.ulpgcflix.ui.vistas.Login.LoginScreen
import com.company.ulpgcflix.ui.vistas.Register.RegistroScreen
import com.company.ulpgcflix.ui.vistas.Profile.ProfileScreen
import com.company.ulpgcflix.ui.vistas.Setting.Setting
import com.company.ulpgcflix.ui.vistas.SocialMedia.Channel.NewChannelDialog
import com.company.ulpgcflix.ui.vistas.SocialMedia.SocialMedia
import com.company.ulpgcflix.ui.viewmodel.SocialMediaViewModelFactory
import com.company.ulpgcflix.ui.viewmodel.SocialMediaViewModel
import com.company.ulpgcflix.ui.servicios.SocialMediaService
import com.company.ulpgcflix.ui.vistas.SocialMedia.Channel.ChannelDialog
import com.company.ulpgcflix.ui.vistas.SocialMedia.Channel.ProfileGroupDialog
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.NavType
import androidx.navigation.navArgument

// 💡 IMPORTACIÓN NECESARIA para el nuevo Composable
import com.company.ulpgcflix.ui.vistas.SocialMedia.Friends.FollowDialog


@Composable
fun NavigationGraph(
    onToggleDarkMode: (Boolean) -> Unit,
    isDarkModeEnabled: Boolean,
    paddingValues: PaddingValues // <-- Aquí recibes el relleno de Scaffold
) {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    var isProfileEditing by remember { mutableStateOf(false) }
    var isChannelEditing by remember { mutableStateOf(false) }

    val socialMediaService = remember {
        SocialMediaService(
            firebaseService = com.company.ulpgcflix.firebase.FirebaseFirestore(),
            auth = auth
        )
    }

    // El modificador base que contiene el relleno de la barra de estado/navegación
    val safeModifier = Modifier.padding(paddingValues)

    NavHost(navController = navController, startDestination = Screen.Onboarding.route) {

        // --- Rutas de Autenticación y Home (SIN CAMBIOS) ---
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onContinueClick = { navController.navigate(Screen.Login.route) },
                modifier = safeModifier
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Screen.VisualContent.route) { popUpTo(Screen.Login.route) { inclusive = true } } },
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                modifier = safeModifier
            )
        }
        composable(Screen.Register.route) {
            RegistroScreen(
                onRegisterSuccess = { navController.navigate(Screen.Categories.route) { popUpTo(Screen.Register.route) { inclusive = true } } },
                modifier = safeModifier
            )
        }
        composable(Screen.Categories.route) {
            Categories(
                onCategoriesSelected = { navController.navigate(Screen.VisualContent.route) { popUpTo(Screen.Categories.route) { inclusive = true } } },
                modifier = safeModifier
            )
        }
        composable(Screen.VisualContent.route) {
            VisualContent(
                setingSucess = { navController.navigate(Screen.Profile.route) },
                onSocialMedia = { navController.navigate(Screen.SocialMedia.route) },
                modifier = safeModifier
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen (
                onSettings={ navController.navigate(Screen.Setting.route) },
                onVisualContent={ navController.popBackStack() },
                onGoToFavorites={ navController.navigate(Screen.FavouriteVisualContent.route) },
                isEditing = isProfileEditing,
                onSetEditing = { isProfileEditing = it },
                modifier = safeModifier
            )
        }
        composable(Screen.Setting.route) {
            Setting(
                onNavigateBack = { navController.popBackStack() },
                onEditProfile = {
                    isProfileEditing = true
                    navController.navigate(Screen.Profile.route)
                },
                onEditPreferences = { navController.navigate(Screen.Categories.route) },
                onToggleDarkMode = onToggleDarkMode,
                isDarkModeEnabled = isDarkModeEnabled,
                onLogout = {
                    auth.signOut()
                    navController.navigate(Screen.Login.route) { popUpTo(navController.graph.id) { inclusive = true } }
                },
                modifier = safeModifier
            )
        }
        composable(Screen.FavouriteVisualContent.route) {
            FavouriteVisualContent (
                onNavigateBack = { navController.popBackStack() },
                modifier = safeModifier
            )
        }

        // --- Rutas de Social Media (SocialMedia se actualiza para navegar a FollowDialog) ---
        composable(Screen.SocialMedia.route){
            val socialMediaViewModelFactory = remember { SocialMediaViewModelFactory(socialMediaService) }

            SocialMedia (
                viewModel = viewModel<SocialMediaViewModel>(factory = socialMediaViewModelFactory),
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreateChannel = { navController.navigate(Screen.NewChannelDialog.route) },
                onChannelDialog = { channelId ->
                    navController.navigate("${Screen.ChannelDialog.route}/$channelId")
                },
                // 💡 CAMBIO: Navegación al FollowDialog al presionar el icono de personas
                onNavigateToFriendsOrRequests = { navController.navigate(Screen.FollowDialog.route) },
                modifier = safeModifier
            )
        }

        // ----------------------------------------------------------------------------------
        // 💡 NUEVA RUTA: FOLLOW DIALOG
        // ----------------------------------------------------------------------------------
        composable(Screen.FollowDialog.route) {
            FollowDialog(
                onNavigateBack = { navController.popBackStack() },
                // Aquí podrías pasar el SocialMediaViewModel o un ViewModel dedicado
                // para manejar las solicitudes de seguimiento reales.
                modifier = safeModifier
            )
        }
        // ----------------------------------------------------------------------------------


        // --- Rutas de Diálogos (SIN CAMBIOS) ---
        composable(Screen.NewChannelDialog.route){
            val socialMediaViewModelFactory = remember { SocialMediaViewModelFactory(socialMediaService) }

            NewChannelDialog(
                viewModel = viewModel<SocialMediaViewModel>(factory = socialMediaViewModelFactory),
                onNavigateBack = { navController.popBackStack() },
                modifier = safeModifier
            )
        }

        composable(
            route = "${Screen.ChannelDialog.route}/{channelId}",
            arguments = listOf(navArgument("channelId") { type = NavType.StringType })
        ) { backStackEntry ->
            val channelId = backStackEntry.arguments?.getString("channelId") ?: return@composable

            ChannelDialog(
                channelId = channelId,
                onNavigateBack = { navController.popBackStack() },
                onProfileGroup = {
                    navController.navigate(Screen.ProfileGroupDialog.route)
                },
                modifier = safeModifier
            )
        }

        composable(Screen.ProfileGroupDialog.route) {
            ProfileGroupDialog(
                onNavigateBack = { navController.popBackStack() },
                modifier = safeModifier
            )
        }
    }
}