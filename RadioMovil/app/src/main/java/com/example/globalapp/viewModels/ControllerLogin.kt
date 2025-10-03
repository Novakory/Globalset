package com.example.globalapp.viewModels

import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.globalapp.models.retrofit.LoginResponse
import com.example.globalapp.navegation.AppScreens
import com.example.globalapp.retrofit.LoginRepository
import com.example.globalapp.util.StoreLogin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ControllerLogin @Inject constructor(private val repository: LoginRepository) : ViewModel() {
    //FORMA 2
    //VARIABLES
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
    var loginResponse by mutableStateOf(LoginResponse(false, "",0))
        private set

    //AUTENTICACION HUELLA
    var canAutenticate by mutableStateOf(false)
        private set
    fun setInCanAutenticate(canAutenticate:Boolean){
        this.canAutenticate = canAutenticate
    }
    var isAutenticate by mutableStateOf(false)
        private set
    fun setInIsAutenticate(isAutenticate:Boolean){
        this.isAutenticate = isAutenticate
    }
    lateinit var promptInfo: BiometricPrompt.PromptInfo
    lateinit var biometricPrompt:BiometricPrompt
    fun showDialogAutenticate(){
        biometricPrompt.authenticate(promptInfo)
    }
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
                ERROR_MESSAGE = "Campos vacios"
            )
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
                    ERROR_MESSAGE = loginData.ERROR_MESSAGE
                )
                idUsuario = loginData.ID_USUARIO
                return true;
            } else {
//                    Log.i("login:into","ELSE DE LOGIN DATA")
                loginResponse = loginResponse.copy(
                    SUCCESS = false,
                    ERROR_MESSAGE = "No se pudo conectar al servidor"
                )
                return false
            }
        } catch (e: Exception) {
//                Log.i("login:into","EXCEPTION")
            loginResponse = loginResponse.copy(
                SUCCESS = false,
                ERROR_MESSAGE = e.message.toString()
            )
            return false
        } finally {
            isLoading = false;
        }
    }

    fun handlerFingerPrint(navController: NavController,userStore:String) {
        if(!canAutenticate){
            Toast.makeText(navController.context,"Tu dispositivo no tiene esta opción",Toast.LENGTH_LONG);
            return
        }
        var credencialesGuardadas:Boolean = userStore.trim().isNotEmpty()
        if(!credencialesGuardadas){
            Toast.makeText(navController.context,"Necesitas logearte por primera vez",Toast.LENGTH_LONG);
            return
        }
        showDialogAutenticate()
    }

    fun handlerEntrar(navController: NavController, user: String, password: String,storeLogin:StoreLogin) {
        loginResponse = loginResponse.copy(SUCCESS = false, ERROR_MESSAGE = "");
        viewModelScope.launch {
            validateApiLogin(user, password)
            try {
                if (loginResponse.SUCCESS) {
                    isLoading =true
                    storeLogin.saveUserStore(user);
                    storeLogin.savePasswordStore(password);
                    clearValuesLogin();
                    isLoading=false
                    isAutenticate = false;
                    gotoMain(navController)
                } else{
                    //clearValuesLogin();
                    isLoading=false
                    isAutenticate = false;
                }
            }catch (e:Exception){
                e.printStackTrace()
            }finally {
                isLoading=false
                isAutenticate = false;
            }

            Log.i("login:success", loginResponse.SUCCESS.toString())
            Log.i("login:error_message", loginResponse.ERROR_MESSAGE)
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
}