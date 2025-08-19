package com.example.projectointegrador.presentation.screens

import android.content.Context
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.projectointegrador.presentation.components.EnergyBarChart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

object AppColors {
    val Primary = Color(0xFF1FA1AE)
    val Secondary = Color(0xFF0A67AC)
    val Tertiary = Color(0xFF324B61)
    val Success = Color(0xFF16BE80)
    val Surface = Color(0xFFEAEFF5)
    val Background = Color(0xFF1A1A1A)
    val BackgroundGradientStart = Color(0xFF324B61)
    val BackgroundGradientEnd = Color(0xFF1A1A1A)
    val OnSurface = Color.White
    val OnSurfaceVariant = Color(0xFFB0B0B0)
    val Warning = Color(0xFFFF9800)
    val Error = Color(0xFFFF5722)
    val Accent = Color(0xFF1FA1AE)
}

data class Luminaria(
    val id: String,
    val lat: Double,
    val lon: Double,
    val consumoTotal: String,
    val estado: String,
    val datosGenerales: String,
    val consumoDiario: List<Double>,
    val tipoFoco: List<String>,
    val nombre: String,
    val ubicacion: String,
    val lumenes: List<Int> = listOf(),
    val encendida: Boolean = true
)

data class Coordenadas(
    val lat: Double,
    val lon: Double
)

data class LuminariaAPI(
    val _id: String,
    val identificador: String,
    val tipo_luminaria: String,
    val estado: String,
    val ciudad: String,
    val region: String,
    val activo: Boolean,
    val pais: String,
    val coordenadas: Coordenadas,
    val fecha_instalacion: String
)

data class ConsumoAPI(
    val _id: String,
    val luminaria_id: String,
    val timestamp: String,
    val consumo: Double,
    val lumenes: Int,
    val encendida: Boolean
)

interface ApiService {
    @GET("api/luminarias")
    suspend fun getLuminarias(
        @Header("Authorization") token: String
    ): List<LuminariaAPI>

    @GET("api/consumo")
    suspend fun getConsumo(
        @Header("Authorization") token: String
    ): List<ConsumoAPI>
}

