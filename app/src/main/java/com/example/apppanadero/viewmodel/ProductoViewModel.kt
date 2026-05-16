package com.example.apppanadero.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apppanadero.data.model.Producto
import com.example.apppanadero.data.repository.ProductoRepository

class ProductoViewModel(

    private val repository: ProductoRepository

) : ViewModel() {

    // ------------------------------------------------
    // LISTA PRODUCTOS
    // ------------------------------------------------

    private val _listaProductos =
        MutableLiveData<List<Producto>>()

    val listaProductos:
            LiveData<List<Producto>> =
        _listaProductos

    // ------------------------------------------------
    // PRODUCTO DETALLE
    // ------------------------------------------------

    private val _productoDetalle =
        MutableLiveData<Producto?>()

    val productoDetalle:
            LiveData<Producto?> =
        _productoDetalle

    // ------------------------------------------------
    // ERROR
    // ------------------------------------------------

    private val _error =
        MutableLiveData<String>()

    val error:
            LiveData<String> =
        _error

    // ------------------------------------------------
    // GUARDAR PRODUCTO
    // ------------------------------------------------

    fun guardarProducto(

        producto: Producto

    ) {

        repository.guardarProducto(
            producto
        ) { correcto ->

            if (!correcto) {

                _error.postValue(
                    "Error al guardar producto"
                )
            }
        }
    }

    // ------------------------------------------------
    // OBTENER TODOS PRODUCTOS
    // ------------------------------------------------

    fun obtenerTodosProductos() {

        repository.obtenerTodosProductos { productos ->

            _listaProductos.postValue(
                productos
            )
        }
    }

    // ------------------------------------------------
    // OBTENER PRODUCTO POR ID
    // ------------------------------------------------

    fun obtenerProductoPorId(

        productoId: String

    ) {

        repository.obtenerProductoPorId(
            productoId
        ) { producto ->

            _productoDetalle.postValue(
                producto
            )
        }
    }

    // ------------------------------------------------
    // ACTUALIZAR PRODUCTO
    // ------------------------------------------------

    fun actualizarProducto(

        producto: Producto

    ) {

        repository.actualizarProducto(
            producto
        ) { correcto ->

            if (!correcto) {

                _error.postValue(
                    "Error al actualizar producto"
                )
            }
        }
    }

    // ------------------------------------------------
    // ELIMINAR PRODUCTO
    // ------------------------------------------------

    fun eliminarProducto(

        productoId: String

    ) {

        repository.eliminarProducto(
            productoId
        ) { correcto ->

            if (!correcto) {

                _error.postValue(
                    "Error al eliminar producto"
                )
            }
        }
    }

    // ------------------------------------------------
    // LIMPIAR ERROR
    // ------------------------------------------------

    fun limpiarError() {

        _error.value = ""
    }
}