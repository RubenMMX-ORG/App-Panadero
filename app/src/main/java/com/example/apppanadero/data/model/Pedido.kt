package com.example.apppanadero.data.model

import com.google.firebase.Timestamp

data class Pedido(

    val lineasPedido: List<LineaPedido> = emptyList(),

    // ID documento Firestore
    val id: String = "",

    // Numero de pedido, es global para todos los usuarios asi no habra dos pedidos con el mismo numero
    // aunque la identificacion real es con el id, esto es solo para mostrar
    val numeroPedido: Int = 0,

    // Cliente propietario pedido
    val clienteId: String = "",

    // Fecha pedido
    val fecha: Timestamp = Timestamp.now(),

    // pendiente
    // en_camino
    // entregado
    // cancelado
    val estado: String = "pendiente",

    // Precio total pedido
    val precioTotal: Double = 0.0

)