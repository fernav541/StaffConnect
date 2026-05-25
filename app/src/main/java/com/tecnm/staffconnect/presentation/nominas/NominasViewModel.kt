package com.tecnm.staffconnect.presentation.nominas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecnm.staffconnect.data.local.NominaDao
import com.tecnm.staffconnect.data.local.NominaEntity
import com.tecnm.staffconnect.data.remote.NominaDto
import com.tecnm.staffconnect.data.remote.StaffConnectApi
import com.tecnm.staffconnect.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class NominasEstado {
    object Cargando : NominasEstado()
    data class Exito(val nominas: List<NominaDto>) : NominasEstado()
    data class Error(val mensaje: String) : NominasEstado()
}

@HiltViewModel
class NominasViewModel @Inject constructor(
    private val api: StaffConnectApi,
    private val authRepository: AuthRepository,
    private val nominaDao: NominaDao
) : ViewModel() {

    private val _estado = MutableStateFlow<NominasEstado>(NominasEstado.Cargando)
    val estado: StateFlow<NominasEstado> = _estado

    init {
        cargarNominas()
    }

    fun cargarNominas() {
        viewModelScope.launch {
            _estado.value = NominasEstado.Cargando
            val userId = authRepository.getUserIdString()
            try {
                // Intenta cargar desde API
                val todasLasNominas = withContext(Dispatchers.IO) {
                    api.getNominas()
                }
                val nominasDelUsuario = todasLasNominas.filter { nomina ->
                    nomina.id.trim() == userId.trim()
                }

                // Guarda en Room para modo offline
                nominaDao.deleteAllByUsuario(userId)
                nominasDelUsuario.forEach { dto ->
                    nominaDao.insertNomina(
                        NominaEntity(
                            usuarioId = userId,
                            periodo = dto.periodo,
                            monto = dto.monto,
                            fecha = dto.fecha,
                            salarioBruto = dto.salarioBruto,
                            isr = dto.isr,
                            imss = dto.imss,
                            otrasDeduciones = dto.otrasDeduciones,
                            diasTrabajados = dto.diasTrabajados
                        )
                    )
                }
                _estado.value = NominasEstado.Exito(nominasDelUsuario)
            } catch (e: Exception) {
                // Sin internet — carga desde Room
                nominaDao.getAllNominas(userId).collect { nominasLocales ->
                    if (nominasLocales.isNotEmpty()) {
                        val dtos = nominasLocales.map { entity ->
                            NominaDto(
                                id = entity.usuarioId,
                                periodo = entity.periodo,
                                monto = entity.monto,
                                fecha = entity.fecha,
                                salarioBruto = entity.salarioBruto,
                                isr = entity.isr,
                                imss = entity.imss,
                                otrasDeduciones = entity.otrasDeduciones,
                                diasTrabajados = entity.diasTrabajados
                            )
                        }
                        _estado.value = NominasEstado.Exito(dtos)
                    } else {
                        _estado.value = NominasEstado.Error(
                            "Sin conexión — no hay datos guardados localmente"
                        )
                    }
                    return@collect
                }
            }
        }
    }
}