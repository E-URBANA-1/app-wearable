package com.example.projectointegrador.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.*
import com.example.projectointegrador.presentation.theme.ProjectoIntegradorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
        setContent { WearApp() }
    }
}

@Composable
fun WearApp() {
    ProjectoIntegradorTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "main") {
            composable("main") { MainScreen(navController) }
            composable("second") { SecondScreen(navController) }
            composable("third") { ThirdScreen(navController) }
        }
    }
}

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
            CustomButton("Encender", Color(0xFF00BFA5)) { /* Acción */ }
            Spacer(modifier = Modifier.width(6.dp))
            CustomButton("Apagar", Color(0xFF1E88E5)) { /* Acción */ }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.padding(2.dp)) {
            CustomButton("Registrar", Color(0xFF1565C0)) { /* Acción */ }
            Spacer(modifier = Modifier.width(6.dp))
            CustomButton("Leer", Color(0xFF0D47A1)) { navController.navigate("second") }
        }
        Spacer(modifier = Modifier.height(4.dp))
        CustomButton(
            text = "⚠️ Fallo",
            backgroundColor = Color(0xFFD32F2F),
            onClick = { navController.navigate("third") }
        )
    }

}

@Composable
fun CustomButton(text: String, backgroundColor: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(56.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor),
        shape = RoundedCornerShape(28.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SecondScreen(navController: NavController) {
    val energyData = listOf(20, 35, 15, 40, 25, 30, 10) // Datos de ejemplo
    val days = listOf("L", "M", "M", "J", "V", "S", "D")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .background(MaterialTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Consumo Energético (kWh)",
            fontSize = 14.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        EnergyBarChart(data = energyData, labels = days)
    }
}

@Composable
fun EnergyBarChart(data: List<Int>, labels: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        data.forEachIndexed { index, value ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .width(10.dp)
                        .height((value * 1.2).dp.coerceAtMost(50.dp))
                        .background(Color(0xFF007F3E), shape = RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = labels[index],
                    fontSize = 8.sp, // más pequeño
                    color = Color.White
                )
            }
        }
    }

}

@Composable
fun ThirdScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "⚠️ Alerta de Fallo", fontSize = 14.sp)
        Text(text = "Luminaria #EUB-025", fontSize = 12.sp)
        Text(text = "Calle Principal 16 de Septiembre", fontSize = 10.sp)
        Text(text = "Error: Circuito dañado", fontSize = 10.sp)

        Spacer(modifier = Modifier.height(24.dp))
        CustomButton(
            text = "Entendido",
            backgroundColor = Color(0xFF1E88E5),
            onClick = { navController.popBackStack() }
        )
    }
}
