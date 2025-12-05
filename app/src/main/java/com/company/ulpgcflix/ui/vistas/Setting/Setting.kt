package com.company.ulpgcflix.ui.vistas.Setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Setting(
    onNavigateBack: () -> Unit,
    onEditProfile: () -> Unit,
    onEditPreferences: () -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onLogout: () -> Unit,
    isDarkModeEnabled: Boolean = false // <-- Usamos este parámetro
) {
    // ❌ LÍNEA ELIMINADA: var darkModeState by remember { mutableStateOf(isDarkModeEnabled) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes de Usuario") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                // ✅ IMPORTANTE: Asegúrate de que el contenedor principal dentro de Scaffold
                // no tenga un color de fondo fijo. Scaffold debería aplicar el color del tema
                // automáticamente, pero si tienes problemas, añade:
                // .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {


            Text(
                text = "Cuenta",
                style = MaterialTheme.typography.titleMedium,
                // ✅ Usa el color del tema para el texto
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            SettingItem(
                title = "Editar Perfil",
                icon = Icons.Default.Person,
                onClick = onEditProfile
            )
            Divider()

            SettingItem(
                title = "Editar Gustos y Preferencias",
                icon = Icons.Default.Star,
                onClick = onEditPreferences
            )
            Divider()


            Text(
                text = "Apariencia",
                style = MaterialTheme.typography.titleMedium,
                // ✅ Usa el color del tema para el texto
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    // ❌ ELIMINADA la lógica de clic en el Row, el Switch la maneja
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Modo Oscuro",
                    style = MaterialTheme.typography.bodyLarge,
                    // ✅ Usa el color del tema para el texto
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f) // Ocupa el espacio
                )
                Spacer(Modifier.width(16.dp))
                Switch(
                    // ✅ CORREGIDO: Usamos el estado global
                    checked = isDarkModeEnabled,
                    onCheckedChange = { isChecked ->
                        // ✅ CORREGIDO: Llamamos directamente al callback global
                        onToggleDarkMode(isChecked)
                    }
                )
            }
            Divider()


            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Salir")
                    Spacer(Modifier.width(8.dp))
                    Text("Cerrar Sesión y Salir")
                }
            }
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                // ✅ Usa el color del tema para el texto
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}