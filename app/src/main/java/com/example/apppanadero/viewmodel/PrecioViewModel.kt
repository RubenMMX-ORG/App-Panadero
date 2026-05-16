package com.example.apppanadero.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apppanadero.data.model.Precio
import com.example.apppanadero.data.repository.PrecioRepository

class PrecioViewModel(

    private val repository:
    PrecioRepository

) : ViewModel() {

    // ------------------------------------------------
    // PRECIO ACTUAL
    // ------------------------------------------------

    private val _precioActual =
        MutableLiveData<Precio?>()

    val precioActual:
            LiveData<Precio?> =
        _precioActual

    // ------------------------------------------------
    // HISTORICO
    // ------------------------------------------------

    private val _historicoPrecios =
        MutableLiveData<List<Precio>>()

    val historicoPrecios:
            LiveData<List<Precio>> =
        _historicoPrecios

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
    // OBTENER PRECIO VIGENTE
    // ------------------------------------------------

    fun obtenerPrecioVigente(
        productoId: String
    ) {

        repository.obtenerPrecioVigente(
            productoId
        ) { precio ->

            _precioActual.postValue(
                precio
            )
        }
    }

    // ------------------------------------------------
    // HISTORICO PRECIOS
    // ------------------------------------------------

    fun obtenerHistoricoPrecios(
        productoId: String
    ) {

        repository.obtenerHistoricoPrecios(
            productoId
        ) { lista ->

            _historicoPrecios.postValue(
                lista
            )
        }
    }

    // ------------------------------------------------
    // ACTUALIZAR PRECIO
    // ------------------------------------------------

    fun actualizarPrecio(

        productoId: String,

        nuevoPrecio: Double

    ) {

        repository.actualizarPrecio(

            productoId,
            nuevoPrecio

        ) { correcto ->

            if (correcto) {

                _precioGuardado.postValue(
                    true
                )

            } else {

                _error.postValue(
                    "Error actualizando precio"
                )
            }
        }
    }

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

                _precioGuardado.postValue(
                    true
                )

            } else {

                _error.postValue(
                    "Error al guardar precio"
                )
            }
        }
    }
}