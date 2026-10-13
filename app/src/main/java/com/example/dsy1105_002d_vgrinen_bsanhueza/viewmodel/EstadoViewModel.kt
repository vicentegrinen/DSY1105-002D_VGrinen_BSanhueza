package com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_002d_vgrinen_bsanhueza.data.EstadoDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Extiende AndroidViewModel porque necesitamos el contexto de la aplicación
class EstadoViewModel(application: Application) : AndroidViewModel(application) {

    // DataStore creado con el contexto de la aplicación
    private val estadoDataStore = EstadoDataStore(application)

    // null = todavía cargando (se muestra el loader); true/false = ya cargado
    private val _activo = MutableStateFlow<Boolean?>(null)
    val activo: StateFlow<Boolean?> = _activo

    // Para mostrar u ocultar el mensaje animado
    private val _mostrarMensaje = MutableStateFlow(false)
    val mostrarMensaje: StateFlow<Boolean> = _mostrarMensaje

    init {
        // Al iniciar el ViewModel, cargamos el estado desde DataStore
        cargarEstado()
    }

    private fun cargarEstado() {
        viewModelScope.launch {
            // Simula demora para mostrar el loader
            delay(1500)
            _activo.value = estadoDataStore.obtenerEstado().first() ?: false
        }
    }

    fun alternarEstado() {
        viewModelScope.launch {
            // Alternamos el valor actual
            val nuevoValor = !(_activo.value ?: false)

            // Guardamos en DataStore
            estadoDataStore.guardarEstado(nuevoValor)

            // Actualizamos el flujo
            _activo.value = nuevoValor

            // Mostramos el mensaje visual animado
            _mostrarMensaje.value = true
            delay(2000) // Lo ocultamos después de 2 segundos
            _mostrarMensaje.value = false
        }
    }
}