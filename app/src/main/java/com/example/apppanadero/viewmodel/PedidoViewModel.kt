package com.example.apppanadero.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apppanadero.data.model.Pedido
import com.example.apppanadero.data.repository.PedidoRepository

// ViewModel = cerebro de la UI
// Guarda y gestiona datos relacionados con pedidos
class PedidoViewModel(

    // Dependencia Repository
    private val repository: PedidoRepository



) : ViewModel() {

    private val _pedidoDetalle: MutableLiveData<Pedido?> = MutableLiveData()

    val pedidoDetalle: LiveData<Pedido?> = _pedidoDetalle


    // ------------------------------------------------
    // LISTA PEDIDOS
    // ------------------------------------------------

    // LiveData privado modificable
    private val _listaPedidos = MutableLiveData<List<Pedido>>()

    // LiveData público solo lectura
    val listaPedidos: LiveData<List<Pedido>> = _listaPedidos

    // ------------------------------------------------
    // PEDIDO GUARDADO
    // ------------------------------------------------

    // true = guardado correcto
    // false = error
    private val _pedidoGuardado = MutableLiveData<Boolean>()

    val pedidoGuardado: LiveData<Boolean> = _pedidoGuardado

    // ------------------------------------------------
    // ERROR
    // ------------------------------------------------

    // Mensajes error para Toast
    private val _error = MutableLiveData<String>()

    val error: LiveData<String> = _error

    // ------------------------------------------------
    // GUARDAR PEDIDO
    // ------------------------------------------------

    fun guardarPedido(pedido: Pedido) {

        repository.guardarPedido(pedido) { guardadoCorrecto ->

            if (guardadoCorrecto) {

                _pedidoGuardado.postValue(true)

            } else {

                _error.postValue("Error al guardar pedido")
            }
        }
    }

    // ------------------------------------------------
    // OBTENER PEDIDOS CLIENTE
    // ------------------------------------------------

    fun obtenerPedidosCliente(clienteId: String) {

        repository.obtenerPedidosCliente(clienteId) { pedidos ->

            _listaPedidos.postValue(pedidos)
        }
    }

    // ------------------------------------------------
    // OBTENER TODOS PEDIDOS
    // ------------------------------------------------

    fun obtenerTodosPedidos() {

        repository.obtenerTodosPedidos { pedidos ->

            _listaPedidos.postValue(pedidos)
        }
    }

    // ------------------------------------------------
    // ACTUALIZAR ESTADO PEDIDO
    // ------------------------------------------------

    fun actualizarEstadoPedido(
        pedidoId: String,
        nuevoEstado: String
    ) {

        repository.actualizarEstadoPedido(
            pedidoId,
            nuevoEstado
        ) { actualizadoCorrectamente ->

            if (!actualizadoCorrectamente) {

                _error.postValue("Error al actualizar pedido")
            }
        }
    }

    // ------------------------------------------------
    // OBTENER PEDIDO POR ID
    // ------------------------------------------------

    fun obtenerPedidoPorId(
        pedidoId: String
    ) {

        repository.obtenerPedidoPorId(pedidoId) { pedido ->

            _pedidoDetalle.postValue(pedido)
        }
    }

    // ------------------------------------------------
    // ACTUALIZAR ESTADO PEDIDO
    // ------------------------------------------------

    fun actualizarPedido(
        pedido: Pedido
    ) {

        repository.actualizarPedido(
            pedido
        ) { actualizado ->

            if (actualizado) {

                // Activamos observer
                _pedidoGuardado.postValue(true)

            } else {

                _error.postValue(
                    "Error al actualizar pedido"
                )
            }
        }
    }

    // ------------------------------------------------
    // ELIMINAR PEDIDO
    // ------------------------------------------------

    fun eliminarPedido(pedidoId: String) {

        repository.eliminarPedido(pedidoId) { eliminadoCorrectamente ->

            if (!eliminadoCorrectamente) {

                _error.postValue("Error al eliminar pedido")
            }
        }
    }

    // ------------------------------------------------
    // LIMPIAR ERROR
    // ------------------------------------------------

    // Evita bucles infinitos Toast
    fun limpiarError() {

        _error.value = ""
    }

    // ------------------------------------------------
    // LIMPIAR PEDIDO GUARDADO
    // ------------------------------------------------

    fun limpiarPedidoGuardado() {

        _pedidoGuardado.value = false
    }
}