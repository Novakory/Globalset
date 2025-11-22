package com.example.globalapp.retrofit

import android.util.Log
import com.example.globalapp.models.retrofit.AutorizarPropuestasRequest
import com.example.globalapp.models.retrofit.AutorizarPropuestasResponse
import com.example.globalapp.models.retrofit.GenericResponse
import com.example.globalapp.models.retrofit.LoginRequest
import com.example.globalapp.models.retrofit.LoginResponse
import com.example.globalapp.models.retrofit.PropuestasResponse
import com.example.globalapp.models.retrofit.RechazarPropuestasRequest
import com.google.gson.Gson
import javax.inject.Inject

class LoginRepository @Inject constructor(private val loginClient: LoginClient){
    suspend fun login(user:String,password:String): LoginResponse?{
        val response = loginClient.login(LoginRequest(user,password))
        //Log.i("login:response",response.toString())
        if(response.isSuccessful)  return response.body()
        else {
            val errorResponse = response.errorBody()?.string()
            //Log.e("login:response", "Error al hacer login: $errorResponse")
            //return response.errorBody().
            return Gson().fromJson(errorResponse, LoginResponse::class.java);
        }
    }

    suspend fun getPropuestas(token: String):List<PropuestasResponse>?{
        val response = loginClient.getPropuestas(token)
        //Log.i("login:response",response.toString())
        if(response.isSuccessful)  return response.body()
        else {
            val errorResponse = response.errorBody()?.string()
            //Log.e("login:response", "Error al hacer login: $errorResponse")
            //return response.errorBody().
//            return Gson().fromJson(errorResponse, LoginResponse::class.java);
            return emptyList<PropuestasResponse>();
        }
    }
    suspend fun getPropuestasByUser(token:String,claveUsuario:String):List<PropuestasResponse>?{
        val response = loginClient.getPropuestasByUser(token,claveUsuario)
        if(response.isSuccessful)  return response.body()
        else {
            val errorResponse = response.errorBody()?.string()
            Log.e("api:response", "Error al obtener propuestas: $errorResponse")
            return emptyList<PropuestasResponse>();
        }
    }

    suspend fun getPropuestasPendientesByUser(token:String, claveUsuario:String):List<PropuestasResponse>?{
        val response = loginClient.getPropuestasPendientesByUser(token,claveUsuario)
        if(response.isSuccessful)  return response.body()
        else {
            val errorResponse = response.errorBody()?.string()
            Log.e("api:response", "Error al obtener propuestas: $errorResponse")
            return emptyList<PropuestasResponse>();
        }
    }

    suspend fun autorizarPropuestas(token:String, autorizarPropuestasRequest: AutorizarPropuestasRequest): AutorizarPropuestasResponse?{
        val response = loginClient.autorizarPropuestas(token,autorizarPropuestasRequest)
        if(response.isSuccessful)  {
            Log.e("api:response","OK ${response.body()}")
            return response.body()
        }
        else {
            val errorResponse = response.errorBody()?.string()
            Log.e("api:response",errorResponse!!)
            return Gson().fromJson(errorResponse, AutorizarPropuestasResponse::class.java);
        }
    }

    suspend fun desautorizarPropuestas(token:String, autorizarPropuestasRequest: AutorizarPropuestasRequest): AutorizarPropuestasResponse?{
        val response = loginClient.desautorizarPropuestas(token,autorizarPropuestasRequest)
        if(response.isSuccessful)  {
            Log.e("api:response","OK ${response.body()}")
            return response.body()
        }
        else {
            val errorResponse = response.errorBody()?.string()
            Log.e("api:response",errorResponse!!)
            return Gson().fromJson(errorResponse, AutorizarPropuestasResponse::class.java);
        }
    }

    suspend fun rechazarPropuestas(token:String, rechazarPropuestasRequest: RechazarPropuestasRequest): GenericResponse?{
        val response = loginClient.rechazarPropuestas(token,rechazarPropuestasRequest)
        if(response.isSuccessful)  {
            Log.e("api:response","OK ${response.body()}")
            return response.body()
        }
        else {
            val errorResponse = response.errorBody()?.string()
            Log.e("api:response",errorResponse!!)
            return Gson().fromJson(errorResponse, GenericResponse::class.java);
        }
    }
}