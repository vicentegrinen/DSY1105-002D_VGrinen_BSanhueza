package com.example.dsy1105_002d_vgrinen_bsanhueza.repository

import com.example.dsy1105_002d_vgrinen_bsanhueza.data.CuentaDao
import com.example.dsy1105_002d_vgrinen_bsanhueza.data.CuentaEntity
import com.example.dsy1105_002d_vgrinen_bsanhueza.data.Seguridad
import java.util.Date

class CuentaRepository(private val dao: CuentaDao) {

    suspend fun correoExiste(correo: String): Boolean =
        dao.buscarPorCorreo(correo.trim()) != null

    suspend fun usernameExiste(username: String): Boolean =
        dao.buscarPorUsername(username.trim()) != null

    suspend fun registrar(
        correo: String,
        username: String,
        clave: String,
        pais: String,
        recibirNovedades: Boolean
    ) {
        val salt = Seguridad.generarSalt()
        dao.insertar(
            CuentaEntity(
                correo = correo.trim(),
                username = username.trim(),
                claveHash = Seguridad.hash(clave, salt),
                salt = salt,
                pais = pais,
                recibirNovedades = recibirNovedades,
                fechaRegistro = Date()
            )
        )
    }

    // Devuelve la cuenta si el correo existe y la clave coincide; si no, null
    suspend fun autenticar(correo: String, clave: String): CuentaEntity? {
        val cuenta = dao.buscarPorCorreo(correo.trim()) ?: return null
        return if (Seguridad.hash(clave, cuenta.salt) == cuenta.claveHash) cuenta else null
    }
}
