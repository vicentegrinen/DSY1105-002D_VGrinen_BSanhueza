package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Rol
import com.example.dsy1105_002d_vgrinen_bsanhueza.notificaciones.NotificadorGuardian
import com.example.dsy1105_002d_vgrinen_bsanhueza.remote.ApiSimulada
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.BarraSuperiorGuardian
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.BotonPrincipal
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.BotonSecundario
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.TarjetaGuardian
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.TextoSecundarioTarjeta
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.TituloSeccion

private const val NOTIFICACION_ENVIADA = "Notificación enviada. Revisa la barra de notificaciones."

// Pestaña "Perfil": datos de la sesión, herramientas de prueba, acceso a los módulos
// de las guías anteriores (no se eliminaron) y cierre de sesión.
// No confundir con PerfilScreen, que es la pantalla de cámara y galería (Guía 13).
@Composable
fun PerfilGuardianScreen(
    correo: String,
    rol: Rol?,
    onAbrirModulo: (ruta: String) -> Unit,
    onCerrarSesion: () -> Unit
) {
    // Interruptor de prueba: hace fallar las llamadas de la API simulada
    var sinConexion by remember { mutableStateOf(ApiSimulada.sinConexion) }

    // Notificación de prueba: resultado que se muestra bajo el botón
    val contexto = LocalContext.current
    var avisoNotificacion by remember { mutableStateOf<String?>(null) }
    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        avisoNotificacion =
            if (concedido && NotificadorGuardian.enviarPrueba(contexto)) NOTIFICACION_ENVIADA
            else "Sin permiso de notificaciones. Puedes activarlo en los ajustes de la app."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BarraSuperiorGuardian(titulo = "Perfil", subtitulo = "Sesión y herramientas de prueba")

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            TarjetaGuardian {
                TextoSecundarioTarjeta("Sesión iniciada como")
                Text(
                    text = correo.ifBlank { "Usuario de prueba" },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
                )
                TextoSecundarioTarjeta("Rol: ${rol?.etiqueta ?: "sin rol"}")
            }

            TituloSeccion("Herramientas de prueba")
            TarjetaGuardian {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp)
                    ) {
                        Text("Simular sin conexión", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        TextoSecundarioTarjeta(
                            "Hace fallar las llamadas a la API de prueba para ver el aviso de error."
                        )
                    }
                    Switch(
                        checked = sinConexion,
                        onCheckedChange = {
                            sinConexion = it
                            ApiSimulada.sinConexion = it
                        },
                        modifier = Modifier.semantics { contentDescription = "Simular sin conexión" }
                    )
                }
            }

            BotonSecundario(
                texto = "Enviar notificación de prueba",
                onClick = {
                    if (NotificadorGuardian.tienePermiso(contexto)) {
                        NotificadorGuardian.enviarPrueba(contexto)
                        avisoNotificacion = NOTIFICACION_ENVIADA
                    } else {
                        // Android muestra su diálogo; la respuesta llega a "pedirPermiso"
                        pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            avisoNotificacion?.let { TextoSecundarioTarjeta(it) }

            TituloSeccion("Módulos de guías anteriores")
            BotonSecundario(
                texto = "Foto de perfil (cámara y galería)",
                onClick = { onAbrirModulo("perfil") },
                modifier = Modifier.fillMaxWidth()
            )
            BotonSecundario(
                texto = "Modo especial",
                onClick = { onAbrirModulo("modo_especial") },
                modifier = Modifier.fillMaxWidth()
            )
            BotonSecundario(
                texto = "Registro integral (Room + cámara)",
                onClick = { onAbrirModulo("registro") },
                modifier = Modifier.fillMaxWidth()
            )
            BotonSecundario(
                texto = "Usuarios guardados (offline)",
                onClick = { onAbrirModulo("listado") },
                modifier = Modifier.fillMaxWidth()
            )

            BotonPrincipal(
                texto = "Cerrar sesión",
                onClick = onCerrarSesion,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}
