package com.company.ulpgcflix.ui.vistas.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun PerfilScreen(
    FavSuccess: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            IconButton(onClick = { /* abrir ajustes */ }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Ajustes",
                    tint = Color.Black
                )
            }
        }
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color(0xFFD9D9D9), CircleShape)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Nombre de usuario",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

        ProfileButton(text = "Lista de Favoritos") {
            FavSuccess()
        }

        Spacer(modifier = Modifier.height(20.dp))

        ProfileButton(text = "Lista de Amigos") {
            // acción
        }

        Spacer(modifier = Modifier.height(20.dp))

        ProfileButton(text = "Lista de Favoritos") {
            // acción
        }
    }
}

// 🌟 Composable reutilizable para cada botón azul
@Composable
fun ProfileButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFBBD0FF),  // Azul clarito
            contentColor = Color.Black
        )
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}