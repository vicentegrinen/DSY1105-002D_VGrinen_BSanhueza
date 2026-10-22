package com.example.dsy1105_002d_vgrinen_bsanhueza.navegation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dsy1105_002d_vgrinen_bsanhueza.repository.CuentaRepository
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.BienvenidaScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.ListadoOfflineScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.LoginScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.PantallaPrincipal
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.PerfilScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.RegistroCuentaScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.RegistroIntegralScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.ResumenScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.LoginViewModel
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.LoginViewModelFactory
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.RegistroCuentaViewModel
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.RegistroCuentaViewModelFactory
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.UsuarioViewModel

@Composable
fun AppNavigation(
    usuarioViewModel: UsuarioViewModel,
    cuentaRepository: CuentaRepository
) {
    val navController = rememberNavController()

    // Los ViewModels se crean una sola vez y se comparten entre pantallas
    val loginViewModel: LoginViewModel =
        viewModel(factory = LoginViewModelFactory(cuentaRepository))
    val registroCuentaViewModel: RegistroCuentaViewModel =
        viewModel(factory = RegistroCuentaViewModelFactory(cuentaRepository))

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") { entry ->
            // "cuenta_creada" lo activa RegistroCuentaScreen al terminar el registro
            val cuentaCreada by entry.savedStateHandle
                .getStateFlow("cuenta_creada", false)
                .collectAsState()
            LoginScreen(
                navController = navController,
                viewModel = loginViewModel,
                cuentaCreada = cuentaCreada,
                onMensajeVisto = { entry.savedStateHandle["cuenta_creada"] = false }
            )
        }
        // Registro de usuarios nuevos (se llega desde el login)
        composable("crear_cuenta") {
            RegistroCuentaScreen(navController, registroCuentaViewModel)
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
