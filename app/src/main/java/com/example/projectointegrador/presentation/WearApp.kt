package com.example.projectointegrador.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projectointegrador.presentation.screens.DetalleLuminariaScreen
import com.example.projectointegrador.presentation.screens.MainScreen
import com.example.projectointegrador.presentation.screens.ThirdScreen
import com.example.projectointegrador.presentation.screens.MapaScreen
import com.example.projectointegrador.presentation.screens.HistorialScreen
import com.example.projectointegrador.presentation.theme.ProjectoIntegradorTheme


//{
//    "correo": "luisivmaraz03@gmail.com",
//    "password": "19357cbcb4"
//}
@Composable
fun WearApp() {
    ProjectoIntegradorTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "main") {
            composable("main") { MainScreen(navController) }
            composable("third") { ThirdScreen(navController) }
            composable("mapa") { MapaScreen(navController,context = navController.context) }
            composable("detalle/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                DetalleLuminariaScreen(navController, id)
            }
            composable("historial") { HistorialScreen(navController) }
        }
    }
}
