package com.tecnm.staffconnect.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NominaDao {

    @Query("SELECT * FROM nominas WHERE usuarioId = :usuarioId")
    fun getAllNominas(usuarioId: String): Flow<List<NominaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNomina(nomina: NominaEntity)

    @Query("DELETE FROM nominas WHERE usuarioId = :usuarioId")
    suspend fun deleteAllByUsuario(usuarioId: String)
}