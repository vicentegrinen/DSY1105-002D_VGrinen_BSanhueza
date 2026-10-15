package com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PerfilViewModel : ViewModel() {

    private val _imagenUri = MutableStateFlow<Uri?>(null)
    val imagenUri: StateFlow<Uri?> = _imagenUri.asStateFlow()

    fun actualizarImagenDesdeGaleria(uri: Uri?) {
        if (uri != null) _imagenUri.value = uri
    }

    fun actualizarImagenDesdeCamara(uri: Uri, exito: Boolean) {
        if (exito) _imagenUri.value = uri
    }
}