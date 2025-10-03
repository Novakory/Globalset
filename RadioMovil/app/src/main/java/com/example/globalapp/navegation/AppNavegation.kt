package com.example.globalapp.navegation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.globalapp.models.controllers.DetallesPropuesta
import com.example.globalapp.navegation.AppScreens
import com.example.globalapp.viewModels.ControllerDetallePropuesta
import com.example.globalapp.viewModels.ControllerLogin
import com.example.globalapp.viewModels.ControllerPropuestas
import com.example.globalapp.views.DetailNav
import com.example.globalapp.views.LoginNav
import com.example.globalapp.views.MainNav

@Composable
fun AppNavegation(viewModelLogin: ControllerLogin,viewModelPropuestas: ControllerPropuestas,viewModelDetallesPropuesta: ControllerDetallePropuesta) {
    val navController = rememberNavController()
    NavHost(navController= navController, startDestination = AppScreens.Login.route) {
        composable(route=AppScreens.Login.route){
            LoginNav(navController,viewModelLogin)
        }
        composable(route=AppScreens.Main.route){
            MainNav(navController,viewModelPropuestas,viewModelLogin,viewModelDetallesPropuesta)
        }
        composable(AppScreens.Detail.route+"/{claveControl}",
            arguments = listOf(
                navArgument("claveControl"){type=NavType.StringType}
//                navArgument("params"){type=NavType.StringArrayType}
            )
        ){
            //el primer ? es para que permita valores nulos, el ?: es para que si viene undefined o null remplazar por ese calor especificado
//            val params = it.arguments?.getStringArrayList("params") ?: null;
//            val params = it.arguments.getString("params");
            val claveControl = it.arguments?.getString("claveControl");
            DetailNav(navController,viewModelDetallesPropuesta,claveControl!!,viewModelLogin)
//            DetailNav(navController,params,paramsOpcional)
        }
    }
}