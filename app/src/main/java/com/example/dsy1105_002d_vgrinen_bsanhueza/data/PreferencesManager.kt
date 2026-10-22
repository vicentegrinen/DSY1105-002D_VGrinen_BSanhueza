package com.example.dsy1105_002d_vgrinen_bsanhueza.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Nombre de propiedad distinto a 'dataStore' (ya usado en EstadoDataStore.kt)
// y archivo distinto ("preferencias") para no chocar con "preferencias_usuario".
private val Context.preferenciasDataStore by preferencesDataStore(name = "preferencias")

class PreferencesManager(private val context: Context) {

    private val modoEspecialKey = booleanPreferencesKey("modo_especial")

    val modoEspecialFlow: Flow<Boolean> =
        context.preferenciasDataStore.data.map { it[modoEspecialKey] ?: false }

    suspend fun guardarModoEspecial(valor: Boolean) {
        context.preferenciasDataStore.edit { it[modoEspecialKey] = valor }
    }
}