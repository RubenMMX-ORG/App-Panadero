package com.example.apppanadero.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.apppanadero.R
import com.example.apppanadero.data.di.Injector
import com.example.apppanadero.databinding.FragmentLoginBinding
import com.example.apppanadero.viewmodel.UsuarioViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    // Binding del fragment
    private lateinit var binding: FragmentLoginBinding

    // Credential Manager para login con Google
    private lateinit var credentialManager: CredentialManager

    // ViewModel con Injector (igual que antes pero en Fragment)
    private val usuarioViewModel: UsuarioViewModel by viewModels {
        Injector.provideUsuarioViewModelFactory()
    }


    // Se crea la vista del fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar CredentialManager con contexto del fragment
        credentialManager = CredentialManager.create(requireContext())

        // Inicialización de lógica
        observarUsuario()
        configurarRegistro()
        configurarLogin()
        accesoGoogle()
    }

    // BOTÓN REGISTRO (solo crea usuario en Firebase Auth)
    private fun configurarRegistro() {

        binding.tVRegistro.setOnClickListener {

            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {

                // Llamada al ViewModel
                usuarioViewModel.registrarUsuario(email, password)

            } else {
                Toast.makeText(requireContext(), "Completa los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // BOTÓN LOGIN EMAIL/PASSWORD
    private fun configurarLogin() {

        binding.btnLogin.setOnClickListener {

            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {

                usuarioViewModel.loginUsuario(email, password)

            } else {
                Toast.makeText(requireContext(), "Completa los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // LOGIN CON GOOGLE
    private fun accesoGoogle() {

        binding.btnGoogle.setOnClickListener {

            // Configuración de Google Sign-In
            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(getString(R.string.default_web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            // Se ejecuta en coroutine porque es operación asíncrona
            lifecycleScope.launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = requireContext()
                    )

                    handleSignIn(result.credential)

                } catch (e: Exception) {
                    Log.e("GoogleSignIn", "Error", e)
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Procesa el token de Google
    private fun handleSignIn(credential: Credential) {

        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

            val googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(credential.data)

            val idToken = googleIdTokenCredential.idToken

            // Se envía al ViewModel para autenticación con Firebase
            usuarioViewModel.loginConGoogle(idToken)
        }
    }

    // OBSERVADORES PRINCIPALES
    private fun observarUsuario() {

        // 1. OBSERVA FIREBASE AUTH (solo login)
        usuarioViewModel.firebaseUser.observe(viewLifecycleOwner) { user ->

            if (user != null) {

                Toast.makeText(
                    requireContext(),
                    "Login OK: ${user.email}",
                    Toast.LENGTH_SHORT
                ).show()

                // IMPORTANTE:
                // Aquí NO navegamos todavía
                // Primero necesitamos datos de Firestore
                usuarioViewModel.obtenerUsuario(user.uid)

            } else {

                Toast.makeText(
                    requireContext(),
                    "Login fallido",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // 2. OBSERVA FIRESTORE (datos reales del usuario)
        usuarioViewModel.usuario.observe(viewLifecycleOwner) { usuario ->

            if (usuario != null) {

                // Caso: usuario existe pero NO está aprobado
                if (usuario.aprobado == false) {

                    Toast.makeText(
                        requireContext(),
                        "Usuario pendiente de aprobación",
                        Toast.LENGTH_LONG
                    ).show()
                    // cerramos sesion
                    usuarioViewModel.logout()

                    // Asegurar que vuelve al  login (por si acaso)
                    findNavController().popBackStack()

                    return@observe
                }

                // Caso: usuario aprobado → navegación según rol
                if (usuario.rol == "cliente") {

                    startActivity(
                        android.content.Intent(requireContext(), ClienteHomeActivity::class.java)
                    )

                } else if (usuario.rol == "empleado") {

                    startActivity(
                        android.content.Intent(requireContext(), EmpleadoHomeActivity::class.java)
                    )

                } else if (usuario.rol == "admin") {

                    startActivity(
                        android.content.Intent(requireContext(), AdminHomeActivity::class.java)
                    )
                }

                requireActivity().finish()

            } else {

                // Caso: usuario NO existe en Firestore
                // Esto pasa en login con Google o registro inicial

                Toast.makeText(
                    requireContext(),
                    "Completa tu registro",
                    Toast.LENGTH_SHORT
                ).show()

                // Navegación al fragment de registro
                findNavController().navigate(R.id.registroFragment)
            }
        }
    }
}