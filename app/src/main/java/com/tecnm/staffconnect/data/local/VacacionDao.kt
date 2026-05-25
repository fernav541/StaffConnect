package com.tecnm.staffconnect.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VacacionDao {

    @Query("SELECT * FROM vacaciones WHERE usuarioId = :usuarioId")
    fun getAllVacaciones(usuarioId: String): Flow<List<VacacionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVacacion(vacacion: VacacionEntity)

    @Query("DELETE FROM vacaciones WHERE usuarioId = :usuarioId")
    suspend fun deleteAllByUsuario(usuarioId: String)
}