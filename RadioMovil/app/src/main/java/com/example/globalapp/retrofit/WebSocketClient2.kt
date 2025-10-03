package com.example.globalapp.retrofit

import android.util.Log
import com.example.globalapp.models.retrofit.WebSocketRequest
import com.example.globalapp.util.Constants
import com.example.globalapp.util.dtoToGson
//import com.example.globalapp.util.gsonToListDetallesPropuestas
import com.example.globalapp.util.gsonToWebSocketDetailsResponse
import com.example.globalapp.util.gsonToWebSocketGenericResponse
import com.example.globalapp.util.gsonToWebSocketResponse
import com.example.globalapp.util.jsonToDetallesPropuestaList
import com.example.globalapp.viewModels.ControllerDetallePropuesta
import com.example.globalapp.viewModels.ControllerPropuestas
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class WebSocketClient2 constructor(
    private val controllerPropuestas: ControllerPropuestas,
    private val controllerDetallePropuesta: ControllerDetallePropuesta
) : WebSocketListener() {
    private var webSocket: WebSocket? = null
    private var client: OkHttpClient? = null

    override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
        Log.i("WebSocketClient:", "conectado")
        webSocket.send("Hola, servidor desde móvil!")
    }

    override fun onMessage(webSocket: WebSocket, gsonObject: String) {
        Log.i("WebSocketClient:", gsonObject)
        val webSocketGenericResponse = gsonToWebSocketGenericResponse(gsonObject)
        Log.i("WebSocketClient","ok2")
        if (webSocketGenericResponse.type == Constants.WS_SEND_ALERT_BY_API) {
            val webSocketResponse = gsonToWebSocketResponse(gsonObject)
            controllerPropuestas.updateNumPropuestasAfectadas(webSocketResponse.numChanges)
        } else if (webSocketGenericResponse.type == Constants.WS_TYPE_SEND_DETALLE_BY_API) {
            Log.e("WebSocketClient","ok3")
            val webSocketDetalleResponse = gsonToWebSocketDetailsResponse(gsonObject)
            Log.e("WebSocketClient","ok4")
            val listDetallePropuesta = jsonToDetallesPropuestaList(webSocketDetalleResponse.data)
            Log.e("WebSocketClient","ok5")
            Log.i("WebSocketClient:", listDetallePropuesta.toString())
            // TODO guardar los datos
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        println("Mensaje binario recibido: ${bytes.hex()}")
        Log.i("WebSocketClient:", "onMessage2")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("WebSocket cerrando: $code / $reason")
        Log.i("WebSocketClient:", "onClosing")
        webSocket.close(1000, null)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
        println("WebSocket error: ${t.message}")
        Log.i("WebSocketClient:", "onFailure")
    }

    fun connect(client: OkHttpClient) {
        val request = Request.Builder().url(Constants.BASE_URL_WS).build()
        webSocket = client.newWebSocket(request, this)
    }

    fun closeConnection() {
        webSocket?.close(1000, "Conexión cerrada manualmente")
        webSocket = null
        client?.dispatcher?.executorService?.shutdown()

        INSTANCE = null;
    }

    fun sendMessage(webSocketRequest: WebSocketRequest) {
        val jsonData = dtoToGson(webSocketRequest)
        webSocket?.send(jsonData)
    }

    fun openClient() {
        client = OkHttpClient()
        connect(client!!)
        Runtime.getRuntime().addShutdownHook(Thread {
            closeConnection()
        })
    }

    companion object {
        @Volatile
        private var INSTANCE: WebSocketClient2? = null
        @Volatile
        var currentWebSocket: WebSocket? = null


//        private var INSTANCE: WebSocketClient2? = null

//        fun getInstance(
//            controllerPropuestas: ControllerPropuestas,
//            controllerDetallePropuesta: ControllerDetallePropuesta
//        ): WebSocketListener {
//            return INSTANCE ?: synchronized(this) {
//                INSTANCE ?: WebSocketClient2(controllerPropuestas, controllerDetallePropuesta).also {
//                    it.openClient()
//                    INSTANCE = it
//                    currentWebSocket = it.webSocket
//                }
//            }
//        }
    }
}
fun openWebSocket2(controllerPropuestas: ControllerPropuestas,controllerDetallePropuesta: ControllerDetallePropuesta):WebSocketClient2 {
    val webSocketClient = WebSocketClient2(controllerPropuestas,controllerDetallePropuesta)
    webSocketClient.openClient()
    return webSocketClient
}