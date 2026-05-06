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

    //  LiveData interno (privado) que guarda el usuario autenticado de Firebase
    // Se usa para actualizar el estado cuando el login (Google o email) cambia
    private val _firebaseUser = MutableLiveData<FirebaseUser?>()

    //  LiveData interno (privado) que indica si el usuario se ha guardado correctamente en Firestore
    // true = guardado OK, false = error al guardar
    private val _usuarioGuardado = MutableLiveData<Boolean>()


    //  LiveData público (solo lectura) que observa la UI (Activity/Fragment)
    // Se usa para reaccionar cuando el usuario inicia sesión correctamente
    val firebaseUser: LiveData<FirebaseUser?> = _firebaseUser

    //  LiveData público (solo lectura) que observa la UI
    // Se usa para saber si el guardado en Firestore ha sido exitoso y decidir qué hacer (navegar o mostrar error)
    val usuarioGuardado: LiveData<Boolean> = _usuarioGuardado

    //  Usuario completo desde Firestore (rol, aprobado, etc.)
    private val _usuario = MutableLiveData<Usuario?>()

    //  LiveData público que observará la UI
    val usuario: LiveData<Usuario?> = _usuario

    //Funcion de Auth
    fun registrarUsuario(email: String, password: String) {

        repository.registrarUsuario(email, password) { user ->
            _firebaseUser.postValue(user)
        }
    }

    //Funcion de Auth
    fun loginConGoogle(idToken: String) {
        repository.loginConGoogle(idToken) { user ->
            _firebaseUser.postValue(user)
        }
    }

    //Funcion de Auth
    fun loginUsuario(email: String, password: String) {

        repository.loginUsuario(email, password) { user ->
            _firebaseUser.postValue(user)
        }
    }

    //Funcion de Firestore
    fun guardarUsuario(usuario: Usuario) {

        val uid = repository.getCurrentUser()?.uid ?: return

        repository.guardarUsuario(uid, usuario) { success ->
            _usuarioGuardado.postValue(success)
        }
    }

    // Obtener usuario desde Firestore
    fun obtenerUsuario(uid: String) {

        repository.obtenerUsuario(uid) { usuarioFirestore ->

            // Actualizamos el LiveData con el resultado
            _usuario.postValue(usuarioFirestore)
        }
    }

    //Funcion de Auth
    fun checkSesion() {
        _firebaseUser.value = repository.getCurrentUser()
    }

    //Funcion de Auth
    fun logout() {
        repository.logout()
        _firebaseUser.value = null
    }
}