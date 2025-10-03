package com.example.globalapp.models.retrofit

data class LoginResponse(
    val SUCCESS:Boolean,
    val ERROR_MESSAGE:String,
    val ID_USUARIO:Int
)


data class LoginRequest(
    val user: String,
    val password: String
)