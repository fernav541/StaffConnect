package com.tecnm.staffconnect.domain.repository

import com.tecnm.staffconnect.domain.model.Vacacion
import kotlinx.coroutines.flow.Flow

interface VacacionRepository {
    fun getVacaciones(): Flow<List<Vacacion>>
    suspend fun solicitarVacacion(vacacion: Vacacion): Result<Unit>
}