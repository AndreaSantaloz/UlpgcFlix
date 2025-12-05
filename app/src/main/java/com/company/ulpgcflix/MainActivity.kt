package com.company.ulpgcflix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.company.ulpgcflix.ui.theme.UlpgcFlixTheme
import com.company.ulpgcflix.ui.vistas.nav.NavigationGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppContent()
        }
    }
}

@Composable
fun AppContent() {
    var isDarkTheme by remember { mutableStateOf(false) }
    val onToggleDarkMode: (Boolean) -> Unit = { shouldBeDark ->
        isDarkTheme = shouldBeDark
    }

    UlpgcFlixTheme(darkTheme = isDarkTheme) {
        // ✅ SOLUCIÓN: Usamos Scaffold para asegurar que el tema de fondo se aplique
        Scaffold(
            // El color del contenedor (el fondo principal) toma automáticamente
            // MaterialTheme.colorScheme.background.
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            // El NavigationGraph va dentro del content de Scaffold
            NavigationGraph(
                onToggleDarkMode = onToggleDarkMode,
                isDarkModeEnabled = isDarkTheme,
                // Si NavigationGraph necesita paddingValues, pásalos
                // paddingValues = paddingValues
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AppPreview() {
    AppContent()
}