package com.company.ulpgcflix.ui.vistas.Profile

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.company.ulpgcflix.ui.theme.ColorFavoritos
import com.company.ulpgcflix.ui.theme.ColorFavoritosDark
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    onSettings: () -> Unit,
    onVisualContent: () -> Unit,
    onGoToFavorites: () -> Unit,
    isEditing: Boolean,
    onSetEditing: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("user_profile_prefs", Context.MODE_PRIVATE)

    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val username = remember(firebaseUser) {
        firebaseUser?.displayName ?:
        firebaseUser?.email?.substringBefore('@') ?:
        "Usuario Desconocido"
    }

    val isProfileOwner = true
    var aboutMeText by remember { mutableStateOf("Hola, soy un crítico de cine apasionado y me encantan las películas de ciencia ficción.") }
    val isDark = isSystemInDarkTheme()

    var profileImageUrlString by remember {
        mutableStateOf<String?>(prefs.getString("profile_url", null))
    }

    var currentUrlInput by remember(isEditing, profileImageUrlString) {
        mutableStateOf(profileImageUrlString ?: "")
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 0.dp),
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(
                    color = Color(0xFFD9D9D9),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                )
        ) {

            if (!profileImageUrlString.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(model = profileImageUrlString),
                    contentDescription = "Foto de perfil de $username",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Sin imagen de perfil",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(150.dp) // Icono grande para cubrir el fondo
                    )
                }
            }


            // Iconos de navegación y ajustes (Se renderizan después, superponiéndose a la imagen)
            IconButton(
                onClick = onVisualContent,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver a Contenido Visual",
                    tint = Color.Black, // Asegúrate de que el color contraste con la imagen
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(
                onClick = onSettings,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Ajustes",
                    tint = Color.Black, // Asegúrate de que el color contraste con la imagen
                    modifier = Modifier.size(32.dp)
                )
            }


        }


        Spacer(modifier = Modifier.height(20.dp))

         Text(
            text = username,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sobre mí",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (isProfileOwner && isEditing) {
            // RAMA 1: MODO EDICIÓN
            OutlinedTextField(
                value = aboutMeText,
                onValueChange = { aboutMeText = it },
                label = { Text("Edita tu biografía") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(150.dp)
            )


            OutlinedTextField(
                value = currentUrlInput,
                onValueChange = { currentUrlInput = it },
                label = { Text("URL de la Foto de Cabecera") },
                leadingIcon = { Icon(Icons.Default.Image, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            Button(
                onClick = {
                    profileImageUrlString = currentUrlInput.takeIf { it.isNotBlank() }
                    prefs.edit().putString("profile_url", profileImageUrlString).apply()
                    onSetEditing(false)
                },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.End)
                    .padding(horizontal = 24.dp)
            ) {
                Text("Guardar")
            }

        } else {
            // RAMA 2: MODO VISTA
            Text(
                text = aboutMeText,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))
            val favoritosColor = if (isDark) ColorFavoritosDark else ColorFavoritos
            OutlinedButton(
                onClick = onGoToFavorites,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.8f)
                    .padding(horizontal = 24.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = favoritosColor,
                    containerColor = Color.Transparent
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favoritos",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ver Mis Favoritos",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}