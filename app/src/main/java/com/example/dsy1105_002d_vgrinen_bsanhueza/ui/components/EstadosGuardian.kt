package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UiState
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.theme.AzulGuardian
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.theme.MediaBorde
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.theme.MediaFondo
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.theme.MediaTexto

// Dibuja una pantalla según su UiState:
// - Cargando: indicador de progreso.
// - Exito: el contenido con los datos.
// - Error con datos guardados: el contenido con esos datos y el mensaje, para mostrar el aviso arriba.
// - Error sin datos: solo el aviso con el botón "Reintentar".
@Composable
fun <T> EstadoGuardian(
    estado: UiState<T>,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier,
    contenido: @Composable (datos: T, mensajeError: String?) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (estado) {
            is UiState.Cargando -> CargandoGuardian()
            is UiState.Exito -> contenido(estado.datos, null)
            is UiState.Error -> {
                val guardados = estado.datosGuardados
                if (guardados != null) {
                    contenido(guardados, estado.mensaje)
                } else {
                    BannerAviso(
                        mensaje = estado.mensaje,
                        onClick = onReintentar,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CargandoGuardian(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.semantics { contentDescription = "Cargando" }
        )
    }
}

// Aviso ámbar del diseño ("Sin conexión. Mostrando datos guardados.") con un botón de acción
@Composable
fun BannerAviso(
    mensaje: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textoBoton: String = "Reintentar"
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MediaFondo),
        border = BorderStroke(1.dp, MediaBorde)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = mensaje, color = MediaTexto, fontSize = 13.sp)
            OutlinedButton(
                onClick = onClick,
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, AzulGuardian),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = AzulGuardian
                ),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Text(textoBoton, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
