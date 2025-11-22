package com.example.globalapp.views.home.controllers

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.globalapp.models.HeaderData
import com.example.globalapp.models.controllers.ProgressBarModel
import com.example.globalapp.models.retrofit.AutorizarPropuestasRequest
import com.example.globalapp.models.retrofit.AutorizarPropuestasResponse
import com.example.globalapp.models.retrofit.GenericResponse
import com.example.globalapp.models.retrofit.PropuestasResponse
import com.example.globalapp.models.retrofit.RechazarPropuestasRequest
import com.example.globalapp.retrofit.LoginRepository
import com.example.globalapp.util.Constants
import com.example.globalapp.util.formatNumber
import com.example.globalapp.views.login.controllers.ControllerLogin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ControllerPropuestas @Inject constructor(private val repository: LoginRepository) : ViewModel() {//TODO private val secondaryViewModel: SecondaryViewModel - agregar el viewModelLogin dentro de este

    //LOGOUT TOME__________________________________________
    var controllerLogout = ControllerLogout();
//    var tiempoInicioSuspendido by mutableStateOf(0L)
//        private set
//    fun updateTiempoInicioSuspendido(tiempoSuspendido: Long){
//        tiempoInicioSuspendido = tiempoSuspendido
//    }
//    fun validaTiempoSuspendido(tiempoSuspendido:Long):Boolean{
//        if(tiempoSuspendido> Constants.Companion.MAX_TIME_SUSPEND){
//            Log.i("MainNav:ON_STOP","logout")
//            return true;
//        }
//        return false;
//    }
    //END LOGOUT TOME__________________________________________

    //WEB SOCKET ALERTS
    var numPropuestasAfectadas by mutableIntStateOf(0)
        private set
    fun updateNumPropuestasAfectadas(numPropuestasAfectadas:Int){
        this.numPropuestasAfectadas += numPropuestasAfectadas
    }
    fun resetNumPropuestasAfectadas(){
        numPropuestasAfectadas = 0;
    }
    //DIALOG___________________
    val controllerDialogAutorizarDesautorizar = ControllerDialogAutorizarDesautorizar()
    val controllerDialogRechazar = ControllerDialogRechazar();

//    var showDialog by mutableStateOf(false)
//        private set
//    var txtPasswordDialog by mutableStateOf("")
//        private set
//    fun updateShowDialog(showDialog:Boolean){
//        controllerDialogAutorizarDesautorizar.updateShowDialog(showDialog)
//        if(showDialog){
//            updateResponse(null,"")
//        }
//    }
    //END DIALOG___________________

    //SWITCHS_________________________________________________________________________________________--
    var switchAutorizando by mutableStateOf(true)
        private set

    var switchPropuestasPendientes by mutableStateOf(true)
        private set

    fun updateSwitchAutorizando(isAutorizando:Boolean){
        switchAutorizando = isAutorizando
    }
    fun updateSwitchPropuestasPendientes(isPropuestasPendientes:Boolean,claveUsuario: String,viewModelLogin: ControllerLogin){
        switchPropuestasPendientes = isPropuestasPendientes
        viewModelScope.launch {
            handlerGetPropuestas(claveUsuario,viewModelLogin)
        }
    }

    //END SWITCHES__________________________________________________________________________________

    //VARIABLE BUSQUEDA_____________________________________________________________________________
    var search by mutableStateOf("")
        private set

    var isSearching by mutableStateOf("")
        private set
    var isSearchingAsc by mutableStateOf(true)
        private set

    fun onValue(
        value: String,
        key: String
    ) {//ES LA FUNCION QUE SE LE MANDARA AL onValueChange DEL TEXTFIELD PARA QUE SE ACTUALIZE AUTOMATICAMENTE AL ESCRIBIR
        when (key) {
            "search" -> search = value
            "txtPasswordDialog"->controllerDialogAutorizarDesautorizar.updateTxtPassword(value)

            "txtPasswordDialogRechazar"->controllerDialogRechazar.updateTxtPassword(value)
            "txtMotivoRechazoDialog"->controllerDialogRechazar.updateTxtMotivoRechazo(value)
        }
    }
    //FIN VARIABLES BUSQUEDA________________________________________________________________________

    //CHECK ALL_________________________________________________________________________________________
    var checkAll by mutableStateOf(false)
        private set
    fun updateCheckAll(isCheckAll:Boolean){
        checkAll = isCheckAll

        val propuestasFiltradasSet = _lista.value.toSet()
        _listaOriginal.value = _listaOriginal.value.map { propuesta ->
//            if (propuesta in _lista.value) {//FORMA 1 - menos eficiente si la lista es muy grande
            if (propuestasFiltradasSet.contains(propuesta)) {//FORMA 2 - mas eficiente en buscar
                propuesta.copy(checked = checkAll)
            } else {
                propuesta
            }
        }

        _lista.value = _listaOriginal.value;
        filterSearch();
    }
    //FIN CHECK ALL______________________________________________________________________________________



    var progressbarPropuestasState by mutableStateOf(ProgressBarModel(false, ""))
        private set
//    var progressbarDialogState by mutableStateOf(ProgressBarModel(false, ""))
//        private set
//    var autorizarPropuestasResponse by mutableStateOf(AutorizarPropuestasResponse(null, ""))
//        private set
    init {

    }

    private  val _listaOriginal: MutableStateFlow<List<PropuestasResponse>> =
        MutableStateFlow(emptyList());
    private  val _lista: MutableStateFlow<List<PropuestasResponse>> = MutableStateFlow(emptyList());
    var lista = _lista;

    //DE ESTA FORMA SI SE PUEDE MODIFICAR LA VARIABLE DESDE FUERA?
//    private var _headers = MutableStateFlow<List<HeaderData>>(emptyList())
//    val headers = _headers

    //DE ESTA FORMA NO SE PUEDE MODIFICAR DESDE FUERA LA VARIABLE
    private var _headers = MutableStateFlow<List<HeaderData>>(emptyList())
    val headers = _headers.asStateFlow()



    init {
        initHeaders()
    }
    private fun initHeaders(){
        _headers.value = listOf(
            HeaderData("Clave control", 110, Constants.Companion.CVE_CONTROL) { it.cve_control },
            HeaderData("Importe", 100, Constants.Companion.IMPORTE) { formatNumber(it.importe) },
            HeaderData("Divisa", 40, Constants.Companion.ID_DIVISA) { it.id_divisa },
            HeaderData(
                "Fecha propuesta",
                90,
                Constants.Companion.FEC_PROPUESTA
            ) { it.fec_propuesta },
            HeaderData("Empresa", 60, Constants.Companion.NO_EMPRESA) { it.no_empresa.toString() },
            HeaderData("Desc empresa", 100, Constants.Companion.DESC_EMPRESA) { it.desc_empresa },
            HeaderData("Banco", 50, Constants.Companion.ID_BANCO) { it.id_banco.toString() },
            HeaderData("Desc Banco", 100, Constants.Companion.DESC_BANCO) { it.desc_banco },
            HeaderData("Chequera", 105, Constants.Companion.ID_CHEQUERA) { it.id_chequera },
            HeaderData(
                "Nivel autorización",
                90,
                Constants.Companion.NIVEL_AUTORIZACION
            ) { it.nivel_autorizacion.toString() },
            HeaderData(
                "Usuario 1",
                100,
                Constants.Companion.NOMBRE_USUARIO_UNO
            ) { it.nombre_usuario_uno.toString() },
            HeaderData(
                "Usuario 2",
                100,
                Constants.Companion.NOMBRE_USUARIO_DOS
            ) { it.nombre_usuario_dos.toString() },
            HeaderData(
                "Usuario 3",
                100,
                Constants.Companion.NOMBRE_USUARIO_TRES
            ) { it.nombre_usuario_tres.toString() },
        )
    }

    fun handlerGetPropuestas(claveUsuario:String,viewModelLogin: ControllerLogin) {
        viewModelScope.launch {
            try {
                val token = "Bearer ${viewModelLogin.loginResponse.TOKEN}"
                _listaOriginal.value = emptyList()
                _lista.value = emptyList()
                resetNumPropuestasAfectadas()

                updateProgressbarPropuestasState(true, "Cargando propuestas")
//            delay(5000)//SIMULA EL TIEMPO DE RESPUESTA
                _listaOriginal.value = getPropuestas(token,claveUsuario) ?: emptyList()
                _lista.value = _listaOriginal.value
                filterSearch()
                if (lista != null) {
                    controllerDialogAutorizarDesautorizar.updateResponse(true, "")
                } else {
                    controllerDialogAutorizarDesautorizar.updateResponse(false, "No se pudo conectar al servidor")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                controllerDialogAutorizarDesautorizar.updateResponse(false, e.message.toString())
            } finally {
                updateProgressbarPropuestasState(false, "")
            }
        }
    }

    fun updateCheckState(cveControl: String, isChecked: Boolean){
        _listaOriginal.value = _listaOriginal.value.map { propuesta ->
            if (propuesta.cve_control == cveControl) {
                propuesta.copy(checked = isChecked)
            } else {
                propuesta
            }
        }

        _lista.value = _listaOriginal.value;
        filterSearch();
    }

    fun updateSortHeader(title:String,isChecked:Boolean,dataIndex:String){
        isSearching = dataIndex
        isSearchingAsc = isChecked
        _headers.value = _headers.value.map { header->
            if(header.title == title){
                header.copy(sort = isChecked)
            }else{
                header.copy(sort = null)
            }
        }
        filterSearch();
    }

    private fun ordenar(){
        if(isSearching.isNotBlank()) {
            _lista.value = if (isSearchingAsc) {
                _lista.value.sortedBy { propuesta ->
                    when (isSearching) {
                        Constants.Companion.CVE_CONTROL -> propuesta.cve_control
                        Constants.Companion.FEC_PROPUESTA -> propuesta.fec_propuesta
                        Constants.Companion.IMPORTE -> propuesta.importe
                        Constants.Companion.ID_DIVISA -> propuesta.id_divisa
                        Constants.Companion.CONCEPTO -> propuesta.concepto
                        Constants.Companion.NO_EMPRESA -> propuesta.no_empresa
                        Constants.Companion.DESC_EMPRESA -> propuesta.desc_empresa
                        Constants.Companion.ID_BANCO -> propuesta.id_banco
                        Constants.Companion.DESC_BANCO -> propuesta.desc_banco
                        Constants.Companion.ID_CHEQUERA -> propuesta.id_chequera
                        Constants.Companion.NIVEL_AUTORIZACION -> propuesta.nivel_autorizacion
                        Constants.Companion.NOMBRE_USUARIO_UNO -> propuesta.nombre_usuario_uno
                        Constants.Companion.NOMBRE_USUARIO_DOS -> propuesta.nombre_usuario_dos
                        Constants.Companion.NOMBRE_USUARIO_TRES -> propuesta.nombre_usuario_tres
                        else -> propuesta.importe
                    } as Comparable<Comparable<*>>
                }
            } else {
                _lista.value.sortedByDescending { propuesta ->
                    when (isSearching) {
                        Constants.Companion.CVE_CONTROL -> propuesta.cve_control
                        Constants.Companion.FEC_PROPUESTA -> propuesta.fec_propuesta
                        Constants.Companion.IMPORTE -> propuesta.importe
                        Constants.Companion.ID_DIVISA -> propuesta.id_divisa
                        Constants.Companion.CONCEPTO -> propuesta.concepto
                        Constants.Companion.NO_EMPRESA -> propuesta.no_empresa
                        Constants.Companion.DESC_EMPRESA -> propuesta.desc_empresa
                        Constants.Companion.ID_BANCO -> propuesta.id_banco
                        Constants.Companion.DESC_BANCO -> propuesta.desc_banco
                        Constants.Companion.ID_CHEQUERA -> propuesta.id_chequera
                        Constants.Companion.NIVEL_AUTORIZACION -> propuesta.nivel_autorizacion
                        Constants.Companion.NOMBRE_USUARIO_UNO -> propuesta.nombre_usuario_uno
                        Constants.Companion.NOMBRE_USUARIO_DOS -> propuesta.nombre_usuario_dos
                        Constants.Companion.NOMBRE_USUARIO_TRES -> propuesta.nombre_usuario_tres
                        else -> propuesta.importe
                    } as Comparable<Comparable<*>>
                }
            }
        }
    }

    fun filterSearch(){
        _lista.value = _listaOriginal.value.filter { propuesta ->
                when (isSearching) {
                    Constants.Companion.CVE_CONTROL -> propuesta.cve_control.startsWith(search,true)
                    Constants.Companion.FEC_PROPUESTA -> propuesta.fec_propuesta.startsWith(search,true)
                    Constants.Companion.IMPORTE -> propuesta.importe.toString().startsWith(search,true)
                    Constants.Companion.ID_DIVISA -> propuesta.id_divisa.toString().startsWith(search,true)
                    Constants.Companion.CONCEPTO -> propuesta.concepto?.startsWith(search,true) == true
                    Constants.Companion.NO_EMPRESA -> propuesta.no_empresa.toString().startsWith(search,true)
                    Constants.Companion.NO_EMPRESA -> propuesta.desc_empresa.startsWith(search,true)
                    Constants.Companion.ID_BANCO -> propuesta.id_banco.toString().startsWith(search,true)
                    Constants.Companion.ID_BANCO -> propuesta.desc_banco.startsWith(search,true)
                    Constants.Companion.ID_CHEQUERA -> propuesta.id_chequera.startsWith(search,true)
                    Constants.Companion.NIVEL_AUTORIZACION -> propuesta.nivel_autorizacion.toString().startsWith(search,true)
                    Constants.Companion.NOMBRE_USUARIO_UNO -> propuesta.nombre_usuario_uno?.startsWith(search,true) == true
                    Constants.Companion.NOMBRE_USUARIO_DOS -> propuesta.nombre_usuario_dos?.startsWith(search,true) == true
                    Constants.Companion.NOMBRE_USUARIO_TRES -> propuesta.nombre_usuario_tres?.startsWith(search,true) == true
                    else -> propuesta.importe.toString().startsWith(search,true)
                }
            }

        ordenar()
    }



    fun handlerAutorizarDesautorizar(idUsuario:Int, viewModelLogin: ControllerLogin, user:String, password:String) {
        viewModelScope.launch {
            controllerDialogAutorizarDesautorizar.updateResponse(null,"");
            try {
                val token = "Bearer ${viewModelLogin.loginResponse.TOKEN}"
                controllerDialogAutorizarDesautorizar.updateProgressbarState(true,"Validando datos")
                if(password.isBlank()) throw Exception("Campo de contraseña vacio")//NOTA - return@launch se puede usar este return dentro de este scope

                //VALIDA USUARIO
                val loginResponse = viewModelLogin.loginApi(user,password)
                if(loginResponse===null || !loginResponse.SUCCESS){
                    if(loginResponse!==null) {
                        throw Exception(loginResponse.MESSAGE)
                    }else{
                        throw Exception("No se pudo conectar al servidor para validar el usuario")
                    }
                }

                delay(2000)//SIMULA EL TIEMPO DE RESPUESTA
                if(switchAutorizando) controllerDialogAutorizarDesautorizar.updateProgressbarState(true,"Autorizando propuestas")
                else controllerDialogAutorizarDesautorizar.updateProgressbarState(true,"Desautorizando propuestas")
                delay(3000)//SIMULA EL TIEMPO DE RESPUESTA

                //AUTORIZA PROPUESTAS
                val autorizarPropuestasRequest = AutorizarPropuestasRequest(
                    propuestas = _lista.value.filter { propuesta ->
                        propuesta.checked
                    },
                    idUsuario
                )

                val autorizarPropuestasResponseData = if(switchAutorizando) {
                    autorizarPropuestasApi(token,autorizarPropuestasRequest)
                }else{
                    desautorizarPropuestasApi(token,autorizarPropuestasRequest)
                }

                if (autorizarPropuestasResponseData != null) {
                    controllerDialogAutorizarDesautorizar.updateResponse(
                        autorizarPropuestasResponseData.SUCCESS,
                        autorizarPropuestasResponseData.MESSAGE
                    )
                    if (controllerDialogAutorizarDesautorizar.autorizarPropuestasResponse.SUCCESS == true) {
                        controllerDialogAutorizarDesautorizar.updateProgressbarState(false,"")
//                        showDialog = false;
//                        txtPasswordDialog = "";
                        controllerDialogAutorizarDesautorizar.updateShowDialog(false)
                        controllerDialogAutorizarDesautorizar.updateTxtPassword("")
                        handlerGetPropuestas(viewModelLogin.user,viewModelLogin)
                    }
                } else {
                    controllerDialogAutorizarDesautorizar.updateResponse(
                        false,
                        "No se pudo conectar al servidor"
                    )
                }

            }catch (e:Exception){
                e.printStackTrace()
                controllerDialogAutorizarDesautorizar.updateResponse(false, e.message.toString())
            }finally {
                controllerDialogAutorizarDesautorizar.updateProgressbarState(false,"")
                viewModelLogin.controllerAuthFingerprint.updateIsAutenticate(false)
            }
        }
    }

    fun handlerRechazar(viewModelLogin: ControllerLogin, user:String, password:String,motivoRechazo:String) {
        viewModelScope.launch {
            controllerDialogRechazar.updateResponse(null,"");
            try {
                val token = "Bearer ${viewModelLogin.loginResponse.TOKEN}"
                controllerDialogRechazar.updateProgressbarState(true,"Validando datos")
                if(password.isBlank()) throw Exception("Campo de contraseña vacio")//NOTA - return@launch se puede usar este return dentro de este scope

                //VALIDA USUARIO
                val loginResponse = viewModelLogin.loginApi(user,password)
                if(loginResponse===null || !loginResponse.SUCCESS){
                    if(loginResponse!==null) {
                        throw Exception(loginResponse.MESSAGE)
                    }else{
                        throw Exception("No se pudo conectar al servidor para validar el usuario")
                    }
                }
                if(motivoRechazo.isBlank()) throw Exception("Campo de motivo de rechazo vacio")

                delay(2000)//SIMULA EL TIEMPO DE RESPUESTA //TODO
                controllerDialogRechazar.updateProgressbarState(true,"Rechazando propuestas")
                delay(3000)//SIMULA EL TIEMPO DE RESPUESTA //TODO

                //PETICION API
                val propuestasRequest = RechazarPropuestasRequest(
                    propuestas = _lista.value.filter { propuesta ->
                        propuesta.checked
                    },
                    motivoRechazo
                )
                val response = rechazarPropuestasApi(token,propuestasRequest)


                //SHOW RESPONSE
                if (response != null) {
                    controllerDialogRechazar.updateResponse(
                        response.SUCCESS,
                        response.MESSAGE
                    )
                    if (controllerDialogRechazar.responseService.SUCCESS == true) {
                        controllerDialogRechazar.resetVariables()
                        handlerGetPropuestas(viewModelLogin.user,viewModelLogin)
                    }
                } else {
                    controllerDialogRechazar.updateResponse(
                        false,
                        "No se pudo conectar al servidor"
                    )
                }

            }catch (e:Exception){
                e.printStackTrace()
                controllerDialogRechazar.updateResponse(false, e.message.toString())
            }finally {
                controllerDialogRechazar.updateProgressbarState(false,"")
                viewModelLogin.controllerAuthFingerprint.updateIsAutenticate(false)
            }
        }
    }

    //PETICIONES API --------------------------------------------------------------------------------
    private suspend fun getPropuestas(token:String,claveUsuario: String): List<PropuestasResponse>?{
        return if(switchPropuestasPendientes){
            getPropuestasPendientesApi(token,claveUsuario)
        }else{
            getPropuestasApi(token,claveUsuario)
        }
    }
    private suspend fun getPropuestasApi(token:String,claveUsuario:String): List<PropuestasResponse>? {
        val response = withContext(Dispatchers.IO) {
            repository.getPropuestasByUser(token,claveUsuario)
        }
        return response
    }
    private suspend fun getPropuestasPendientesApi(token:String, claveUsuario:String): List<PropuestasResponse>? {
        val response = withContext(Dispatchers.IO) {
            repository.getPropuestasPendientesByUser(token,claveUsuario)
        }
        return response
    }
    private suspend fun autorizarPropuestasApi(token:String, autorizarPropuestasRequest: AutorizarPropuestasRequest): AutorizarPropuestasResponse? {
        val response = withContext(Dispatchers.IO) {
            repository.autorizarPropuestas(token,autorizarPropuestasRequest)
        }
        return response
    }

    private suspend fun desautorizarPropuestasApi(token:String, autorizarPropuestasRequest: AutorizarPropuestasRequest): AutorizarPropuestasResponse? {
        val response = withContext(Dispatchers.IO) {
            repository.desautorizarPropuestas(token,autorizarPropuestasRequest)
        }
        return response
    }
    private suspend fun rechazarPropuestasApi(token:String, rechazarPropuestasRequest: RechazarPropuestasRequest): GenericResponse? {
        val response = withContext(Dispatchers.IO) {
            repository.rechazarPropuestas(token,rechazarPropuestasRequest)
        }
        return response
    }

    //FIN PETICIONES API----------------------------------------------------------------------------


    //SETTERS_______________________________________________________________________________________

    private fun updateProgressbarPropuestasState(isLoading:Boolean,message:String){
        progressbarPropuestasState = progressbarPropuestasState.copy(isLoading, message)
    }
}