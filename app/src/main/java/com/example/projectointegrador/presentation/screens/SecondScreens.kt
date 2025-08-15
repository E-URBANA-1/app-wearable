package com.example.projectointegrador.presentation.screens

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

data class LuminariaConsumo(
    val id: String,
    val nombre: String,
    val ubicacion: String,
    val consumoDiario: List<Int>,
    val tipoFoco: List<String>
)

@Composable
fun SecondScreen(navController: NavController) {
    var luminariaSeleccionada by remember { mutableStateOf<LuminariaConsumo?>(null) }

    val luminariasData = listOf(
        LuminariaConsumo(
            id = "1",
            nombre = "Luminaria Principal",
            ubicacion = "Entrada Norte",
            consumoDiario = listOf(20, 35, 15, 40, 25, 30, 10),
            tipoFoco = listOf("LED", "Normal", "LED", "Normal", "LED", "LED", "Normal")
        ),
        LuminariaConsumo(
            id = "2",
            nombre = "Luminaria Secundaria",
            ubicacion = "Plaza Central",
            consumoDiario = listOf(15, 25, 20, 30, 18, 22, 12),
            tipoFoco = listOf("LED", "LED", "LED", "LED", "LED", "LED", "LED")
        ),
        LuminariaConsumo(
            id = "3",
            nombre = "Luminaria Este",
            ubicacion = "Calle Principal",
            consumoDiario = listOf(25, 30, 22, 35, 28, 32, 18),
            tipoFoco = listOf("Normal", "Normal", "LED", "Normal", "LED", "Normal", "Normal")
        ),
        LuminariaConsumo(
            id = "4",
            nombre = "Luminaria Oeste",
            ubicacion = "Parque Sur",
            consumoDiario = listOf(18, 28, 16, 33, 21, 26, 14),
            tipoFoco = listOf("LED", "LED", "Normal", "LED", "LED", "LED", "Normal")
        ),
        LuminariaConsumo(
            id = "5",
            nombre = "Luminaria Norte",
            ubicacion = "Zona Comercial",
            consumoDiario = listOf(30, 40, 35, 45, 38, 42, 25),
            tipoFoco = listOf("Normal", "Normal", "Normal", "LED", "Normal", "LED", "LED")
        )
    )

    if (luminariaSeleccionada == null) {
        // Pantalla de lista de luminarias
        ListaLuminarias(
            luminarias = luminariasData,
            onLuminariaClick = { luminaria ->
                luminariaSeleccionada = luminaria
            }
        )
    } else {
        // Pantalla de consumo energético
        ConsumoEnergeticoScreen(
            luminaria = luminariaSeleccionada!!,
            onBackClick = { luminariaSeleccionada = null }
        )
    }
}

@Composable
fun ListaLuminarias(
    luminarias: List<LuminariaConsumo>,
    onLuminariaClick: (LuminariaConsumo) -> Unit
) {
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
                .padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Consumo por Luminaria",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.caption1.copy(fontSize = 12.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(luminarias) { luminaria ->
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp) // Aumenté la altura para mejor espaciado
                        .padding(horizontal = 1.dp, vertical = 2.dp),
                    onClick = { onLuminariaClick(luminaria) }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = luminaria.nombre,
                            fontSize = 11.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp)) // Espaciado añadido
                        Text(
                            text = luminaria.ubicacion,
                            fontSize = 9.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConsumoEnergeticoScreen(
    luminaria: LuminariaConsumo,
    onBackClick: () -> Unit
) {
    val days = listOf("L", "M", "M", "J", "V", "S", "D")

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
                // Encabezado con mejor espaciado
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = luminaria.nombre,
                        fontSize = 11.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp)) // Espaciado entre nombre y ubicación
                    Text(
                        text = luminaria.ubicacion,
                        fontSize = 9.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(8.dp)) // Mayor espaciado después del encabezado
            }

            item {
                Text(
                    text = "Consumo Energético",
                    fontSize = 10.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp)) // Aumenté el espaciado
            }

            item {
                EnergyBarChart(
                    data = luminaria.consumoDiario,
                    labels = days,
                    types = luminaria.tipoFoco
                )
                Spacer(modifier = Modifier.height(8.dp)) // Aumenté el espaciado después del gráfico
            }

            item {
                Text(
                    text = "Consumo diario (kWh) y tipo de foco usado.",
                    fontSize = 8.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp)) // Aumenté el espaciado antes de la tabla
            }

            items(days.indices.toList()) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp), // Aumenté el padding vertical
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = days[index],
                        fontSize = 8.sp,
                        color = Color.White
                    )
                    Text(
                        text = "${luminaria.consumoDiario[index]} kWh",
                        fontSize = 8.sp,
                        color = Color.Yellow
                    )
                    Text(
                        text = luminaria.tipoFoco[index],
                        fontSize = 8.sp,
                        color = if (luminaria.tipoFoco[index] == "LED") Color(0xFF4CAF50) else Color(0xFFFF9800)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp)) // Mayor espaciado antes del botón
                Button(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                ) {
                    Text("Volver", fontSize = 10.sp)
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
            .height(65.dp) // Aumenté la altura del gráfico
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
                    color = Color.Yellow
                )
                Spacer(modifier = Modifier.height(2.dp)) // Aumenté el espaciado
                Box(
                    modifier = Modifier
                        .width(7.dp) // Aumenté ligeramente el ancho de las barras
                        .height((value * 0.9).dp.coerceAtMost(38.dp)) // Aumenté la altura máxima
                        .background(
                            if (types[index] == "LED") Color(0xFF007F3E) else Color(0xFFFF9800),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
                Spacer(modifier = Modifier.height(2.dp)) // Aumenté el espaciado
                Text(
                    text = labels[index],
                    fontSize = 6.sp,
                    color = Color.White
                )
            }
        }
    }
}