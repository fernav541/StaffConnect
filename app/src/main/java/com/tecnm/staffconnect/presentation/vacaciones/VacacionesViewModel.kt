package com.tecnm.staffconnect.presentation.vacaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecnm.staffconnect.data.local.VacacionDao
import com.tecnm.staffconnect.data.local.VacacionEntity
import com.tecnm.staffconnect.data.remote.StaffConnectApi
import com.tecnm.staffconnect.data.remote.VacacionCreateDto
import com.tecnm.staffconnect.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SyncEstado {
    object Idle : SyncEstado()
    object Cargando : SyncEstado()
    object Exito : SyncEstado()
    data class Error(val mensaje: String) : SyncEstado()
}

@HiltViewModel
class VacacionesViewModel @Inject constructor(
    private val vacacionDao: VacacionDao,
    private val api: StaffConnectApi,
    private val authRepository: AuthRepository
) : ViewModel() {

    private var userId = ""
    private val _vacaciones = MutableStateFlow<List<VacacionEntity>>(emptyList())
    val vacaciones: StateFlow<List<VacacionEntity>> = _vacaciones
    val syncEstado = MutableStateFlow<SyncEstado>(SyncEstado.Idle)

    init {
        viewModelScope.launch {
            userId = authRepository.getUserIdString()
            sincronizarDesdeApi()
            vacacionDao.getAllVacaciones(userId).collect { lista ->
                _vacaciones.value = lista
            }
        }
    }

    fun sincronizarDesdeApi() {
        viewModelScope.launch {
            if (userId.isEmpty()) {
                userId = authRepository.getUserIdString()
            }
            syncEstado.value = SyncEstado.Cargando
            try {
                val vacacionesRemotas = api.getVacaciones()
                val filtradas = vacacionesRemotas.filter {
                    it.usuarioID.trim() == userId.trim()
                }
                // Borra primero los registros locales del usuario
                vacacionDao.deleteAllByUsuario(userId)
                // Inserta los nuevos desde la API
                filtradas.forEach { dto ->
                    vacacionDao.insertVacacion(
                        VacacionEntity(
                            usuarioId = dto.usuarioID,
                            fechaInicio = dto.fechaInicio,
                            fechaFin = dto.fechaFin,
                            motivo = dto.motivo,
                            estado = dto.estado
                        )
                    )
                }
                syncEstado.value = SyncEstado.Exito
            } catch (e: Exception) {
                syncEstado.value = SyncEstado.Error("Sin conexión — mostrando datos locales")
            }
        }
    }

    fun solicitarVacacion(fechaInicio: String, fechaFin: String, motivo: String) {
        viewModelScope.launch {
            android.util.Log.e("STAFFCONNECT_DEBUG", "userId al enviar: '$userId'")

            val dto = VacacionCreateDto(
                usuarioID = userId,
                fechaInicio = fechaInicio,
                fechaFin = fechaFin,
                motivo = motivo,
                estado = "PENDIENTE"
            )
            android.util.Log.e("STAFFCONNECT_DEBUG", "DTO: usuarioID='${dto.usuarioID}'")

            syncEstado.value = SyncEstado.Cargando
            try {
                val response = api.crearVacacion(dto)
                android.util.Log.e("STAFFCONNECT_DEBUG", "Respuesta: usuarioID='${response.usuarioID}' id='${response.id}'")
                vacacionDao.insertVacacion(
                    VacacionEntity(
                        usuarioId = response.usuarioID,
                        fechaInicio = response.fechaInicio,
                        fechaFin = response.fechaFin,
                        motivo = response.motivo,
                        estado = response.estado
                    )
                )
                syncEstado.value = SyncEstado.Exito
            } catch (e: Exception) {
                android.util.Log.e("STAFFCONNECT_DEBUG", "Error: ${e.message}")
                vacacionDao.insertVacacion(
                    VacacionEntity(
                        usuarioId = userId,
                        fechaInicio = fechaInicio,
                        fechaFin = fechaFin,
                        motivo = motivo,
                        estado = "PENDIENTE"
                    )
                )
                syncEstado.value = SyncEstado.Error("Sin internet — guardado localmente")
            }
        }
    }

    fun getUserIdDebug(): String = userId
}