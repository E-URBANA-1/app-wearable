package com.example.projectointegrador.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.TimeText
import com.example.projectointegrador.presentation.components.CustomButton

@Composable
fun MainScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        TimeText(modifier = Modifier.align(Alignment.TopCenter))
        ButtonGrid(navController)
    }
}

@Composable
fun ButtonGrid(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 15.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.padding(2.dp)) {
            CustomButton("Encender", androidx.compose.ui.graphics.Color(0xFF00BFA5)) { }
            Spacer(modifier = Modifier.width(6.dp))
            CustomButton("Mapa", androidx.compose.ui.graphics.Color(0xFF1E88E5)) {
                navController.navigate("mapa")
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.padding(2.dp)) {
            CustomButton("Registrar", androidx.compose.ui.graphics.Color(0xFF1565C0)) { }
            Spacer(modifier = Modifier.width(6.dp))
            CustomButton("Leer", androidx.compose.ui.graphics.Color(0xFF0D47A1)) {
                navController.navigate("second")
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        CustomButton(
            text = "⚠️ Fallo",
            backgroundColor = androidx.compose.ui.graphics.Color(0xFFD32F2F),
            onClick = { navController.navigate("third") }
        )
    }
}
