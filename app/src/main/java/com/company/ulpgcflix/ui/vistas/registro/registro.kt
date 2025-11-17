package com.company.ulpgcflix.ui.vistas.registro

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
import com.example.ulpgcflix.R
import com.company.ulpgcflix.firestore.AuthCallback
import com.company.ulpgcflix.firestore.FirestoreClass
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun RegistroScreen(
    onRegisterSuccess: () -> Unit
) {
    // 1. Estados para los campos y la lógica
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") } // Renombrado de gMail a email
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // 2. Instancias y Contexto
    val context = LocalContext.current
    val firestoreClass = remember { FirestoreClass() }
    var registerAttemptResult by remember { mutableStateOf<Result<String>?>(null) }


    // 3. Manejo de la Lógica Asíncrona (LaunchedEffect)
    // Se ejecuta cada vez que el estado 'registerAttemptResult' cambia tras el callback de Firebase
    LaunchedEffect(registerAttemptResult) {
        registerAttemptResult?.let { result ->
            isLoading = false // Siempre detenemos la carga al recibir respuesta

            result.onSuccess {
                // Éxito: Muestra mensaje y navega (onRegisterSuccess)
                Toast.makeText(context, "¡Registro exitoso! Ya puedes iniciar sesión.", Toast.LENGTH_LONG).show()
                onRegisterSuccess() // Esto debería navegar de vuelta a la pantalla de login o al home
            }.onFailure { error ->
                // Fallo: Muestra el mensaje de error de Firebase (ej. 'La contraseña debe tener al menos 6 caracteres')
                Toast.makeText(context, "Error de registro: ${error.message}", Toast.LENGTH_LONG).show()
            }
            // Limpia el resultado para evitar re-ejecuciones
            registerAttemptResult = null
        }
    }

    // 4. Función de Registro
    val attemptRegister: () -> Unit = {
        if (email.isNotBlank() && password.isNotBlank()) {
            isLoading = true

            // Llama a la función de Firebase Register
            firestoreClass.RegisterUser(email, password, object : AuthCallback {
                override fun onSuccess(message: String) {
                    // Actualiza el estado con éxito
                    registerAttemptResult = Result.success(message)
                }

                override fun onFailure(errorMessage: String) {
                    // Actualiza el estado con fallo, usando Exception para el mensaje
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
                center = Offset(size.width / 2, 0f)
            )
        }

        // Elipse inferior
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

        // Contenido principal
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

            // Contenido de campos y botones centrado
            Spacer(modifier = Modifier.height(screenHeight * 0.05f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // esto centra el bloque en el espacio disponible
            ) {
                // Campo de Nombre de Usuario (opcional en Firebase Auth, pero mantenido en UI)
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

                // Campo de Correo
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

                // Campo de Contraseña
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

                // BOTÓN DE REGISTRARSE
                Button(
                    onClick = attemptRegister,
                    enabled = !isLoading, // Deshabilita mientras carga
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