package com.company.ulpgcflix.ui.vistas.Categories

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.ulpgcflix.ui.servicios.CategoryServices
import com.company.ulpgcflix.ui.model.CategoryUi
import com.company.ulpgcflix.ui.servicios.UserCategoriesService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Categories(
    isEditMode: Boolean = false,
    onBack: (() -> Unit)? = null,
    onCategoriesSelected: () -> Unit,
    modifier: Modifier
) {

    val categoryService = remember { CategoryServices() }
    val userCategoriesService = remember { UserCategoriesService() }
    val categories = categoryService.getCategories()

    val initialSelection = remember { mutableStateOf(setOf<CategoryUi>()) }
    var selected by remember { initialSelection }

    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    val userId = remember { FirebaseAuth.getInstance().currentUser?.uid }
    val isUserLoggedIn = userId != null
    val isButtonEnabled = selected.isNotEmpty() && !isLoading && isUserLoggedIn

    LaunchedEffect(userId, isEditMode) {
        if (isEditMode && userId != null) {
            val savedCategories = userCategoriesService.getUserCategories(userId)
            initialSelection.value = savedCategories.toSet()
        }
    }

    // 💡 1. ENVOLVEMOS TODA LA PANTALLA EN UN SCAFFOLD
    Scaffold(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                // 💡 2. APLICAMOS statusBarsPadding AQUÍ para separar el contenido de la barra
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF9FAFB))
                    .statusBarsPadding()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = if (isEditMode) "Editar Preferencias" else "Elige tus intereses",
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

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selected.contains(category)

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
                                    selected =
                                        if (isSelected) selected - category
                                        else selected + category
                                }
                                .padding(horizontal = 20.dp, vertical = 12.dp)
                        ) {

                            Icon(
                                imageVector = category.categoryIcon,
                                contentDescription = category.categoryName,
                                tint = if (isSelected) Color.White else Color(0xFF2D2D2D)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = category.categoryName,
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
                        if (isButtonEnabled && userId != null) {
                            isLoading = true
                            scope.launch {
                                try {
                                    userCategoriesService.saveUserCategories(userId, selected)
                                    onCategoriesSelected()

                                } catch (e: Exception) {
                                    println("🚨 ERROR CRÍTICO AL GUARDAR EN FIREBASE O NAVEGAR:")
                                    e.printStackTrace()
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    },
                    enabled = isButtonEnabled,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D2D2D)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp)
                ) {
                    val buttonText = when {
                        !isUserLoggedIn -> "Error: No Autenticado"
                        isLoading -> if (isEditMode) "Guardando Cambios..." else "Continuar..."
                        else -> if (isEditMode) "Guardar Cambios" else "Confirmar y Continuar"
                    }
                    Text(buttonText, color = Color.White)
                }
            }

            // El botón de retroceso se mueve fuera de la Column y dentro del Box principal
            if (isEditMode && onBack != null) {
                IconButton(
                    onClick = onBack,
                    // 💡 3. APLICAMOS statusBarsPadding AQUÍ para que el botón de volver respete la barra
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .statusBarsPadding()
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color(0xFF2D2D2D)
                    )
                }
            }
        }
    }
}