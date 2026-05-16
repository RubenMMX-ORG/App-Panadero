package com.example.apppanadero.data.repository

import com.example.apppanadero.data.model.Producto
import com.google.firebase.firestore.FirebaseFirestore

class ProductoRepository {

    // ------------------------------------------------
    // FIRESTORE
    // ------------------------------------------------

    private val db =
        FirebaseFirestore.getInstance()

    // ------------------------------------------------
    // GUARDAR PRODUCTO
    // ------------------------------------------------

    fun guardarProducto(

        producto: Producto,
        respuesta: (Boolean) -> Unit

    ) {

        val documento =
            db.collection("productos")
                .document()

        val productoConId =
            producto.copy(
                id = documento.id
            )

        documento
            .set(productoConId)
            .addOnSuccessListener {

                respuesta(true)
            }
            .addOnFailureListener {

                respuesta(false)
            }
    }

    // ------------------------------------------------
    // OBTENER TODOS PRODUCTOS
    // ------------------------------------------------

    fun obtenerTodosProductos(

        respuesta: (List<Producto>) -> Unit

    ) {

        db.collection("productos")
            .get()
            .addOnSuccessListener { resultado ->

                val listaProductos =
                    resultado.documents.mapNotNull { documento ->

                        val producto =
                            documento.toObject(
                                Producto::class.java
                            )

                        producto?.copy(
                            id = documento.id
                        )
                    }

                respuesta(listaProductos)
            }
            .addOnFailureListener {

                respuesta(emptyList())
            }
    }

    // ------------------------------------------------
    // OBTENER PRODUCTO POR ID
    // ------------------------------------------------

    fun obtenerProductoPorId(

        productoId: String,
        respuesta: (Producto?) -> Unit

    ) {

        db.collection("productos")
            .document(productoId)
            .get()
            .addOnSuccessListener { documento ->

                if (documento.exists()) {

                    val producto =
                        documento.toObject(
                            Producto::class.java
                        )

                    respuesta(
                        producto?.copy(
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
    // ACTUALIZAR PRODUCTO
    // ------------------------------------------------

    fun actualizarProducto(

        producto: Producto,
        respuesta: (Boolean) -> Unit

    ) {

        db.collection("productos")
            .document(producto.id)
            .set(producto)
            .addOnSuccessListener {

                respuesta(true)
            }
            .addOnFailureListener {

                respuesta(false)
            }
    }

    // ------------------------------------------------
    // ELIMINAR PRODUCTO
    // ------------------------------------------------

    fun eliminarProducto(

        productoId: String,
        respuesta: (Boolean) -> Unit

    ) {

        db.collection("productos")
            .document(productoId)
            .delete()
            .addOnSuccessListener {

                respuesta(true)
            }
            .addOnFailureListener {

                respuesta(false)
            }
    }
}