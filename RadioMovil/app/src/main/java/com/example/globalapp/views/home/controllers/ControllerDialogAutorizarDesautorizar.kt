package com.example.globalapp.views.home.controllers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.globalapp.models.controllers.ProgressBarModel
import com.example.globalapp.models.retrofit.AutorizarPropuestasResponse

class ControllerDialogAutorizarDesautorizar {
    var showDialog by mutableStateOf(false)
        private set

    var txtPasswordDialog by mutableStateOf("")
        private set

    var progressbarState by mutableStateOf(ProgressBarModel(false, ""))
        private set

    var autorizarPropuestasResponse by mutableStateOf(AutorizarPropuestasResponse(null, ""))
        private set

    //SETTERS
    fun updateShowDialog(show: Boolean) {
        showDialog = show
        if(showDialog){
            updateResponse(null,"")
        }
    }

    fun updateTxtPassword(value: String) {
        txtPasswordDialog = value
    }

    fun updateProgressbarState(isLoading:Boolean,message:String){
        progressbarState = progressbarState.copy(isLoading, message)
    }

    fun updateResponse(success:Boolean?,message:String){
        autorizarPropuestasResponse = autorizarPropuestasResponse.copy(
            SUCCESS = success,
            MESSAGE = message
        )
    }

}