package com.example.apppanadero.data.model

data class Usuario(

    // 🔹 comunes
    val nombre: String = "",
    val apellidos: String = "",
    val email: String = "",
    val rol: String = "",

    // 🔹 cliente
    val nombreComercio: String? = null,
    val direccion: String? = null,
    val telefono: String? = null,
    val cif: String? = null,

    // 🔹 ubicación (IMPORTANTE)
    val latitud: Double? = null,
    val longitud: Double? = null,

    // 🔹 empleado
    val cargo: String? = null,
    val aprobado: Boolean? = null
)