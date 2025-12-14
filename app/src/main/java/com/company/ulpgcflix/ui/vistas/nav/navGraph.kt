package com.company.ulpgcflix.ui.vistas.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.company.ulpgcflix.firebase.FirestoreRepository

// --- Importaciones de Vistas ---
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
import com.company.ulpgcflix.ui.vistas.SocialMedia.Channel.ChannelDialog
import com.company.ulpgcflix.ui.vistas.SocialMedia.Channel.ProfileGroupDialog

// --- Importaciones de Servicios ---
import com.company.ulpgcflix.ui.servicios.SocialMediaService
import com.company.ulpgcflix.ui.servicios.ChannelDialogService
import com.company.ulpgcflix.ui.servicios.ChannelProfileService

// --- Importaciones de ViewModels y Factories ---
import com.company.ulpgcflix.ui.viewmodel.SocialMediaViewModel
import com.company.ulpgcflix.ui.viewmodel.SocialMediaViewModelFactory
import com.company.ulpgcflix.ui.viewmodel.ChannelDialogViewModel
import com.company.ulpgcflix.ui.viewmodel.ChannelViewModelFactory
import com.company.ulpgcflix.ui.viewmodel.ChannelProfileViewModel
import com.company.ulpgcflix.ui.viewmodel.ChannelProfileViewModelFactory
import com.company.ulpgcflix.ui.vistas.nav.Screen
@Composable
fun NavigationGraph(
    onToggleDarkMode: (Boolean) -> Unit,
    isDarkModeEnabled: Boolean,
    paddingValues: PaddingValues
) {
    val navController = rememberNavController()

    // --- INSTANCIAS ÚNICAS DE DEPENDENCIAS BASE ---
    val auth = remember { FirebaseAuth.getInstance() }
    val firestoreRepository = remember { FirestoreRepository() }

    // --- INSTANCIAS ÚNICAS DE SERVICIOS ---
    val socialMediaService = remember {
        SocialMediaService(firebaseService = firestoreRepository, auth = auth)
    }
    val channelDialogService = remember { ChannelDialogService(firestoreRepository) }
    // Asumo que ChannelProfileService ha sido actualizado con la lógica de edición
    val channelProfileService = remember { ChannelProfileService(service = firestoreRepository, auth = auth) }

    // --- Variables de Estado ---
    var isProfileEditing by remember { mutableStateOf(false) }
    var isChannelEditing by remember { mutableStateOf(false) }

    val safeModifier = Modifier.padding(paddingValues)

    NavHost(navController = navController, startDestination = Screen.Onboarding.route) {

        // --- 1. Rutas de Autenticación y Perfiles ---
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


        // --- 2. Rutas de Redes Sociales (SocialMedia) ---
        composable(Screen.SocialMedia.route){
            val socialMediaViewModelFactory = remember { SocialMediaViewModelFactory(socialMediaService) }

            SocialMedia (
                viewModel = viewModel<SocialMediaViewModel>(factory = socialMediaViewModelFactory),
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreateChannel = { navController.navigate(Screen.NewChannelDialog.route) },
                onChannelDialog = { channelId ->
                    navController.navigate("${Screen.ChannelDialog.route}/$channelId")
                },
                modifier = safeModifier
            )
        }



        composable(Screen.NewChannelDialog.route){
            val socialMediaViewModelFactory = remember { SocialMediaViewModelFactory(socialMediaService) }

            NewChannelDialog(
                viewModel = viewModel<SocialMediaViewModel>(factory = socialMediaViewModelFactory),
                onNavigateBack = { navController.popBackStack() },
                modifier = safeModifier
            )
        }

        // --- 3. Ruta de Mensajería (ChannelDialog) ---
        composable(
            route = "${Screen.ChannelDialog.route}/{channelId}",
            arguments = listOf(navArgument("channelId") { type = NavType.StringType })
        ) { backStackEntry ->

            val channelId = backStackEntry.arguments?.getString("channelId") ?: return@composable

            val channelViewModelFactory = remember {
                ChannelViewModelFactory(
                    dialogService = channelDialogService,
                    profileService = channelProfileService
                )
            }

            val channelViewModel: ChannelDialogViewModel = viewModel(factory = channelViewModelFactory)

            ChannelDialog(
                channelId = channelId,
                onNavigateBack = { navController.popBackStack() },
                onProfileGroup = {
                    navController.navigate("${Screen.ProfileGroupDialog.route}/$channelId")
                },
                channelViewModel = channelViewModel,
                modifier = safeModifier,
            )
        }

        // --- 4. Ruta del Perfil del Canal (ProfileGroupDialog) ---
        composable(
            route = "${Screen.ProfileGroupDialog.route}/{channelId}",
            arguments = listOf(navArgument("channelId") { type = NavType.StringType })
        ) { backStackEntry ->

            val channelId = backStackEntry.arguments?.getString("channelId") ?: return@composable

            val channelProfileViewModelFactory = remember {
                ChannelProfileViewModelFactory(channelProfileService)
            }

            val channelProfileViewModel: ChannelProfileViewModel = viewModel(factory = channelProfileViewModelFactory)

            ProfileGroupDialog(
                channelId = channelId,
                onNavigateBack = { navController.popBackStack() },

                onEditDescriptionClick = {
                },

                onEditImageClick = {
                },

                onEditChannelGeneralClick = {
                },

                channelProfileService = channelProfileService,
                modifier = safeModifier
            )
        }
    }
}