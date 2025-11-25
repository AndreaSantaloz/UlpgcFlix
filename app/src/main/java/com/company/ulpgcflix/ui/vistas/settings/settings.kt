package com.company.ulpgcflix.ui.vistas.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.ulpgcflix.ui.viewmodels.AppViewModel   // ⬅️ IMPORT NECESARIO

@Composable
fun SettingsScreen(viewModel: AppViewModel, onBack: () -> Unit) {

    var darkMode by remember { mutableStateOf(viewModel.darkMode) }

    Column(modifier = Modifier.fillMaxSize()) {

        // 🔙 Barra superior (como en tu ejemplo)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onBack() }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Settings",
                fontSize = 22.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🔥 Botón de Dark Mode
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1A1A), RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Dark Mode",
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = darkMode,
                onCheckedChange = { isChecked ->
                    darkMode = isChecked
                    viewModel.toggleDarkMode(isChecked)
                }
            )
        }
    }
}