object RetrofitClient {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    val api: ApiService = Retrofit.Builder()
        .baseUrl("https://api-eurbana-erjo.onrender.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}

fun procesarConsumoSemanal(consumoData: List<ConsumoAPI>): List<Double> {
    val diasSemana = Array(7) { mutableListOf<Double>() }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")

    consumoData.forEach { consumo ->
        try {
            val fecha = dateFormat.parse(consumo.timestamp)
            val calendar = Calendar.getInstance()
            calendar.time = fecha

            val diaSemana = when(calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> 6
                Calendar.MONDAY -> 0
                Calendar.TUESDAY -> 1
                Calendar.WEDNESDAY -> 2
                Calendar.THURSDAY -> 3
                Calendar.FRIDAY -> 4
                Calendar.SATURDAY -> 5
                else -> 0
            }

            diasSemana[diaSemana].add(consumo.consumo)
        } catch (e: Exception) {
            println("❌ Error parseando fecha: ${consumo.timestamp}")
        }
    }
    return diasSemana.map { consumosDia ->
        if (consumosDia.isNotEmpty()) {
            consumosDia.average()
        } else {
            0.0
        }
    }
}

@Composable
fun MapaScreen(navController: NavController, context: Context) {
    var userLocation by remember { mutableStateOf<Location?>(null) }
    var luminarias by remember { mutableStateOf<List<Luminaria>>(emptyList()) }
    var consumoData by remember { mutableStateOf<List<ConsumoAPI>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY4YTJhYzRiN2U1ZWVlZGM5ODVkMDkxYiIsImNvcnJlbyI6Imx1aXNpdm1hcmF6MDNAZ21haWwuY29tIiwicm9sIjoiQWRtaW4iLCJpYXQiOjE3NTU1NzczMDUsImV4cCI6MTc1NTY2MzcwNX0.JZHG0HqpDfLf8QFNC3fpS1g5cstMAq6LUk_aBT_Ex7o"

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            errorMessage = null

            println("🔵 Iniciando llamada a la API...")

            val (luminariasResponse, consumoResponse) = withContext(Dispatchers.IO) {
                val luminariasCall = RetrofitClient.api.getLuminarias(token)
                val consumoCall = RetrofitClient.api.getConsumo(token)
                Pair(luminariasCall, consumoCall)
            }

            println("🟢 Respuesta recibida: ${luminariasResponse.size} luminarias, ${consumoResponse.size} registros de consumo")

            consumoData = consumoResponse

            luminarias = luminariasResponse.map { apiLuminaria ->

                val consumoLuminaria = consumoResponse.filter { it.luminaria_id == apiLuminaria._id }
                val consumoSemanal = procesarConsumoSemanal(consumoLuminaria)
                val consumoTotal = if (consumoLuminaria.isNotEmpty()) {
                    "${String.format("%.1f", consumoLuminaria.map { it.consumo }.average())}W"
                } else {
                    "N/A"
                }

                println("📊 Luminaria ${apiLuminaria.identificador}: ${consumoLuminaria.size} registros, consumo semanal: $consumoSemanal")

                Luminaria(
                    id = apiLuminaria._id,
                    lat = apiLuminaria.coordenadas.lat,
                    lon = apiLuminaria.coordenadas.lon,
                    consumoTotal = consumoTotal,
                    estado = apiLuminaria.estado,
                    datosGenerales = apiLuminaria.tipo_luminaria,
                    consumoDiario = if (consumoSemanal.any { it > 0 }) consumoSemanal else listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                    tipoFoco = List(7) { apiLuminaria.tipo_luminaria },
                    nombre = apiLuminaria.identificador,
                    ubicacion = "${apiLuminaria.ciudad}, ${apiLuminaria.region}, ${apiLuminaria.pais}"
                )
            }

            val fakeLocation = Location("mockProvider").apply {
                latitude = 19.4330
                longitude = -99.1350
            }
            userLocation = fakeLocation

            isLoading = false
            println("✅ Datos cargados correctamente: ${luminarias.size} luminarias con datos reales de consumo")

        } catch (e: Exception) {
            isLoading = false
            errorMessage = "Error al cargar datos: ${e.message}"
            println("❌ Error en la llamada: ${e.message}")
            e.printStackTrace()

            luminarias = listOf(
                Luminaria(
                    id = "test1",
                    lat = 19.4330,
                    lon = -99.1350,
                    consumoTotal = "Sin datos",
                    estado = "Desconocido",
                    datosGenerales = "LED Público",
                    consumoDiario = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                    tipoFoco = listOf("LED", "LED", "LED", "LED", "LED", "LED", "LED"),
                    nombre = "Luminaria Sin Datos 1",
                    ubicacion = "Ubicación desconocida"
                )
            )
        }
    }

    Scaffold(timeText = { TimeText() }) {
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
                    text = "Luminarias con Datos Reales",
                    color = AppColors.OnSurface,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.caption1.copy(fontSize = 12.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            when {
                isLoading -> {
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = AppColors.Primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Cargando datos de consumo...",
                                color = AppColors.OnSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                errorMessage != null && luminarias.isEmpty() -> {
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                errorMessage!!,
                                color = AppColors.Error,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    isLoading = true
                                    errorMessage = null
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = AppColors.Primary
                                ),
                                modifier = Modifier.height(40.dp)
                            ) {
                                Text("Reintentar", fontSize = 10.sp, color = AppColors.OnSurface)
                            }
                        }
                    }
                }

                luminarias.isEmpty() -> {
                    item {
                        Text(
                            "No se encontraron luminarias",
                            color = AppColors.OnSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }

                else -> {
                    if (errorMessage != null) {
                        item {
                            Text(
                                "⚠️ Error de conexión - Datos limitados",
                                color = AppColors.Warning,
                                fontSize = 8.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }

                    items(luminarias) { luminaria ->
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(horizontal = 1.dp, vertical = 2.dp),
                            onClick = {
                                println("🔍 Navegando a detalle: ${luminaria.id}")
                                navController.navigate("detalle/${luminaria.id}")
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = AppColors.Primary
                            )
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(luminaria.nombre, fontSize = 11.sp, color = AppColors.OnSurface)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("${luminaria.estado} • ${luminaria.consumoTotal}", fontSize = 9.sp, color = AppColors.OnSurfaceVariant)
                                Text(
                                    "${String.format("%.4f", luminaria.lat)}, ${String.format("%.4f", luminaria.lon)}",
                                    fontSize = 8.sp,
                                    color = AppColors.OnSurfaceVariant
                                )
                            }
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
    var luminaria by remember { mutableStateOf<Luminaria?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY4YTJhYzRiN2U1ZWVlZGM5ODVkMDkxYiIsImNvcnJlbyI6Imx1aXNpdm1hcmF6MDNAZ21haWwuY29tIiwicm9sIjoiQWRtaW4iLCJpYXQiOjE3NTU1NzczMDUsImV4cCI6MTc1NTY2MzcwNX0.JZHG0HqpDfLf8QFNC3fpS1g5cstMAq6LUk_aBT_Ex7o"

    LaunchedEffect(luminariaId) {
        try {
            isLoading = true
            errorMessage = null
            println("🔍 Buscando luminaria con ID: $luminariaId")

            // Obtener datos de luminarias y consumo
            val (luminariasResponse, consumoResponse) = withContext(Dispatchers.IO) {
                val luminariasCall = RetrofitClient.api.getLuminarias(token)
                val consumoCall = RetrofitClient.api.getConsumo(token)
                Pair(luminariasCall, consumoCall)
            }

            val found = luminariasResponse.find { it._id == luminariaId }

            if (found != null) {
                // Filtrar consumo para esta luminaria específica
                val consumoLuminaria = consumoResponse.filter { it.luminaria_id == found._id }
                val consumoSemanal = procesarConsumoSemanal(consumoLuminaria)
                val consumoTotal = if (consumoLuminaria.isNotEmpty()) {
                    "${String.format("%.1f", consumoLuminaria.map { it.consumo }.average())}W"
                } else {
                    "N/A"
                }

                println("📊 Datos de consumo para ${found.identificador}:")
                println("   - Registros totales: ${consumoLuminaria.size}")
                println("   - Consumo semanal: $consumoSemanal")
                println("   - Consumo promedio: $consumoTotal")

                luminaria = Luminaria(
                    id = found._id,
                    lat = found.coordenadas.lat,
                    lon = found.coordenadas.lon,
                    consumoTotal = consumoTotal,
                    estado = found.estado,
                    datosGenerales = found.tipo_luminaria,
                    consumoDiario = if (consumoSemanal.any { it > 0 }) consumoSemanal else listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                    tipoFoco = List(7) { found.tipo_luminaria },
                    nombre = found.identificador,
                    ubicacion = "${found.ciudad}, ${found.region}, ${found.pais}"
                )
                println("✅ Luminaria encontrada con datos reales")
            } else {
                errorMessage = "Luminaria no encontrada en la base de datos"
            }

            isLoading = false

        } catch (e: Exception) {
            isLoading = false
            errorMessage = "Error al cargar detalle: ${e.message}"
            println("❌ Error cargando detalle: ${e.message}")
            e.printStackTrace()
        }
    }

    Scaffold(timeText = { TimeText() }) {
        when {
            isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = AppColors.Primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Cargando datos reales...", color = AppColors.OnSurface, fontSize = 10.sp)
                }
            }

            errorMessage != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(errorMessage!!, color = AppColors.Error, fontSize = 10.sp, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.Tertiary)
                    ) {
                        Text("Volver", fontSize = 10.sp, color = AppColors.OnSurface)
                    }
                }
            }

            luminaria != null -> {
                val days = listOf("L", "M", "M", "J", "V", "S", "D")
                val estadoColor = when (luminaria!!.estado) {
                    "Activa" -> AppColors.Success
                    "Inactiva" -> AppColors.Error
                    "Mantenimiento" -> AppColors.Warning
                    else -> AppColors.OnSurfaceVariant
                }

                val hayDatos = luminaria!!.consumoDiario.any { it > 0 }

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
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(luminaria!!.nombre, color = AppColors.Primary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                luminaria!!.ubicacion,
                                color = AppColors.OnSurfaceVariant,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    item {
                        Text("Estado: ${luminaria!!.estado}", color = estadoColor, fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Consumo Promedio: ${luminaria!!.consumoTotal}", color = AppColors.Accent, fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Tipo: ${luminaria!!.datosGenerales}",
                                color = AppColors.OnSurface,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp)) // ⬆️ más espacio
                            Text(
                                "Coordenadas:\n${String.format("%.4f", luminaria!!.lat)}, ${String.format("%.4f", luminaria!!.lon)}",
                                color = AppColors.OnSurfaceVariant,
                                fontSize = 9.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }


                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Consumo Energético Semanal", fontSize = 10.sp, color = AppColors.OnSurface)
                            if (!hayDatos) {
                                Text("(Sin datos disponibles)", fontSize = 8.sp, color = AppColors.Warning)
                            } else {
                                Text("(Datos reales de la API)", fontSize = 8.sp, color = AppColors.Success)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        EnergyBarChart(
                            data = luminaria!!.consumoDiario,
                            labels = days,
                            types = luminaria!!.tipoFoco
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.Tertiary)
                        ) {
                            Text("Volver", fontSize = 10.sp, color = AppColors.OnSurface)
                        }
                    }
                }
            }
        }
    }
}

// 📊 Gráfico de barras actualizado para manejar datos reales
@Composable
fun EnergyBarChart(data: List<Double>, labels: List<String>, types: List<String>) {
    val maxValue = data.maxOrNull() ?: 1.0
    val hasData = data.any { it > 0 }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp) // ⬆️ Más alto para que no se encimen
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        data.forEachIndexed { index, value ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.padding(horizontal = 2.dp)
            ) {
                // Valor numérico arriba
                Text(
                    if (hasData) String.format("%.1f", value) else "0",
                    fontSize = 8.sp, // ⬆️ Un poco más grande para mejor lectura
                    color = if (hasData && value > 0) AppColors.Accent else AppColors.OnSurfaceVariant,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(6.dp)) // ⬆️ Más espacio arriba de la barra

                // Barra del gráfico
                val barHeight = if (hasData && maxValue > 0) {
                    ((value / maxValue) * 40).coerceAtLeast(if (value > 0) 4.0 else 0.0)
                } else {
                    0.0
                }

                Box(
                    modifier = Modifier
                        .width(10.dp) // ⬆️ Más ancho para mejor proporción
                        .height(barHeight.dp)
                        .background(
                            if (hasData && value > 0) {
                                if (types.getOrNull(index)?.contains("LED") == true) AppColors.Success else AppColors.Warning
                            } else {
                                AppColors.OnSurfaceVariant.copy(alpha = 0.3f)
                            },
                            shape = RoundedCornerShape(3.dp)
                        )
                )

                Spacer(modifier = Modifier.height(6.dp)) // ⬆️ Más separación debajo de la barra

                // Etiqueta del día abajo
                Text(
                    labels.getOrNull(index) ?: "",
                    fontSize = 9.sp, // ⬆️ Más grande para distinguirse
                    color = AppColors.OnSurface,
                    maxLines = 1
                )
            }
        }
    }
}
