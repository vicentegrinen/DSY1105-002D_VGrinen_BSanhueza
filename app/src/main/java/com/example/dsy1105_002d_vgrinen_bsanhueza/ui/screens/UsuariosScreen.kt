package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Rol
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UiState
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UsuarioPrueba
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.BannerAviso
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.BarraSuperiorGuardian
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.ChipSeleccion
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.EstadoGuardian
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.TarjetaGuardian
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.TextoSecundarioTarjeta
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.UsuariosViewModel

// Administración de usuarios de prueba y roles: solo la ve el rol Admin.
// "correoSesion" es el usuario que está usando la app: no puede cambiar su propio rol.
@Composable
fun UsuariosScreen(
    viewModel: UsuariosViewModel,
    correoSesion: String
) {
    val estado by viewModel.estado.collectAsState()
    val guardando by viewModel.guardando.collectAsState()
    val errorAccion by viewModel.errorAccion.collectAsState()

    LaunchedEffect(Unit) { viewModel.cargar() }

    UsuariosContenido(
        estado = estado,
        correoSesion = correoSesion,
        guardando = guardando,
        errorAccion = errorAccion,
        onCambiarRol = viewModel::cambiarRol,
        onDescartarError = viewModel::descartarErrorAccion,
        onReintentar = viewModel::cargar
    )
}

// Sin ViewModel: solo dibuja el estado que recibe (se puede probar con Compose UI Test)
@Composable
fun UsuariosContenido(
    estado: UiState<List<UsuarioPrueba>>,
    correoSesion: String,
    guardando: Boolean,
    errorAccion: String?,
    onCambiarRol: (UsuarioPrueba, Rol) -> Unit,
    onDescartarError: () -> Unit,
    onReintentar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BarraSuperiorGuardian(titulo = "Usuarios y roles", subtitulo = "Usuarios de prueba (ficticios)")

        EstadoGuardian(estado, onReintentar, Modifier.weight(1f)) { usuarios, mensajeError ->
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (mensajeError != null) {
                    item { BannerAviso(mensajeError, onClick = onReintentar) }
                }
                if (errorAccion != null) {
                    item {
                        BannerAviso(errorAccion, onClick = onDescartarError, textoBoton = "Entendido")
                    }
                }
                item {
                    TextoSecundarioTarjeta(
                        "El rol define qué opciones ve cada usuario. Los cambios son de prueba: " +
                            "se reinician al cerrar la app y no modifican el ingreso."
                    )
                }
                items(usuarios, key = { it.id }) { usuario ->
                    TarjetaUsuario(
                        usuario = usuario,
                        esSesionActual = usuario.correo.equals(correoSesion, ignoreCase = true),
                        guardando = guardando,
                        onCambiarRol = { rol -> onCambiarRol(usuario, rol) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaUsuario(
    usuario: UsuarioPrueba,
    esSesionActual: Boolean,
    guardando: Boolean,
    onCambiarRol: (Rol) -> Unit
) {
    TarjetaGuardian {
        Text(text = usuario.nombre, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        TextoSecundarioTarjeta(usuario.correo)

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(top = 4.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            Rol.entries.forEach { rol ->
                ChipSeleccion(
                    texto = rol.etiqueta,
                    seleccionado = usuario.rol == rol,
                    enabled = !esSesionActual && !guardando,
                    onClick = { onCambiarRol(rol) }
                )
            }
        }

        if (esSesionActual) {
            TextoSecundarioTarjeta("Es tu sesión: no puedes cambiar tu propio rol.")
        }
    }
}
