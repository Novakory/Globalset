package com.example.globalapp.views.home.controllers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.globalapp.models.controllers.ProgressBarModel
import com.example.globalapp.models.retrofit.GenericResponse

class ControllerDialogRechazar {
    var showDialog by mutableStateOf(false)
        private set

    var txtPasswordDialog by mutableStateOf("")
        private set

    var txtMotivoRechazo by mutableStateOf("")
        private set

    var progressbarState by mutableStateOf(ProgressBarModel(false, ""))
        private set

    var responseService by mutableStateOf(GenericResponse(null, ""))
        private set


    //SETTERS
    fun updateShowDialog(show: Boolean) {
        showDialog = show
    }
    fun updateTxtPassword(value: String) {
        txtPasswordDialog = value
    }
    fun updateTxtMotivoRechazo(value: String) {
        txtMotivoRechazo = value
    }

    fun updateProgressbarState(isLoading:Boolean,message:String){
        progressbarState = progressbarState.copy(isLoading, message)
    }

    fun updateResponse(success:Boolean?,message:String){
        responseService = responseService.copy(
            SUCCESS = success,
            MESSAGE = message
        )
    }

    fun resetVariables(){
        updateProgressbarState(false,"")
        updateShowDialog(false)
        updateTxtPassword("")
        updateTxtMotivoRechazo("")
    }
}