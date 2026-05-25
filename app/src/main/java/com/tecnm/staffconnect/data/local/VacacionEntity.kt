package com.tecnm.staffconnect.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vacaciones")
data class VacacionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val usuarioId: String = "",

    val fechaInicio: String,

    val fechaFin: String,

    val motivo: String,

    val estado: String = "PENDIENTE"
)