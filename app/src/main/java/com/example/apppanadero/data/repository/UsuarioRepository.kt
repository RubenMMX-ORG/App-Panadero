package com.example.apppanadero.data.repository

import com.example.apppanadero.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class UsuarioRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    //  REGISTRO
    fun registrarUsuario(
        email: String,
        password: String,
        usuario: Usuario,
        onResult: (FirebaseUser?) -> Unit
    ) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val user = auth.currentUser

                    user?.let {
                        guardarUsuarioFirestore(it, usuario)
                    }

                    onResult(user)

                } else {
                    onResult(null)
                }
            }
    }

    //  LOGIN EMAIL
    fun loginUsuario(
        email: String,
        password: String,
        onResult: (FirebaseUser?) -> Unit
    ) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val user = auth.currentUser

                    user?.let {
                        guardarSiNoExiste(it) // 🔥 AÑADIDO
                    }

                    onResult(user)

                } else {
                    onResult(null)
                }
            }
    }

    //  LOGIN GOOGLE
    fun loginConGoogle(
        idToken: String,
        onResult: (FirebaseUser?) -> Unit
    ) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val user = auth.currentUser

                    user?.let {
                        guardarSiNoExiste(it) //  AÑADIDO
                    }

                    onResult(user)

                } else {
                    onResult(null)
                }
            }
    }

    //  CREAR USUARIO SI NO EXISTE
    private fun guardarSiNoExiste(user: FirebaseUser) {

        val docRef = db.collection("usuarios").document(user.uid)

        docRef.get().addOnSuccessListener { document ->

            if (!document.exists()) {

                val nuevoUsuario = Usuario(
                    nombre = user.displayName ?: "",
                    email = user.email ?: "",
                    rol = "cliente" // 🔥 por defecto
                )

                docRef.set(nuevoUsuario)
            }
        }
    }

    //  GUARDAR USUARIO COMPLETO (registro manual)
    private fun guardarUsuarioFirestore(user: FirebaseUser, usuario: Usuario) {

        db.collection("usuarios")
            .document(user.uid)
            .set(usuario)
    }

    //  OBTENER USUARIO
    fun obtenerUsuario(
        uid: String,
        onResult: (Usuario?) -> Unit
    ) {

        db.collection("usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                val usuario = document.toObject(Usuario::class.java)
                onResult(usuario)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    //  LOGOUT
    fun logout() {
        auth.signOut()
    }
}