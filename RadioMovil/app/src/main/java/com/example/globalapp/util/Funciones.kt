package com.example.globalapp.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import com.example.globalapp.R
import com.example.globalapp.models.controllers.DetallesPropuesta
//import com.example.globalapp.models.controllers.ListDetallesPropuesta
import com.example.globalapp.models.retrofit.WebSocketDetalleResponse
import com.example.globalapp.models.retrofit.WebSocketGenericResponse
//import com.example.globalapp.models.retrofit.WebSocketRequest
import com.example.globalapp.models.retrofit.WebSocketResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.itextpdf.html2pdf.HtmlConverter
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfName.Document
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.DecimalFormat
import java.util.Date

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

fun detailToHtml(list: List<DetallesPropuesta>,claveControl:String,empresa:String):String{
    val html = StringBuilder()

    html.append("<html><body>")
    html.append("<h1>${claveControl}</h1>")
    html.append("<h1>${empresa}</h1>")
    html.append("<table border='1'>") // Agregamos borde a la tabla

    // Encabezados
    html.append("<tr>")
    html.append("<th>${list.size}</th>")
    html.append("<th>Beneficiario</th>")
    html.append("<th>Importe</th>")
    html.append("<th>Divisa</th>")
    html.append("<th>Forma pago</th>")
    html.append("<th>Concepto</th>")
    html.append("<th>Fecha propuesta</th>")
    html.append("<th>Fecha vencimiento</th>")
    html.append("<th>Fecha documento</th>")
    html.append("<th>Banco pago</th>")
    html.append("<th>Desc Banco pago</th>")
    html.append("<th>Chequera pago</th>")
    html.append("<th>Banco benef</th>")
    html.append("<th>Desc Banco benef</th>")
    html.append("<th>Chequera benef</th>")

    html.append("</tr>")

    // Filas de datos
    for ((index,detail) in list.withIndex()) {
        html.append("<tr>")
        html.append("<td>${(index+1)}</td>");
        html.append("<td>${detail.razon_social}</td>");
        html.append("<td>${formatNumber(detail.importe)}</td>");
        html.append("<td>${detail.id_divisa}</td>");
        html.append("<td>${detail.desc_forma_pago}</td>");
        html.append("<td>${detail.concepto}</td>");
        html.append("<td>${detail.fec_propuesta}</td>");
        html.append("<td>${detail.fec_vencimiento}</td>");
        html.append("<td>${detail.fec_documento}</td>");
        html.append("<td>${detail.id_banco}</td>");
        html.append("<td>${detail.desc_banco}</td>");
        html.append("<td>${detail.id_chequera}</td>");
        html.append("<td>${detail.id_banco_benef}</td>");
        html.append("<td>${detail.desc_banco_benef}</td>");
        html.append("<td>${detail.id_chequera_benef}</td>");
        html.append("</tr>")
    }

    html.append("</table>")
    html.append("</body></html>")

    return html.toString()
}
fun generatePdfFromHtml(context: Context, htmlContent: String): Uri? {
    val fileName = "Reporte-${Date().time}.pdf"

    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS
                )
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            )

            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    generatePdf(htmlContent, outputStream)
                }
            }

            uri // 👈 DEVOLVEMOS URI

        } else {
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )

            FileOutputStream(file).use {
                generatePdf(htmlContent, it)
            }

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun generatePdf(htmlContent: String, outputStream: OutputStream) {
    val writer = PdfWriter(outputStream)
    val pdf = PdfDocument(writer)

//    val customSize = PageSize(1000f, 1000f)//DIMENSION DINAMICA
    val document = Document(pdf, PageSize.A2.rotate())
//    val document = Document(pdf, PageSize.A4.rotate())

    HtmlConverter.convertToPdf(htmlContent, pdf, null) // Convierte el HTML a PDF

    document.close()
}

fun showNotification(context: Context, uri: Uri) {

    val channelId = "pdf_channel"

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "PDF Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.baseline_account_circle_24)
        .setContentTitle("PDF generado")
        .setContentText("Toca para abrir el PDF")
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    NotificationManagerCompat.from(context)
        .notify(1, notification)
}
