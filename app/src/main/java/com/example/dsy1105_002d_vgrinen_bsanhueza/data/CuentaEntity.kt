package com.example.dsy1105_002d_vgrinen_bsanhueza.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

// Cuenta creada desde "Crear cuenta". La contraseña nunca se guarda en texto plano:
// solo el hash SHA-256 (con sal) y la sal.
@Entity(
    tableName = "cuentas",
    indices = [
        Index(value = ["correo"], unique = true),
        Index(value = ["username"], unique = true)
    ]
)
data class CuentaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val correo: String,
    val username: String,
    val claveHash: String,
    val salt: String,
    val pais: String,
    val recibirNovedades: Boolean,
    val fechaRegistro: Date
)
