package com.tecnm.staffconnect.domain.model

data class Usuario(
    val id: Int,
    val nombre: String,
    val email: String,
    val puesto: String,
    val departamento: String
)