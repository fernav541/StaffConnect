package com.tecnm.staffconnect.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthApi {

    @GET("login")
    suspend fun getUsuarios(): List<UsuarioDto>

    @PUT("login/{id}")
    suspend fun actualizarPassword(
        @Path("id") id: String,
        @Body usuario: UsuarioDto
    ): UsuarioDto
}