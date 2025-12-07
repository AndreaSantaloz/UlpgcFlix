package com.company.ulpgcflix.ui.vistas.SocialMedia

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
    val channelsList by viewModel.visibleChannels
    val followedChannelIds by viewModel.followedChannelIds.collectAsState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val createdChannels = remember(channelsList, userId) {
        if (userId != null) {
            channelsList.filter { it.getIdOwner == userId }
        } else emptyList()
    }
    val subscribedChannels = remember(channelsList, userId, followedChannelIds, searchText) {
        if (userId == null) return@remember emptyList()

        val followed = channelsList.filter { followedChannelIds.contains(it.getId) && it.getIdOwner != userId }
        if (searchText.isNotEmpty()) {
            followed.filter {
                it.getName.contains(searchText, ignoreCase = true) ||
                        it.getDescription.contains(searchText, ignoreCase = true)
            }
        } else {
            followed
        }
    }


    val availableChannelsToFollow = remember(channelsList, userId, followedChannelIds, searchText) {
        if (userId != null && searchText.isNotEmpty()) {
            channelsList.filter {
                it.getIdOwner != userId && !followedChannelIds.contains(it.getId)
            }
        } else emptyList()
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {


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

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
            }

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

                        if (createdChannels.isNotEmpty()) {
                            item {
                                SectionHeader(title = "🚀 Mis Canales Creados")
                            }
                            items(createdChannels, key = { it.getId + "_created" }) { channel ->
                                ChannelCard(
                                    channel = channel,
                                    onFollowClick = { /* No aplica */ },
                                    onUnfollowClick = { /* No aplica */ },
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

                        if (subscribedChannels.isNotEmpty()) {
                            item {
                                SectionHeader(title = "✅ Canales Suscritos")
                            }
                            items(subscribedChannels, key = { it.getId + "_subscribed" }) { channel ->
                                ChannelCard(
                                    channel = channel,
                                    onFollowClick = { viewModel.followChannel(channel.getId) },
                                    onUnfollowClick = { viewModel.unfollowChannel(channel.getId) },
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


                        if (availableChannelsToFollow.isNotEmpty()) {
                            item {
                                SectionHeader(title = "✨ Canales a Seguir (Resultados de Búsqueda)")
                            }
                            items(availableChannelsToFollow, key = { it.getId + "_available" }) { channel ->
                                ChannelCard(
                                    channel = channel,
                                    onFollowClick = { viewModel.followChannel(channel.getId) },
                                    onUnfollowClick = { /* No aplica */ },
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
    showFollowButton: Boolean,
    isCreator: Boolean,
    isFollowing: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = channel.getImage,
                contentDescription = channel.getName,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))
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