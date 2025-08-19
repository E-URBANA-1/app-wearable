package com.example.projectointegrador.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.*

@Composable
fun MainScreen(navController: NavController) {
    // Estado para simular fallos de API - reemplazar con llamada real a API
    val hayFallos = remember { mutableStateOf(false) }

    // Simulación de llamada a API para detectar fallos
    LaunchedEffect(Unit) {
        // TODO: Reemplazar con llamada real a tu API
        // val apiResponse = checkLuminariasFaults()
        // hayFallos.value = apiResponse.hasFaults

        hayFallos.value = false // Cambiar a true para probar la visualización del botón
    }

    Scaffold(
        timeText = { TimeText() }
    ) {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF324B61), Color(0xFF1A1A1A))
                    )
                )
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                CustomButtonWithIcon(
                    text = "Historial",
                    icon = "⏱",
                    backgroundColor = Color(0xFF1FA1AE)
                ) {
                    navController.navigate("historial")
                }
            }

            item {
                CustomButtonWithIcon(
                    text = "Luminarias",
                    icon = "💡",
                    backgroundColor = Color(0xFF0A67AC)
                ) {
                    navController.navigate("mapa")
                }
            }

            item {
                CustomButtonWithIcon(
                    text = "Info App",
                    icon = "ℹ",
                    backgroundColor = Color(0xFF16BE80)
                ) {
                    // ✅ Ahora sí navega a la pantalla de información
                    navController.navigate("info")
                }
            }

            // Solo mostrar botón de fallo si hay fallos reales
            if (hayFallos.value) {
                item {
                    CustomButtonWithIcon(
                        text = "Fallo Detectado",
                        icon = "⚠",
                        backgroundColor = Color(0xFFFF5722)
                    ) {
                        navController.navigate("third")
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CustomButtonWithIcon(
    text: String,
    icon: String,
    backgroundColor: Color,
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 16.sp,
                color = if (backgroundColor == Color(0xFFEAEFF5)) Color(0xFF324B61) else Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                color = textColor,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}
