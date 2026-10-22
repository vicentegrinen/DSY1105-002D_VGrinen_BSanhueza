package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.ImagenInteligente
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.PremiumCard
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.utils.copiarImagenALocal
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.utils.crearUriParaCamara
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.UsuarioViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RegistroIntegralScreen(
    navController: NavController,
    viewModel: UsuarioViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsState()
    val errores by viewModel.errores.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val mensajeExito by viewModel.mensajeExito.collectAsState()

    // rememberSaveable: sobrevive a la rotación (Uri temporal de la cámara y contador de toques)
    var uriCamara by rememberSaveable { mutableStateOf<Uri?>(null) }
    var toquesModo by rememberSaveable { mutableIntStateOf(0) }

    // --- Galería (GetContent): si se cancela, uri llega null y no se hace nada
    val galeriaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val local = withContext(Dispatchers.IO) { copiarImagenALocal(context, uri) }
                viewModel.actualizarFoto(local ?: uri)
            }
        }
    }

    // --- Cámara (TakePicture): si se cancela, exito = false y se conserva la foto anterior
    val camaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito ->
        val uri = uriCamara
        if (exito && uri != null) viewModel.actualizarFoto(uri)
    }

    fun lanzarCamara() {
        val uri = crearUriParaCamara(context)
        uriCamara = uri
        camaraLauncher.launch(uri)
    }

    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido -> if (concedido) lanzarCamara() }

    if (cargando) {
        // Carga inicial simulada (delay 1500 ms en el ViewModel)
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Color animado del botón premium: gris -> verde #2E7D32
    val colorBoton by animateColorAsState(
        targetValue = if (uiState.modoEspecial) Color(0xFF2E7D32) else Color.Gray,
        animationSpec = tween(durationMillis = 500),
        label = "colorBotonPremium"
    )
    // Texto derivado del estado
    val textoDerivado by remember(uiState.modoEspecial) {
        derivedStateOf {
            val base = if (uiState.modoEspecial) "Desactivar modo Premium" else "Activar modo Premium"
            if (toquesModo == 0) base else "$base (cambios: $toquesModo)"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Registro integral", style = MaterialTheme.typography.headlineSmall)

        ImagenInteligente(uri = uiState.fotoUri)
        errores.foto?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
                val tienePermiso = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
                if (tienePermiso) lanzarCamara()
                else permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
            }) { Text("Cámara") }

            OutlinedButton(onClick = { galeriaLauncher.launch("image/*") }) { Text("Galería") }
        }

        OutlinedTextField(
            value = uiState.nombre,
            onValueChange = viewModel::onNombreChange,
            label = { Text("Nombre") },
            singleLine = true,
            isError = errores.nombre != null,
            supportingText = { errores.nombre?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.correo,
            onValueChange = viewModel::onCorreoChange,
            label = { Text("Correo") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = errores.correo != null,
            supportingText = { errores.correo?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.clave,
            onValueChange = viewModel::onClaveChange,
            label = { Text("Clave") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = errores.clave != null,
            supportingText = { errores.clave?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.confirmarClave,
            onValueChange = viewModel::onConfirmarClaveChange,
            label = { Text("Confirmar clave") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = errores.confirmarClave != null,
            supportingText = { errores.confirmarClave?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.direccion,
            onValueChange = viewModel::onDireccionChange,
            label = { Text("Dirección") },
            isError = errores.direccion != null,
            supportingText = { errores.direccion?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        Column(Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = uiState.aceptaTerminos,
                    onCheckedChange = viewModel::onTerminosChange
                )
                Text("Acepto los términos y condiciones")
            }
            errores.terminos?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        }

        // Botón modo premium con color animado y texto derivado
        Button(
            onClick = {
                toquesModo++
                viewModel.toggleModoEspecial()
            },
            colors = ButtonDefaults.buttonColors(containerColor = colorBoton),
            modifier = Modifier.fillMaxWidth()
        ) { Text(textoDerivado) }

        // Mensaje de éxito de DataStore (se oculta solo a los 2 segundos)
        AnimatedVisibility(
            visible = mensajeExito,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            Text(
                "¡Preferencia guardada en DataStore!",
                color = Color(0xFF2E7D32),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        PremiumCard(visible = uiState.modoEspecial)

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                if (viewModel.validarFormulario()) {
                    viewModel.guardarEnRoom()
                    navController.navigate("resumen")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Registrar y ver Resumen") }

        OutlinedButton(
            onClick = { navController.navigate("listado") },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Ver usuarios guardados") }
    }
}
