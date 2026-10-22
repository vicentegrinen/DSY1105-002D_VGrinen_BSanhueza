package com.example.dsy1105_002d_vgrinen_bsanhueza.data

import androidx.room.TypeConverter
import java.util.Date

// Room no sabe guardar Date, así que se guarda como Long (milisegundos)
// y se reconstruye al leer.
class Converters {
    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.time

    @TypeConverter
    fun toDate(millis: Long?): Date? = millis?.let { Date(it) }
}