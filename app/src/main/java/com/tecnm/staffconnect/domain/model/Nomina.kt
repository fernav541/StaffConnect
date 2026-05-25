package com.tecnm.staffconnect.domain.model

data class Nomina(
    val id: Int,
    val periodo: String,
    val salarioNeto: Double,
    val fechaPago: String,
    val urlPdf: String
)