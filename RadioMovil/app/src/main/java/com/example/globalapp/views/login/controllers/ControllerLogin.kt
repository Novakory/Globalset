package com.example.globalapp.views.login.controllers

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.globalapp.models.controllers.Usuario
import com.example.globalapp.models.retrofit.LoginResponse
import com.example.globalapp.navegation.AppScreens
import com.example.globalapp.retrofit.LoginRepository
import com.example.globalapp.util.StoreLogin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ControllerLogin @Inject constructor(private val repository: LoginRepository) : ViewModel() {
    //FORMA 2
    //VARIABLES
    private val _usuarioLogin: MutableStateFlow<Usuario?> = MutableStateFlow(null)
    val usuarioLogin: StateFlow<Usuario?> = _usuarioLogin// 🔹 Exponemos una versión "solo lectura" para la UI

    var idUsuario  by mutableStateOf(0)
        private set
    //VARIABLES DE TEXTFIELDS
    var user by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set



    fun onValue(
        value: String,
        key: String
    ) {//ES LA FUNCION QUE SE LE MANDARA AL onValueChange DEL TEXTFIELD PARA QUE SE ACTUALIZE AUTOMATICAMENTE AL ESCRIBIR
        when (key) {
            "user" -> user = value
            "password" -> password = value
        }
    }

    var isLoading by mutableStateOf(false);
    var loginResponse by mutableStateOf(LoginResponse(false, "", null, null))
        private set

    //AUTENTICACION HUELLA
    val controllerAuthFingerprint = ControllerAuthFingerprint();
//    var canAutenticate by mutableStateOf(false)
//        private set
//    fun setInCanAutenticate(canAutenticate:Boolean){
//        this.canAutenticate = canAutenticate
//    }
//    var isAutenticate by mutableStateOf(false)
//        private set
//    fun setInIsAutenticate(isAutenticate:Boolean){
//        this.isAutenticate = isAutenticate
//    }
//    lateinit var promptInfo: BiometricPrompt.PromptInfo
//    lateinit var biometricPrompt: BiometricPrompt
//    fun showDialogAutenticate(){
//        biometricPrompt.authenticate(promptInfo)
//    }


    init {

    }
    fun validateLogin(): Boolean {
        //TODO validar datos
        return true;
    }

    private fun validateDatos(user: String, password: String): Boolean {
        return !(user.trim() == "" || password.trim() == "")
    }

    suspend fun validateApiLogin(user: String, password: String):Boolean {
        if (!validateDatos(user, password)) {
            loginResponse = loginResponse.copy(
                SUCCESS = false,
                MESSAGE = "Campos vacios"
            )
            setUsuario(null)
            return false
        };
        try {
            isLoading = true;
//            delay(5000)//SIMULA EL TIEMPO DE RESPUESTA
            val loginData = loginApi(user, password);
//                Log.i("login:into","DESPUES DE LOGIN DATA")
//                Log.i("login:loginData",loginData.toString())
            if (loginData != null) {
//                    Log.i("login:into","DENTRO DE LOGIN DATA")
                loginResponse = loginResponse.copy(
                    SUCCESS = loginData.SUCCESS,
                    MESSAGE = loginData.MESSAGE,
                    USUARIO = loginData.USUARIO,
                    TOKEN = loginData.TOKEN
                )
                idUsuario = loginData.USUARIO?.id_usuario ?: 0
                setUsuario(loginResponse.USUARIO)
                return true;
            } else {
//                    Log.i("login:into","ELSE DE LOGIN DATA")
                loginResponse = loginResponse.copy(
                    SUCCESS = false,
                    MESSAGE = "No se pudo conectar al servidor"
                )
                setUsuario(null)
                return false
            }
        } catch (e: Exception) {
//                Log.i("login:into","EXCEPTION")
            loginResponse = loginResponse.copy(
                SUCCESS = false,
                MESSAGE = e.message.toString()
            )
            setUsuario(null)
            return false
        } finally {
            isLoading = false;
        }
        return false
    }

    fun handlerFingerPrint(navController: NavController, userStore:String,purpose: AuthPurpose) {
        if(!controllerAuthFingerprint.canAutenticate){
            Toast.makeText(navController.context,"Tu dispositivo no tiene esta opción", Toast.LENGTH_LONG);
            return
        }
        var credencialesGuardadas:Boolean = userStore.trim().isNotEmpty()
        if(!credencialesGuardadas){
            Toast.makeText(navController.context,"Necesitas logearte por primera vez", Toast.LENGTH_LONG);
            return
        }
        controllerAuthFingerprint.showDialogAutenticate(purpose)
    }

    fun handlerEntrar(navController: NavController, user: String, password: String, storeLogin: StoreLogin) {
        loginResponse = loginResponse.copy(SUCCESS = false, MESSAGE = "");
        viewModelScope.launch {
            validateApiLogin(user, password)
            try {
                if (loginResponse.SUCCESS) {
                    isLoading =true
                    storeLogin.saveUserStore(user);
                    storeLogin.savePasswordStore(password);
                    clearValuesLogin();
                    isLoading=false
                    controllerAuthFingerprint.updateIsAutenticate(false)
                    gotoMain(navController)
                } else{
                    //clearValuesLogin();
                    isLoading=false
                    controllerAuthFingerprint.updateIsAutenticate(false)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }finally {
                isLoading=false
                controllerAuthFingerprint.updateIsAutenticate(false)
            }

            Log.i("login:success", loginResponse.SUCCESS.toString())
            Log.i("login:error_message", loginResponse.MESSAGE)
        }
    }

    fun gotoMain(navController: NavController) {
        //TODO enviar datos de usuario como parametro
        navController.navigate(route = AppScreens.Main.route) {
            popUpTo(0);//para eliminar las vistas detras, o especificar que esta es la primera vista y no pueda retroceder
        }
    }

    suspend fun loginApi(user: String, password: String): LoginResponse? {
        val response = withContext(Dispatchers.IO) {
            repository.login(user, password)
        }
        return response
    }

    fun setUserLogin(user:String){
        this.user = user
    }
    fun setPasswordLogin(password:String){
        this.password = password
    }
    private suspend fun clearValuesLogin(){
        this.user = "";
        this.password = "";
    }


    //SETTERS
    fun setUsuario(usuario: Usuario?){
        _usuarioLogin.value = usuario
    }
}