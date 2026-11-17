package com.example.dsy1105_002d_vgrinen_bsanhueza.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

// Lectura y escritura de la copia local de Modo Guardián.
@Dao
interface GuardianCacheDao {

    // ---- Eventos ----
    @Query("SELECT * FROM eventos_cache ORDER BY fechaHora DESC")
    suspend fun obtenerEventos(): List<EventoCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEventos(eventos: List<EventoCacheEntity>)

    @Query("DELETE FROM eventos_cache")
    suspend fun borrarEventos()

    // Borra e inserta en una sola transacción: la copia queda igual a la última respuesta
    @Transaction
    suspend fun reemplazarEventos(eventos: List<EventoCacheEntity>) {
        borrarEventos()
        insertarEventos(eventos)
    }

    // ---- Cámaras ----
    @Query("SELECT * FROM camaras_cache ORDER BY id")
    suspend fun obtenerCamaras(): List<CamaraCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCamaras(camaras: List<CamaraCacheEntity>)

    @Query("DELETE FROM camaras_cache")
    suspend fun borrarCamaras()

    @Transaction
    suspend fun reemplazarCamaras(camaras: List<CamaraCacheEntity>) {
        borrarCamaras()
        insertarCamaras(camaras)
    }

    // ---- Indicadores (una sola fila) ----
    @Query("SELECT * FROM indicadores_cache LIMIT 1")
    suspend fun obtenerIndicadores(): IndicadoresCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarIndicadores(indicadores: IndicadoresCacheEntity)
}
