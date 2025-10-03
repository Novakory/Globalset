package com.example.globalapp.util

import com.example.globalapp.models.controllers.DetallesPropuesta
//import com.example.globalapp.models.controllers.ListDetallesPropuesta
import com.example.globalapp.models.retrofit.WebSocketDetalleResponse
import com.example.globalapp.models.retrofit.WebSocketGenericResponse
//import com.example.globalapp.models.retrofit.WebSocketRequest
import com.example.globalapp.models.retrofit.WebSocketResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.DecimalFormat

fun formatNumber(monto:Double=0.00):String{
    val customFormat = DecimalFormat("#,###.00")
    val formattedAmount = "$${customFormat.format(monto)}"
    return formattedAmount
}
fun gsonToWebSocketGenericResponse(jsonString: String): WebSocketGenericResponse {
    val gson = Gson()
    return gson.fromJson(jsonString, WebSocketGenericResponse::class.java)
}
fun gsonToWebSocketResponse(jsonString: String): WebSocketResponse {
    val gson = Gson()
    return gson.fromJson(jsonString, WebSocketResponse::class.java)
}
fun gsonToWebSocketDetailsResponse(jsonString: String): WebSocketDetalleResponse {
    val gson = Gson()
    return gson.fromJson(jsonString, WebSocketDetalleResponse::class.java)
}

fun dtoToGson(dto: Any):String{
//fun dtoToGson(webSocketRequest: WebSocketRequest):String{
    val gson = Gson()
    return gson.toJson(dto)
}
//fun gsonToListDetallesPropuestas(data:String): ListDetallesPropuesta? {
//    val gson = Gson()
//    return gson.fromJson(data, ListDetallesPropuesta::class.java)
//}
fun jsonToDetallesPropuestaList(jsonString: String): List<DetallesPropuesta> {
    val gson = Gson()
    val type = object : TypeToken<List<DetallesPropuesta>>() {}.type
    return gson.fromJson(jsonString, type)
}
fun jsonToMap(jsonString: String): Map<String, String> {
    val gson = Gson()
    val type = object : TypeToken<Map<String, String>>() {}.type
    return gson.fromJson(jsonString, type)
}
