package com.example.globalapp.models

import com.example.globalapp.models.controllers.DetallesPropuesta
import com.example.globalapp.models.retrofit.PropuestasResponse

//data class Table(
//
//)

data class HeaderData(
    var title:String,
    var width:Int,
    var dataIndexStg:String,
    var sort:Boolean?=null,
    var visible:Boolean=true,
    val dataIndex: (PropuestasResponse) -> String,
)

data class HeaderDataDetalle(
    var title:String,
    var width:Int,
    var dataIndexStg:String,
    var sort:Boolean?=null,
    var visible:Boolean=true,
    val dataIndex: (DetallesPropuesta) -> String,
)
