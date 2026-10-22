package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.PAISES
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.RegistroCuentaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroCuentaScreen(
    navController: NavController,
    viewModel: RegistroCuentaViewModel
) {
    val estado by viewModel.estado.collectAsState()
    val errores by viewModel.errores.collectAsState()
    val guardando by viewModel.guardando.collectAsState()

    var mostrarClave by rememberSaveable { mutableStateOf(false) }
    var mostrarConfirmacion by rememberSaveable { mutableStateOf(false) }
    var paisExpandido by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium)

        // Correo
        OutlinedTextField(
            value = estado.correo,
            onValueChange = viewModel::onCorreoChange,
            label = { Text("Correo electrónico *") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = errores.correo != null,
            supportingText = { errores.correo?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        // Contraseña
        OutlinedTextField(
            value = estado.clave,
            onValueChange = viewModel::onClaveChange,
            label = { Text("Contraseña *") },
            singleLine = true,
            visualTransformation =
                if (mostrarClave) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                TextButton(onClick = { mostrarClave = !mostrarClave }) {
                    Text(if (mostrarClave) "Ocultar" else "Mostrar")
                }
            },
            isError = errores.clave != null,
            supportingText = {
                Text(
                    errores.clave
                        ?: "Mínimo 15 caracteres, o al menos 8 que incluyan un número y una letra minúscula."
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Confirmar contraseña
        OutlinedTextField(
            value = estado.confirmarClave,
            onValueChange = viewModel::onConfirmarClaveChange,
            label = { Text("Confirmar contraseña *") },
            singleLine = true,
            visualTransformation =
                if (mostrarConfirmacion) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                TextButton(onClick = { mostrarConfirmacion = !mostrarConfirmacion }) {
                    Text(if (mostrarConfirmacion) "Ocultar" else "Mostrar")
                }
            },
            isError = errores.confirmarClave != null,
            supportingText = { errores.confirmarClave?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        // Nombre de usuario
        OutlinedTextField(
            value = estado.username,
            onValueChange = viewModel::onUsernameChange,
            label = { Text("Nombre de usuario *") },
            singleLine = true,
            isError = errores.username != null,
            supportingText = {
                Text(
                    errores.username
                        ?: "Solo letras, números y guiones simples; no puede empezar ni terminar con guion."
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        // País / Región (Chile por defecto)
        ExposedDropdownMenuBox(
            expanded = paisExpandido,
            onExpandedChange = { paisExpandido = it }
        ) {
            OutlinedTextField(
                value = estado.pais,
                onValueChange = {},
                readOnly = true,
                label = { Text("País / Región *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paisExpandido) },
                supportingText = {
                    Text("Por requisitos de cumplimiento, necesitamos recopilar tu país para enviarte novedades y anuncios ocasionales.")
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = paisExpandido,
                onDismissRequest = { paisExpandido = false }
            ) {
                PAISES.forEach { pais ->
                    DropdownMenuItem(
                        text = { Text(pais) },
                        onClick = {
                            viewModel.onPaisChange(pais)
                            paisExpandido = false
                        }
                    )
                }
            }
        }

        // Preferencias de correo
        Text("Preferencias de correo", style = MaterialTheme.typography.titleSmall)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = estado.recibirNovedades,
                onCheckedChange = viewModel::onNovedadesChange
            )
            Text("Recibir novedades y anuncios del producto de forma ocasional")
        }

        Button(
            onClick = {
                viewModel.registrar {
                    // Avisa al login que la cuenta se creó y vuelve a él
                    navController.previousBackStackEntry?.savedStateHandle?.set("cuenta_creada", true)
                    navController.popBackStack()
                }
            },
            enabled = !guardando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (guardando) "Creando cuenta..." else "Crear cuenta")
        }

        TextButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}
