package com.company.ulpgcflix.ui.vistas.favList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.company.ulpgcflix.ui.interfaces.film
import com.company.ulpgcflix.ui.servicios.FavServiceImpl
import kotlinx.coroutines.launch

@Composable
fun ListaFavoritosScreen(
) {
    var favoritos by remember { mutableStateOf<List<film>>(emptyList()) }
    val scope = rememberCoroutineScope()

    // Cargar favoritos al entrar
    LaunchedEffect(Unit) {
        favoritos = FavServiceImpl.fav()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .background(Color(0xFFBBD0FF), shape = RoundedCornerShape(bottomStart = 40.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                IconButton(onClick = {  }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
                }
                Text(
                    text = "Películas que te gustan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            favoritos.forEach { film ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = film.url,
                            contentDescription = film.name,
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color(0xFF1C1F23), RoundedCornerShape(8.dp))
                        )

                        Spacer(modifier = Modifier.width(16.dp))
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(film.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(film.category, fontSize = 14.sp, color = Color.Gray)
                        }
                        IconButton(onClick = {
                            scope.launch {
                                FavServiceImpl.removeFav(film)
                                favoritos = FavServiceImpl.fav()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}