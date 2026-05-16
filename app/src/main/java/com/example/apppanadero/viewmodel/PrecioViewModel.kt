package com.example.apppanadero.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apppanadero.data.model.Precio
import com.example.apppanadero.data.repository.PrecioRepository

class PrecioViewModel(

    private val repository: PrecioRepository

) : ViewModel() {

    // ------------------------------------------------
    // LISTA PRECIOS
    // ------------------------------------------------

    private val _listaPrecios =
        MutableLiveData<List<Precio>>()

    val listaPrecios:
            LiveData<List<Precio>> =
        _listaPrecios

    // ------------------------------------------------
    // PRECIO VIGENTE
    // ------------------------------------------------

    private val _precioVigente =
        MutableLiveData<Precio?>()

    val precioVigente:
            LiveData<Precio?> =
        _precioVigente

    // ------------------------------------------------
    // PRECIO GUARDADO
    // ------------------------------------------------

    private val _precioGuardado =
        MutableLiveData<Boolean>()

    val precioGuardado:
            LiveData<Boolean> =
        _precioGuardado

    // ------------------------------------------------
    // ERROR
    // ------------------------------------------------

    private val _error =
        MutableLiveData<String>()

    val error:
            LiveData<String> =
        _error

    // ------------------------------------------------
    // GUARDAR PRECIO
    // ------------------------------------------------

    fun guardarPrecio(

        precio: Precio

    ) {

        repository.guardarPrecio(
            precio
        ) { correcto ->

            if (correcto) {

                _precioGuardado.postValue(true)

            } else {

                _error.postValue(
                    "Error al guardar precio"
                )
            }
        }
    }

    // ------------------------------------------------
    // OBTENER PRECIO VIGENTE
    // ------------------------------------------------

    fun obtenerPrecioVigente(

        productoId: String

    ) {

        repository.obtenerPrecioVigente(
            productoId
        ) { precio ->

            _precioVigente.postValue(
                precio
            )
        }
    }

    // ------------------------------------------------
    // OBTENER HISTORIAL PRECIOS
    // ------------------------------------------------

    fun obtenerHistorialPrecios(

        productoId: String

    ) {

        repository.obtenerHistorialPrecios(
            productoId
        ) { precios ->

            _listaPrecios.postValue(
                precios
            )
        }
    }

    // ------------------------------------------------
    // ACTUALIZAR PRECIO
    // ------------------------------------------------

    fun actualizarPrecio(

        precio: Precio

    ) {

        repository.actualizarPrecio(
            precio
        ) { correcto ->

            if (!correcto) {

                _error.postValue(
                    "Error al actualizar precio"
                )
            }
        }
    }

    // ------------------------------------------------
    // ELIMINAR PRECIO
    // ------------------------------------------------

    fun eliminarPrecio(

        precioId: String

    ) {

        repository.eliminarPrecio(
            precioId
        ) { correcto ->

            if (!correcto) {

                _error.postValue(
                    "Error al eliminar precio"
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

    // ------------------------------------------------
    // LIMPIAR PRECIO GUARDADO
    // ------------------------------------------------

    fun limpiarPrecioGuardado() {

        _precioGuardado.value = false
    }
}