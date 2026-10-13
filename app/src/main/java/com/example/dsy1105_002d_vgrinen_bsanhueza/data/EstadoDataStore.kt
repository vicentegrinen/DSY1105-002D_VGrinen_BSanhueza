package com.example.dsy1105_002d_vgrinen_bsanhueza.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Declaramos el DataStore con nombre 'preferencias_usuario' (siempre a nivel superior del archivo)
val Context.dataStore by preferencesDataStore(name = "preferencias_usuario")

class EstadoDataStore(private val context: Context) {

    // Clave usada para guardar el estado
    private val ESTADO_ACTIVADO = booleanPreferencesKey("modo_activado")

    // Guarda el estado en DataStore
    suspend fun guardarEstado(valor: Boolean) {
        context.dataStore.edit { preferencias ->
            preferencias[ESTADO_ACTIVADO] = valor
        }
    }

    // Obtiene el estado como Flow (reactivo). null = nunca se ha guardado
    fun obtenerEstado(): Flow<Boolean?> {
        return context.dataStore.data.map { preferencias ->
            preferencias[ESTADO_ACTIVADO]
        }
    }
}