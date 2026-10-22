package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File

// Carpeta propia de la app (getExternalFilesDir); no requiere permisos de almacenamiento.
private fun carpetaFotos(context: Context): File =
    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.filesDir

private fun archivoAUri(context: Context, archivo: File): Uri =
    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)

/** Crea un archivo temporal para la cámara y devuelve su Uri (FileProvider). */
fun crearUriParaCamara(context: Context): Uri {
    val archivo = File.createTempFile("foto_", ".jpg", carpetaFotos(context))
    return archivoAUri(context, archivo)
}

/**
 * La Uri que entrega la galería es temporal y puede dejar de ser accesible al reiniciar la app.
 * Se copia la imagen a la carpeta de la app para que la foto persista junto al registro en ROOM.
 */
fun copiarImagenALocal(context: Context, origen: Uri): Uri? {
    return try {
        val destino = File.createTempFile("galeria_", ".jpg", carpetaFotos(context))
        val entrada = context.contentResolver.openInputStream(origen) ?: return null
        entrada.use { input -> destino.outputStream().use { output -> input.copyTo(output) } }
        archivoAUri(context, destino)
    } catch (e: Exception) {
        null
    }
}
