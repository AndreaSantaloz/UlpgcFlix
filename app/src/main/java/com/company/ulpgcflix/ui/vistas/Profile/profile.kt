package com.company.ulpgcflix.ui.vistas.Profile

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.company.ulpgcflix.ui.theme.ColorFavoritos
import com.company.ulpgcflix.ui.theme.ColorFavoritosDark
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


private const val FIRESTORE_COLLECTION_PROFILES = "profiles"
private const val FIRESTORE_FIELD_URL = "urlImagen"
private const val FIRESTORE_FIELD_DESCRIPTION = "description"
private const val FIRESTORE_FIELD_UID = "uidUsuario"
private const val FIRESTORE_FIELD_NAME = "name" // Nuevo campo para el nombre

data class UserProfileData(
    val imageUrl: String? = null,
    val description: String = "Hola, soy un crítico de cine apasionado y me encantan las películas de ciencia ficción.",
    val name: String? = null // Nuevo campo en el modelo
)


private fun saveProfileData(uid: String, url: String?, description: String, name: String?, onComplete: (Boolean) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val data = hashMapOf(
        FIRESTORE_FIELD_UID to uid,
        FIRESTORE_FIELD_URL to (url ?: ""),
        FIRESTORE_FIELD_DESCRIPTION to description,
        FIRESTORE_FIELD_NAME to (name ?: "") // Guardar el nombre
    )

    db.collection(FIRESTORE_COLLECTION_PROFILES).document(uid)
        .set(data)
        .addOnSuccessListener {
            onComplete(true)
            println("Datos de perfil guardados con éxito para UID: $uid")
        }
        .addOnFailureListener { e ->
            onComplete(false)
            println("Error al guardar los datos de perfil: $e")
        }
}


private fun fetchProfileData(uid: String, onResult: (UserProfileData?) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection(FIRESTORE_COLLECTION_PROFILES).document(uid)
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val imageUrl = document.getString(FIRESTORE_FIELD_URL)
                val description = document.getString(FIRESTORE_FIELD_DESCRIPTION) ?: UserProfileData().description
                val name = document.getString(FIRESTORE_FIELD_NAME) // Obtener el nombre

                onResult(UserProfileData(imageUrl.takeIf { !it.isNullOrEmpty() }, description, name.takeIf { !it.isNullOrEmpty() }))
            } else {
                onResult(UserProfileData())
            }
        }
        .addOnFailureListener { e ->
            println("Error al obtener los datos de perfil: $e")
            onResult(UserProfileData())
        }
}

// Añadimos la anotación para ignorar el parámetro de padding de Scaffold.
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    onSettings: () -> Unit,
    onVisualContent: () -> Unit,
    onGoToFavorites: () -> Unit,
    isEditing: Boolean,
    onSetEditing: (Boolean) -> Unit,
    modifier: Modifier,
) {
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val uid = firebaseUser?.uid
    var profileImageUrlString by remember { mutableStateOf<String?>(null) }
    var aboutMeText by remember { mutableStateOf(UserProfileData().description) }
    var profileName by remember { mutableStateOf<String?>(null) } // Estado para el nombre
    var isLoadingUrl by remember { mutableStateOf(true) }


    LaunchedEffect(uid) {
        if (uid != null) {
            fetchProfileData(uid) { profileData ->
                profileImageUrlString = profileData?.imageUrl
                aboutMeText = profileData?.description ?: UserProfileData().description
                profileName = profileData?.name // Asignar el nombre del perfil
                isLoadingUrl = false
            }
        } else {
            isLoadingUrl = false
        }
    }

    // Lógica para mostrar el nombre: usar Firestore (profileName), luego Auth (displayName), luego Email
    val displayedUsername = remember(firebaseUser, profileName) {
        profileName ?: // 1. Usar el nombre de Firestore (el que el usuario guardó)
        firebaseUser?.displayName ?: // 2. Fallback al nombre de Auth
        firebaseUser?.email?.substringBefore('@') ?: // 3. Fallback al email
        "Usuario Desconocido" // 4. Fallback final
    }

    val isProfileOwner = true
    val isDark = isSystemInDarkTheme()
    var currentUrlInput by remember(isEditing, profileImageUrlString) {
        mutableStateOf(profileImageUrlString ?: "")
    }

    // Input para el nombre en modo edición
    var currentNameInput by remember(isEditing, profileName) {
        mutableStateOf(profileName ?: firebaseUser?.displayName ?: "")
    }

    // SE ELIMINARON LOS CONTADORES DUMMY (followersCount, followingCount)

    Scaffold(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 0.dp),
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .background(
                        color = Color(0xFFD9D9D9),
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    )
            ) {
                // ... (Código para cargar la imagen o mostrar el placeholder)
                if (isLoadingUrl) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (!profileImageUrlString.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(model = profileImageUrlString),
                        contentDescription = "Foto de perfil de $displayedUsername",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Sin imagen de perfil",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(150.dp)
                        )
                    }
                }


                // Botón de Volver (ArrowBack)
                IconButton(
                    onClick = onVisualContent,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver a Contenido Visual",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }
                // Botón de Ajustes (Settings)
                IconButton(
                    onClick = onSettings,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Ajustes",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }


            Spacer(modifier = Modifier.height(20.dp))

            // Usar displayedUsername (el nombre cargado de Firestore)
            Text(
                text = displayedUsername,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 24.dp)
            )

            // SE ELIMINA LA ZONA DE CONTADORES DE SEGUIDORES/SEGUIDOS
            Spacer(modifier = Modifier.height(16.dp)) // Espacio adicional después del nombre

            Text(
                text = "Sobre mí",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (isProfileOwner && isEditing) {

                // Campo: Edición del Nombre
                OutlinedTextField(
                    value = currentNameInput,
                    onValueChange = { currentNameInput = it },
                    label = { Text("Nombre del Perfil") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = aboutMeText,
                    onValueChange = { aboutMeText = it },
                    label = { Text("Edita tu biografía") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .heightIn(min = 100.dp, max = 200.dp)
                )


                OutlinedTextField(
                    value = currentUrlInput,
                    onValueChange = { currentUrlInput = it },
                    label = { Text("URL de la Foto de Cabecera") },
                    leadingIcon = { Icon(Icons.Default.Image, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )

                Button(
                    onClick = {
                        val newUrl = currentUrlInput.takeIf { it.isNotBlank() }
                        val newName = currentNameInput.takeIf { it.isNotBlank() } // Obtener el nuevo nombre

                        if (uid != null) {
                            // PASAR EL NUEVO NOMBRE A LA FUNCIÓN DE GUARDADO
                            saveProfileData(uid, newUrl, aboutMeText, newName) { success ->
                                if (success) {
                                    profileImageUrlString = newUrl
                                    profileName = newName // Actualizar el estado del nombre
                                    onSetEditing(false)
                                } else {
                                    println("Error al guardar los datos en la base de datos.")
                                }
                            }
                        } else {
                            onSetEditing(false)
                        }

                    },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.End)
                        .padding(horizontal = 24.dp)
                ) {
                    Text("Guardar")
                }

            } else {

                Text(
                    text = aboutMeText,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 16.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))
                val favoritosColor = if (isDark) ColorFavoritosDark else ColorFavoritos
                OutlinedButton(
                    onClick = onGoToFavorites,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(0.8f)
                        .padding(horizontal = 24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = favoritosColor,
                        containerColor = Color.Transparent
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favoritos",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ver Mis Favoritos",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}


// SE ELIMINA LA FUNCIÓN FollowStat