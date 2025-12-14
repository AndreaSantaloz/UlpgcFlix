package com.company.ulpgcflix.ui.vistas.SocialMedia.Friends

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import com.company.ulpgcflix.R

data class DummyFollowRequest(
    val id: String,
    val name: String,
    val profileImageUrl: String? = null
)

fun getDummyRequests(): List<DummyFollowRequest> {
    return listOf(
        DummyFollowRequest("u1", "Ariadna_24"),
        DummyFollowRequest("u2", "Borja_T"),
        DummyFollowRequest("u3", "Carlos.ULPGC"),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowDialog(
    onNavigateBack: () -> Unit,
    pendingRequests: List<DummyFollowRequest> = getDummyRequests(),
    onAcceptRequest: (DummyFollowRequest) -> Unit = {},
    onRejectRequest: (DummyFollowRequest) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Solicitudes de Seguimiento") },
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
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            if (pendingRequests.isEmpty()) {
                EmptyRequestsMessage()
            } else {
                Text(
                    text = "${pendingRequests.size} Solicitudes Pendientes",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                LazyColumn {
                    items(pendingRequests, key = { it.id }) { request ->
                        FollowRequestItem( // 💡 Nombre del componente Item ajustado
                            request = request,
                            onAccept = { onAcceptRequest(request) },
                            onReject = { onRejectRequest(request) }
                        )
                        Divider(modifier = Modifier.padding(start = 72.dp), thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

// ===================================================
// COMPONENTE DE ITEM INDIVIDUAL
// ===================================================

@Composable
fun FollowRequestItem( // 💡 Nombre del componente Item ajustado
    request: DummyFollowRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Imagen de Perfil (Placeholder del logo)
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Foto de ${request.name}",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.width(16.dp))

        // 2. Info del usuario
        Column(modifier = Modifier.weight(1f)) {
            Text(request.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text("Quiere seguirte", fontSize = 12.sp, color = Color.Gray)
        }

        // 3. Botones de Acción (Aceptar / Rechazar)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Botón Aceptar
            Button(
                onClick = onAccept,
                modifier = Modifier.height(36.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Text("Aceptar", fontSize = 13.sp)
            }

            // Botón Rechazar
            OutlinedButton(
                onClick = onReject,
                modifier = Modifier.height(36.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Text("Rechazar", fontSize = 13.sp)
            }
        }
    }
}

// ===================================================
// COMPONENTE DE MENSAJE VACÍO
// ===================================================

@Composable
fun EmptyRequestsMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Filled.People,
                contentDescription = "Sin solicitudes",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "¡Todo en orden!",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                "No tienes solicitudes de seguimiento pendientes.",
                color = Color.Gray
            )
        }
    }
}

// ===================================================
// PREVIEW
// ===================================================

@Preview(showBackground = true)
@Composable
fun PreviewFollowDialog() {
    MaterialTheme {
        FollowDialog(onNavigateBack = {})
    }
}