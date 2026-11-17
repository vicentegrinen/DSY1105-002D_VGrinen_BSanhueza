package com.example.dsy1105_002d_vgrinen_bsanhueza.remote

import com.example.dsy1105_002d_vgrinen_bsanhueza.model.AccionEvento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Camara
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Evento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Indicadores
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UsuarioPrueba
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Endpoints de la API REST de Modo Guardián (Guía 14).
// Las rutas son relativas a la baseUrl definida en RetrofitInstance.
// Si la respuesta no es 2xx, Retrofit lanza HttpException; si no hay red, IOException.
interface ApiService {

    // Lista de eventos y alertas, del más reciente al más antiguo
    @GET("eventos")
    suspend fun obtenerEventos(): List<Evento>

    // Detalle de un evento con su historial de acciones
    @GET("eventos/{id}")
    suspend fun obtenerEvento(@Path("id") id: String): Evento

    // Registra una acción (quién y qué); la API responde la acción con su id y fecha/hora
    @POST("eventos/{id}/acciones")
    suspend fun registrarAccion(
        @Path("id") eventoId: String,
        @Body accion: NuevaAccionRequest
    ): AccionEvento

    // Cambia el estado de atención; la API responde el evento actualizado
    @PUT("eventos/{id}/estado")
    suspend fun cambiarEstado(
        @Path("id") eventoId: String,
        @Body cambio: CambioEstadoRequest
    ): Evento

    // Cámaras simuladas con su estado (en línea / sin señal)
    @GET("camaras")
    suspend fun obtenerCamaras(): List<Camara>

    // Indicadores del panel operacional
    @GET("indicadores")
    suspend fun obtenerIndicadores(): Indicadores

    // Usuarios de prueba y sus roles (pantalla del Administrador)
    @GET("usuarios")
    suspend fun obtenerUsuarios(): List<UsuarioPrueba>

    // Cambia el rol de un usuario de prueba; la API responde el usuario actualizado
    @PUT("usuarios/{id}/rol")
    suspend fun cambiarRol(
        @Path("id") usuarioId: Int,
        @Body cambio: CambioRolRequest
    ): UsuarioPrueba
}
