package com.company.ulpgcflix.ui.vistas.Categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.ulpgcflix.ui.servicios.CategoryServices
import com.company.ulpgcflix.model.Category
import com.company.ulpgcflix.ui.servicios.UserCategoriesService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


@Composable
fun Categories(onCategoriesSelected:() -> Unit){

    val categoryService = remember { CategoryServices() }
    val userCategoriesService = remember { UserCategoriesService() }
    val categories = categoryService.getCategories()
    var selected by remember { mutableStateOf(setOf<Category>()) }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    val userId = remember { FirebaseAuth.getInstance().currentUser?.uid }
    val isUserLoggedIn = userId != null
    val isButtonEnabled = selected.isNotEmpty() && !isLoading && isUserLoggedIn

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Elige tus intereses",
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
                if (isButtonEnabled) {
                    isLoading = true
                    scope.launch {
                        try {

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
                isLoading -> "Guardando..."
                else -> "Confirmar"
            }
            Text(buttonText, color = Color.White)
        }
    }
}