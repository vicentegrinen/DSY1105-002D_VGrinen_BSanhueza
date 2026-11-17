package com.example.dsy1105_002d_vgrinen_bsanhueza.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Punto único de configuración de Retrofit (Guía 14).
object RetrofitInstance {

    // Único lugar donde se define la URL del backend.
    // Desde el emulador, 10.0.2.2 es el "localhost" del PC. Cambia el puerto si tu backend usa otro.
    private const val BASE_URL = "http://10.0.2.2:3000/"

    // true: responde ApiSimulada dentro de la app (no hace falta levantar un servidor).
    // false: las llamadas salen por red hacia BASE_URL.
    private const val USAR_API_SIMULADA = true

    private val cliente: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
        if (USAR_API_SIMULADA) {
            builder.addInterceptor(ApiSimulada())
        }
        builder.build()
    }

    // lazy: Retrofit se construye recién la primera vez que se usa "api"
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
