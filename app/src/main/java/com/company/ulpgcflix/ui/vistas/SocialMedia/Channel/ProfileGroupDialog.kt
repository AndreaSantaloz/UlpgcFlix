package com.company.ulpgcflix.ui.vistas.SocialMedia.Channel

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.company.ulpgcflix.R
import com.company.ulpgcflix.ui.viewmodel.ChannelProfileViewModel
import com.company.ulpgcflix.ui.viewmodel.ChannelProfileViewModelFactory
import com.company.ulpgcflix.ui.model.ChannelUiState
import com.company.ulpgcflix.ui.servicios.ChannelProfileService
import com.company.ulpgcflix.domain.model.GroupMember
import com.company.ulpgcflix.domain.model.enums.GroupRole
import androidx.compose.material3.OutlinedTextField


@Composable
fun MiembroItem(
    member: GroupMember,
    isCurrentUserCreator: Boolean,
    onRemoveClick: (String) -> Unit
) {
    val imagePainter = rememberAsyncImagePainter(
        model = member.getProfileImageUrl(),
        placeholder = painterResource(id = R.drawable.ic_launcher_background),
        error = painterResource(id = R.drawable.ic_launcher_background)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Ver perfil del miembro */ }
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = imagePainter,
            contentDescription = "Avatar de ${member.getName()}",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.size(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = member.getName(), fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface)

            if (member.getRolMember() != GroupRole.MEMBER) {
                Text(
                    text = member.getRolMember().name.lowercase().replaceFirstChar { it.uppercase() },
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        // Icono de Papelera para Eliminar (Visible solo para el creador y si no es el propietario)
        if (isCurrentUserCreator && member.getRolMember() != GroupRole.OWNER) {
            IconButton(onClick = { onRemoveClick(member.getIdMember()) }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar miembro",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileGroupDialog(
    channelId: String,
    onNavigateBack: () -> Unit,
    onEditDescriptionClick: () -> Unit = {},
    onEditImageClick: () -> Unit = {},
    onEditChannelGeneralClick: () -> Unit = {},
    channelProfileService: ChannelProfileService,
    modifier: Modifier = Modifier
) {

    val viewModelFactory = remember {
        ChannelProfileViewModelFactory(channelProfileService)
    }
    val channelProfileViewModel: ChannelProfileViewModel = viewModel(factory = viewModelFactory)

    val uiState by channelProfileViewModel.uiState.collectAsState()
    val currentUserId = remember { channelProfileViewModel.getCurrentUserId() }

    var isEditModeEnabled by remember { mutableStateOf(false) }

    var bufferDescriptionText by remember { mutableStateOf("") }
    var bufferNameText by remember { mutableStateOf("") }
    var bufferImageUrlText by remember { mutableStateOf("") }


    val saveChanges = {
        if (bufferNameText.isNotBlank()) {
            channelProfileViewModel.updateName(channelId, bufferNameText)
        }
        if (bufferDescriptionText.isNotBlank()) {
            channelProfileViewModel.updateDescription(channelId, bufferDescriptionText)
        }
        if (bufferImageUrlText.isNotBlank()) {
            channelProfileViewModel.updateImage(channelId, bufferImageUrlText)
        }
    }


    LaunchedEffect(channelId) {
        channelProfileViewModel.getInformationChannel(channelId)
    }

    LaunchedEffect(uiState) {
        if (uiState is ChannelUiState.Success) {
            val successState = uiState as ChannelUiState.Success
            bufferDescriptionText = successState.group.getDescription
            bufferNameText = successState.group.getName
            bufferImageUrlText = successState.group.getImage
        }
    }


    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Información del Canal") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    val isCreator = (uiState as? ChannelUiState.Success)?.let { state ->
                        val creatorIdCheck = state.group.getIdOwner
                        creatorIdCheck == currentUserId && creatorIdCheck.isNotBlank()
                    } ?: false

                    if (isCreator) {
                        IconButton(
                            onClick = {
                                if (isEditModeEnabled) {
                                    saveChanges()
                                }
                                isEditModeEnabled = !isEditModeEnabled // Alternar estado
                            }
                        ) {
                            Icon(
                                imageVector = if (isEditModeEnabled) Icons.Default.Done else Icons.Default.Edit,
                                contentDescription = if (isEditModeEnabled) "Guardar cambios" else "Habilitar edición",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->

        when (val state = uiState) {

            ChannelUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Cargando información del canal...")
                }
            }

            is ChannelUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Error al cargar: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }

            is ChannelUiState.Success -> {
                val groupDetails = state.group
                val membersList = state.members
                val participantesCount = membersList.size
                val isCreator = currentUserId == groupDetails.getIdOwner && groupDetails.getIdOwner.isNotBlank()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(paddingValues)
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = bufferImageUrlText.takeIf { it.isNotBlank() },
                                placeholder = painterResource(id = R.drawable.ic_launcher_background),
                                error = painterResource(id = R.drawable.ic_launcher_background)
                            ),
                            contentDescription = "Foto de perfil del grupo",
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable(enabled = isCreator && isEditModeEnabled) {
                                    Log.d("EDIT", "Clicked image for editing")
                                }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isEditModeEnabled) {
                            OutlinedTextField(
                                value = bufferNameText,
                                onValueChange = { bufferNameText = it },
                                label = { Text("Nombre del Canal") },
                                singleLine = true,
                                textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                                modifier = Modifier.fillMaxWidth(0.8f).padding(bottom = 8.dp)
                            )
                        } else {
                            Text(text = groupDetails.getName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }

                        Text(text = "$participantesCount participantes", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

                    // --- 2. DESCRIPCIÓN DEL GRUPO (Editable en modo edición) ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "Descripción", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.size(8.dp))

                        // DESCRIPCIÓN EDITABLE / NO EDITABLE
                        if (isEditModeEnabled) {
                            Column(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = bufferDescriptionText,
                                    onValueChange = { bufferDescriptionText = it },
                                    label = { Text("Editar descripción") },
                                    singleLine = false,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                // CAMPO DE EDICIÓN DE URL DE IMAGEN
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = bufferImageUrlText,
                                    onValueChange = { bufferImageUrlText = it },
                                    label = { Text("URL de la imagen (temporal)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        } else {
                            Text(
                                text = groupDetails.getDescription,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            text = "Participantes ($participantesCount)",
                            modifier = Modifier.padding(16.dp),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(membersList, key = { it.getIdMember() }) { member ->
                                MiembroItem(
                                    member = member,
                                    isCurrentUserCreator = isCreator,
                                    onRemoveClick = { memberId ->
                                        // TODO: Implementar lógica de eliminación
                                        Log.d("MEMBER_ACTION", "Solicitud de eliminación para miembro: $memberId")
                                    }
                                )
                                Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}