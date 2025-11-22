package com.example.globalapp.retrofit

import com.example.globalapp.models.retrofit.AutorizarPropuestasRequest
import com.example.globalapp.models.retrofit.AutorizarPropuestasResponse
import com.example.globalapp.models.retrofit.GenericResponse
import com.example.globalapp.models.retrofit.LoginRequest
import com.example.globalapp.models.retrofit.LoginResponse
import com.example.globalapp.models.retrofit.PropuestasResponse
import com.example.globalapp.models.retrofit.RechazarPropuestasRequest
import com.example.globalapp.util.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface LoginClient {
    @Headers("Content-Type: application/json")
    @POST("${Constants.SUBBASE_URL}login")
    //suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @Headers("Content-Type: application/json")
    @GET("${Constants.SUBBASE_URL}propuestas")
    suspend fun getPropuestas(@Header("Authorization") token: String): Response<List<PropuestasResponse>>


    @Headers("Content-Type: application/json")
    @GET("${Constants.SUBBASE_URL}propuestas/user/{clave_usuario}")
    suspend fun getPropuestasByUser(@Header("Authorization") token: String,@Path("clave_usuario") claveUsuario:String): Response<List<PropuestasResponse>>

    @Headers("Content-Type: application/json")
    @GET("${Constants.SUBBASE_URL}propuestas-pendientes/user/{clave_usuario}")
    suspend fun getPropuestasPendientesByUser(@Header("Authorization") token: String,@Path("clave_usuario") claveUsuario:String): Response<List<PropuestasResponse>>


    @Headers("Content-Type: application/json")
    @POST("${Constants.SUBBASE_URL}autorizar-propuestas")
    suspend fun autorizarPropuestas(@Header("Authorization") token: String,@Body autorizarPropuestasRequest: AutorizarPropuestasRequest): Response<AutorizarPropuestasResponse>

    @Headers("Content-Type: application/json")
    @POST("${Constants.SUBBASE_URL}desautorizar-propuestas")
    suspend fun desautorizarPropuestas(@Header("Authorization") token: String,@Body autorizarPropuestasRequest: AutorizarPropuestasRequest): Response<AutorizarPropuestasResponse>

    @Headers("Content-Type: application/json")
    @POST("${Constants.SUBBASE_URL}rechazar-propuestas")
    suspend fun rechazarPropuestas(@Header("Authorization") token: String,@Body rechazarPropuestasRequest: RechazarPropuestasRequest): Response<GenericResponse>

}