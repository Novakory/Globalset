package com.example.globalapp.retrofit

import android.util.Log
import com.example.globalapp.models.controllers.DetallesPropuesta
import com.example.globalapp.models.retrofit.WebSocketRequest
import com.example.globalapp.models.retrofit.WebSocketRequestRegister
import com.example.globalapp.util.Constants
import com.example.globalapp.util.dtoToGson
//import com.example.globalapp.util.gsonToListDetallesPropuestas
import com.example.globalapp.util.gsonToWebSocketDetailsResponse
import com.example.globalapp.util.gsonToWebSocketGenericResponse
import com.example.globalapp.util.gsonToWebSocketResponse
import com.example.globalapp.util.jsonToDetallesPropuestaList
import com.example.globalapp.util.jsonToMap
import com.example.globalapp.viewModels.ControllerDetallePropuesta
import com.example.globalapp.viewModels.ControllerLogin
import com.example.globalapp.viewModels.ControllerPropuestas
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class WebSocketClient(val controllerLogin: ControllerLogin,val controllerPropuestas: ControllerPropuestas,val controllerDetallePropuesta: ControllerDetallePropuesta): WebSocketListener() {

    private var webSocket: WebSocket? = null
    private var client: OkHttpClient? = null
    var currentClient: WebSocket? = null

    override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
        Log.i("WebSocketClient:","conectado")
        webSocket.send("Hola, servidor desde movil!") // Enviar mensaje al servidor
        var webSocketRequestRegister = WebSocketRequestRegister(controllerLogin.user,Constants.WS_TYPE_REGISTRAR_CLIENTE)

        var dataJson = dtoToGson(webSocketRequestRegister);
        webSocket.send(dataJson) // Enviar mensaje al servidor
//        currentClient = webSocket;
    }

    override fun onMessage(webSocket: WebSocket, gsonObject: String) {

        Log.i("WebSocketClient:",gsonObject)
        val webSocketGenericResponse = gsonToWebSocketGenericResponse(gsonObject)
        Log.i("WebSocketClient:","ok1")
//        Log.i("WebSocketClient:",listDetallePropuesta.toString())
//        val data = jsonToMap(gsonObject)
        if(webSocketGenericResponse.type==Constants.WS_SEND_ALERT_BY_API) {
//        if(data.get("type")?.toInt()==Constants.WS_SEND_ALERT_BY_API) {
            val webSocketResponse = gsonToWebSocketResponse(gsonObject)
            controllerPropuestas.updateNumPropuestasAfectadas(webSocketResponse.numChanges)
        }else if(webSocketGenericResponse.type == Constants.WS_TYPE_SEND_DETALLE_BY_API){
            try {
                Log.i("WebSocketClient:","ok2")
//        }else if(data.get("type")?.toInt() == Constants.WS_TYPE_SEND_DETALLE_BY_API){
                val webSocketDetalleResponse = gsonToWebSocketDetailsResponse(gsonObject)
                Log.i("WebSocketClient:","ok3")
                Log.i("WebSocketClient:claveControl",webSocketDetalleResponse.claveControl)
                Log.i("WebSocketClient:data",webSocketDetalleResponse.data)
//            val listDetallePropuesta = gsonToListDetallesPropuestas(webSocketDetalleResponse.data);
                val stgDetalle = ""+webSocketDetalleResponse.data+ ""
                val listaDetalle:List<DetallesPropuesta> = jsonToDetallesPropuestaList(stgDetalle);
                Log.i("WebSocketClient:",listaDetalle.toString())
                controllerDetallePropuesta.updateListDetalle(listaDetalle)
            }catch (e:Exception){
                //TODO mandar errror
            }finally {

            }

        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        println("Mensaje binario recibido: ${bytes.hex()}")
        Log.i("WebSocketClient:","onMessage2")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("WebSocket cerrando: $code / $reason")
        Log.i("WebSocketClient:","onClosing")
        webSocket.close(1000, null)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
        println("WebSocket error: ${t.message}")
        Log.i("WebSocketClient:","onFailure")
    }

    fun connect(client: OkHttpClient) {
        val request = Request.Builder().url(Constants.BASE_URL_WS).build()
        webSocket = client.newWebSocket(request, this)
    }
    fun closeConnection() {
//        Log.i("Main:logout","Cerrar session aqui 3")
        webSocket?.close(1000, "Conexión cerrada manualmente")
        webSocket = null
        val trueClient = client!!
        trueClient.dispatcher.executorService.shutdown()
//        Log.i("WebSocketClient:", "Conexión cerrada manualmente")
    }
    fun sendMessage(webSocketRequest: WebSocketRequest){
        val jsonData = dtoToGson(webSocketRequest);
        webSocket?.send(jsonData)
    }

    fun openClient() {
        client = OkHttpClient()
        this.connect(client!!)
        Runtime.getRuntime().addShutdownHook(Thread {
            this.closeConnection()
        })
    }
}
fun openWebSocket(controllerLogin: ControllerLogin,controllerPropuestas: ControllerPropuestas,controllerDetallePropuesta: ControllerDetallePropuesta):WebSocketClient {
    val webSocketClient = WebSocketClient(controllerLogin,controllerPropuestas,controllerDetallePropuesta)
    webSocketClient.openClient()
//    webSocketClient.closeConnection()
    return webSocketClient
}