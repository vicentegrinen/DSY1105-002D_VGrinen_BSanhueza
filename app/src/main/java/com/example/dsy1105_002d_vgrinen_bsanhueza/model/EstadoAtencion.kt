package com.example.dsy1105_002d_vgrinen_bsanhueza.model

import com.google.gson.annotations.SerializedName

// Estado de atención de un evento: pendiente -> en atención -> atendida.
enum class EstadoAtencion(val etiqueta: String) {
    @SerializedName("pendiente")
    PENDIENTE("Pendiente"),

    @SerializedName("en_atencion")
    EN_ATENCION("En atención"),

    @SerializedName("atendida")
    ATENDIDA("Atendida")
}
