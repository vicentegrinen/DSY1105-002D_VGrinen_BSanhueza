package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.ImagenInteligente
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.UsuarioViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ListadoOfflineScreen(viewModel: UsuarioViewModel) {
    // Flow de ROOM: funciona sin internet y se actualiza solo al insertar
    val usuarios by viewModel.usuarios.collectAsState(initial = emptyList())
    val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Usuarios guardados (offline)", style = MaterialTheme.typography.headlineSmall)

        if (usuarios.isEmpty()) {
            Text("Aún no hay usuarios registrados.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(usuarios, key = { it.id }) { u ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ImagenInteligente(uri = u.fotoPath?.let { Uri.parse(it) }, size = 64.dp)
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(u.nombre, style = MaterialTheme.typography.titleMedium)
                                Text(u.correo)
                                Text(u.direccion)
                                Text("Modo: ${if (u.modoEspecial) "Premium" else "Básico"}")
                                Text("Registrado: ${formato.format(u.fechaRegistro)}",
                                    style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
