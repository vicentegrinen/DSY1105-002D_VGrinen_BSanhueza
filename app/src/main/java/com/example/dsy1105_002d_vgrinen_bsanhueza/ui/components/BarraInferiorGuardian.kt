package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

// Una pestaña de la barra inferior: ruta de navegación, texto e ícono.
data class PestanaGuardian(
    val ruta: String,
    val etiqueta: String,
    val icono: ImageVector
)

// Barra inferior del diseño: Panel, Eventos, Cámaras y Perfil.
@Composable
fun BarraInferiorGuardian(
    pestanas: List<PestanaGuardian>,
    rutaSeleccionada: String,
    onSeleccionar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            // MainActivity ya aplica el margen de las barras del sistema
            windowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            pestanas.forEach { pestana ->
                NavigationBarItem(
                    selected = pestana.ruta == rutaSeleccionada,
                    onClick = { onSeleccionar(pestana.ruta) },
                    // El texto ya describe la pestaña: el ícono es decorativo
                    icon = { Icon(pestana.icono, contentDescription = null) },
                    label = { Text(pestana.etiqueta) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}
