package com.company.ulpgcflix.ui.vistas.Register

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.ulpgcflix.firebase.AuthCallback
import com.company.ulpgcflix.firebase.FirebaseAuthentication
import com.company.ulpgcflix.R
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun RegistroScreen(
    onRegisterSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val firebaseAuthentication = remember { FirebaseAuthentication() }
    var registerAttemptResult by remember { mutableStateOf<Result<String>?>(null) }


    LaunchedEffect(registerAttemptResult) {
        registerAttemptResult?.let { result ->
            isLoading = false

            result.onSuccess {

                Toast.makeText(context, "¡Registro exitoso! Ya puedes iniciar sesión.", Toast.LENGTH_LONG).show()
                onRegisterSuccess()
            }.onFailure { error ->
                Toast.makeText(context, "Error de registro: ${error.message}", Toast.LENGTH_LONG).show()
            }
            registerAttemptResult = null
        }
    }

    val attemptRegister: () -> Unit = {
        if (email.isNotBlank() && password.isNotBlank()) {
            isLoading = true

            firebaseAuthentication.RegisterUser(email, password, object : AuthCallback {
                override fun onSuccess(message: String) {
                    registerAttemptResult = Result.success(message)
                }

                override fun onFailure(errorMessage: String) {
                    registerAttemptResult = Result.failure(Exception(errorMessage))
                }
            })
        } else {
            Toast.makeText(context, "Por favor, completa los campos de correo y contraseña.", Toast.LENGTH_SHORT).show()
        }
    }


    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9FB))
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.35f)
                .align(Alignment.TopCenter)
        ) {
            drawCircle(
                color = Color(0xFFE6E7F2),
                radius = size.width * 0.7f,
                center = Offset(size.width / 2, 0f)
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.25f)
                .align(Alignment.BottomCenter)
        ) {
            drawCircle(
                color = Color(0xFFE6E7F2),
                radius = size.width * 0.3f,
                center = Offset(size.width, size.height)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                        text = "Crear\ncuenta",
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

            Spacer(modifier = Modifier.height(screenHeight * 0.05f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
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

                Spacer(modifier = Modifier.height(26.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo") },
                    shape = RoundedCornerShape(50),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF2D2D2D),
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(26.dp))

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

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = attemptRegister,
                    enabled = !isLoading,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D2D2D)),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(screenHeight * 0.07f)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Registrarse", color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Registrarse",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(screenHeight * 0.05f))
        }
    }
}