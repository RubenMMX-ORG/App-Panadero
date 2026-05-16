package com.example.apppanadero.data.model

import com.google.firebase.Timestamp



data class Precio(

    // ID documento Firestore
    val id: String = "",

    // FK lógica al producto
    val productoId: String = "",

    // Precio producto
    val precio: Double = 0.0,

    // Fecha inicio vigencia
    val fechaInicio: Timestamp =
        Timestamp.now(),

    // Fecha fin vigencia
    //
    // null mientras siga activo
    val fechaFin: Timestamp? = null,

    // true = precio actual
    // false = precio histórico
    val vigente: Boolean = true
)