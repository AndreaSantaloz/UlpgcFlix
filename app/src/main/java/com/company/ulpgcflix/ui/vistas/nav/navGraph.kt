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

        composable(Screen.Categories.route) {
            val isEditMode = navController.previousBackStackEntry?.destination?.route != Screen.Register.route

            Categories(
                isEditMode = isEditMode,
                onBack = { navController.popBackStack() },
                onCategoriesSelected = {
                    if (isEditMode) {
                        navController.popBackStack()
                    } else {
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
                onVisualContent = {
                    isEditingProfile = false
                    navController.popBackStack()
                },
                onGoToFavorites = { navController.navigate(Screen.FavouriteVisualContent.route) },
                isEditing = isEditingProfile,
                onSetEditing = { isEditingProfile = it }
            )
        }

        composable(Screen.Setting.route) {
            Setting(
                onNavigateBack = {
                    if (navController.previousBackStackEntry?.destination?.route == Screen.Profile.route) {
                        isEditingProfile = false
                    }
                    navController.popBackStack()
                },
                onEditProfile = {
                    isEditingProfile = true
                    navController.navigate(Screen.Profile.route)
                },
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