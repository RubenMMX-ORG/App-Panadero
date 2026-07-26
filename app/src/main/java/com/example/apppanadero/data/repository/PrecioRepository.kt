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
    // CREAR PRECIO
    // ------------------------------------------------

    fun guardarPrecio(

        precio: Precio,

        respuesta: (Boolean) -> Unit

    ) {

        val documento =
            db.collection("precios")
                .document()

        val precioConId =
            precio.copy(
                id = documento.id
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
                "vigente",
                true
            )
            .limit(1)
            .get()
            .addOnSuccessListener { resultado ->

                val documento =
                    resultado.documents.firstOrNull()

                val precio =
                    documento?.toObject(
                        Precio::class.java
                    )

                respuesta(precio)
            }
            .addOnFailureListener {

                respuesta(null)
            }
    }

    // ------------------------------------------------
    // OBTENER TODOS LOS PRECIOS VIGENTES
    // ------------------------------------------------
    fun obtenerPreciosVigentes(

    respuesta: (List<Precio>) -> Unit

) {

    db.collection("precios")

        .whereEqualTo(
            "vigente",
            true
        )

        .get()

        .addOnSuccessListener { resultado ->

            val listaPrecios =

                resultado.documents.mapNotNull {

                    it.toObject(
                        Precio::class.java
                    )
                }

            respuesta(listaPrecios)
        }

        .addOnFailureListener {

            respuesta(emptyList())
        }
}

    // ------------------------------------------------
    // OBTENER HISTORICO PRECIOS
    // ------------------------------------------------

    fun obtenerHistoricoPrecios(

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

                    resultado.documents.mapNotNull {

                        it.toObject(
                            Precio::class.java
                        )
                    }

                respuesta(listaPrecios)
            }
            .addOnFailureListener {

                respuesta(emptyList())
            }
    }

    // ------------------------------------------------
    // ACTUALIZAR PRECIO PRODUCTO
    // ------------------------------------------------

    fun actualizarPrecio(

        productoId: String,

        nuevoPrecio: Double,

        respuesta: (Boolean) -> Unit

    ) {

        //  Buscar precio vigente actual
        obtenerPrecioVigente(productoId) { precioActual ->

            if (precioActual != null) {

                // 2 Desactivar precio antiguo
                db.collection("precios")
                    .document(precioActual.id)
                    .update(

                        mapOf(

                            "vigente" to false,

                            "fechaFin" to
                                    Timestamp.now()
                        )
                    )
                    .addOnSuccessListener {

                        // 3 Crear nuevo precio
                        val nuevo = Precio(

                            productoId = productoId,

                            precio = nuevoPrecio,

                            fechaInicio =
                                Timestamp.now(),

                            vigente = true
                        )

                        guardarPrecio(
                            nuevo,
                            respuesta
                        )
                    }
                    .addOnFailureListener {

                        respuesta(false)
                    }

            } else {

                // Si no existía precio previo
                val nuevo = Precio(

                    productoId = productoId,

                    precio = nuevoPrecio,

                    fechaInicio =
                        Timestamp.now(),

                    vigente = true
                )

                guardarPrecio(
                    nuevo,
                    respuesta
                )
            }
        }
    }

}
