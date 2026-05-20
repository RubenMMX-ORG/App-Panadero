package com.example.apppanadero.data.repository

import com.example.apppanadero.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class UsuarioRepository {

    // ------------------------------------------------
    // FIREBASE AUTH
    // ------------------------------------------------

    private val auth =
        FirebaseAuth.getInstance()

    // ------------------------------------------------
    // FIRESTORE
    // ------------------------------------------------

    private val db =
        FirebaseFirestore.getInstance()

    // ------------------------------------------------
    // REGISTRAR USUARIO
    // ------------------------------------------------

    // SOLO registra en FirebaseAuth
    //
    // Firestore se guarda después
    fun registrarUsuario(

        email: String,

        password: String,

        respuesta: (FirebaseUser?, String?) -> Unit

    ) {

        auth.createUserWithEmailAndPassword(

            email,
            password

        ).addOnCompleteListener { resultado ->

            if (resultado.isSuccessful) {

                respuesta(
                    auth.currentUser,
                    null
                )

            } else {

                if (resultado.exception
                    is FirebaseAuthUserCollisionException
                ) {

                    respuesta(

                        null,

                        "Ya existe una cuenta con este email"
                    )

                } else {

                    respuesta(

                        null,

                        "Error al registrarse"
                    )
                }
            }
        }
    }

    // ------------------------------------------------
    // LOGIN GOOGLE
    // ------------------------------------------------

    fun loginConGoogle(

        idToken: String,

        respuesta: (FirebaseUser?, String?) -> Unit

    ) {

        val credential =

            GoogleAuthProvider.getCredential(

                idToken,
                null
            )

        auth.signInWithCredential(
            credential
        )

            .addOnCompleteListener { resultado ->

                if (resultado.isSuccessful) {

                    respuesta(
                        auth.currentUser,
                        null
                    )

                } else {

                    respuesta(

                        null,

                        "Credenciales incorrectas"
                    )
                }
            }
    }

    // ------------------------------------------------
    // LOGIN EMAIL/PASSWORD
    // ------------------------------------------------

    fun loginUsuario(

        email: String,

        password: String,

        respuesta: (FirebaseUser?, String?) -> Unit

    ) {

        auth.signInWithEmailAndPassword(

            email,
            password

        ).addOnCompleteListener { resultado ->

            if (resultado.isSuccessful) {

                respuesta(
                    auth.currentUser,
                    null
                )

            } else {

                respuesta(

                    null,

                    "Email o contraseña incorrectos"
                )
            }
        }
    }

    // ------------------------------------------------
    // GUARDAR USUARIO FIRESTORE
    // ------------------------------------------------

    // IMPORTANTE:
    //
    // El uid FirebaseAuth
    // se usa como:
    //
    // - documentId Firestore
    // - id real usuario
    // - referencia pedidos
    //
    // usuarios/{uid}
    fun guardarUsuario(

        uid: String,

        usuario: Usuario,

        respuesta: (Boolean) -> Unit

    ) {

        db.collection("usuarios")

            .document(uid)

            .set(usuario)

            .addOnSuccessListener {

                respuesta(true)
            }

            .addOnFailureListener {

                respuesta(false)
            }
    }

    // ------------------------------------------------
    // OBTENER USUARIO
    // ------------------------------------------------

    // IMPORTANTE:
    //
    // Firestore NO mete automáticamente
    // el document.id dentro del data class.
    //
    // Por eso usamos:
    //
    // copy(id = document.id)
    //
    // para que Usuario.id tenga
    // el uid FirebaseAuth real.
    fun obtenerUsuario(

        uid: String,

        respuesta: (Usuario?) -> Unit

    ) {

        db.collection("usuarios")

            .document(uid)

            .get()

            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val usuario =

                        document.toObject(
                            Usuario::class.java
                        )?.copy(

                            id = document.id
                        )

                    respuesta(usuario)

                } else {

                    respuesta(null)
                }
            }

            .addOnFailureListener {

                respuesta(null)
            }
    }

    // ------------------------------------------------
    // OBTENER USUARIO POR ID
    // ------------------------------------------------

    fun obtenerUsuarioPorId(

        usuarioId: String,

        callback: (Usuario?) -> Unit

    ) {

        db.collection("usuarios")

            .document(usuarioId)

            .get()

            .addOnSuccessListener { documento ->

                if (documento.exists()) {

                    val usuario =

                        documento.toObject(
                            Usuario::class.java
                        )?.copy(

                            id = documento.id
                        )

                    callback(usuario)

                } else {

                    callback(null)
                }
            }

            .addOnFailureListener {

                callback(null)
            }
    }

    // ------------------------------------------------
    // OBTENER CLIENTES
    // ------------------------------------------------

    // Solo usuarios con rol cliente
    fun obtenerClientes(

        callback: (List<Usuario>) -> Unit

    ) {

        db.collection("usuarios")

            .whereEqualTo(

                "rol",
                "cliente"
            )

            .get()

            .addOnSuccessListener { resultado ->

                val listaClientes =

                    resultado.documents.mapNotNull { documento ->

                        val usuario =

                            documento.toObject(
                                Usuario::class.java
                            )

                        // ------------------------------------------------
                        // IMPORTANTE
                        // ------------------------------------------------
                        //
                        // Inyectamos document.id
                        // dentro del data class.
                        //
                        // Así:
                        //
                        // usuario.id == uid FirebaseAuth
                        //
                        usuario?.copy(

                            id = documento.id
                        )
                    }

                callback(listaClientes)
            }

            .addOnFailureListener {

                callback(emptyList())
            }
    }

    // ------------------------------------------------
    // USUARIO ACTUAL
    // ------------------------------------------------

    fun getCurrentUser(): FirebaseUser? {

        return auth.currentUser
    }

    // ------------------------------------------------
    // LOGOUT
    // ------------------------------------------------

    fun logout() {

        auth.signOut()
    }
}
