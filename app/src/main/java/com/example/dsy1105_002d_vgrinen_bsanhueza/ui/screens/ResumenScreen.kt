package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.ImagenInteligente
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.UsuarioViewModel

@Composable
fun ResumenScreen(
    navController: NavController,
    viewModel: UsuarioViewModel
) {
    // Mismo UsuarioViewModel que la pantalla de registro (compartido)
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Resumen del registro", style = MaterialTheme.typography.headlineSmall)

        ImagenInteligente(uri = uiState.fotoUri)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Nombre: ${uiState.nombre}")
                Text("Correo: ${uiState.correo}")
                Text("Dirección: ${uiState.direccion}")
                Text("Modo: ${if (uiState.modoEspecial) "Premium" else "Básico"}")
            }
        }

        Button(
            onClick = { navController.navigate("listado") },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Ver listado offline") }

        OutlinedButton(
            onClick = {
                viewModel.reiniciarFormulario()
                navController.popBackStack("registro", inclusive = false)
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Nuevo registro") }
    }
}
