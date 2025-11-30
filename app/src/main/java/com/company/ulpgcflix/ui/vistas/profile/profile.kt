package com.company.ulpgcflix.ui.vistas.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    onSettings: () -> Unit,
    onVisualContent: () -> Unit,
    onGoToFavorites: () -> Unit,
) {
    val firebaseUser = FirebaseAuth.getInstance().currentUser

    val username = remember(firebaseUser) {
        firebaseUser?.displayName ?:
        firebaseUser?.email?.substringBefore('@') ?:
        "Usuario Desconocido"
    }

    val isProfileOwner = true
    var isEditing by remember { mutableStateOf(false) }
    var aboutMeText by remember { mutableStateOf("Hola, soy un crítico de cine apasionado y me encantan las películas de ciencia ficción.") }


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
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
            }
            // 2. BOTÓN AJUSTES
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
                    tint = Color.Black,
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

        //Se cambiara cuando meta la red social

        Text(
            text = "⭐ 4.5",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
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
            OutlinedTextField(
                value = aboutMeText,
                onValueChange = { aboutMeText = it },
                label = { Text("Edita tu biografía") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(150.dp)
            )

            Button(
                onClick = { isEditing = false /* Aquí iría la lógica de guardar en la DB */ },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.End)
                    .padding(horizontal = 24.dp)
            ) {
                Text("Guardar")
            }

        } else {
            Text(
                text = aboutMeText,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 16.dp)
            )

            if (isProfileOwner) {
                TextButton(
                    onClick = { isEditing = true },
                    modifier = Modifier.align(Alignment.End).padding(horizontal = 24.dp)
                ) {
                    Text("Editar")
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedButton(
            onClick = onGoToFavorites,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.8f)
                .padding(horizontal = 24.dp)
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
                fontSize = 18.sp
            )
        }


        Spacer(modifier = Modifier.height(30.dp))
    }
}