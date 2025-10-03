package com.example.globalapp.navegation

sealed class AppScreens(val route:String) {
    object Login: AppScreens("login")
    object Main: AppScreens("main")
    object Detail: AppScreens("detail")
}