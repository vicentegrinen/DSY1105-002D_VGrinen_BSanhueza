package com.example.dsy1105_002d_vgrinen_bsanhueza.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.AccionEvento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Camara
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.EstadoAtencion
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Evento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Indicadores
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Prioridad
import com.google.gson.Gson

// Tablas de la copia local de Modo Guardián: guardan lo último que respondió la API
// para mostrarlo cuando no hay conexión ("Sin conexión. Mostrando datos guardados.").

@Entity(tableName = "eventos_cache")
data class EventoCacheEntity(
    @PrimaryKey val id: String,
    val titulo: String,
    val camaraId: String,
    val camaraNombre: String,
    val ubicacionReferencial: String,
    val prioridad: String,     // nombre del enum Prioridad
    val fechaHora: String,
    val estado: String,        // nombre del enum EstadoAtencion
    val accionesJson: String   // historial de acciones como texto JSON
)

@Entity(tableName = "camaras_cache")
data class CamaraCacheEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val enLinea: Boolean,
    val ultimaSenal: String?,
    val alertasPendientes: Int
)

// Tabla de una sola fila (id fijo) con los indicadores del panel
@Entity(tableName = "indicadores_cache")
data class IndicadoresCacheEntity(
    @PrimaryKey val id: Int = 0,
    val eventosHoy: Int,
    val alertasPendientes: Int,
    val atendidas: Int,
    val minutosRespuestaPromedio: Int
)

// ---- Conversión entre los modelos de la app y las tablas ----

private val gson = Gson()

fun Evento.aEntidad() = EventoCacheEntity(
    id = id,
    titulo = titulo,
    camaraId = camaraId,
    camaraNombre = camaraNombre,
    ubicacionReferencial = ubicacionReferencial,
    prioridad = prioridad.name,
    fechaHora = fechaHora,
    estado = estado.name,
    accionesJson = gson.toJson(acciones)
)

fun EventoCacheEntity.aModelo() = Evento(
    id = id,
    titulo = titulo,
    camaraId = camaraId,
    camaraNombre = camaraNombre,
    ubicacionReferencial = ubicacionReferencial,
    prioridad = Prioridad.valueOf(prioridad),
    fechaHora = fechaHora,
    estado = EstadoAtencion.valueOf(estado),
    acciones = gson.fromJson(accionesJson, Array<AccionEvento>::class.java).toList()
)

fun Camara.aEntidad() = CamaraCacheEntity(id, nombre, enLinea, ultimaSenal, alertasPendientes)

fun CamaraCacheEntity.aModelo() = Camara(id, nombre, enLinea, ultimaSenal, alertasPendientes)

fun Indicadores.aEntidad() = IndicadoresCacheEntity(
    eventosHoy = eventosHoy,
    alertasPendientes = alertasPendientes,
    atendidas = atendidas,
    minutosRespuestaPromedio = minutosRespuestaPromedio
)

fun IndicadoresCacheEntity.aModelo() =
    Indicadores(eventosHoy, alertasPendientes, atendidas, minutosRespuestaPromedio)
