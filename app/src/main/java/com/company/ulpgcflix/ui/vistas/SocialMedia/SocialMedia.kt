package com.company.ulpgcflix.ui.vistas.SocialMedia

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.company.ulpgcflix.model.Group
import com.company.ulpgcflix.ui.viewmodel.SocialMediaViewModel
import com.company.ulpgcflix.ui.viewmodel.SocialMediaViewModelFactory
import com.company.ulpgcflix.ui.servicios.SocialMediaService
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun SocialMedia(
    onNavigateBack: () -> Unit,
    onNavigateToCreateChannel: () -> Unit,
    onChannelDialog: (channelId: String) -> Unit,
    socialMediaService: SocialMediaService = remember {
        SocialMediaService(
            firebaseService = com.company.ulpgcflix.firebase.FirebaseFirestore(),
            auth = FirebaseAuth.getInstance()
        )
    },
    viewModel: SocialMediaViewModel = viewModel(
        factory = SocialMediaViewModelFactory(socialMediaService)
    )
) {

    val searchText by viewModel.searchText.collectAsState()
    val isLoading by viewModel.isLoading
    val error by viewModel.error
    val channelsList by viewModel.visibleChannels // Lista de canales visibles/filtrados por búsqueda

    // 🟢 OBTENEMOS la lista de IDs de canales seguidos del ViewModel (Ahora persistente)
    val followedChannelIds by viewModel.followedChannelIds.collectAsState()

    // --- Lógica para separar los canales ---
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    // 1. Canales Creados (createdChannels): Siempre se muestran si pasan el filtro de búsqueda.
    val createdChannels = remember(channelsList, userId) {
        if (userId != null) {
            channelsList.filter { it.getIdOwner == userId }
        } else emptyList()
    }

    // 2. Canales Suscritos (subscribedChannels): Canales que el usuario ya sigue. Siempre visibles.
    val subscribedChannels = remember(channelsList, userId, followedChannelIds, searchText) {
        if (userId == null) return@remember emptyList()

        // Canales que están en la lista de seguidos Y no son creados por el usuario
        val followed = channelsList.filter { followedChannelIds.contains(it.getId) && it.getIdOwner != userId }

        // Si hay texto en el buscador, filtramos estos canales seguidos también
        if (searchText.isNotEmpty()) {
            followed.filter {
                it.getName.contains(searchText, ignoreCase = true) ||
                        it.getDescription.contains(searchText, ignoreCase = true)
            }
        } else {
            // Si el buscador está vacío, mostramos todos los seguidos
            followed
        }
    }


    // 3. Canales para Seguir (availableToFollow): Solo aparecen si el campo de búsqueda tiene texto
    // y no están ya seguidos o creados por mí.
    val availableChannelsToFollow = remember(channelsList, userId, followedChannelIds, searchText) {
        if (userId != null && searchText.isNotEmpty()) {
            // Canales que cumplen el filtro de búsqueda, no son míos, y NO están seguidos.
            channelsList.filter {
                it.getIdOwner != userId && !followedChannelIds.contains(it.getId)
            }
        } else emptyList()
    }
    // ---------------------------------------

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // --- Encabezado ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                }
                Text(
                    text = "Canales de difusión",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )
            }

            // --- Campo de Búsqueda ---
            OutlinedTextField(
                value = searchText,
                onValueChange = viewModel::onSearchTextChanged,
                label = { Text("Buscar canales") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchTextChanged("") }) {
                            Icon(Icons.Filled.Close, contentDescription = "Limpiar")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )

            // --- Indicador de Carga y Error ---
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
            }

            // --- Lista de Canales Separada ---
            if (!isLoading) {
                val hasContent = createdChannels.isNotEmpty() || subscribedChannels.isNotEmpty() || (availableChannelsToFollow.isNotEmpty() && searchText.isNotEmpty())

                if (!hasContent) {
                    EmptyChannelsMessage(searchText = searchText)
                }
                else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {

                        // 1. CANALES CREADOS POR EL USUARIO
                        if (createdChannels.isNotEmpty()) {
                            item {
                                SectionHeader(title = "🚀 Mis Canales Creados")
                            }
                            items(createdChannels, key = { it.getId + "_created" }) { channel ->
                                ChannelCard(
                                    channel = channel,
                                    onFollowClick = { /* No aplica */ },
                                    onUnfollowClick = { /* No aplica */ },
                                    onChannelClick = { onChannelDialog(channel.getId) },
                                    onDeleteClick = { viewModel.deleteChannel(channel) },
                                    showFollowButton = false,
                                    isCreator = true,
                                    isFollowing = true
                                )
                            }
                            if (subscribedChannels.isNotEmpty() || availableChannelsToFollow.isNotEmpty()) {
                                item { Spacer(modifier = Modifier.height(16.dp)) }
                            }
                        }

                        // 2. CANALES SEGUIDOS (Persistentes)
                        if (subscribedChannels.isNotEmpty()) {
                            item {
                                SectionHeader(title = "✅ Canales Suscritos")
                            }
                            items(subscribedChannels, key = { it.getId + "_subscribed" }) { channel ->
                                ChannelCard(
                                    channel = channel,
                                    // 🟢 Las acciones llaman al VM, que actualiza el estado persistente.
                                    onFollowClick = { viewModel.followChannel(channel.getId) },
                                    onUnfollowClick = { viewModel.unfollowChannel(channel.getId) },
                                    onChannelClick = { onChannelDialog(channel.getId) },
                                    onDeleteClick = { /* No aplica */ },
                                    showFollowButton = true,
                                    isCreator = false,
                                    isFollowing = true
                                )
                            }
                            if (availableChannelsToFollow.isNotEmpty()) {
                                item { Spacer(modifier = Modifier.height(16.dp)) }
                            }
                        }


                        // 3. CANALES PARA SEGUIR (Solo búsqueda)
                        if (availableChannelsToFollow.isNotEmpty()) {
                            item {
                                SectionHeader(title = "✨ Canales a Seguir (Resultados de Búsqueda)")
                            }
                            items(availableChannelsToFollow, key = { it.getId + "_available" }) { channel ->
                                ChannelCard(
                                    channel = channel,
                                    // 🟢 Al seguir, el VM actualiza el estado.
                                    onFollowClick = { viewModel.followChannel(channel.getId) },
                                    onUnfollowClick = { /* No aplica */ },
                                    onChannelClick = { onChannelDialog(channel.getId) },
                                    onDeleteClick = { /* No aplica */ },
                                    showFollowButton = true,
                                    isCreator = false,
                                    isFollowing = false
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- FloatingActionButton ---
        FloatingActionButton(
            onClick = onNavigateToCreateChannel,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Filled.Create, contentDescription = "Crear canal")
        }
    }
}

// ===================================================
// COMPONENTES AUXILIARES
// ===================================================

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun ChannelCard(
    channel: Group,
    onFollowClick: () -> Unit,
    onUnfollowClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onChannelClick: (channelId: String) -> Unit,
    showFollowButton: Boolean,
    isCreator: Boolean,
    isFollowing: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = { onChannelClick(channel.getId) }),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del canal (AsyncImage)
            AsyncImage(
                model = channel.getImage,
                contentDescription = channel.getName,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Info del canal
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = channel.getName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = channel.getDescription,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // --- Botón Seguir/Siguiendo (Condicional) ---
            if (showFollowButton) {
                val onClickAction = if (isFollowing) onUnfollowClick else onFollowClick
                val buttonText = if (isFollowing) "Siguiendo" else "Seguir"

                Button(
                    onClick = onClickAction,
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    enabled = !isCreator
                ) {
                    Text(buttonText)
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            // --- Botón Eliminar (Condicional) ---
            if (isCreator) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete Channel",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyChannelsMessage(searchText: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Group,
                contentDescription = "Sin canales",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (searchText.isEmpty()) {
                Text(
                    text = "No hay canales disponibles",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "¡Crea el primer canal!",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                Text(
                    text = "No se encontraron canales para \"$searchText\"",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}