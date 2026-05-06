package com.example.apppanadero.data.repository

import com.example.apppanadero.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class UsuarioRepository {

    //Variable para auth
    private val auth = FirebaseAuth.getInstance()

    //variable para Firestore
    private val db = FirebaseFirestore.getInstance()

    //Funcion para firebase auth
    fun registrarUsuario(
        email: String,
        password: String,
        onResult: (FirebaseUser?) -> Unit
    ) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    onResult(auth.currentUser)
                } else {
                    onResult(null)
                }
            }
    }

    //Funcion para firebase auth
    fun loginConGoogle(
        idToken: String,
        onResult: (FirebaseUser?) -> Unit
    ) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    onResult(auth.currentUser)
                } else {
                    onResult(null)
                }
            }
    }

    //Funcion para firebase auth
    fun loginUsuario(
        email: String,
        password: String,
        onResult: (FirebaseUser?) -> Unit
    ) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    onResult(auth.currentUser)
                } else {
                    onResult(null)
                }
            }
    }

    //Funcion para Firebase Firestore
    fun guardarUsuario(
        uid: String,
        usuario: Usuario,
        onResult: (Boolean) -> Unit
    ) {

        db.collection("usuarios")
            .document(uid)
            .set(usuario)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    //Funcion para Firebase Firestore
    fun obtenerUsuario(
        uid: String,
        onResult: (Usuario?) -> Unit
    ) {

        db.collection("usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {
                    val usuario = document.toObject(Usuario::class.java)
                    onResult(usuario)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
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