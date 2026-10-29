package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.dsy1105_002d_vgrinen_bsanhueza.R
import com.example.dsy1105_002d_vgrinen_bsanhueza.auth.ResultadoSocial
import com.example.dsy1105_002d_vgrinen_bsanhueza.auth.SocialAuthManager
import kotlinx.coroutines.launch

// Botones "Google" y "Apple" reutilizables (login y registro).
// onExito se llama cuando el proveedor autenticó al usuario y entregó su correo.
@Composable
fun BotonesSociales(
    onExito: (ResultadoSocial.Exito) -> Unit,
    modifier: Modifier = Modifier,
    textoGoogle: String = "Continuar con Google",
    textoApple: String = "Continuar con Apple"
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()
    val manager = remember { SocialAuthManager(context) }

    var cargando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val lanzar: (suspend (Activity) -> ResultadoSocial) -> Unit = lanzar@{ intento ->
        val act = activity ?: return@lanzar
        if (cargando) return@lanzar
        cargando = true
        error = null
        scope.launch {
            try {
                when (val r = intento(act)) {
                    is ResultadoSocial.Exito -> onExito(r)
                    is ResultadoSocial.Error -> error = r.mensaje
                    ResultadoSocial.Cancelado -> Unit
                }
            } finally {
                cargando = false
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (cargando) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

        OutlinedButton(
            onClick = { lanzar { manager.conGoogle(it) } },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            // tint = Unspecified para conservar los 4 colores del logo
            Icon(
                painter = painterResource(R.drawable.ic_google),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(textoGoogle)
        }

        OutlinedButton(
            onClick = { lanzar { manager.conApple(it) } },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Sin tint explícito: toma el color del texto, así se ve bien en claro y oscuro
            Icon(
                painter = painterResource(R.drawable.ic_apple),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(textoApple)
        }

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// Separador con texto en medio: ——— o ———
@Composable
fun SeparadorConTexto(texto: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            text = texto,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}
