package com.tecnm.staffconnect.presentation.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecnm.staffconnect.data.remote.StaffConnectApi
import com.tecnm.staffconnect.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeResumen(
    val nombreUsuario: String = "",
    val ultimoSalario: String = "$0.00",
    val solicitudesPendientes: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: StaffConnectApi,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _resumen = MutableStateFlow(HomeResumen())
    val resumen: StateFlow<HomeResumen> = _resumen

    init {
        cargarResumen()
    }

    fun cargarResumen() {
        viewModelScope.launch {
            try {
                val userId = authRepository.getUserIdString()
                val usuario = authRepository.getUsuarioActual()

                // Obtiene nóminas y filtra por usuario
                val nominas = api.getNominas().filter { it.id.trim() == userId.trim() }

                // Obtiene el monto más reciente
                val ultimoSalario = nominas.lastOrNull()?.monto ?: "0.00"

                // Obtiene vacaciones y cuenta pendientes
                val vacaciones = api.getVacaciones().filter {
                    it.usuarioID.trim() == userId.trim()
                }
                val pendientes = vacaciones.count {
                    it.estado.uppercase() == "PENDIENTE"
                }

                _resumen.value = HomeResumen(
                    nombreUsuario = usuario?.nombre ?: "Empleado",
                    ultimoSalario = "$$ultimoSalario",
                    solicitudesPendientes = pendientes
                )
            } catch (e: Exception) {
                // Si falla la API mantiene valores por defecto
            }
        }
    }
}