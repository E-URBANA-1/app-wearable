package com.example.projectointegrador.presentation.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import java.net.HttpURLConnection
import java.net.URL

// Modelo de datos
data class HistorialLuminaria(
    val id: String,
    val nombre: String,
    val ubicacion: String,
    val fechaVisita: Date,
    val estadoUltimaVisita: String,
    val consumoUltimaVisita: String,
    val vecesVisitada: Int,
    val tipoMantenimiento: String,
    val observaciones: String
)

@Composable
fun HistorialScreen(navController: NavController) {
    var historial by remember { mutableStateOf<List<HistorialLuminaria>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var luminariaSeleccionada by remember { mutableStateOf<HistorialLuminaria?>(null) }
    val scope = rememberCoroutineScope()

    // Cargar datos desde la API
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val url = URL("https://api-eurbana-erjo.onrender.com/api/mantenimiento")
                withContext(Dispatchers.IO) {
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "GET"
                    conn.setRequestProperty(
                        "Authorization",
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY4YTQxMjg2M2ZjOWRhOGI4MDgzYzdiMyIsImNvcnJlbyI6Imx1aXNpdm1hcmF6MDNAZ21haWwuY29tIiwicm9sIjoiYWRtaW4iLCJpYXQiOjE3NTU1ODk0MTIsImV4cCI6MTc1NTY3NTgxMn0.5iIgaAVsA5_t1F4-OAypX7ir0BBG3nvgubpMMrzrTcw" // Reemplaza con tu token
                    )
                    conn.connect()

                    if (conn.responseCode == 200) {
                        val data = conn.inputStream.bufferedReader().use { it.readText() }
                        val lista = mutableListOf<HistorialLuminaria>()

                        val jsonArray = JSONArray(data)
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            val fecha = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                                .parse(obj.optString("fecha")) ?: Date()

                            lista.add(
                                HistorialLuminaria(
                                    id = obj.optString("luminaria_id"),
                                    nombre = "Luminaria ${obj.optString("luminaria_id")}",
                                    ubicacion = obj.optString("ubicacion", "Sin ubicación"),
                                    fechaVisita = fecha,
                                    estadoUltimaVisita = obj.optString("estatus", "Desconocido"),
                                    consumoUltimaVisita = obj.optString("consumo", "0 kWh"),
                                    vecesVisitada = obj.optInt("veces_visitada", 1),
                                    tipoMantenimiento = obj.optString("tipo_mantenimiento", "No especificado"),
                                    observaciones = obj.optString("observaciones", "Sin observaciones")
                                )
                            )
                        }

                        withContext(Dispatchers.Main) {
                            historial = lista.sortedByDescending { it.fechaVisita }
                        }
                    } else {
                        Log.e("Historial", "Error en API: ${conn.responseCode}")
                    }
                }
            } catch (e: Exception) {
                Log.e("Historial", "Error: ${e.message}")
            } finally {
                loading = false
            }
        }
    }

    // Mostrar lista o detalle
    if (luminariaSeleccionada == null) {
        ListaHistorial(
            historial = historial,
            onLuminariaClick = { luminariaSeleccionada = it },
            onBackClick = { navController.popBackStack() },
            loading = loading
        )
    } else {
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
    onBackClick: () -> Unit,
    loading: Boolean
) {
    Scaffold(timeText = { TimeText() }) {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF324B61), Color(0xFF1A1A1A))))
                .padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Historial",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            when {
                loading -> item { CircularProgressIndicator(color = Color.White) }
                historial.isEmpty() -> item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                        Text("📝", fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Sin historial", color = Color(0xFFEAEFF5), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Visita algunas luminarias para ver el historial aquí",
                            color = Color(0xFFEAEFF5),
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> items(historial) { HistorialItem(it, onClick = { onLuminariaClick(it) }) }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0A67AC))
                ) {
                    Text("Volver", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun HistorialItem(luminaria: HistorialLuminaria, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
    val estadoColor = when (luminaria.estadoUltimaVisita) {
        "Activa" -> Color(0xFF16BE80)
        "Inactiva" -> Color(0xFF1FA1AE)
        "Mantenimiento" -> Color(0xFFFF9800)
        else -> Color(0xFFEAEFF5)
    }

    // Acortar el ID para mejor visualización
    val shortId = if (luminaria.id.length > 8) {
        luminaria.id.take(8) + "..."
    } else {
        luminaria.id
    }

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(horizontal = 2.dp, vertical = 3.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF324B61))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 2.dp)
        ) {
            Text(
                text = "Luminaria",
                fontSize = 12.sp,
                color = Color.White
            )
            Text(
                text = "#$shortId",
                fontSize = 10.sp,
                color = Color(0xFF1FA1AE),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = dateFormat.format(luminaria.fechaVisita),
                fontSize = 10.sp,
                color = Color(0xFFEAEFF5),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = luminaria.estadoUltimaVisita,
                    fontSize = 9.sp,
                    color = estadoColor,
                    maxLines = 1
                )
                Text(
                    text = "${luminaria.vecesVisitada}x",
                    fontSize = 9.sp,
                    color = Color(0xFF1FA1AE),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun DetalleHistorial(luminaria: HistorialLuminaria, onBackClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val estadoColor = when (luminaria.estadoUltimaVisita) {
        "Activa" -> Color(0xFF16BE80)
        "Inactiva" -> Color(0xFF1FA1AE)
        "Mantenimiento" -> Color(0xFFFF9800)
        else -> Color(0xFFEAEFF5)
    }

    val shortId = if (luminaria.id.length > 12) {
        luminaria.id.take(8) + "..." + luminaria.id.takeLast(4)
    } else {
        luminaria.id
    }

    Scaffold(timeText = { TimeText() }) {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF324B61), Color(0xFF1A1A1A))))
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text("Historial Detallado", fontSize = 13.sp, color = Color.White, textAlign = TextAlign.Center)
                Spacer(Modifier.height(8.dp))
            }

            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Luminaria", fontSize = 12.sp, color = Color.White, textAlign = TextAlign.Center)
                    Text(shortId, fontSize = 10.sp, color = Color(0xFF1FA1AE), textAlign = TextAlign.Center)
                }
                Spacer(Modifier.height(8.dp))
            }

            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Ubicación:", fontSize = 10.sp, color = Color(0xFFEAEFF5), textAlign = TextAlign.Center)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = if (luminaria.ubicacion.isBlank() || luminaria.ubicacion == "Sin ubicación")
                            "No especificada"
                        else luminaria.ubicacion,
                        fontSize = 11.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Última visita:", fontSize = 10.sp, color = Color(0xFFEAEFF5), textAlign = TextAlign.Center)
                    Spacer(Modifier.height(2.dp))
                    Text(dateFormat.format(luminaria.fechaVisita), fontSize = 11.sp, color = Color.White, textAlign = TextAlign.Center)
                }
                Spacer(Modifier.height(6.dp))
            }

            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Estado:", fontSize = 10.sp, color = Color(0xFFEAEFF5), textAlign = TextAlign.Center)
                    Spacer(Modifier.height(2.dp))
                    Text(luminaria.estadoUltimaVisita, fontSize = 11.sp, color = estadoColor, textAlign = TextAlign.Center)
                }
                Spacer(Modifier.height(6.dp))
            }

            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Consumo:", fontSize = 10.sp, color = Color(0xFFEAEFF5), textAlign = TextAlign.Center)
                    Spacer(Modifier.height(2.dp))
                    Text(luminaria.consumoUltimaVisita, fontSize = 11.sp, color = Color(0xFF16BE80), textAlign = TextAlign.Center)
                }
                Spacer(Modifier.height(6.dp))
            }

            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Tipo de mantenimiento:", fontSize = 10.sp, color = Color(0xFFEAEFF5), textAlign = TextAlign.Center)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = luminaria.tipoMantenimiento.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                        },
                        fontSize = 11.sp,
                        color = Color(0xFFFF9800),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(6.dp))
            }

            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Observaciones:", fontSize = 10.sp, color = Color(0xFFEAEFF5), textAlign = TextAlign.Center)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = luminaria.observaciones,
                        fontSize = 10.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        maxLines = 3
                    )
                }
                Spacer(Modifier.height(6.dp))
            }

            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Veces visitada:", fontSize = 10.sp, color = Color(0xFFEAEFF5), textAlign = TextAlign.Center)
                    Spacer(Modifier.height(2.dp))
                    Text("${luminaria.vecesVisitada} veces", fontSize = 11.sp, color = Color.White, textAlign = TextAlign.Center)
                }
                Spacer(Modifier.height(10.dp))
            }

            item {
                Button(
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0A67AC))
                ) {
                    Text("Volver", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}