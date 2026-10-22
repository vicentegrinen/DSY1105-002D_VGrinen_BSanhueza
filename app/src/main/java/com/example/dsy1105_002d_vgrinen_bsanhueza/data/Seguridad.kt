package com.example.dsy1105_002d_vgrinen_bsanhueza.data

import java.security.MessageDigest
import java.security.SecureRandom

// Utilidades para no guardar contraseñas en texto plano
object Seguridad {

    fun generarSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun hash(clave: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest((salt + clave).toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }
}
