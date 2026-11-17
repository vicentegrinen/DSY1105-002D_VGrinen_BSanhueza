package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dsy1105_002d_vgrinen_bsanhueza.navegation.Rutas
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.BotonesSociales
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.SeparadorConTexto
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.TarjetaGuardian
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel,
    cuentaCreada: Boolean = false,
    onMensajeVisto: () -> Unit = {}
) {
    val estado by viewModel.estado.collectAsState()
    var mostrarClave by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Encabezado del diseño de Modo Guardián: escudo, nombre y descripción
        Icon(
            imageVector = Icons.Filled.VerifiedUser,
            contentDescription = "Escudo Modo Guardián",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = "Modo Guardián",
            fontSize = 30.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.semantics { heading() }
        )
        Text(
            text = "Monitoreo de eventos y alertas. MVP académico con datos de prueba.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Aviso al volver desde "Crear cuenta"
        if (cuentaCreada) {
            Text(
                text = "Cuenta creada. Ya puedes iniciar sesión.",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        OutlinedTextField(
            value = estado.correo,
            onValueChange = viewModel::onCorreoChange,
            label = { Text("Usuario (correo electrónico)") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = estado.errores.correo != null,
            supportingText = {
                estado.errores.correo?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = estado.clave,
            onValueChange = viewModel::onClaveChange,
            label = { Text("Contraseña") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            visualTransformation =
                if (mostrarClave) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                TextButton(onClick = { mostrarClave = !mostrarClave }) {
                    Text(if (mostrarClave) "Ocultar" else "Mostrar")
                }
            },
            isError = estado.errores.clave != null,
            supportingText = {
                estado.errores.clave?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Error de credenciales incorrectas
        estado.errores.general?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = {
                onMensajeVisto()
                viewModel.iniciarSesion {
                    // Modo Guardián: tras el login se entra a la pantalla de inicio del rol
                    // (antes "bienvenida"). En este punto el rol ya está en el estado.
                    navController.navigate(Rutas.inicioPara(viewModel.estado.value.rol)) {
                        // Quita el login de la pila: con "atrás" no se vuelve a él
                        popUpTo("login") { inclusive = true }
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
        ) {
            Text("Ingresar", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }

        // Aviso del diseño: los usuarios son ficticios y el rol define las opciones
        TarjetaGuardian {
            Text(
                text = buildAnnotatedString {
                    append("Usuarios ficticios: ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("admin") }
                    append(", ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("supervisor") }
                    append(" y ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("operador") }
                    append(" (correo nombre@guardian.test). El rol define qué opciones ves.")
                },
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Inicio de sesión con Google / Apple
        SeparadorConTexto("o continúa con")
        BotonesSociales(
            onExito = { resultado ->
                onMensajeVisto()
                viewModel.iniciarSesionSocial(resultado.correo)
                navController.navigate(Rutas.inicioPara(viewModel.estado.value.rol)) {
                    popUpTo("login") { inclusive = true }
                }
            },
            textoGoogle = "Iniciar sesión con Google",
            textoApple = "Iniciar sesión con Apple"
        )

        // Acceso al registro de usuarios nuevos
        TextButton(
            onClick = { navController.navigate("crear_cuenta") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}
