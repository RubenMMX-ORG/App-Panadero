package com.example.apppanadero.data.repository

import com.example.apppanadero.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class UsuarioRepository {

    //Variable para auth
    private val auth = FirebaseAuth.getInstance()

    //variable para Firestore
    private val db = FirebaseFirestore.getInstance()

    //Funcion para firebase auth
    fun registrarUsuario(
        email: String,
        password: String,
        respuesta: (FirebaseUser?, String?) -> Unit
    ) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { resultado ->

                if (resultado.isSuccessful) {
                    respuesta(auth.currentUser, null)
                } else {
                    if (resultado.exception is FirebaseAuthUserCollisionException) {

                        respuesta(null, "Ya existe una cuenta con este email")

                    } else {

                        respuesta(null, "Error al registrarse")
                    }
                }
            }
    }

    //Funcion para firebase auth
    fun loginConGoogle(
        idToken: String,
        respuesta: (FirebaseUser?, String?) -> Unit
    ) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { resultado ->

                if (resultado.isSuccessful) {
                    respuesta(auth.currentUser,null)
                } else {
                    respuesta(null, "Credenciales incorrectas")
                }
            }
    }


    //Funcion para firebase auth
    fun loginUsuario(
        email: String,
        password: String,
        respuesta: (FirebaseUser?, String?) -> Unit
    ) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { resultado ->

                if (resultado.isSuccessful) {
                    respuesta(auth.currentUser,null)
                } else {
                    respuesta(null, "Email o contraseña incorrectos")
                }
            }
    }

    //Funcion para Firebase Firestore
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

    fun obtenerUsuario(
        uid: String,
        respuesta: (Usuario?) -> Unit
    ) {

        db.collection("usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {
                    val usuario = document.toObject(Usuario::class.java)
                    respuesta(usuario)
                } else {
                    respuesta(null)
                }
            }
            .addOnFailureListener {
                respuesta(null)
            }
    }

    // ---------------------------------------------------
    // OBTENER USUARIO POR ID
    // ---------------------------------------------------

    fun obtenerUsuarioPorId(

        usuarioId: String,

        callback: (Usuario?) -> Unit

    ) {

        db
            .collection("usuarios")
            .document(usuarioId)
            .get()

            .addOnSuccessListener { documento ->

                if (documento.exists()) {

                    val usuario =

                        documento.toObject(
                            Usuario::class.java
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

                    resultado.documents.mapNotNull {

                        it.toObject(
                            Usuario::class.java
                        )
                    }

                callback(listaClientes)
            }

            .addOnFailureListener {

                callback(emptyList())
            }
    }

    //Funcion de Auth
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    //Funcion de Auth
    fun logout() {
        auth.signOut()
    }
}