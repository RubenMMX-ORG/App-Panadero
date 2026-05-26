package com.example.apppanadero.data.repository

import com.example.apppanadero.data.model.Pedido
import com.google.firebase.firestore.FirebaseFirestore

class PedidoRepository {

    // Instancia Firestore
    private val db = FirebaseFirestore.getInstance()

    // ------------------------------------------------
    // GUARDAR PEDIDO
    // ------------------------------------------------

    fun guardarPedido(

        pedido: Pedido,
        respuesta: (Boolean) -> Unit

    ) {

        // Obtenemos siguiente número pedido
        obtenerSiguienteNumeroPedido { siguienteNumero ->

            // Documento Firestore
            val documento =
                db.collection("pedidos").document()

            // Pedido final
            val pedidoConId = pedido.copy(

                id = documento.id,

                numeroPedido = siguienteNumero
            )

            documento
                .set(pedidoConId)
                .addOnSuccessListener {

                    respuesta(true)
                }
                .addOnFailureListener {

                    respuesta(false)
                }
        }
    }

    // ------------------------------------------------
    // OBTENER PEDIDOS CLIENTE
    // ------------------------------------------------

    fun obtenerPedidosCliente(
        clienteId: String,
        respuesta: (List<Pedido>) -> Unit
    ) {

        db.collection("pedidos")
            .whereEqualTo("clienteId", clienteId)
            .get()
            .addOnSuccessListener { resultado ->

                val listaPedidos = resultado.documents.mapNotNull { documento ->

                    val pedido =
                        documento.toObject(Pedido::class.java)

                    pedido?.copy(id = documento.id)
                }

                respuesta(listaPedidos)
            }
            .addOnFailureListener {

                respuesta(emptyList())
            }
    }

    // ------------------------------------------------
    // OBTENER TODOS LOS PEDIDOS
    // ------------------------------------------------

    fun obtenerTodosPedidos(
        respuesta: (List<Pedido>) -> Unit
    ) {

        db.collection("pedidos")
            .get()
            .addOnSuccessListener { resultado ->

                val listaPedidos =
                    resultado.documents.mapNotNull { documento ->

                        val pedido =
                            documento.toObject(Pedido::class.java)

                        pedido?.copy(
                            id = documento.id
                        )
                    }

                respuesta(listaPedidos)
            }
            .addOnFailureListener {

                respuesta(emptyList())
            }
    }

    // ------------------------------------------------
    // OBTENER PEDIDO POR ID
    // ------------------------------------------------

    fun obtenerPedidoPorId(

        pedidoId: String,
        respuesta: (Pedido?) -> Unit

    ) {

        db.collection("pedidos")
            .document(pedidoId)
            .get()
            .addOnSuccessListener { documento ->

                if (documento.exists()) {

                    val pedido =
                        documento.toObject(Pedido::class.java)

                    respuesta(
                        pedido?.copy(
                            id = documento.id
                        )
                    )

                } else {

                    respuesta(null)
                }
            }
            .addOnFailureListener {

                respuesta(null)
            }
    }

    // ------------------------------------------------
    // ACTUALIZAR ESTADO PEDIDO
    // ------------------------------------------------

    fun actualizarEstadoPedido(
        pedidoId: String,
        nuevoEstado: String,
        respuesta: (Boolean) -> Unit
    ) {

        db.collection("pedidos")
            .document(pedidoId)
            .update("estado", nuevoEstado)
            .addOnSuccessListener {

                respuesta(true)
            }
            .addOnFailureListener {

                respuesta(false)
            }
    }

    // ------------------------------------------------
    // ACTUALIZAR PEDIDO
    // ------------------------------------------------

    fun actualizarPedido(

        pedido: Pedido,
        respuesta: (Boolean) -> Unit

    ) {

        db.collection("pedidos")
            .document(pedido.id)
            .set(pedido)
            .addOnSuccessListener {

                respuesta(true)
            }
            .addOnFailureListener {

                respuesta(false)
            }
    }

    // ------------------------------------------------
    // ELIMINAR PEDIDO
    // ------------------------------------------------

    fun eliminarPedido(
        pedidoId: String,
        respuesta: (Boolean) -> Unit
    ) {

        db.collection("pedidos")
            .document(pedidoId)
            .delete()
            .addOnSuccessListener {

                respuesta(true)
            }
            .addOnFailureListener {

                respuesta(false)
            }
    }

    // ------------------------------------------------
    // ACTUALIZAR DEVOLUCIÓN
    // ------------------------------------------------

    fun actualizarCantidadDevuelta(

        pedidoId: String,

        lineaPedidoId: String,

        cantidadDevuelta: Int,

        respuesta: (Boolean) -> Unit

    ) {

        // ------------------------------------------------
        // OBTENER PEDIDO
        // ------------------------------------------------

        db.collection("pedidos")

            .document(pedidoId)

            .get()

            .addOnSuccessListener { documento ->

                val pedido =

                    documento.toObject(
                        Pedido::class.java
                    )

                if (pedido != null) {

                    // ------------------------------------------------
                    // MODIFICAR LÍNEA PEDIDO
                    // ------------------------------------------------

                    val nuevasLineas =

                        pedido.lineasPedido.map { linea ->

                            if (linea.productoId == lineaPedidoId) {

                                linea.copy(

                                    cantidadDevuelta =
                                        cantidadDevuelta
                                )

                            } else {

                                linea
                            }
                        }

                    // ------------------------------------------------
                    // ACTUALIZAR PEDIDO
                    // ------------------------------------------------

                    db.collection("pedidos")

                        .document(pedidoId)

                        .update(

                            "lineasPedido",

                            nuevasLineas
                        )

                        .addOnSuccessListener {

                            respuesta(true)
                        }

                        .addOnFailureListener {

                            respuesta(false)
                        }

                } else {

                    respuesta(false)
                }
            }

            .addOnFailureListener {

                respuesta(false)
            }
    }

    // ------------------------------------------------
    // INCREMENTAR EL NUMERO DE PEDIDO
    // ------------------------------------------------

    fun obtenerSiguienteNumeroPedido(
        respuesta: (Int) -> Unit
    ) {

        db.collection("pedidos")
            .get()
            .addOnSuccessListener { resultado ->

                val siguienteNumero =
                    resultado.size() + 1

                respuesta(siguienteNumero)
            }
    }
}