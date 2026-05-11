package com.example.apppanadero.data.model

data class LineaPedido(

    val productoId: String = "",

    val nombreProducto: String = "",

    val cantidadPedida: Int = 0,

    val cantidadDevuelta: Int = 0,

    val cantidadFinal: Int = 0,

    val precioUnitario: Double = 0.0
)