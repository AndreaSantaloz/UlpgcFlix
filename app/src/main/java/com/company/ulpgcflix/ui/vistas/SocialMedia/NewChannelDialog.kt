package com.company.ulpgcflix.ui.vistas.SocialMedia

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.ulpgcflix.ui.viewmodel.SocialMediaViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewChannelDialog(
    onNavigateBack: () -> Unit,
    viewModel: SocialMediaViewModel
) {
    var channelName by remember { mutableStateOf("") }
    var channelDescription by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(true) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading
    val error by viewModel.error

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Nuevo Canal") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = channelName,
                onValueChange = { channelName = it },
                label = { Text("Nombre del Canal (Requerido)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                enabled = !isLoading
            )

            OutlinedTextField(
                value = channelDescription,
                onValueChange = { channelDescription = it },
                label = { Text("Descripción (Opcional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(8.dp),
                enabled = !isLoading
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPrivacyDialog = true },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isPublic) Icons.Filled.Public else Icons.Filled.Lock,
                        contentDescription = "Privacidad",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Visibilidad",
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (isPublic) "Público: Visible para todos" else "Privado: Solo por invitación",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    if (channelName.isNotBlank()) {
                        viewModel.createChannel(
                            name = channelName,
                            description = channelDescription,
                            isPublic = isPublic,
                            onComplete = onNavigateBack
                        )
                    } else {
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = channelName.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Crear Canal", fontSize = 16.sp)
                }
            }
        }
    }

    if (showPrivacyDialog) {
        PrivacySelectionDialog(
            currentIsPublic = isPublic,
            onDismiss = { showPrivacyDialog = false },
            onSelect = { selectedIsPublic ->
                isPublic = selectedIsPublic
                showPrivacyDialog = false
            }
        )
    }
}


@Composable
fun PrivacySelectionDialog(
    currentIsPublic: Boolean,
    onDismiss: () -> Unit,
    onSelect: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Visibilidad") },
        text = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(true) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentIsPublic,
                        onClick = { onSelect(true) }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Filled.Public, contentDescription = "Público")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Público")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(false) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = !currentIsPublic,
                        onClick = { onSelect(false) }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Filled.Lock, contentDescription = "Privado")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Privado")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}