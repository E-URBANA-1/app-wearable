package com.example.projectointegrador.presentation.screens

import androidx.compose.foundation.layout.*

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.material.Text
import com.example.projectointegrador.presentation.components.CustomButton

@Composable
fun ThirdScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⚠️ Alerta de Fallo",
            fontSize = 14.sp,
            color = Color(0xFF1FA1AE) // Turquesa de tu paleta para destacar la alerta
        )
        Text(
            text = "Luminaria #EUB-025",
            fontSize = 12.sp,
            color = Color(0xFF324B61) // Azul oscuro para información principal
        )
        Text(
            text = "Calle Principal 16 de Septiembre",
            fontSize = 10.sp,
            color = Color(0xFFEAEFF5) // Gris claro para información secundaria
        )
        Text(
            text = "Error: Circuito dañado",
            fontSize = 10.sp,
            color = Color(0xFF16BE80) // Verde para destacar el tipo de error
        )

        Spacer(modifier = Modifier.height(24.dp))
        CustomButton(
            text = "Entendido",
            backgroundColor = Color(0xFF0A67AC), // Azul medio de tu paleta
            modifier = Modifier.fillMaxWidth(), // Botón alargado
            onClick = { navController.popBackStack() }
        )
    }
}