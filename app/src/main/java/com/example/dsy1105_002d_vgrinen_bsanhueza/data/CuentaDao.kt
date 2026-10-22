package com.example.dsy1105_002d_vgrinen_bsanhueza.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CuentaDao {
    @Insert
    suspend fun insertar(cuenta: CuentaEntity)

    @Query("SELECT * FROM cuentas WHERE LOWER(correo) = LOWER(:correo) LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): CuentaEntity?

    @Query("SELECT * FROM cuentas WHERE LOWER(username) = LOWER(:username) LIMIT 1")
    suspend fun buscarPorUsername(username: String): CuentaEntity?
}
