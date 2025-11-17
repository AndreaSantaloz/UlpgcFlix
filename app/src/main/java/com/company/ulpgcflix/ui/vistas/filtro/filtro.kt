package com.company.ulpgcflix.ui.vistas.filtro

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.ulpgcflix.ui.interfaces.Gusto
import com.example.ulpgcflix.data.service.FiltroServiceImpl
import kotlinx.coroutines.launch



@Composable
fun ElegirGustosScreen(onConfirmar: () -> Unit) {
    val gustos = listOf(
        Gusto("Acción", Icons.Default.LocalFireDepartment),
        Gusto("Comedia", Icons.Default.EmojiEmotions),
        Gusto("Drama", Icons.Default.TheaterComedy),
        Gusto("Terror", Icons.Default.MoodBad),
        Gusto("Ciencia Ficción", Icons.Default.AirplanemodeActive),
        Gusto("Romance", Icons.Default.Favorite),
        Gusto("Animación", Icons.Default.Movie),
        Gusto("Documental", Icons.Default.Book),
        Gusto("Aventura", Icons.Default.Explore),
        Gusto("Superhéroes", Icons.Default.Star),
        Gusto("Fantasia", Icons.Default.AutoAwesome),
        Gusto("Familiar/infantil", Icons.Default.ChildFriendly),

        )

    //Estado con los gustos seleccionados
    var seleccionados by remember { mutableStateOf(setOf<Gusto>()) }
    val scope = rememberCoroutineScope()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Elige tus intereses",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D2D2D)
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Selecciona las categorias\n que mas te gusten",
            fontSize = 19.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2D2D2D)
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Lista desplazable
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(gustos) { gusto ->
                val isSelected = seleccionados.contains(gusto)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isSelected) Color(0xFFA3BFFA) else Color.White,
                            RoundedCornerShape(50)
                        )
                        .border(
                            1.dp,
                            if (isSelected) Color(0xFFA3BFFA) else Color(0xFFE3E8EF),
                            RoundedCornerShape(50)
                        )
                        .clickable {
                            seleccionados =
                                if (isSelected) seleccionados - gusto
                                else seleccionados + gusto
                        }
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = gusto.icono,
                        contentDescription = gusto.nombre,
                        tint = if (isSelected) Color.White else Color(0xFF2D2D2D)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = gusto.nombre,
                        fontSize = 16.sp,
                        color = if (isSelected) Color.White else Color(0xFF2D2D2D),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    FiltroServiceImpl.guardarFiltros(seleccionados.toList())
                    onConfirmar()
                }
             },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D2D2D)),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp)
        ) {
            Text("Confirmar", color = Color.White)
        }
    }
}