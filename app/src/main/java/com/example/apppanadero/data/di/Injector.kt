package com.example.apppanadero.data.di
import com.example.apppanadero.data.repository.PedidoRepository
import com.example.apppanadero.data.repository.PrecioRepository
import com.example.apppanadero.data.repository.ProductoRepository
import com.example.apppanadero.data.repository.UsuarioRepository
import com.example.apppanadero.viewmodel.PedidoViewModelFactory
import com.example.apppanadero.viewmodel.factory.PrecioViewModelFactory
import com.example.apppanadero.viewmodel.factory.ProductoViewModelFactory
import com.example.apppanadero.viewmodel.factory.UsuarioViewModelFactory

// Un objeto injector es un sitio donde construimos dependencias
// Una dependencia es un objeto/clase que otra clase necesita usar para funcionar
// Podriamos decir "este ViewModel necesita Repository para funcionar"
// El Repository es la dependencia del ViewModel
//
// El Injector se encarga de construir esas dependencias y pasárselas al ViewModel

object Injector {

    // ------------------------------------------------
    // USUARIOS
    // ------------------------------------------------

    //cuando alguien necesite un Repository, crealo aquí
    private fun provideUsuarioRepository(): UsuarioRepository {
        return UsuarioRepository()
    }

    //cuando alguien necesite la Factory, crealo aquí
    fun provideUsuarioViewModelFactory(): UsuarioViewModelFactory {
        return UsuarioViewModelFactory(provideUsuarioRepository())
    }

    // ------------------------------------------------
    // PEDIDOS
    // ------------------------------------------------

    // Cuando alguien necesite PedidoRepository
    private fun providePedidoRepository(): PedidoRepository {

        return PedidoRepository()
    }

    // Cuando alguien necesite PedidoViewModelFactory
    fun providePedidoViewModelFactory(): PedidoViewModelFactory {

        return PedidoViewModelFactory(
            providePedidoRepository()
        )
    }

    // ------------------------------------------------
    // PRODUCTOS
    // ------------------------------------------------

    fun provideProductoViewModelFactory(): ProductoViewModelFactory {

        val repository =
            ProductoRepository()

        return ProductoViewModelFactory(
            repository
        )
    }

    // ------------------------------------------------
    // PRECIOS
    // ------------------------------------------------

    // Repository
    private val precioRepository =
        PrecioRepository()

    // Factory
    fun providePrecioViewModelFactory():
            PrecioViewModelFactory {

        return PrecioViewModelFactory(
            precioRepository
        )
    }
}