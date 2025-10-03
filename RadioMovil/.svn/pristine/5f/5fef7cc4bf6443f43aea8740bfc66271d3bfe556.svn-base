package com.example.globalapp.retrofit

import android.util.Log
import com.example.globalapp.models.retrofit.AutorizarPropuestasRequest
import com.example.globalapp.models.retrofit.AutorizarPropuestasResponse
import com.example.globalapp.models.retrofit.LoginRequest
import com.example.globalapp.models.retrofit.LoginResponse
import com.example.globalapp.models.retrofit.PropuestasResponse
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

    suspend fun getPropuestas():List<PropuestasResponse>?{
        val response = loginClient.getPropuestas()
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
    suspend fun getPropuestasByUser(claveUsuario:String):List<PropuestasResponse>?{
        val response = loginClient.getPropuestasByUser(claveUsuario)
        if(response.isSuccessful)  return response.body()
        else {
            val errorResponse = response.errorBody()?.string()
            Log.e("api:response", "Error al obtener propuestas: $errorResponse")
            return emptyList<PropuestasResponse>();
        }
    }

    suspend fun getPropuestasPendientesByUser(claveUsuario:String):List<PropuestasResponse>?{
        val response = loginClient.getPropuestasPendientesByUser(claveUsuario)
        if(response.isSuccessful)  return response.body()
        else {
            val errorResponse = response.errorBody()?.string()
            Log.e("api:response", "Error al obtener propuestas: $errorResponse")
            return emptyList<PropuestasResponse>();
        }
    }

    suspend fun autorizarPropuestas(autorizarPropuestasRequest: AutorizarPropuestasRequest): AutorizarPropuestasResponse?{
        val response = loginClient.autorizarPropuestas(autorizarPropuestasRequest)
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

    suspend fun desautorizarPropuestas(autorizarPropuestasRequest: AutorizarPropuestasRequest): AutorizarPropuestasResponse?{
        val response = loginClient.desautorizarPropuestas(autorizarPropuestasRequest)
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
}