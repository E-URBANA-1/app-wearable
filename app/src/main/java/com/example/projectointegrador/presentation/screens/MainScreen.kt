// Archivo: presentation/screens/MainScreen.kt
package com.example.projectointegrador.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Text

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
    // Estado para simular fallos de API - reemplazar con llamada real a API
    val hayFallos = remember { mutableStateOf(false) }

    // Simulación de llamada a API para detectar fallos
    LaunchedEffect(Unit) {
        // TODO: Reemplazar con llamada real a tu API
        // val apiResponse = checkLuminariasFaults()
        // hayFallos.value = apiResponse.hasFaults

        // Simulación temporal (eliminar cuando conectes la API real)
        hayFallos.value = false // Cambiar a true para probar la visualización del botón
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 15.dp, horizontal = 6.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CustomButtonWithIcon(
                text = "Historial",
                icon = "📋", // Icono de historial/lista
                backgroundColor = Color(0xFF00BFA5)
            ) {
                // TODO: Navegar a historial de luminarias vistas
                navController.navigate("historial")
            }

            CustomButtonWithIcon(
                text = "Luminarias",
                icon = "🏮", // Icono de lámpara
                backgroundColor = Color(0xFF1E88E5)
            ) {
                navController.navigate("mapa")
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CustomButtonWithIcon(
                text = "Info App",
                icon = "ℹ️", // Icono de información
                backgroundColor = Color(0xFF1565C0)
            ) {
                // TODO: Navegar a información de la app
                // navController.navigate("info")
            }

            CustomButtonWithIcon(
                text = "Consumo",
                icon = "📊", // Icono de gráfica
                backgroundColor = Color(0xFF0D47A1)
            ) {
                navController.navigate("second")
            }
        }

        // Solo mostrar botón de fallo si hay fallos reales
        if (hayFallos.value) {
            Spacer(modifier = Modifier.height(4.dp))
            CustomButtonWithIcon(
                text = "Fallo",
                icon = "⚠️", // Icono de advertencia
                backgroundColor = Color(0xFFD32F2F),
                onClick = { navController.navigate("third") }
            )
        }
    }
}

@Composable
fun CustomButtonWithIcon(
    text: String,
    icon: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(70.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                fontSize = 18.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = text,
                fontSize = 9.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 10.sp
            )
        }
    }
}