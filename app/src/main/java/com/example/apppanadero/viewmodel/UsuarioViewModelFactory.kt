package com.example.apppanadero.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apppanadero.data.repository.UsuarioRepository
import com.example.apppanadero.viewmodel.UsuarioViewModel
// Una factory es una clase encargada de fabricar viewmodels
class UsuarioViewModelFactory(
    // Repository que necesitará el ViewModel
    private val repository: UsuarioRepository

    // Heredamos del sistema oficial de Factory de Android
) : ViewModelProvider.Factory {
    // Función que crea ViewModels cuando Android los necesita
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuarioViewModel::class.java)) {//si el viewModel pedido es UsuarioViewModel
            // Ignoramos warning del casteo genérico, Android se queja por un casteo raro y con @Suppress le decimos calla que se lo que hago!
            @Suppress("UNCHECKED_CAST")
            return UsuarioViewModel(repository) as T//crealo usando el Repository
        }
        throw IllegalArgumentException("Unknown ViewModel class")// Si intentan crear otro ViewModel diferente → error
    }
}