package com.example.globalapp.models.retrofit

import com.example.globalapp.models.controllers.Usuario

data class LoginResponse(
    val SUCCESS: Boolean,
    val MESSAGE: String,
    val USUARIO: Usuario?,
    val TOKEN: String?
)


data class LoginRequest(
    val user: String,
    val password: String
)