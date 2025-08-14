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
        Text(text = "⚠️ Alerta de Fallo", fontSize = 14.sp)
        Text(text = "Luminaria #EUB-025", fontSize = 12.sp)
        Text(text = "Calle Principal 16 de Septiembre", fontSize = 10.sp)
        Text(text = "Error: Circuito dañado", fontSize = 10.sp)

        Spacer(modifier = Modifier.height(24.dp))
        CustomButton(
            text = "Entendido",
            backgroundColor = Color(0xFF1E88E5),
            onClick = { navController.popBackStack() }
        )
    }
}
