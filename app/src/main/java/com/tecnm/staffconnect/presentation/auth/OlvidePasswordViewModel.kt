package com.tecnm.staffconnect.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecnm.staffconnect.data.remote.AuthApi
import com.tecnm.staffconnect.data.remote.UsuarioDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OlvidePasswordState {
    object Idle : OlvidePasswordState()
    object Cargando : OlvidePasswordState()
    object Exito : OlvidePasswordState()
    data class Error(val mensaje: String) : OlvidePasswordState()
}

@HiltViewModel
class OlvidePasswordViewModel @Inject constructor(
    private val authApi: AuthApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<OlvidePasswordState>(OlvidePasswordState.Idle)
    val uiState: StateFlow<OlvidePasswordState> = _uiState

    fun cambiarPassword(email: String, nuevaPassword: String) {
        viewModelScope.launch {
            _uiState.value = OlvidePasswordState.Cargando
            try {
                // Busca el usuario por email
                val usuarios = authApi.getUsuarios()
                val usuario = usuarios.find {
                    it.email.trim().lowercase() == email.trim().lowercase()
                }

                if (usuario != null) {
                    // Actualiza la contraseña en la API
                    authApi.actualizarPassword(
                        id = usuario.id,
                        usuario = UsuarioDto(
                            id = usuario.id,
                            nombre = usuario.nombre,
                            email = usuario.email,
                            password = nuevaPassword,
                            departamento = usuario.departamento,
                            token = usuario.token
                        )
                    )
                    _uiState.value = OlvidePasswordState.Exito
                } else {
                    _uiState.value = OlvidePasswordState.Error(
                        "No existe una cuenta con ese correo"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = OlvidePasswordState.Error(
                    "Error de conexión: ${e.message}"
                )
            }
        }
    }
}