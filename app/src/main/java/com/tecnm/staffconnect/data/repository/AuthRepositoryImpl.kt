package com.tecnm.staffconnect.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tecnm.staffconnect.data.remote.AuthApi
import com.tecnm.staffconnect.domain.model.Usuario
import com.tecnm.staffconnect.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val dataStore: DataStore<Preferences>
) : AuthRepository {

    companion object {
        val TOKEN_KEY = stringPreferencesKey("token")
        val USER_ID_KEY = intPreferencesKey("user_id")
        val USER_ID_STRING_KEY = stringPreferencesKey("user_id_string")
        val USER_NOMBRE_KEY = stringPreferencesKey("user_nombre")
        val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        val USER_DEPARTAMENTO_KEY = stringPreferencesKey("user_departamento")
    }

    override suspend fun login(email: String, password: String): Result<Usuario> {
        return try {
            val usuarios = authApi.getUsuarios()
            val usuarioEncontrado = usuarios.find {
                it.email.trim().lowercase() == email.trim().lowercase() &&
                        it.password.trim() == password.trim()
            }
            if (usuarioEncontrado != null) {
                dataStore.edit { prefs ->
                    prefs[TOKEN_KEY] = usuarioEncontrado.token
                    prefs[USER_ID_KEY] = usuarioEncontrado.id.toIntOrNull() ?: 1
                    prefs[USER_ID_STRING_KEY] = usuarioEncontrado.id
                    prefs[USER_NOMBRE_KEY] = usuarioEncontrado.nombre
                    prefs[USER_EMAIL_KEY] = usuarioEncontrado.email
                    prefs[USER_DEPARTAMENTO_KEY] = usuarioEncontrado.departamento
                }
                Result.success(
                    Usuario(
                        id = usuarioEncontrado.id.toIntOrNull() ?: 1,
                        nombre = usuarioEncontrado.nombre,
                        email = usuarioEncontrado.email,
                        puesto = "Empleado",
                        departamento = usuarioEncontrado.departamento
                    )
                )
            } else {
                Result.failure(Exception("Correo o contraseña incorrectos"))
            }
        } catch (e: Exception) {
            // Sin internet — intenta usar sesión guardada
            val prefs = dataStore.data.first()
            val token = prefs[TOKEN_KEY]
            val emailGuardado = prefs[USER_EMAIL_KEY]

            if (token != null && emailGuardado?.trim()?.lowercase() == email.trim().lowercase()) {
                // Hay sesión guardada para este usuario
                Result.success(
                    Usuario(
                        id = prefs[USER_ID_KEY] ?: 1,
                        nombre = prefs[USER_NOMBRE_KEY] ?: "",
                        email = emailGuardado,
                        puesto = "Empleado",
                        departamento = prefs[USER_DEPARTAMENTO_KEY] ?: ""
                    )
                )
            } else {
                Result.failure(Exception("Sin conexión — inicia sesión con internet al menos una vez"))
            }
        }
    }

    override suspend fun logout() {
        dataStore.edit { prefs -> prefs.clear() }
    }

    override suspend fun getUsuarioActual(): Usuario? {
        val prefs = dataStore.data.first()
        val id = prefs[USER_ID_KEY] ?: return null
        return Usuario(
            id = id,
            nombre = prefs[USER_NOMBRE_KEY] ?: "",
            email = prefs[USER_EMAIL_KEY] ?: "",
            puesto = "Empleado",
            departamento = prefs[USER_DEPARTAMENTO_KEY] ?: ""
        )
    }

    override suspend fun getUserIdString(): String {
        val prefs = dataStore.data.first()
        return prefs[USER_ID_STRING_KEY] ?: "1"
    }
}