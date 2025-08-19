package com.example.projectointegrador.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AboutAppScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0A67AC), Color.Black)
                )
            )
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Información de la App",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            InfoItem(label = "Desarrollador", value = "Aldo Tolentino")
            InfoItem(label = "Versión", value = "1.0.0")
            InfoItem(label = "Año", value = "2025")
            InfoItem(label = "Derechos", value = "© Aldo Tolentino")
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFB0E0FF)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White
        )
    }
}
