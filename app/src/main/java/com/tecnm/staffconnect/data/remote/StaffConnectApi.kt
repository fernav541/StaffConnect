package com.tecnm.staffconnect.data.remote
import retrofit2.http.*

interface StaffConnectApi {
    @GET("vacaciones")
    suspend fun getVacaciones(): List<VacacionDto>

    @POST("vacaciones")
    suspend fun crearVacacion(@Body vacacion: VacacionCreateDto): VacacionDto

    @DELETE("vacaciones/{id}")
    suspend fun eliminarVacacion(@Path("id") id: String): VacacionDto

    @GET("nominas")
    suspend fun getNominas(): List<NominaDto>

    @GET("login")
    suspend fun login(): List<LoginDto>

    @PUT("login/{id}")
    suspend fun actualizarPassword(
        @Path("id") id: String,
        @Body usuario: UsuarioDto
    ): UsuarioDto

    @GET("login")
    suspend fun buscarUsuarioPorEmail(): List<UsuarioDto>
}