package com.company.ulpgcflix.ui.vistas.VisualContent

import com.company.ulpgcflix.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.company.ulpgcflix.model.VisualContent
import com.company.ulpgcflix.ui.interfaces.ApiService
import com.company.ulpgcflix.ui.servicios.CategoryServices
import com.company.ulpgcflix.ui.servicios.VisualContentService
import com.company.ulpgcflix.ui.servicios.FavoritesService
import com.company.ulpgcflix.ui.viewmodel.VisualContentViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable
import androidx.compose.ui.graphics.graphicsLayer
import com.company.ulpgcflix.ui.servicios.UserCategoriesService
import kotlin.math.roundToInt
import kotlin.math.abs


class VisualContentViewModelFactory(
    private val visualContentService: VisualContentService,
    private val categoryServices: CategoryServices,
    private val apiService: ApiService,
    private val favoritesService: FavoritesService,
    private val userCategoriesService: UserCategoriesService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisualContentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VisualContentViewModel(
                visualContentService,
                categoryServices,
                apiService,
                favoritesService,
                userCategoriesService
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

object RetrofitClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}


@Composable
fun SwipeableCard(
    item: VisualContent,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val offsetX = remember { Animatable(0f) }
    val swipeThreshold = 400f
    val rotationFactor = 0.05f

    Card(
        modifier = modifier
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .graphicsLayer(
                rotationZ = offsetX.value * rotationFactor,
                alpha = 1f - (abs(offsetX.value) / swipeThreshold).coerceIn(0f, 1f)
            )
            .pointerInput(item.getTitle) {
                detectDragGestures(
                    onDragEnd = {
                        coroutineScope.launch {
                            when {
                                offsetX.value > swipeThreshold -> {
                                    offsetX.animateTo(targetValue = size.width.toFloat() * 1.5f, animationSpec = tween(300))
                                    onSwipeRight()
                                }
                                offsetX.value < -swipeThreshold -> {
                                    offsetX.animateTo(targetValue = -size.width.toFloat() * 1.5f, animationSpec = tween(300))
                                    onSwipeLeft()
                                }
                                else -> {
                                    offsetX.animateTo(0f, tween(300))
                                }
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount.x)
                        }
                    }
                )
            },

        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500${item.getImage}",
                contentDescription = item.getTitle,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,

            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(16.dp)
            ) {
                Text(
                    text = item.getTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                val primaryCategory = item.getCategory.firstOrNull()?.categoryName ?: "Sin Categoría"
                Text(
                    text = primaryCategory,
                    fontSize = 16.sp,
                    color = Color.LightGray
                )
                Text(
                    text = "⭐ %.1f".format(item.getAssessment),
                    fontSize = 16.sp,
                    color = Color.Yellow
                )
            }
        }
    }


    LaunchedEffect(item.getTitle) {
        offsetX.snapTo(0f)
    }
}



@Composable
fun VisualContent(
    setingSucess: () -> Unit,
    onSocialMedia:() -> Unit,
    favoritesService: FavoritesService = remember {
        FavoritesService(
            FirebaseFirestore.getInstance(),
            FirebaseAuth.getInstance()
        )
    },
    userCategoriesService: UserCategoriesService = remember {
        UserCategoriesService()
    },
    viewModel: VisualContentViewModel = viewModel(
        factory = VisualContentViewModelFactory(
            apiService = RetrofitClient.apiService,
            visualContentService = VisualContentService(RetrofitClient.apiService),
            categoryServices = CategoryServices(),
            favoritesService = favoritesService,
            userCategoriesService = userCategoriesService
        )
    )
) {
    val contenido by viewModel.contentList
    val scope = rememberCoroutineScope()
    var currentIndex by remember { mutableStateOf(0) }
    var viewCount by remember { mutableStateOf(0) }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    LaunchedEffect(currentUserId) {
        if (currentUserId != null) {
            viewModel.loadContentForUser(currentUserId, append = false)
        } else {

        }
    }

    val handleDislikeAction: () -> Unit = {
        scope.launch {
            viewCount++
            if (viewCount >= 10 && contenido.isNotEmpty() && currentUserId != null) {
                viewModel.loadContentForUser(currentUserId, append = true)
                viewCount = 0
            }
            if (contenido.isNotEmpty()) {
                currentIndex = (currentIndex + 1) % contenido.size
            }
        }
    }

    val handleLikeAction: (VisualContent) -> Unit = { item ->
        scope.launch {
            viewModel.saveFavorite(item)
            viewCount++
            if (viewCount >= 10 && contenido.isNotEmpty() && currentUserId != null) {
                viewModel.loadContentForUser(currentUserId, append = true)
                viewCount = 0
            }
            if (contenido.isNotEmpty()) {
                currentIndex = (currentIndex + 1) % contenido.size
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal=16.dp,vertical=8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top=15.dp,bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FloatingActionButton(
                onClick = onSocialMedia,
                modifier = Modifier.size(45.dp),
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    painter = painterResource(id=R.drawable.ic_popcorn_social_media),
                    contentDescription = "Red Social",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(40.dp)
                )
            }
            Text(
                text = "Recomendaciones de Películas",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            )
            FloatingActionButton(
                onClick = setingSucess,
                modifier = Modifier.size(45.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        viewModel.error.value?.let {
            Text(it, color = Color.Red, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 8.dp))
        }
        if (contenido.isNotEmpty() && currentIndex < contenido.size) {
            val item: VisualContent = contenido[currentIndex]

            SwipeableCard(
                item = item,
                onSwipeLeft = handleDislikeAction,
                onSwipeRight = { handleLikeAction(item) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp)
            )

            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(onClick = handleDislikeAction, containerColor = Color.Red) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "dislike",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Text(
                    "Vistas: $viewCount / 10",
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )
                FloatingActionButton(onClick = { handleLikeAction(item) }, containerColor = Color.Green) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "like",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

        } else if (viewModel.error.value == null && currentUserId != null) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text("Cargando películas...", modifier = Modifier.padding(top = 16.dp))
                }
            }
        } else if (currentUserId == null) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Inicia sesión para ver tus recomendaciones personalizadas.", textAlign = TextAlign.Center)
            }
        } else if (contenido.isEmpty() && viewModel.error.value == null) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No se encontraron películas. Intenta más tarde.", textAlign = TextAlign.Center)
            }
        }
    }
}