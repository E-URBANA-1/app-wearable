package com.example.projectointegrador.presentation.screens

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
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.*
import java.text.SimpleDateFormat
import java.util.*

data class HistorialLuminaria(
    val id: String,
    val nombre: String,
    val ubicacion: String,
    val fechaVisita: Date,
    val estadoUltimaVisita: String,
    val consumoUltimaVisita: String,
    val vecesVisitada: Int
)

@Composable
fun HistorialScreen(navController: NavController) {
    // Simulación de historial - en la app real esto vendría de una base de datos local
    val historialLuminarias = remember {
        listOf(
            HistorialLuminaria(
                id = "1",
                nombre = "Luminaria Principal",
                ubicacion = "Entrada Norte",
                fechaVisita = Calendar.getInstance().apply { add(Calendar.HOUR, -2) }.time,
                estadoUltimaVisita = "Activa",
                consumoUltimaVisita = "45 kWh",
                vecesVisitada = 5
            ),
            HistorialLuminaria(
                id = "8",
                nombre = "Luminaria Este",
                ubicacion = "Calle Principal",
                fechaVisita = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
                estadoUltimaVisita = "Activa",
                consumoUltimaVisita = "38 kWh",
                vecesVisitada = 3
            ),
            HistorialLuminaria(
                id = "3",
                nombre = "Luminaria Sur",
                ubicacion = "Plaza Central",
                fechaVisita = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -2) }.time,
                estadoUltimaVisita = "Mantenimiento",
                consumoUltimaVisita = "0 kWh",
                vecesVisitada = 2
            ),
            HistorialLuminaria(
                id = "5",
                nombre = "Luminaria Oeste",
                ubicacion = "Parque Sur",
                fechaVisita = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -3) }.time,
                estadoUltimaVisita = "Inactiva",
                consumoUltimaVisita = "0 kWh",
                vecesVisitada = 1
            ),
            HistorialLuminaria(
                id = "2",
                nombre = "Luminaria Norte",
                ubicacion = "Zona Comercial",
                fechaVisita = Calendar.getInstance().apply { add(Calendar.WEEK_OF_YEAR, -1) }.time,
                estadoUltimaVisita = "Activa",
                consumoUltimaVisita = "52 kWh",
                vecesVisitada = 4
            )
        ).sortedByDescending { it.fechaVisita } // Ordenar por fecha más reciente
    }

    var luminariaSeleccionada by remember { mutableStateOf<HistorialLuminaria?>(null) }

    if (luminariaSeleccionada == null) {
        // Lista de historial
        ListaHistorial(
            historial = historialLuminarias,
            onLuminariaClick = { luminaria ->
                luminariaSeleccionada = luminaria
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    } else {
        // Detalle del historial
        DetalleHistorial(
            luminaria = luminariaSeleccionada!!,
            onBackClick = { luminariaSeleccionada = null }
        )
    }
}

@Composable
fun ListaHistorial(
    historial: List<HistorialLuminaria>,
    onLuminariaClick: (HistorialLuminaria) -> Unit,
    onBackClick: () -> Unit
) {
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
                .padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Historial",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.caption1.copy(fontSize = 12.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (historial.isEmpty()) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "📝",
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sin historial",
                            color = Color(0xFFEAEFF5),
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Visita algunas luminarias para ver el historial aquí",
                            color = Color(0xFFEAEFF5),
                            fontSize = 8.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(historial) { luminaria ->
                    HistorialItem(
                        luminaria = luminaria,
                        onClick = { onLuminariaClick(luminaria) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF0A67AC)
                    )
                ) {
                    Text("Volver", fontSize = 10.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun HistorialItem(
    luminaria: HistorialLuminaria,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
    val estadoColor = when (luminaria.estadoUltimaVisita) {
        "Activa" -> Color(0xFF16BE80) // Verde de la paleta
        "Inactiva" -> Color(0xFF1FA1AE) // Turquesa de la paleta
        "Mantenimiento" -> Color(0xFFFF9800) // Naranja para mantenimiento
        else -> Color(0xFFEAEFF5) // Gris claro de la paleta
    }

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .padding(horizontal = 1.dp, vertical = 2.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFF324B61) // Azul grisáceo oscuro
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Luminaria #${luminaria.id}",
                fontSize = 11.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = dateFormat.format(luminaria.fechaVisita),
                fontSize = 9.sp,
                color = Color(0xFFEAEFF5)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = luminaria.estadoUltimaVisita,
                    fontSize = 8.sp,
                    color = estadoColor
                )
                Text(
                    text = "${luminaria.vecesVisitada}x",
                    fontSize = 8.sp,
                    color = Color(0xFF1FA1AE) // Turquesa para contador
                )
            }
        }
    }
}

@Composable
fun DetalleHistorial(
    luminaria: HistorialLuminaria,
    onBackClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val estadoColor = when (luminaria.estadoUltimaVisita) {
        "Activa" -> Color(0xFF16BE80) // Verde de la paleta
        "Inactiva" -> Color(0xFF1FA1AE) // Turquesa de la paleta
        "Mantenimiento" -> Color(0xFFFF9800) // Naranja para mantenimiento
        else -> Color(0xFFEAEFF5) // Gris claro de la paleta
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
                .padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Historial Detallado",
                    fontSize = 11.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Text(
                    text = luminaria.nombre,
                    fontSize = 12.sp,
                    color = Color(0xFF1FA1AE) // Turquesa para nombre principal
                )
                Spacer(modifier = Modifier.height(2.dp))
            }

            item {
                Text(
                    text = luminaria.ubicacion,
                    fontSize = 10.sp,
                    color = Color(0xFFEAEFF5) // Gris claro para ubicación
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Sección de última visita
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Última visita:",
                        fontSize = 9.sp,
                        color = Color(0xFFEAEFF5)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = dateFormat.format(luminaria.fechaVisita),
                        fontSize = 10.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Sección de estado
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Estado:",
                        fontSize = 9.sp,
                        color = Color(0xFFEAEFF5)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = luminaria.estadoUltimaVisita,
                        fontSize = 10.sp,
                        color = estadoColor
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Sección de consumo
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Consumo:",
                        fontSize = 9.sp,
                        color = Color(0xFFEAEFF5)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = luminaria.consumoUltimaVisita,
                        fontSize = 10.sp,
                        color = Color(0xFF16BE80) // Verde para consumo
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Sección de veces visitada
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Veces visitada:",
                        fontSize = 9.sp,
                        color = Color(0xFFEAEFF5)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${luminaria.vecesVisitada} veces",
                        fontSize = 10.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Button(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF0A67AC) // Azul de la paleta
                    )
                ) {
                    Text("Volver", fontSize = 10.sp, color = Color.White)
                }
            }
        }
    }
}