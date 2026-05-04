package com.example.apppanadero.data.di
import com.example.apppanadero.data.repository.UsuarioRepository
import com.example.apppanadero.viewmodel.factory.UsuarioViewModelFactory


object Injector {

    private fun provideUsuarioRepository(): UsuarioRepository {
        return UsuarioRepository()
    }

    fun provideUsuarioViewModelFactory(): UsuarioViewModelFactory {
        return UsuarioViewModelFactory(provideUsuarioRepository())
    }
}