package com.example.projectointegrador.presentation.screens

import android.content.Context
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.material.*
import kotlin.math.pow
import kotlin.math.sqrt

data class Luminaria(
    val id: String,
    val lat: Double,
    val lon: Double,
    val consumoTotal: String,
    val estado: String,
    val datosGenerales: String
)

@Composable
fun MapaScreen(navController: NavController, context: Context) {
    var userLocation by remember { mutableStateOf<Location?>(null) }
    var luminariasCercanas by remember { mutableStateOf<List<Luminaria>>(emptyList()) }

    val todasLuminarias = listOf(
        Luminaria("1", 19.4326, -99.1332, "50 kWh", "Activa", "Modelo X, LED 50W"),
        Luminaria("2", 19.4350, -99.1300, "40 kWh", "Inactiva", "Modelo Y, LED 30W"),
        Luminaria("3", 19.4400, -99.1400, "60 kWh", "Activa", "Modelo Z, LED 70W"),
        Luminaria("4", 19.4200, -99.1200, "55 kWh", "Mantenimiento", "Modelo A, LED 60W"),
        Luminaria("5", 19.4500, -99.1350, "48 kWh", "Activa", "Modelo B, LED 45W"),
        Luminaria("6", 19.4600, -99.1500, "52 kWh", "Inactiva", "Modelo C, LED 65W"),
        Luminaria("7", 19.4280, -99.1400, "46 kWh", "Activa", "Modelo D, LED 40W"),
        Luminaria("8", 19.4315, -99.1280, "53 kWh", "Activa", "Modelo E, LED 55W")
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
                        listOf(Color(0xFF1E1E1E), Color(0xFF2C2C2C))
                    )
                )
                .padding(horizontal = 4.dp), // Cambiado de 30.dp a 4.dp para botones más anchos
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Luminarias Cercanas",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.caption1.copy(fontSize = 12.sp)
                )
            }

            if (userLocation == null) {
                item {
                    Text(
                        text = "Cargando...",
                        color = Color.Gray,
                        style = MaterialTheme.typography.caption1
                    )
                }
            } else {
                items(luminariasCercanas.size) { index ->
                    val luminaria = luminariasCercanas[index]
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp) // Altura aumentada para mejor visibilidad
                            .padding(horizontal = 1.dp), // Padding mínimo
                        onClick = {
                            navController.navigate("detalle/${luminaria.id}")
                        }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Luminaria #${luminaria.id}",
                                fontSize = 12.sp,
                                color = Color.White
                            )
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
                                color = Color.Gray
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
        "1" to Luminaria("1", 0.0, 0.0, "50 kWh", "Activa", "Modelo X, LED 50W"),
        "2" to Luminaria("2", 0.0, 0.0, "40 kWh", "Inactiva", "Modelo Y, LED 30W"),
        "3" to Luminaria("3", 0.0, 0.0, "60 kWh", "Activa", "Modelo Z, LED 70W"),
        "4" to Luminaria("4", 0.0, 0.0, "55 kWh", "Mantenimiento", "Modelo A, LED 60W"),
        "5" to Luminaria("5", 0.0, 0.0, "48 kWh", "Activa", "Modelo B, LED 45W"),
        "6" to Luminaria("6", 0.0, 0.0, "52 kWh", "Inactiva", "Modelo C, LED 65W"),
        "7" to Luminaria("7", 0.0, 0.0, "46 kWh", "Activa", "Modelo D, LED 40W"),
        "8" to Luminaria("8", 0.0, 0.0, "53 kWh", "Activa", "Modelo E, LED 55W")
    )

    val luminaria = luminariasMap[luminariaId] ?: Luminaria("?", 0.0, 0.0, "N/A", "N/A", "Sin datos")

    Scaffold(
        timeText = { TimeText() }
    ) {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E1E1E))
                .padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text("Luminaria #${luminaria.id}", color = Color.White, fontSize = 12.sp)
            }
            item {
                Text("Consumo: ${luminaria.consumoTotal}", color = Color.Gray, fontSize = 10.sp)
            }
            item {
                Text("Estado: ${luminaria.estado}", color = Color.Gray, fontSize = 10.sp)
            }
            item {
                Text("Datos: ${luminaria.datosGenerales}", color = Color.Gray, fontSize = 9.sp)
            }
            item {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .width(80.dp)
                        .height(30.dp)
                ) {
                    Text("Volver", fontSize = 11.sp)
                }
            }
        }
    }
}