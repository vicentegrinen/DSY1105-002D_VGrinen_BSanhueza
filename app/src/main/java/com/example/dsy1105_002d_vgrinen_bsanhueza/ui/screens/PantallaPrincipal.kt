package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.EstadoViewModel

@Composable
fun PantallaPrincipal(
    modifier: Modifier = Modifier,
    viewModel: EstadoViewModel = viewModel()
) {
    val estado by viewModel.activo.collectAsState()
    val mostrarMensaje by viewModel.mostrarMensaje.collectAsState()

    // PARTE 1: rememberSaveable -> sobrevive a rotaciones de pantalla
    var cambios by rememberSaveable { mutableIntStateOf(0) }

    val activo = estado

    // PARTE 3: mientras el estado es null, se muestra el loader
    if (activo == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // PARTE 1: color animado según el estado
        val colorAnimado by animateColorAsState(
            targetValue = if (activo) Color(0xFF4CAF50) else Color(0xFFB0BEC5),
            animationSpec = tween(durationMillis = 500),
            label = "colorBoton"
        )

        // PARTE 1: texto derivado del estado
        val textoBoton by remember(activo) {
            derivedStateOf { if (activo) "Desactivar" else "Activar" }
        }

        // PARTE 5: texto derivado del contador local
        val resumenCambios by remember {
            derivedStateOf {
                if (cambios == 0) "Aún no has cambiado el modo" else "Cambios en esta sesión: $cambios"
            }
        }

        // PARTE 5: el círculo "rebota" al cambiar de estado
        val escala by animateFloatAsState(
            targetValue = if (activo) 1f else 0.85f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "escalaIndicador"
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // PARTE 5: indicador visual con icono que cambia con AnimatedContent
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(escala)
                    .background(colorAnimado, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(targetState = activo, label = "icono") { encendido ->
                    Icon(
                        imageVector = if (encendido) Icons.Filled.Check else Icons.Filled.Close,
                        contentDescription = if (encendido) "Modo activado" else "Modo desactivado",
                        tint = Color.White,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (activo) "Modo especial ACTIVADO" else "Modo especial DESACTIVADO",
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = resumenCambios, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    cambios++
                    viewModel.alternarEstado()
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorAnimado),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(textoBoton, style = MaterialTheme.typography.titleLarge)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // PARTE 4: mensaje de éxito con animación de entrada/salida
            AnimatedVisibility(
                visible = mostrarMensaje,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 }
            ) {
                Text(
                    text = "¡Estado guardado exitosamente!",
                    color = Color(0xFF4CAF50),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}