package com.example.projectointegrador.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

@Composable
fun SecondScreen(navController: NavController) {
    val energyData = listOf(20, 35, 15, 40, 25, 30, 10)
    val days = listOf("L", "M", "M", "J", "V", "S", "D")
    val bulbTypes = listOf("LED", "Normal", "LED", "Normal", "LED", "LED", "Normal")

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Consumo Energético",
                fontSize = 10.sp,
                color = Color.White
            )
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            EnergyBarChart(
                data = energyData,
                labels = days,
                types = bulbTypes
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        item {
            Text(
                text = "Consumo diario (kWh) y tipo de foco usado.",
                fontSize = 8.sp,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

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
                    color = Color.White
                )
                Text(
                    text = "${energyData[index]} kWh",
                    fontSize = 8.sp,
                    color = Color.Yellow
                )
                Text(
                    text = bulbTypes[index],
                    fontSize = 8.sp,
                    color = if (bulbTypes[index] == "LED") Color(0xFF4CAF50) else Color(0xFFFF9800)
                )
            }
        }
    }
}

@Composable
fun EnergyBarChart(data: List<Int>, labels: List<String>, types: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
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
                Spacer(modifier = Modifier.height(1.dp))
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height((value * 0.9).dp.coerceAtMost(36.dp))
                        .background(
                            if (types[index] == "LED") Color(0xFF007F3E) else Color(0xFFFF9800),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = labels[index],
                    fontSize = 6.sp,
                    color = Color.White
                )
            }
        }
    }
}
