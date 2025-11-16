package com.example.ulpgcflix.ui.vistas.listOfFilms

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ulpgcflix.data.service.FiltroServiceImpl
import com.example.ulpgcflix.ui.interfaces.film

@Composable
fun PeliculasScreen() {
    // Estado para almacenar las películas
    var peliculas by remember { mutableStateOf<List<film>>(emptyList()) }

    // Cargar las películas filtradas al entrar en la pantalla
    LaunchedEffect(Unit) {
        peliculas = FiltroServiceImpl.obtenerPeliculasFiltradas()
    }
    var currentIndex by remember { mutableStateOf(0) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Recomendaciones",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))
        // NO intentar acceder si está vacío
        if (peliculas.isNotEmpty()) {

            val peli = peliculas[currentIndex]

            Card (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),      // Bordes redondeados
                elevation = CardDefaults.cardElevation(8.dp) // Sombra
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)   // Alto de la card con imagen
                ) {
                    // Imagen desde URL
                    AsyncImage(
                        model = peli.url,
                        contentDescription = peli.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier =Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {

                        Text(
                            text = peli.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "${peli.category}",
                            fontSize = 16.sp
                        )

                        Text(
                            text = "⭐ ${peli.rate}",
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Button(onClick = {
                    currentIndex =
                        if (currentIndex - 1 < 0) peliculas.size - 1
                        else currentIndex - 1
                }) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Anterior",
                        tint = Color.White
                    )
                }

                Button(onClick = {
                    currentIndex = (currentIndex + 1) % peliculas.size
                }) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Anterior",
                        tint = Color.White
                    )
                }
            }
        } else {
            // Mensaje mientras carga
            Text("Cargando películas...")
        }
    }
}
