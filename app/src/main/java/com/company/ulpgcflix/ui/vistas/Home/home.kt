package com.company.ulpgcflix.ui.vistas.Home


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.painterResource
import com.company.ulpgcflix.R


@Composable
fun OnboardingScreen(
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    val gradientBrush = Brush.linearGradient(
        colors = listOf(colors.background, colors.primary),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1500f),
        tileMode = TileMode.Clamp
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Logo",
        )
        Button(
            modifier = Modifier.padding(vertical = 24.dp),
            onClick = {onContinueClick()}
        ) {
            Text("Vamos!")
        }
    }
}