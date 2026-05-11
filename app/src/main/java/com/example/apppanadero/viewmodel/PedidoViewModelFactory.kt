package com.example.apppanadero.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apppanadero.data.repository.PedidoRepository

// Una Factory es una clase encargada de fabricar ViewModels
class PedidoViewModelFactory(

    // Dependencia Repository
    private val repository: PedidoRepository

) : ViewModelProvider.Factory {

    // Función que crea ViewModels cuando Android los necesita
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // Si el ViewModel pedido es PedidoViewModel
        if (modelClass.isAssignableFrom(PedidoViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")

            // Creamos el ViewModel usando Repository
            return PedidoViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}