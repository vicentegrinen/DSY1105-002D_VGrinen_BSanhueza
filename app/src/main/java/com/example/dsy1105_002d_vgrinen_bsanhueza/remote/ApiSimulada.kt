package com.example.dsy1105_002d_vgrinen_bsanhueza.remote

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.io.IOException

// API simulada: intercepta cada llamada de Retrofit y la responde con ServidorSimulado,
// sin salir a la red. Así la app funciona sin backend y Retrofit, Gson y ApiService
// trabajan igual que contra un servidor real.
class ApiSimulada(
    private val servidor: ServidorSimulado = ServidorSimulado()
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val solicitud = chain.request()

        // Permite probar el banner "Sin conexión" sin apagar la red del equipo
        if (sinConexion) throw IOException("Sin conexión (simulada)")

        // Pequeña espera para que se vea el estado "Cargando", como en una red real
        Thread.sleep(LATENCIA_MS)

        val cuerpo = solicitud.body?.let { body ->
            Buffer().also { body.writeTo(it) }.readUtf8()
        }
        val respuesta = servidor.responder(
            metodo = solicitud.method,
            ruta = solicitud.url.pathSegments,
            cuerpo = cuerpo
        )

        return Response.Builder()
            .request(solicitud)
            .protocol(Protocol.HTTP_1_1)
            .code(respuesta.codigo)
            .message(if (respuesta.codigo in 200..299) "OK" else "Error")
            .body(respuesta.json.toResponseBody(TIPO_JSON))
            .build()
    }

    companion object {
        // true: todas las llamadas fallan como si no hubiera red
        @Volatile
        var sinConexion: Boolean = false

        private const val LATENCIA_MS = 400L
        private val TIPO_JSON = "application/json; charset=utf-8".toMediaType()
    }
}
