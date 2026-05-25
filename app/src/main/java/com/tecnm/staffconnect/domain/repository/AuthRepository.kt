package com.tecnm.staffconnect.domain.repository

import com.tecnm.staffconnect.domain.model.Usuario

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Usuario>
    suspend fun logout()
    suspend fun getUsuarioActual(): Usuario?
    suspend fun getUserIdString(): String
}