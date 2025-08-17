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

// Paleta de colores personalizada
object AppColors {
    val Primary = Color(0xFF1FA1AE)        // Turquesa principal
    val Secondary = Color(0xFF0A67AC)      // Azul
    val Tertiary = Color(0xFF324B61)       // Azul grisáceo oscuro
    val Success = Color(0xFF16BE80)        // Verde
    val Surface = Color(0xFFEAEFF5)        // Gris claro
    val Background = Color(0xFF1A1A1A)     // Fondo oscuro
    val BackgroundGradientStart = Color(0xFF324B61)
    val BackgroundGradientEnd = Color(0xFF1A1A1A)
    val OnSurface = Color.White
    val OnSurfaceVariant = Color(0xFFB0B0B0)
    val Warning = Color(0xFFFF9800)
    val Error = Color(0xFFFF5722)
}

@Composable
fun MainScreen(navController: NavController) {
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

    Scaffold(
        timeText = { TimeText() }
    ) {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(AppColors.BackgroundGradientStart, AppColors.BackgroundGradientEnd)
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
                    icon = "📋",
                    backgroundColor = AppColors.Primary
                ) {
                    navController.navigate("historial")
                }
            }

            item {
                CustomButtonWithIcon(
                    text = "Luminarias",
                    icon = "🚦",
                    backgroundColor = AppColors.Secondary
                ) {
                    navController.navigate("mapa")
                }
            }

            item {
                CustomButtonWithIcon(
                    text = "Info App",
                    icon = "ℹ️",
                    backgroundColor = AppColors.Success
                ) {
                    // TODO: Navegar a información de la app
                    // navController.navigate("info")
                }
            }

            // Solo mostrar botón de fallo si hay fallos reales
            if (hayFallos.value) {
                item {
                    CustomButtonWithIcon(
                        text = "Fallo Detectado",
                        icon = "⚠️",
                        backgroundColor = AppColors.Error
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
    textColor: Color = AppColors.OnSurface,
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
                color = if (backgroundColor == AppColors.Surface) AppColors.Tertiary else AppColors.OnSurface
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