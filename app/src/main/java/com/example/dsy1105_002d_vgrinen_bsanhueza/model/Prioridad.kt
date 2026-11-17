package com.example.dsy1105_002d_vgrinen_bsanhueza.model

import com.google.gson.annotations.SerializedName

// Prioridad de un evento. @SerializedName es el valor que viaja en el JSON de la API;
// "etiqueta" es el texto que se muestra en pantalla (el color lo define ui/theme).
enum class Prioridad(val etiqueta: String) {
    @SerializedName("critica")
    CRITICA("Crítica"),

    @SerializedName("media")
    MEDIA("Media"),

    @SerializedName("baja")
    BAJA("Baja")
}
