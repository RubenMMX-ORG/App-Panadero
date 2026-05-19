package com.example.apppanadero.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apppanadero.data.model.Usuario
import com.example.apppanadero.data.repository.UsuarioRepository
import com.google.firebase.auth.FirebaseUser

class UsuarioViewModel(
    private val repository: UsuarioRepository
) : ViewModel() {

    // ---------------------------------------------------
    // LIVE DATA
    // ---------------------------------------------------

    // Usuario completo obtenido desde Firestore
    // Aquí guardamos:
    // - rol
    // - aprobado
    // - datos personales
    // etc...
    private val _usuario = MutableLiveData<Usuario?>()

    // LiveData público SOLO lectura
    // La UI observará este usuario
    val usuario: LiveData<Usuario?> = _usuario

    // ------------------------------------------------
    // LISTA CLIENTES
    // ------------------------------------------------

    private val _listaClientes =

        MutableLiveData<List<Usuario>>()

    val listaClientes:
            LiveData<List<Usuario>> =

        _listaClientes


    // ---------------------------------------------------
    // REGISTRO FIREBASE AUTH
    // ---------------------------------------------------

    // Esta función SOLO registra en Firebase Authentication
    // NO guarda todavía en Firestore
    //
    // respuesta devuelve:
    // - FirebaseUser si todo va bien
    // - String error si falla
    fun registrarUsuario(
        email: String,
        password: String,
        respuesta: (FirebaseUser?, String?) -> Unit
    ) {

        repository.registrarUsuario(
            email,
            password,
            respuesta
        )
    }


    // ---------------------------------------------------
    // LOGIN EMAIL Y CONTRASEÑA
    // ---------------------------------------------------

    // Inicia sesión con email/password
    //
    // respuesta devuelve:
    // - usuario Firebase si login correcto
    // - mensaje error si falla
    fun loginUsuario(
        email: String,
        password: String,
        respuesta: (FirebaseUser?, String?) -> Unit
    ) {

        repository.loginUsuario(
            email,
            password,
            respuesta
        )
    }


    // ---------------------------------------------------
    // LOGIN GOOGLE
    // ---------------------------------------------------

    // Login usando credenciales Google
    //
    // respuesta devuelve:
    // - usuario Firebase si login correcto
    // - mensaje error si falla
    fun loginConGoogle(
        idToken: String,
        respuesta: (FirebaseUser?, String?) -> Unit
    ) {

        repository.loginConGoogle(
            idToken,
            respuesta
        )
    }


    // ---------------------------------------------------
    // GUARDAR USUARIO EN FIRESTORE
    // ---------------------------------------------------

    // Guarda datos completos del usuario en Firestore
    //
    // IMPORTANTE:
    // Firebase Auth SOLO guarda email/password
    // Firestore guarda:
    // - rol
    // - aprobado
    // - nombre
    // etc...
    fun guardarUsuario(
        usuario: Usuario,
        respuesta: (Boolean) -> Unit
    ) {

        // Obtenemos usuario autenticado actual
        val uid = repository.getCurrentUser()?.uid ?: return

        // Guardamos documento Firestore
        repository.guardarUsuario(
            uid,
            usuario,
            respuesta
        )
    }


    // ---------------------------------------------------
    // OBTENER USUARIO FIRESTORE
    // ---------------------------------------------------

    // Obtiene datos completos desde Firestore
    // y actualiza el LiveData usuario
    fun obtenerUsuario(uid: String) {

        repository.obtenerUsuario(uid) { usuarioFirestore ->

            // Guardamos resultado en LiveData
            _usuario.postValue(usuarioFirestore)
        }
    }

    // ------------------------------------------------
    // OBTENER USUARIO POR ID
    // ------------------------------------------------

    fun obtenerUsuarioPorId(

        usuarioId: String

    ) {

        repository.obtenerUsuarioPorId(

            usuarioId

        ) { usuario ->

            _usuario.postValue(
                usuario
            )
        }
    }




    // ---------------------------------------------------
    // COMPROBAR SESIÓN ACTUAL
    // ---------------------------------------------------

    // Devuelve directamente el usuario autenticado actual
    // sin usar LiveData
    fun getCurrentUser(): FirebaseUser? {

        return repository.getCurrentUser()
    }

    // ------------------------------------------------
    // OBTENER CLIENTES
    // ------------------------------------------------

    fun obtenerClientes() {

        repository.obtenerClientes {

            _listaClientes.postValue(it)
        }
    }


    // ---------------------------------------------------
    // LOGOUT
    // ---------------------------------------------------

    // Cierra sesión Firebase
    fun logout() {

        repository.logout()
    }
}