package com.example.dsy1105_002d_vgrinen_bsanhueza.navegation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.BienvenidaScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.ListadoOfflineScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.LoginScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.PantallaPrincipal
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.PerfilScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.RegistroIntegralScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.ResumenScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.LoginViewModel
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.UsuarioViewModel

@Composable
fun AppNavigation(usuarioViewModel: UsuarioViewModel) {
    val navController = rememberNavController()

    // Aquí creamos el ViewModel una sola vez y se comparte entre pantallas
    val loginViewModel: LoginViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController, loginViewModel)
        }
        composable("bienvenida") {
            BienvenidaScreen(navController, loginViewModel)
        }
        // Pantalla de la Guía 12 (estado + DataStore + animaciones)
        composable("modo_especial") {
            PantallaPrincipal()
        }
        // Pantalla de la Guía 13 (cámara y galería)
        composable("perfil") {
            PerfilScreen()
        }
        // Guía 14: módulo integral (UsuarioViewModel compartido entre las 3 pantallas)
        composable("registro") {
            RegistroIntegralScreen(navController, usuarioViewModel)
        }
        composable("resumen") {
            ResumenScreen(navController, usuarioViewModel)
        }
        composable("listado") {
            ListadoOfflineScreen(usuarioViewModel)
        }
    }
}
