package com.example.projectointegrador.presentation.screens

import android.content.Context
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.*
import kotlin.math.pow
import kotlin.math.sqrt

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
    val Accent = Color(0xFF1FA1AE)         // Color de acento para consumo
}

data class Luminaria(
    val id: String,
    val lat: Double,
    val lon: Double,
    val consumoTotal: String,
    val estado: String,
    val datosGenerales: String,
    val consumoDiario: List<Int>,
    val tipoFoco: List<String>,
    val nombre: String,
    val ubicacion: String
)

@Composable
fun MapaScreen(navController: NavController, context: Context) {
    var userLocation by remember { mutableStateOf<Location?>(null) }
    var luminariasCercanas by remember { mutableStateOf<List<Luminaria>>(emptyList()) }

    val todasLuminarias = listOf(
        Luminaria(
            id = "1",
            lat = 19.4326,
            lon = -99.1332,
            consumoTotal = "50 kWh",
            estado = "Activa",
            datosGenerales = "Modelo X, LED 50W",
            consumoDiario = listOf(20, 35, 15, 40, 25, 30, 10),
            tipoFoco = listOf("LED", "Normal", "LED", "Normal", "LED", "LED", "Normal"),
            nombre = "Luminaria Principal",
            ubicacion = "Entrada Norte"
        ),
        Luminaria(
            id = "2",
            lat = 19.4350,
            lon = -99.1300,
            consumoTotal = "40 kWh",
            estado = "Inactiva",
            datosGenerales = "Modelo Y, LED 30W",
            consumoDiario = listOf(15, 25, 20, 30, 18, 22, 12),
            tipoFoco = listOf("LED", "LED", "LED", "LED", "LED", "LED", "LED"),
            nombre = "Luminaria Secundaria",
            ubicacion = "Plaza Central"
        ),
        Luminaria(
            id = "3",
            lat = 19.4400,
            lon = -99.1400,
            consumoTotal = "60 kWh",
            estado = "Activa",
            datosGenerales = "Modelo Z, LED 70W",
            consumoDiario = listOf(25, 30, 22, 35, 28, 32, 18),
            tipoFoco = listOf("Normal", "Normal", "LED", "Normal", "LED", "Normal", "Normal"),
            nombre = "Luminaria Este",
            ubicacion = "Calle Principal"
        ),
        Luminaria(
            id = "4",
            lat = 19.4200,
            lon = -99.1200,
            consumoTotal = "55 kWh",
            estado = "Mantenimiento",
            datosGenerales = "Modelo A, LED 60W",
            consumoDiario = listOf(18, 28, 16, 33, 21, 26, 14),
            tipoFoco = listOf("LED", "LED", "Normal", "LED", "LED", "LED", "Normal"),
            nombre = "Luminaria Oeste",
            ubicacion = "Parque Sur"
        ),
        Luminaria(
            id = "5",
            lat = 19.4500,
            lon = -99.1350,
            consumoTotal = "48 kWh",
            estado = "Activa",
            datosGenerales = "Modelo B, LED 45W",
            consumoDiario = listOf(30, 40, 35, 45, 38, 42, 25),
            tipoFoco = listOf("Normal", "Normal", "Normal", "LED", "Normal", "LED", "LED"),
            nombre = "Luminaria Norte",
            ubicacion = "Zona Comercial"
        ),
        Luminaria(
            id = "6",
            lat = 19.4600,
            lon = -99.1500,
            consumoTotal = "52 kWh",
            estado = "Inactiva",
            datosGenerales = "Modelo C, LED 65W",
            consumoDiario = listOf(22, 28, 18, 32, 24, 30, 16),
            tipoFoco = listOf("LED", "Normal", "LED", "LED", "Normal", "LED", "LED"),
            nombre = "Luminaria Sur",
            ubicacion = "Zona Residencial"
        ),
        Luminaria(
            id = "7",
            lat = 19.4280,
            lon = -99.1400,
            consumoTotal = "46 kWh",
            estado = "Activa",
            datosGenerales = "Modelo D, LED 40W",
            consumoDiario = listOf(19, 26, 14, 29, 22, 27, 13),
            tipoFoco = listOf("LED", "LED", "Normal", "LED", "LED", "Normal", "LED"),
            nombre = "Luminaria Central",
            ubicacion = "Centro Histórico"
        ),
        Luminaria(
            id = "8",
            lat = 19.4315,
            lon = -99.1280,
            consumoTotal = "53 kWh",
            estado = "Activa",
            datosGenerales = "Modelo E, LED 55W",
            consumoDiario = listOf(26, 33, 21, 38, 30, 35, 20),
            tipoFoco = listOf("Normal", "LED", "LED", "Normal", "LED", "LED", "Normal"),
            nombre = "Luminaria Industrial",
            ubicacion = "Zona Industrial"
        )
    )

    // Simulación de ubicación para prueba
    LaunchedEffect(Unit) {
        val fakeLocation = Location("mockProvider").apply {
            latitude = 19.4330
            longitude = -99.1350
        }
        userLocation = fakeLocation
        luminariasCercanas = todasLuminarias.sortedBy {
            distanciaMetros(fakeLocation.latitude, fakeLocation.longitude, it.lat, it.lon)
        }.take(5)
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
                .padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Luminarias Cercanas",
                    color = AppColors.OnSurface,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.caption1.copy(fontSize = 12.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (userLocation == null) {
                item {
                    Text(
                        text = "Cargando...",
                        color = AppColors.OnSurfaceVariant,
                        style = MaterialTheme.typography.caption1
                    )
                }
            } else {
                items(luminariasCercanas.size) { index ->
                    val luminaria = luminariasCercanas[index]
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(65.dp)
                            .padding(horizontal = 1.dp, vertical = 2.dp),
                        onClick = {
                            navController.navigate("detalle/${luminaria.id}")
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = AppColors.Primary
                        )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Luminaria #${luminaria.id}",
                                fontSize = 12.sp,
                                color = AppColors.OnSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "Distancia: ${
                                    distanciaMetros(
                                        userLocation!!.latitude,
                                        userLocation!!.longitude,
                                        luminaria.lat,
                                        luminaria.lon
                                    ).toInt()
                                } m",
                                fontSize = 10.sp,
                                color = AppColors.OnSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

fun distanciaMetros(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371000
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2).pow(2.0) +
            Math.cos(Math.toRadians(lat1)) *
            Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2).pow(2.0)
    val c = 2 * Math.atan2(sqrt(a), sqrt(1 - a))
    return r * c
}

@Composable
fun DetalleLuminariaScreen(navController: NavController, luminariaId: String) {
    val luminariasMap = mapOf(
        "1" to Luminaria(
            id = "1", lat = 0.0, lon = 0.0, consumoTotal = "50 kWh", estado = "Activa",
            datosGenerales = "Modelo X, LED 50W",
            consumoDiario = listOf(20, 35, 15, 40, 25, 30, 10),
            tipoFoco = listOf("LED", "Normal", "LED", "Normal", "LED", "LED", "Normal"),
            nombre = "Luminaria Principal", ubicacion = "Entrada Norte"
        ),
        "2" to Luminaria(
            id = "2", lat = 0.0, lon = 0.0, consumoTotal = "40 kWh", estado = "Inactiva",
            datosGenerales = "Modelo Y, LED 30W",
            consumoDiario = listOf(15, 25, 20, 30, 18, 22, 12),
            tipoFoco = listOf("LED", "LED", "LED", "LED", "LED", "LED", "LED"),
            nombre = "Luminaria Secundaria", ubicacion = "Plaza Central"
        ),
        "3" to Luminaria(
            id = "3", lat = 0.0, lon = 0.0, consumoTotal = "60 kWh", estado = "Activa",
            datosGenerales = "Modelo Z, LED 70W",
            consumoDiario = listOf(25, 30, 22, 35, 28, 32, 18),
            tipoFoco = listOf("Normal", "Normal", "LED", "Normal", "LED", "Normal", "Normal"),
            nombre = "Luminaria Este", ubicacion = "Calle Principal"
        ),
        "4" to Luminaria(
            id = "4", lat = 0.0, lon = 0.0, consumoTotal = "55 kWh", estado = "Mantenimiento",
            datosGenerales = "Modelo A, LED 60W",
            consumoDiario = listOf(18, 28, 16, 33, 21, 26, 14),
            tipoFoco = listOf("LED", "LED", "Normal", "LED", "LED", "LED", "Normal"),
            nombre = "Luminaria Oeste", ubicacion = "Parque Sur"
        ),
        "5" to Luminaria(
            id = "5", lat = 0.0, lon = 0.0, consumoTotal = "48 kWh", estado = "Activa",
            datosGenerales = "Modelo B, LED 45W",
            consumoDiario = listOf(30, 40, 35, 45, 38, 42, 25),
            tipoFoco = listOf("Normal", "Normal", "Normal", "LED", "Normal", "LED", "LED"),
            nombre = "Luminaria Norte", ubicacion = "Zona Comercial"
        ),
        "6" to Luminaria(
            id = "6", lat = 0.0, lon = 0.0, consumoTotal = "52 kWh", estado = "Inactiva",
            datosGenerales = "Modelo C, LED 65W",
            consumoDiario = listOf(22, 28, 18, 32, 24, 30, 16),
            tipoFoco = listOf("LED", "Normal", "LED", "LED", "Normal", "LED", "LED"),
            nombre = "Luminaria Sur", ubicacion = "Zona Residencial"
        ),
        "7" to Luminaria(
            id = "7", lat = 0.0, lon = 0.0, consumoTotal = "46 kWh", estado = "Activa",
            datosGenerales = "Modelo D, LED 40W",
            consumoDiario = listOf(19, 26, 14, 29, 22, 27, 13),
            tipoFoco = listOf("LED", "LED", "Normal", "LED", "LED", "Normal", "LED"),
            nombre = "Luminaria Central", ubicacion = "Centro Histórico"
        ),
        "8" to Luminaria(
            id = "8", lat = 0.0, lon = 0.0, consumoTotal = "53 kWh", estado = "Activa",
            datosGenerales = "Modelo E, LED 55W",
            consumoDiario = listOf(26, 33, 21, 38, 30, 35, 20),
            tipoFoco = listOf("Normal", "LED", "LED", "Normal", "LED", "LED", "Normal"),
            nombre = "Luminaria Industrial", ubicacion = "Zona Industrial"
        )
    )

    val luminaria = luminariasMap[luminariaId] ?: Luminaria(
        id = "?", lat = 0.0, lon = 0.0, consumoTotal = "N/A", estado = "N/A",
        datosGenerales = "Sin datos", consumoDiario = listOf(0,0,0,0,0,0,0),
        tipoFoco = listOf("N/A","N/A","N/A","N/A","N/A","N/A","N/A"),
        nombre = "Desconocida", ubicacion = "Sin ubicación"
    )

    val days = listOf("L", "M", "M", "J", "V", "S", "D")
    val estadoColor = when (luminaria.estado) {
        "Activa" -> AppColors.Success
        "Inactiva" -> AppColors.Error
        "Mantenimiento" -> AppColors.Warning
        else -> AppColors.OnSurfaceVariant
    }

    Scaffold(
        timeText = { TimeText() }
    ) {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(AppColors.BackgroundGradientStart, AppColors.Background)
                    )
                )
                .padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Encabezado con información básica
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = luminaria.nombre,
                        color = AppColors.Primary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = luminaria.ubicacion,
                        color = AppColors.OnSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Información básica de la luminaria
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Consumo Total:",
                        fontSize = 9.sp,
                        color = AppColors.OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = luminaria.consumoTotal,
                        color = AppColors.Accent,
                        fontSize = 10.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Estado:",
                        fontSize = 9.sp,
                        color = AppColors.OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = luminaria.estado,
                        color = estadoColor,
                        fontSize = 10.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Datos Técnicos:",
                        fontSize = 9.sp,
                        color = AppColors.OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = luminaria.datosGenerales,
                        color = AppColors.OnSurface,
                        fontSize = 9.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Sección de consumo energético
            item {
                Text(
                    text = "Consumo Energético Semanal",
                    fontSize = 10.sp,
                    color = AppColors.OnSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            item {
                EnergyBarChart(
                    data = luminaria.consumoDiario,
                    labels = days,
                    types = luminaria.tipoFoco
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Text(
                    text = "Consumo diario (kWh) y tipo de foco usado.",
                    fontSize = 8.sp,
                    color = AppColors.OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Tabla de datos detallados
            items(days.indices.toList()) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = days[index],
                        fontSize = 8.sp,
                        color = AppColors.OnSurface
                    )
                    Text(
                        text = "${luminaria.consumoDiario[index]} kWh",
                        fontSize = 8.sp,
                        color = AppColors.Accent
                    )
                    Text(
                        text = luminaria.tipoFoco[index],
                        fontSize = 8.sp,
                        color = if (luminaria.tipoFoco[index] == "LED") AppColors.Success else AppColors.Warning
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppColors.Tertiary
                    )
                ) {
                    Text("Volver", fontSize = 10.sp, color = AppColors.OnSurface)
                }
            }
        }
    }
}

@Composable
fun EnergyBarChart(data: List<Int>, labels: List<String>, types: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .padding(horizontal = 2.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        data.forEachIndexed { index, value ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = "$value",
                    fontSize = 6.sp,
                    color = AppColors.Accent
                )
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .width(7.dp)
                        .height((value * 0.9).dp.coerceAtMost(38.dp))
                        .background(
                            if (types[index] == "LED") AppColors.Success else AppColors.Warning,
                            shape = RoundedCornerShape(2.dp)
                        )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = labels[index],
                    fontSize = 6.sp,
                    color = AppColors.OnSurface
                )
            }
        }
    }
}