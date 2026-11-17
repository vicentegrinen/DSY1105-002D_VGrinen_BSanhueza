package com.example.dsy1105_002d_vgrinen_bsanhueza.repository

import com.example.dsy1105_002d_vgrinen_bsanhueza.data.GuardianCacheDao
import com.example.dsy1105_002d_vgrinen_bsanhueza.data.aEntidad
import com.example.dsy1105_002d_vgrinen_bsanhueza.data.aModelo
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.AccionEvento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.EstadoAtencion
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Evento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Indicadores
import com.example.dsy1105_002d_vgrinen_bsanhueza.remote.ApiService
import com.example.dsy1105_002d_vgrinen_bsanhueza.remote.CambioEstadoRequest
import com.example.dsy1105_002d_vgrinen_bsanhueza.remote.NuevaAccionRequest

// Mediador entre la API REST y los ViewModels de eventos.
// ApiService llega por constructor para poder reemplazarlo por un mock en las pruebas.
// "cache" es opcional: si se entrega, cada respuesta correcta se guarda en Room para
// poder mostrarla sin conexión.
// Los errores de red (IOException) y de servidor (HttpException) no se capturan aquí:
// el ViewModel los convierte en el estado de error que ve el usuario.
class EventoRepository(
    private val api: ApiService,
    private val cache: GuardianCacheDao? = null
) {

    suspend fun obtenerEventos(): List<Evento> {
        val eventos = api.obtenerEventos()
        cache?.reemplazarEventos(eventos.map { it.aEntidad() })
        return eventos
    }

    // Última lista guardada en el teléfono (vacía si nunca se cargó)
    suspend fun eventosGuardados(): List<Evento> =
        cache?.obtenerEventos()?.map { it.aModelo() } ?: emptyList()

    suspend fun obtenerEvento(id: String): Evento = api.obtenerEvento(id)

    suspend fun obtenerIndicadores(): Indicadores {
        val indicadores = api.obtenerIndicadores()
        cache?.guardarIndicadores(indicadores.aEntidad())
        return indicadores
    }

    // Últimos indicadores guardados (null si nunca se cargaron)
    suspend fun indicadoresGuardados(): Indicadores? = cache?.obtenerIndicadores()?.aModelo()

    // POST: deja registro de quién hizo qué; el "cuándo" lo asigna la API
    suspend fun registrarAccion(
        eventoId: String,
        usuario: String,
        descripcion: String
    ): AccionEvento =
        api.registrarAccion(eventoId, NuevaAccionRequest(usuario.trim(), descripcion.trim()))

    // PUT: cambia el estado de atención y devuelve el evento actualizado
    suspend fun cambiarEstado(eventoId: String, estado: EstadoAtencion): Evento =
        api.cambiarEstado(eventoId, CambioEstadoRequest(estado))
}
