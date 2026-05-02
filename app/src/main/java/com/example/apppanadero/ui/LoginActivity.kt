package com.example.apppanadero.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.example.apppanadero.R
import com.example.apppanadero.data.di.Injector
import com.example.apppanadero.databinding.ActivityLoginBinding
import com.example.apppanadero.viewmodel.UsuarioViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var credentialManager: CredentialManager

    private val usuarioViewModel: UsuarioViewModel by viewModels {
        Injector.provideUsuarioViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        credentialManager = CredentialManager.create(this)

        configurarLogin()
        configurarGoogle()
        observarUsuario()
    }

    //  LOGIN EMAIL/PASSWORD
    private fun configurarLogin() {

        binding.btnLogin.setOnClickListener {

            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {

                usuarioViewModel.loginUsuario(email, password)

            } else {
                Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //  LOGIN GOOGLE
    private fun configurarGoogle() {

        binding.btnGoogle.setOnClickListener {
            accesoGoogle()
        }
    }

    private fun accesoGoogle() {

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(getString(R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = this@LoginActivity
                )

                handleSignIn(result.credential)

            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Error Google Sign-In", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSignIn(credential: Credential) {

        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

            val googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(credential.data)

            val idToken = googleIdTokenCredential.idToken

            usuarioViewModel.loginConGoogle(idToken)
        }
    }

    //  OBSERVADORES
    private fun observarUsuario() {

        usuarioViewModel.firebaseUser.observe(this) { user ->

            if (user != null) {

                usuarioViewModel.obtenerUsuario(user.uid)

            } else {
                Toast.makeText(this, "Login incorrecto", Toast.LENGTH_SHORT).show()
            }
        }

        usuarioViewModel.usuario.observe(this) { usuario ->

            usuario?.let {

                when (it.rol) {

                    "cliente" -> {
                        startActivity(Intent(this, ClienteHomeActivity::class.java))
                    }

                    "admin" -> {
                        startActivity(Intent(this, AdminHomeActivity::class.java))
                    }

                    "repartidor" -> {
                        // TODO pantalla repartidor
                    }
                }

                finish()
            }
        }
    }
}