package com.tecnm.staffconnect.data.remote

import com.google.gson.annotations.SerializedName

data class VacacionDto(
    @SerializedName("id_sol") val id: String = "",
    @SerializedName("usuarioID") val usuarioID: String = "",
    @SerializedName("fechaInicio") val fechaInicio: String = "",
    @SerializedName("fechaFin") val fechaFin: String = "",
    @SerializedName("motivo") val motivo: String = "",
    @SerializedName("estado") val estado: String = "PENDIENTE"
)

data class NominaDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("periodo") val periodo: String = "",
    @SerializedName("monto") val monto: String = "",
    @SerializedName("fecha") val fecha: String = "",
    @SerializedName("salarioBruto") val salarioBruto: String = "",
    @SerializedName("isr") val isr: String = "",
    @SerializedName("imss") val imss: String = "",
    @SerializedName("imss_nss") val imssNss: Long = 0,
    @SerializedName("otrasDeduciones") val otrasDeduciones: String = "",
    @SerializedName("diasTrabajados") val diasTrabajados: Int = 0
)

data class UsuarioDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("password") val password: String = "",
    @SerializedName("departamento") val departamento: String = "",
    @SerializedName("token") val token: String = "",

)

data class LoginDto(
    val id: String,
    val nombre: String,
    val email: String,
    val password: String,
    val puesto: String,
    val departamento: String
)

data class VacacionCreateDto(
    @SerializedName("usuarioID") val usuarioID: String = "",
    @SerializedName("fechaInicio") val fechaInicio: String = "",
    @SerializedName("fechaFin") val fechaFin: String = "",
    @SerializedName("motivo") val motivo: String = "",
    @SerializedName("estado") val estado: String = "PENDIENTE"
)

fun parseNominas(json: String): List<NominaDto> {
    return try {
        com.google.gson.Gson().fromJson(
            json,
            object : com.google.gson.reflect.TypeToken<List<NominaDto>>() {}.type
        )
    } catch (e: Exception) {
        emptyList()
    }
}



