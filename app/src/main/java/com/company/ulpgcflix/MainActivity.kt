package com.company.ulpgcflix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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

            NavigationGraph(
                onToggleDarkMode = onToggleDarkMode,
                isDarkModeEnabled = isDarkTheme
            )

    }
}


@Preview(showBackground = true)
@Composable
fun AppPreview() {
    AppContent()
}