package com.example.apppanadero.data.model

import com.google.firebase.Timestamp

data class Precio(

    val id: String = "",

    val productoId: String = "",

    val precio: Double = 0.0,

    val fechaInicio: Timestamp? = null,

    val fechaFin: Timestamp? = null,

    val vigencia: Boolean = true
)