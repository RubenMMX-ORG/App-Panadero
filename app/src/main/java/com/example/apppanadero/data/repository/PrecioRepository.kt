package com.example.apppanadero.data.repository

import com.example.apppanadero.data.model.Precio
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class PrecioRepository {

    // ------------------------------------------------
    // FIRESTORE
    // ------------------------------------------------

    private val db =
        FirebaseFirestore.getInstance()

    // ------------------------------------------------
    // GUARDAR PRECIO
    // ------------------------------------------------

    fun guardarPrecio(

        precio: Precio,
        respuesta: (Boolean) -> Unit

    ) {

        // Primero desactivamos precios vigentes
        desactivarPreciosVigentes(
            precio.productoId
        ) {

            val documento =
                db.collection("precios")
                    .document()

            val precioConId =
                precio.copy(

                    id = documento.id,

                    fechaInicio = Timestamp.now(),

                    fechaFin = null,

                    vigencia = true
                )

            documento
                .set(precioConId)
                .addOnSuccessListener {

                    respuesta(true)
                }
                .addOnFailureListener {

                    respuesta(false)
                }
        }
    }

    // ------------------------------------------------
    // OBTENER PRECIO VIGENTE
    // ------------------------------------------------

    fun obtenerPrecioVigente(

        productoId: String,
        respuesta: (Precio?) -> Unit

    ) {

        db.collection("precios")
            .whereEqualTo(
                "productoId",
                productoId
            )
            .whereEqualTo(
                "vigencia",
                true
            )
            .limit(1)
            .get()
            .addOnSuccessListener { resultado ->

                if (!resultado.isEmpty) {

                    val documento =
                        resultado.documents.first()

                    val precio =
                        documento.toObject(
                            Precio::class.java
                        )

                    respuesta(
                        precio?.copy(
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
    // OBTENER HISTORIAL PRECIOS
    // ------------------------------------------------

    fun obtenerHistorialPrecios(

        productoId: String,
        respuesta: (List<Precio>) -> Unit

    ) {

        db.collection("precios")
            .whereEqualTo(
                "productoId",
                productoId
            )
            .get()
            .addOnSuccessListener { resultado ->

                val listaPrecios =
                    resultado.documents.mapNotNull { documento ->

                        val precio =
                            documento.toObject(
                                Precio::class.java
                            )

                        precio?.copy(
                            id = documento.id
                        )
                    }

                respuesta(listaPrecios)
            }
            .addOnFailureListener {

                respuesta(emptyList())
            }
    }

    // ------------------------------------------------
    // ACTUALIZAR PRECIO
    // ------------------------------------------------

    fun actualizarPrecio(

        precio: Precio,
        respuesta: (Boolean) -> Unit

    ) {

        db.collection("precios")
            .document(precio.id)
            .set(precio)
            .addOnSuccessListener {

                respuesta(true)
            }
            .addOnFailureListener {

                respuesta(false)
            }
    }

    // ------------------------------------------------
    // ELIMINAR PRECIO
    // ------------------------------------------------

    fun eliminarPrecio(

        precioId: String,
        respuesta: (Boolean) -> Unit

    ) {

        db.collection("precios")
            .document(precioId)
            .delete()
            .addOnSuccessListener {

                respuesta(true)
            }
            .addOnFailureListener {

                respuesta(false)
            }
    }

    // ------------------------------------------------
    // DESACTIVAR PRECIOS VIGENTES
    // ------------------------------------------------

    private fun desactivarPreciosVigentes(

        productoId: String,
        respuesta: (Boolean) -> Unit

    ) {

        db.collection("precios")
            .whereEqualTo(
                "productoId",
                productoId
            )
            .whereEqualTo(
                "vigencia",
                true
            )
            .get()
            .addOnSuccessListener { resultado ->

                if (resultado.isEmpty) {

                    respuesta(true)

                } else {

                    val batch =
                        db.batch()

                    resultado.documents.forEach { documento ->

                        batch.update(
                            documento.reference,
                            mapOf(
                                "vigencia" to false,
                                "fechaFin" to Timestamp.now()
                            )
                        )
                    }

                    batch.commit()
                        .addOnSuccessListener {

                            respuesta(true)
                        }
                        .addOnFailureListener {

                            respuesta(false)
                        }
                }
            }
            .addOnFailureListener {

                respuesta(false)
            }
    }
}