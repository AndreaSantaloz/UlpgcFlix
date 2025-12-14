package com.company.ulpgcflix.ui.vistas.SocialMedia.Channel

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChannelDialog(
    channelId: String,
    onNavigateBack: () -> Unit,
    onProfileGroup: () -> Unit,
    modifier: Modifier,
) {
    var messageValue by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            MessageInputBar(
                text = messageValue,
                onTextChanged = { messageValue = it },
                onSendClick = {
                    messageValue = ""
                }
            )
        },

        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
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

                // --- Área de Mensajes ---
                // Aquí irá el LazyColumn que muestra la lista de mensajes
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Ocupa el espacio restante
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                }
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
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                // 💡 Aplicamos padding de insets a la barra inferior para evitar la barra de navegación del sistema (si existe)
                .navigationBarsPadding(),
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

            IconButton(
                onClick = onSendClick,
                enabled = text.isNotBlank(),
            ) {
                Icon(
                    Icons.Filled.Send,
                    contentDescription = "Enviar mensaje",
                    tint = if (text.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}