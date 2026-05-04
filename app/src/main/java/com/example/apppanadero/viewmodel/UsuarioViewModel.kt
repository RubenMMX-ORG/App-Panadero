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

    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    val firebaseUser: LiveData<FirebaseUser?> = _firebaseUser

    fun registrarUsuario(email: String, password: String) {

        repository.registrarUsuario(email, password) { user ->
            _firebaseUser.postValue(user)
        }
    }

    fun loginConGoogle(idToken: String) {
        repository.loginConGoogle(idToken) { user ->
            _firebaseUser.postValue(user)
        }
    }

    fun loginUsuario(email: String, password: String) {

        repository.loginUsuario(email, password) { user ->
            _firebaseUser.postValue(user)
        }
    }

    fun checkSesion() {
        _firebaseUser.value = repository.getCurrentUser()
    }

    fun logout() {
        repository.logout()
        _firebaseUser.value = null
    }
}