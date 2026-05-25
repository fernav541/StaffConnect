package com.tecnm.staffconnect.domain.model

data class Vacacion(
    val id: Int,
    val fechaInicio: String,
    val fechaFin: String,
    val motivo: String,
    val estado: EstadoVacacion
)

enum class EstadoVacacion {
    PENDIENTE, APROBADO, RECHAZADO
}