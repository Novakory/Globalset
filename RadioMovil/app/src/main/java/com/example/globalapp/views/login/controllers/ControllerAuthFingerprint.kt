package com.example.globalapp.views.login.controllers

import android.util.Log
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.globalapp.util.Constants

class ControllerAuthFingerprint {
    var authPurpose by mutableStateOf(AuthPurpose.NONE)
        private set
    var canAutenticate by mutableStateOf(false)
        private set
    var isAutenticate by mutableStateOf(false)
        private set
    lateinit var promptInfo: BiometricPrompt.PromptInfo
    lateinit var biometricPrompt: BiometricPrompt
    fun showDialogAutenticate(purpose: AuthPurpose){
        authPurpose = purpose
        biometricPrompt.authenticate(promptInfo)
    }

    //SETTERS
    fun updateCanAutenticate(canAutenticate:Boolean){
        this.canAutenticate = canAutenticate
    }

    fun updateIsAutenticate(isAutenticate:Boolean){
        this.isAutenticate = isAutenticate
    }

    fun updateAuthPurpose(purpose: AuthPurpose) {
        authPurpose = purpose
    }
    fun setInCanAutenticate(canAutenticate:Boolean){
        this.canAutenticate = canAutenticate
    }

    fun setInIsAutenticate(isAutenticate:Boolean){
        this.isAutenticate = isAutenticate
    }
}

enum class AuthPurpose {
    LOGIN,
    AUTORIZAR_PROPUESTAS,
    RECHAZAR_PROPUESTAS,
    NONE
}