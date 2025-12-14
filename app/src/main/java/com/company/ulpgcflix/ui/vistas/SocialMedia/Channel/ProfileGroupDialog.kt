package com.company.ulpgcflix.ui.vistas.SocialMedia.Channel

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource // Necesitarás este si usas recursos reales
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.ulpgcflix.R // Asegúrate de tener un R para recursos de prueba

// Datos de ejemplo para los miembros del grupo
data class Miembro(val nombre: String, val esAdmin: Boolean = false)

// Añadimos la anotación para ignorar el parámetro de padding de Scaffold.
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileGroupDialog(
    onNavigateBack: () -> Unit,
    modifier: Modifier,
    nombreGrupo: String = "Grupo de PopCorn",
    descripcion: String = "Fans de las películas y series retro de la ULPGC.",
    miembros: List<Miembro> = listOf(
        Miembro("Tú (Admin)", true),
        Miembro("Ariadna", true),
        Miembro("Borja"),
        Miembro("Carlos"),
        Miembro("Diana"),
        Miembro("Elena"),
        Miembro("Félix"),
    )
) {
    // 💡 1. ENVOLVEMOS TODA LA PANTALLA EN UN SCAFFOLD
    Scaffold(modifier = modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F0F0)) // Fondo claro para simular la app

        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                }
                Text(
                    text = "Info. del Grupo",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            }

            // --- 1. INFO PRINCIPAL: Imagen y Nombre del Grupo ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Placeholder para la imagen de perfil (simulado)
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background), // Usa una imagen real aquí
                    contentDescription = "Foto de perfil del grupo",
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(Color.Blue) // Color de fondo si no hay imagen
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = nombreGrupo,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${miembros.size} participantes",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color.LightGray, thickness = 1.dp)

            // --- 2. DESCRIPCIÓN DEL GRUPO ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = "Descripción", tint = Color.Gray)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = descripcion,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color.LightGray, thickness = 1.dp)

            // --- 3. LISTA DE MIEMBROS (Usamos LazyColumn para eficiencia) ---
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                item {
                    Text(
                        text = "Participantes (${miembros.size})",
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray
                    )
                }
                items(miembros) { miembro ->
                    MiembroItem(miembro = miembro)
                    Divider(modifier = Modifier.padding(start = 72.dp), color = Color.LightGray, thickness = 0.5.dp)
                }
            }
        }
    }
}

// Composable para cada elemento de la lista de miembros
@Composable
fun MiembroItem(miembro: Miembro) {
    // ROW: Distribuye los elementos horizontalmente (Imagen, Nombre, Admin Tag)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Placeholder de la imagen del miembro
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background), // Usar imagen de perfil del usuario
            contentDescription = "Foto de ${miembro.nombre}",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Magenta)
        )
        Spacer(modifier = Modifier.size(16.dp))

        // 2. Nombre y Tag de Admin
        Column {
            Text(
                text = miembro.nombre,
                fontWeight = if (miembro.esAdmin) FontWeight.SemiBold else FontWeight.Normal
            )
            if (miembro.esAdmin) {
                Text(
                    text = "Admin. del grupo",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

// Vista previa para verificar el diseño
@Preview(showBackground = true)
@Composable
fun PreviewProfileGroupDialog() {
    // Para que este preview funcione, asegúrate de tener un recurso drawable llamado
    // ic_launcher_background o reemplaza la llamada a painterResource con un painter nulo si es necesario.
    ProfileGroupDialog(onNavigateBack = {}, modifier = Modifier,)
}