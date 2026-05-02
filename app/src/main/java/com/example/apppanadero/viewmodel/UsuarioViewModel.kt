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

    // Usuario autenticado (Firebase Auth)
    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    val firebaseUser: LiveData<FirebaseUser?> = _firebaseUser

    // Usuario completo (Firestore)
    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: LiveData<Usuario?> = _usuario

    // REGISTRO
    fun registrarUsuario(email: String, password: String, usuario: Usuario) {

        repository.registrarUsuario(email, password, usuario) { user ->

            _firebaseUser.postValue(user)

            user?.let {
                obtenerUsuario(it.uid) // Carga ususario una vez registrado
            }
        }
    }

    // LOGIN EMAIL
    fun loginUsuario(email: String, password: String) {

        repository.loginUsuario(email, password) { user ->
            _firebaseUser.postValue(user)
        }
    }

    // LOGIN GOOGLE
    fun loginConGoogle(idToken: String) {

        repository.loginConGoogle(idToken) { user ->
            _firebaseUser.postValue(user)
        }
    }

    // OBTENER USUARIO (Firestore)
    fun obtenerUsuario(uid: String) {

        repository.obtenerUsuario(uid) { userFirestore ->
            _usuario.postValue(userFirestore)
        }
    }

    // LOGOUT
    fun logout() {
        repository.logout()
        _firebaseUser.postValue(null)
        _usuario.postValue(null)
    }
}