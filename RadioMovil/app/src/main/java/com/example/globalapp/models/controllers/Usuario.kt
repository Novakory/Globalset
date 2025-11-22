package com.example.globalapp.models.controllers

 data class Usuario(
   val id_usuario: Int = 0,
   val nombre: String = "",
   val apellido_materno: String = "",
   val apellido_paterno: String = "",
   val clave_usuario: String = "",
//   val contrasena: String = "",
//   val empresas: String = "",
//   val facultad_acceso: String = "",
   val facultad_mancomunada: Boolean = false,
//   val facultad_total: String = "",
   val facultad_rechazar: Boolean = false,
//   val monto_maximo_pagar: String = "",
)