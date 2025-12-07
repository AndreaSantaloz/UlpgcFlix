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
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            NavigationGraph(
                onToggleDarkMode = onToggleDarkMode,
                isDarkModeEnabled = isDarkTheme,

            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AppPreview() {
    AppContent()
}