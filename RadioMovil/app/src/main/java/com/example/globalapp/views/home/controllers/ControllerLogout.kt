package com.example.globalapp.views.home.controllers

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.globalapp.util.Constants

class ControllerLogout {

    var tiempoInicioSuspendido by mutableStateOf(0L)
        private set
    fun validaTiempoSuspendido(tiempoSuspendido:Long):Boolean{
        if(tiempoSuspendido> Constants.Companion.MAX_TIME_SUSPEND){
            Log.i("MainNav:ON_STOP","logout")
            return true;
        }
        return false;
    }

    //SETTERS
    fun updateTiempoInicioSuspendido(tiempoSuspendido: Long){
        tiempoInicioSuspendido = tiempoSuspendido
    }
}