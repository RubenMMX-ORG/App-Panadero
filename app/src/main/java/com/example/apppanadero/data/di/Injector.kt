package com.example.apppanadero.data.di
import com.example.apppanadero.data.repository.UsuarioRepository
import com.example.apppanadero.viewmodel.factory.UsuarioViewModelFactory


object Injector {

    //  Repository (Firebase)
    private fun provideUsuarioRepository(): UsuarioRepository {
        return UsuarioRepository()
    }

    //  ViewModelFactory
    fun provideUsuarioViewModelFactory(): UsuarioViewModelFactory {
        return UsuarioViewModelFactory(provideUsuarioRepository())
    }
}