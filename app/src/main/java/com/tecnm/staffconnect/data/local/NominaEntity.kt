package com.tecnm.staffconnect.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nominas")
data class NominaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val usuarioId: String = "",
    val periodo: String = "",
    val monto: String = "",
    val fecha: String = "",
    val salarioBruto: String = "",
    val isr: String = "",
    val imss: String = "",
    val otrasDeduciones: String = "",
    val diasTrabajados: Int = 0
)