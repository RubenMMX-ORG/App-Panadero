package com.example.apppanadero.data.repository

import com.example.apppanadero.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class UsuarioRepository {

    private val auth = FirebaseAuth.getInstance()

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

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun logout() {
        auth.signOut()
    }
}