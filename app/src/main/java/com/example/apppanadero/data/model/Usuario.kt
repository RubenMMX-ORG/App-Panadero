package com.example.apppanadero.data.model

data class Usuario(

    // 🔹 comunes
    val nombre: String = "",
    val apellidos: String = "",
    val email: String = "",
    val rol: String = "",
    val aprobado: Boolean? = false,

    // 🔹 cliente
    val nombreComercio: String? = null,
    val direccion: String? = null,
    val telefono: String? = null,
    val cif: String? = null,
    val descuento: Double = 0.0,

    // 🔹 ubicación
    val latitud: Double? = null,
    val longitud: Double? = null,

    // 🔹 empleado
    val cargo: String? = null

)