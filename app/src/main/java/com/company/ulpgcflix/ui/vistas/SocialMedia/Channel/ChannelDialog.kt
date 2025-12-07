package com.company.ulpgcflix.ui.vistas.SocialMedia.Channel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun ChannelDialog(
    channelId: String,
    onNavigateBack: () -> Unit,
    onProfileGroup: () -> Unit,
    // Aquí deberías tener tu ViewModel y la carga de datos del canal
) {
    // Ejemplo de estado para el campo de texto
    var messageValue by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                }
                Text(
                    text = "Nombre del grupo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .clickable(onClick = onProfileGroup)
                )
            }
        },

        bottomBar = {
            MessageInputBar(
                text = messageValue,
                onTextChanged = { messageValue = it },
                onSendClick = {
                    messageValue = ""
                }
            )
        },

        // 3. Área de Contenido Principal (Mensajes)
        content = { paddingValues ->
            // Aquí irá el LazyColumn que muestra la lista de mensajes
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TODO: Aquí va el LazyColumn con los mensajes
                Text(
                    text = "Área de mensajes (LazyColumn)",
                    modifier = Modifier.padding(top = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "La barra de entrada está fija abajo",
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}

@Composable
fun MessageInputBar(
    text: String,
    onTextChanged: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        // Usa una ligera elevación para separarlo del contenido
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- Campo de Texto ---
            OutlinedTextField(
                value = text,
                onValueChange = onTextChanged,
                label = { Text("Escribir mensaje...") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),


            )

            Spacer(modifier = Modifier.width(8.dp))

            // --- Botón de Enviar ---
            IconButton(
                onClick = onSendClick,
                enabled = text.isNotBlank(), // Desactivado si no hay texto
            ) {
                Icon(
                    Icons.Filled.Send,
                    contentDescription = "Enviar mensaje",
                    // Cambia el color si está deshabilitado
                    tint = if (text.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}