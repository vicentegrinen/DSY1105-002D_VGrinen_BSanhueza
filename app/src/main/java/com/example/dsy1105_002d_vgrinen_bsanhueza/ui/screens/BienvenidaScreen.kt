package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Rol
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.LoginViewModel

@Composable
fun BienvenidaScreen(
    navController: NavController,
    viewModel: LoginViewModel
) {
    val estado by viewModel.estado.collectAsState()
    val rol = estado.rol

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = rol?.let { tituloBienvenida(it) } ?: "¡Bienvenido!",
            style = MaterialTheme.typography.headlineMedium
        )
        rol?.let { Text(mensajeBienvenida(it), style = MaterialTheme.typography.bodyLarge) }

        Text("Sesión iniciada como ${estado.correo.trim()}")
        rol?.let { Text("Rol: ${it.etiqueta}") }

        Button(
            onClick = {
                viewModel.cerrarSesion()
                navController.navigate("login") {
                    // Limpia la pila: no se puede volver atrás a la pantalla de bienvenida
                    popUpTo("bienvenida") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}

// Un mensaje de inicio distinto para cada rol
private fun tituloBienvenida(rol: Rol): String = when (rol) {
    Rol.ADMIN -> "Panel de Administración"
    Rol.SUPERVISOR -> "Panel de Supervisión"
    Rol.OPERADOR -> "Panel de Operación"
}

private fun mensajeBienvenida(rol: Rol): String = when (rol) {
    Rol.ADMIN ->
        "Bienvenido, Administrador. Tienes acceso total para gestionar usuarios y la configuración de Guardian."
    Rol.SUPERVISOR ->
        "Bienvenido, Supervisor. Revisa el estado de las operaciones y coordina a tu equipo."
    Rol.OPERADOR ->
        "Bienvenido, Operador. Todo listo para comenzar tu turno."
}
