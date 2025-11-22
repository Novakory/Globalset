package com.example.globalapp.util

class Constants {
    companion object{
        const val BASE_URL = "http://192.168.1.165:3002/";
        const val SUBBASE_URL = "api-v1/";
        const val BASE_URL_WS = "ws://192.168.1.165:3002";

        //WEBSOCKET__
        const val WS_SEND_ALERT_BY_API = 1;
//        const val WS_SEND_CHANGE = 2;
        const val WS_TYPE_REGISTRAR_CLIENTE = 2;
        const val WS_TYPE_GET_DETALLE_BY_MOVIL = 3
        const val WS_TYPE_GET_DETALLE_BY_API = 4
        const val WS_TYPE_SEND_DETALLE_BY_SET = 5
        const val WS_TYPE_SEND_DETALLE_BY_API = 6

        //TIEMPOS
        const val MAX_TIME_SUSPEND:Long = 18000;//3 min

        //COLUMNAS PROPUESTAS
        const val CVE_CONTROL = "cve_control"
        const val FEC_PROPUESTA = "fec_propuesta"
        const val IMPORTE = "importe"
        const val ID_DIVISA = "id_divisa"
        const val CONCEPTO = "concepto"
        const val NO_EMPRESA = "no_empresa"
        const val DESC_EMPRESA = "desc_empresa"
        const val ID_BANCO = "id_banco"
        const val DESC_BANCO = "desc_banco"
        const val ID_CHEQUERA = "id_chequera"
        const val NIVEL_AUTORIZACION = "nivel_autorizacion"
        const val USUARIO_UNO = "usuario_uno"
        const val USUARIO_DOS = "usuario_dos"
        const val USUARIO_TRES = "usuario_tres"
        const val STATUS = "status"
        const val NOMBRE_USUARIO_UNO = "nombre_usuario_uno"
        const val NOMBRE_USUARIO_DOS = "nombre_usuario_dos"
        const val NOMBRE_USUARIO_TRES = "nombre_usuario_tres"


        //ICONOS
        const val NO_ICO = 0
        const val ICO_WARNING = 1
        const val ICO_ERROR = 2

        //VARIABLES DE DETALLE PROPUESTAS
        const val D_CVE_CONTROL = "cve_control"
        const val D_NO_EMPRESA = "no_empresa"
        const val D_DESC_EMPRESA = "desc_empresa"
        const val D_EQUIVALE_PERSONA = "equivale_persona"
        const val D_RAZON_SOCIAL = "razon_social"
        const val D_NO_FACTURA = "no_factura"
        const val D_NO_DOCTO = "no_docto"
        const val D_INVOICE_TYPE = "invoice_type"
        const val D_IMPORTE = "importe"
        const val D_ID_DIVISA = "id_divisa"
        const val D_DESC_FORMA_PAGO = "desc_forma_pago"
        const val D_CONCEPTO = "concepto"
        const val D_FEC_PROPUESTA = "fec_propuesta"
        const val D_FEC_VENCIMIENTO = "fec_vencimiento"
        const val D_FEC_DOCUMENTO = "fec_documento"
        const val D_ID_BANCO = "id_banco"
        const val D_DESC_BANCO = "desc_banco"
        const val D_ID_CHEQUERA = "id_chequera"
        const val D_ID_BANCO_BENEF = "id_banco_benef"
        const val D_DESC_BANCO_BENEF = "desc_banco_benef"
        const val D_ID_CHEQUERA_BENEF = "id_chequera_benef"
        const val D_CLABE = "clabe"
        const val D_RFC = "rfc"
        const val D_REFERENCIA_CTA = "referencia_cta"
    }
}