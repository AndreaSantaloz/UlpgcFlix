package com.example.ulpgcflix.ui.vistas.login

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ulpgcflix.R

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9FB))
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        //Elipse superior adaptativa
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.35f)
                .align(Alignment.TopCenter)
        ) {
            drawCircle(
                color = Color(0xFFE6E7F2),
                radius = size.width * 0.7f,
                center = androidx.compose.ui.geometry.Offset(size.width / 2, 0f)
            )
        }

        //Elipse inferior
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.25f)
                .align(Alignment.BottomCenter)
        ) {
            drawCircle(
                color = Color(0xFFE6E7F2),
                radius = size.width * 0.3f,
                center = androidx.compose.ui.geometry.Offset(size.width, size.height)
            )
        }

        //Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Encabezado (dentro de la elipse)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.25f),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Iniciar\nsesión",
                        fontSize = (screenWidth.value * 0.06).sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2D2D2D)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(screenWidth * 0.22f)
                    )
                }
            }

            //Contenido de campos y botones centrado
            Spacer(modifier = Modifier.height(screenHeight * 0.05f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // esto centra el bloque en el espacio disponible
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Usuario") },
                    shape = RoundedCornerShape(50),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFE6E6E6),
                        focusedBorderColor = Color(0xFF2D2D2D),
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(50),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF2D2D2D),
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = { /* TODO */ }) {
                    Text("¿Olvidaste la contraseña?", color = Color.Gray, fontSize = 13.sp)
                }

                TextButton(onClick = {onRegisterClick() }) {
                    Text("Crear cuenta", color = Color.Gray, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onLoginSuccess() },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D2D2D)),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(screenHeight * 0.07f)
                ) {
                    Text("Confirmar", color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Confirmar",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(screenHeight * 0.05f))
        }
    }
}

