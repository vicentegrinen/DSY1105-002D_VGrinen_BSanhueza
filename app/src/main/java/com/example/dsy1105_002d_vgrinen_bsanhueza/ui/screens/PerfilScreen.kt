package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.ImagenInteligente
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.PerfilViewModel
import java.io.File

@Composable
fun PerfilScreen(viewModel: PerfilViewModel = viewModel()) {
    val context = LocalContext.current
    val imagenUri by viewModel.imagenUri.collectAsState()

    var uriCamara by rememberSaveable { mutableStateOf<Uri?>(null) }

    val galeriaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> viewModel.actualizarImagenDesdeGaleria(uri) }

    val camaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito ->
        uriCamara?.let { viewModel.actualizarImagenDesdeCamara(it, exito) }
    }

    fun lanzarCamara() {
        val archivo = File.createTempFile("foto_", ".jpg", context.cacheDir)
        val uri = FileProvider.getUriForFile(
            context, "${context.packageName}.fileprovider", archivo
        )
        uriCamara = uri
        camaraLauncher.launch(uri)
    }

    val permisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido -> if (concedido) lanzarCamara() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ImagenInteligente(uri = imagenUri)
        Spacer(Modifier.height(32.dp))

        Button(onClick = { galeriaLauncher.launch("image/*") }) {
            Text("Elegir de galería")
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = {
            val tienePermiso = ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
            if (tienePermiso) lanzarCamara()
            else permisoLauncher.launch(Manifest.permission.CAMERA)
        }) {
            Text("Tomar foto")
        }
    }
}