package com.example.globalapp.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.globalapp.models.retrofit.AutorizarPropuestasRequest
import com.example.globalapp.models.retrofit.AutorizarPropuestasResponse
import com.example.globalapp.models.HeaderData
import com.example.globalapp.models.HeaderDataDetalle
import com.example.globalapp.models.controllers.DetallesPropuesta
import com.example.globalapp.models.controllers.ProgressBarModel
import com.example.globalapp.models.retrofit.PropuestasResponse
import com.example.globalapp.models.retrofit.WebSocketDetalleResponse
import com.example.globalapp.retrofit.LoginRepository
import com.example.globalapp.util.Constants
import com.example.globalapp.util.Constants.Companion.CONCEPTO
import com.example.globalapp.util.Constants.Companion.CVE_CONTROL
import com.example.globalapp.util.Constants.Companion.DESC_BANCO
import com.example.globalapp.util.Constants.Companion.DESC_EMPRESA
import com.example.globalapp.util.Constants.Companion.FEC_PROPUESTA
import com.example.globalapp.util.Constants.Companion.ID_BANCO
import com.example.globalapp.util.Constants.Companion.ID_CHEQUERA
import com.example.globalapp.util.Constants.Companion.ID_DIVISA
import com.example.globalapp.util.Constants.Companion.IMPORTE
import com.example.globalapp.util.Constants.Companion.NIVEL_AUTORIZACION
import com.example.globalapp.util.Constants.Companion.NOMBRE_USUARIO_DOS
import com.example.globalapp.util.Constants.Companion.NOMBRE_USUARIO_TRES
import com.example.globalapp.util.Constants.Companion.NOMBRE_USUARIO_UNO
import com.example.globalapp.util.Constants.Companion.NO_EMPRESA
import com.example.globalapp.util.formatNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ControllerDetallePropuesta @Inject constructor(private val repository: LoginRepository) : ViewModel() {//TODO private val secondaryViewModel: SecondaryViewModel - agregar el viewModelLogin dentro de este
    //WEB SOCKET ALERTS
    var numPropuestasAfectadas by mutableIntStateOf(0)
        private set
    fun updateNumPropuestasAfectadas(numPropuestasAfectadas:Int){
        this.numPropuestasAfectadas += numPropuestasAfectadas
    }
    fun resetNumPropuestasAfectadas(){
        numPropuestasAfectadas = 0;
    }

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
        }
    }
    //FIN VARIABLES BUSQUEDA________________________________________________________________________


    var progressbarPropuestasState by mutableStateOf(ProgressBarModel(false,""))
        private set

    private  val _listaOriginal: MutableStateFlow<List<DetallesPropuesta>> = MutableStateFlow(emptyList());
    private  val _lista: MutableStateFlow<List<DetallesPropuesta>> = MutableStateFlow(emptyList());
    var lista = _lista;
    public fun updateListDetalle(listaDetalle:List<DetallesPropuesta>){
        Log.i("WebSocketClient:controllerDetallePropuesta",listaDetalle.toString())
        _listaOriginal.value = listaDetalle;
        _lista.value = _listaOriginal.value
    }

    //DE ESTA FORMA NO SE PUEDE MODIFICAR DESDE FUERA LA VARIABLE
    private var _headers = MutableStateFlow<List<HeaderDataDetalle>>(emptyList())
    val headers = _headers.asStateFlow()

    init {
        initHeaders()
    }
    private fun initHeaders(){
        _headers.value = listOf(
            HeaderDataDetalle("Clave control",130,Constants.D_CVE_CONTROL, visible = false){it.cve_control?:""},
            HeaderDataDetalle("Empresa",60, Constants.D_NO_EMPRESA){it.no_empresa.toString()?:""},
            HeaderDataDetalle("Desc empresa",100, Constants.D_DESC_EMPRESA){it.desc_empresa?:""},
            HeaderDataDetalle("Id benef",60, Constants.D_EQUIVALE_PERSONA){it.equivale_persona?:""},
            HeaderDataDetalle("Beneficiario",100, Constants.D_RAZON_SOCIAL){it.razon_social?:""},
            HeaderDataDetalle("Factura",60, Constants.D_NO_FACTURA){it.no_factura?:""},
            HeaderDataDetalle("Documento",80, Constants.D_NO_DOCTO){it.no_docto?:""},
            HeaderDataDetalle("Partida",50, Constants.D_INVOICE_TYPE){it.invoice_type?:""},
            HeaderDataDetalle("Importe",100,Constants.D_IMPORTE){ formatNumber(it.importe)},
            HeaderDataDetalle("Divisa",40, Constants.D_ID_DIVISA){ it.id_divisa?:""},
            HeaderDataDetalle("Forma pago",70, Constants.D_DESC_FORMA_PAGO){ it.desc_forma_pago?:""},
            HeaderDataDetalle("Concepto",100, Constants.D_CONCEPTO){it.concepto?:""},
            HeaderDataDetalle("Fecha propuesta",90, Constants.D_FEC_PROPUESTA){it.fec_propuesta?:""},
            HeaderDataDetalle("Fecha vencimiento",90, Constants.D_FEC_VENCIMIENTO){it.fec_vencimiento?:""},
            HeaderDataDetalle("Fecha documento",90, Constants.D_FEC_DOCUMENTO){it.fec_documento?:""},
            HeaderDataDetalle("Banco pago",50, Constants.D_ID_BANCO){it.id_banco.toString()?:""},
            HeaderDataDetalle("Desc Banco pago",100, Constants.D_DESC_BANCO){it.desc_banco?:""},
            HeaderDataDetalle("Chequera pago",105, Constants.D_ID_CHEQUERA){it.id_chequera?:""},
            HeaderDataDetalle("Banco benef",50, Constants.D_ID_BANCO_BENEF){it.id_banco_benef.toString()?:""},
            HeaderDataDetalle("Desc Banco benef",100, Constants.D_DESC_BANCO_BENEF){it.desc_banco_benef?:""},
            HeaderDataDetalle("Chequera benef",105, Constants.D_ID_CHEQUERA_BENEF){it.id_chequera_benef?:""},
            HeaderDataDetalle("Clabe",115, Constants.D_CLABE){it.clabe?:""},
            HeaderDataDetalle("RFC",105, Constants.D_RFC){it.rfc?:""},
            HeaderDataDetalle("Referencia cliente",105, Constants.D_REFERENCIA_CTA){it.referencia_cta?:""},

//            HeaderDataDetalle("Clave control",110,Constants.D_CVE_CONTROL){it.cve_control},
//            HeaderDataDetalle("Empresa",60, Constants.D_NO_EMPRESA){it.no_empresa.toString()},
//            HeaderDataDetalle("Desc empresa",100, Constants.D_DESC_EMPRESA){it.desc_empresa},
//            HeaderDataDetalle("Id benef",60, Constants.D_EQUIVALE_PERSONA){it.equivale_persona},
//            HeaderDataDetalle("Beneficiario",100, Constants.D_RAZON_SOCIAL){it.razon_social},
//            HeaderDataDetalle("Factura",60, Constants.D_NO_FACTURA){it.no_factura},
//            HeaderDataDetalle("Documento",60, Constants.D_NO_DOCTO){it.no_docto},
//            HeaderDataDetalle("Partida",40, Constants.D_INVOICE_TYPE){it.invoice_type},
//            HeaderDataDetalle("Importe",100,Constants.D_IMPORTE){ formatNumber(it.importe)},
//            HeaderDataDetalle("Divisa",40, Constants.D_ID_DIVISA){ it.id_divisa},
//            HeaderDataDetalle("Forma pago",60, Constants.D_DESC_FORMA_PAGO){ it.desc_forma_pago},
//            HeaderDataDetalle("Concepto",100, Constants.D_CONCEPTO){it.concepto},
//            HeaderDataDetalle("Fecha propuesta",90, Constants.D_FEC_PROPUESTA){it.fec_propuesta},
//            HeaderDataDetalle("Fecha vencimiento",90, Constants.D_FEC_VENCIMIENTO){it.fec_vencimiento},
//            HeaderDataDetalle("Fecha documento",90, Constants.D_FEC_DOCUMENTO){it.fec_documento},
//            HeaderDataDetalle("Banco pago",50, Constants.D_ID_BANCO){it.id_banco.toString()},
//            HeaderDataDetalle("Desc Banco pago",100, Constants.D_DESC_BANCO){it.desc_banco},
//            HeaderDataDetalle("Chequera pago",105, Constants.D_ID_CHEQUERA){it.id_chequera},
//            HeaderDataDetalle("Banco benef",50, Constants.D_ID_BANCO_BENEF){it.id_banco_benef.toString()},
//            HeaderDataDetalle("Desc Banco benef",100, Constants.D_DESC_BANCO_BENEF){it.desc_banco_benef},
//            HeaderDataDetalle("Chequera benef",105, Constants.D_ID_CHEQUERA_BENEF){it.id_chequera_benef},
//            HeaderDataDetalle("Clabe",105, Constants.D_CLABE){it.clabe},
//            HeaderDataDetalle("RFC",105, Constants.D_RFC){it.rfc},
//            HeaderDataDetalle("Referencia cliente",105, Constants.D_REFERENCIA_CTA){it.referencia_cta},
        )
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
                        CVE_CONTROL -> propuesta.cve_control
                        FEC_PROPUESTA -> propuesta.fec_propuesta
                        IMPORTE -> propuesta.importe
                        ID_DIVISA -> propuesta.id_divisa
                        CONCEPTO -> propuesta.concepto
                        NO_EMPRESA -> propuesta.no_empresa
                        DESC_EMPRESA -> propuesta.desc_empresa
                        ID_BANCO -> propuesta.id_banco
                        DESC_BANCO -> propuesta.desc_banco
                        ID_CHEQUERA -> propuesta.id_chequera
//                        NIVEL_AUTORIZACION -> propuesta.nivel_autorizacion
//                        NOMBRE_USUARIO_UNO -> propuesta.nombre_usuario_uno
//                        NOMBRE_USUARIO_DOS -> propuesta.nombre_usuario_dos
//                        NOMBRE_USUARIO_TRES -> propuesta.nombre_usuario_tres
                        else -> propuesta.importe
                    } as Comparable<Comparable<*>>
                }
            } else {
                _lista.value.sortedByDescending { propuesta ->
                    when (isSearching) {
                        CVE_CONTROL -> propuesta.cve_control
                        FEC_PROPUESTA -> propuesta.fec_propuesta
                        IMPORTE -> propuesta.importe
                        ID_DIVISA -> propuesta.id_divisa
                        CONCEPTO -> propuesta.concepto
                        NO_EMPRESA -> propuesta.no_empresa
                        DESC_EMPRESA -> propuesta.desc_empresa
                        ID_BANCO -> propuesta.id_banco
                        DESC_BANCO -> propuesta.desc_banco
                        ID_CHEQUERA -> propuesta.id_chequera
//                        NIVEL_AUTORIZACION -> propuesta.nivel_autorizacion
//                        NOMBRE_USUARIO_UNO -> propuesta.nombre_usuario_uno
//                        NOMBRE_USUARIO_DOS -> propuesta.nombre_usuario_dos
//                        NOMBRE_USUARIO_TRES -> propuesta.nombre_usuario_tres
                        else -> propuesta.importe
                    } as Comparable<Comparable<*>>
                }
            }
        }
    }

    fun filterSearch(){
        _lista.value = _listaOriginal.value.filter { propuesta ->
                when (isSearching) {
                    CVE_CONTROL -> propuesta.cve_control.startsWith(search,true)
                    FEC_PROPUESTA -> propuesta.fec_propuesta?.startsWith(search,true) == true
                    IMPORTE -> propuesta.importe.toString().startsWith(search,true)
                    ID_DIVISA -> propuesta.id_divisa.toString().startsWith(search,true)
                    CONCEPTO -> propuesta.concepto?.startsWith(search,true) == true
                    NO_EMPRESA -> propuesta.no_empresa.toString().startsWith(search,true)
                    NO_EMPRESA -> propuesta.desc_empresa?.startsWith(search,true) == true
                    ID_BANCO -> propuesta.id_banco.toString().startsWith(search,true)
                    ID_BANCO -> propuesta.desc_banco?.startsWith(search,true) == true
                    ID_CHEQUERA -> propuesta.id_chequera?.startsWith(search,true) == true
//                    NIVEL_AUTORIZACION -> propuesta.nivel_autorizacion.toString().startsWith(search,true)
//                    NOMBRE_USUARIO_UNO -> propuesta.nombre_usuario_uno?.startsWith(search,true) == true
//                    NOMBRE_USUARIO_DOS -> propuesta.nombre_usuario_dos?.startsWith(search,true) == true
//                    NOMBRE_USUARIO_TRES -> propuesta.nombre_usuario_tres?.startsWith(search,true) == true
                    else -> propuesta.importe.toString().startsWith(search,true)
                }
            }

        ordenar()
    }

    public fun updateProgressbarPropuestasState(isLoading:Boolean,message:String){
        progressbarPropuestasState = progressbarPropuestasState.copy(isLoading, message)
    }


    public  fun loadDetallePropuestas(webSocketDetalleResponse: WebSocketDetalleResponse){

    }



}