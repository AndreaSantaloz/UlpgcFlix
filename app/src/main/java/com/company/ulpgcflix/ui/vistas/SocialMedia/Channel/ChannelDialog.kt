package com.company.ulpgcflix.ui.vistas.SocialMedia.Channel

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.company.ulpgcflix.ui.viewmodel.ChannelDialogViewModel
import com.company.ulpgcflix.ui.model.ChannelUiState
import com.company.ulpgcflix.domain.model.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChannelDialog(
    channelId: String,
    onNavigateBack: () -> Unit,
    onProfileGroup: () -> Unit,
    modifier: Modifier,
    channelViewModel: ChannelDialogViewModel
) {
    val uiState by channelViewModel.uiState.collectAsState()
    val messagesList by channelViewModel.messages.collectAsState()
    var messageValue by remember { mutableStateOf("") }

    LaunchedEffect(channelId) {
        // 💡 CORRECCIÓN 1: Usar la nueva función loadChannelData
        channelViewModel.loadChannelData(channelId)
    }

    Scaffold(
        bottomBar = {
            MessageInputBar(
                text = messageValue,
                onTextChanged = { messageValue = it },
                onSendClick = {
                    channelViewModel.sendMessage(channelId, messageValue)
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
                // --- Barra Superior del Canal ---
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
                        // 💡 CORRECCIÓN 2: Acceder al nombre a través del objeto Group.getName
                        text = when (val state = uiState) {
                            is ChannelUiState.Loading -> "Cargando..."
                            // Accede al objeto 'group' y luego usa su getter 'getName'
                            is ChannelUiState.Success -> state.group.getName
                            is ChannelUiState.Error -> "Error: ${state.message}"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .clickable(onClick = onProfileGroup)
                    )
                }

                // --- ÁREA DE MENSAJES (LazyColumn) ---
                if (uiState is ChannelUiState.Error) {
                    Text(
                        text = "Error al conectar con el canal.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        reverseLayout = true,
                        contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        items(messagesList.reversed(), key = { it.getId() }) { message ->
                            MessageBubble(
                                message = message,
                                isCurrentUser = message.getIdUser() == channelViewModel.getUserCurrently()
                            )
                        }
                    }
                }
            }
        }
    )
}


// ----------------------------------------------------------------------
// COMPONENTE BURBUJA DE MENSAJE (Sin cambios)
// ----------------------------------------------------------------------

@Composable
fun MessageBubble(message: Message, isCurrentUser: Boolean) {
    val horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
    val bubbleColor = if (isCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val timestampFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = horizontalAlignment
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (isCurrentUser) 20.dp else 4.dp,
                bottomEnd = if (isCurrentUser) 4.dp else 20.dp
            ),
            color = bubbleColor,
            modifier = Modifier.widthIn(min = 70.dp, max = 300.dp),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = message.getText(),
                    color = textColor,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timestampFormatter.format(Date(message.timestamp)),
                    color = textColor.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}


// ----------------------------------------------------------------------
// COMPONENTE DE ENTRADA DE MENSAJES (Sin cambios)
// ----------------------------------------------------------------------

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
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
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