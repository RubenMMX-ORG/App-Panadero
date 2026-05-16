package com.example.apppanadero.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apppanadero.data.repository.PrecioRepository
import com.example.apppanadero.viewmodel.PrecioViewModel

class PrecioViewModelFactory(

    private val repository: PrecioRepository

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                PrecioViewModel::class.java
            )
        ) {

            return PrecioViewModel(
                repository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}