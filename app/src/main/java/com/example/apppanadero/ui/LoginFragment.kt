package com.example.apppanadero.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    // ---------------------------------------------------
    // VIEW BINDING
    // ---------------------------------------------------

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!


    // ---------------------------------------------------
    // CREDENTIAL MANAGER GOOGLE
    // ---------------------------------------------------

    // Clase encargada del login Google moderno
    private lateinit var credentialManager: CredentialManager


    // ---------------------------------------------------
    // VIEWMODEL
    // ---------------------------------------------------

    // Un ViewModel es una clase que guarda y gestiona
    // lógica/datos de la UI sin depender de la pantalla.
    //
    // Podríamos decir:
    // "el cerebro de la pantalla"
    private val viewModel: UsuarioViewModel by viewModels {

        // Injector construye dependencias necesarias
        Injector.provideUsuarioViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)
        
        val preferencias =

        requireActivity()
            .getSharedPreferences(
    
                "sesion",
    
                AppCompatActivity.MODE_PRIVATE
            )
    
    val mantenerSesion =
    
        preferencias.getBoolean(
    
            "mantener_sesion",
    
            false
        )
    
    val usuarioActual = viewModel.getCurrentUser()

            if (
        
            mantenerSesion == true &&
            usuarioActual != null
        
        ) {
        
            viewModel.obtenerUsuario(
                usuarioActual.uid
            )
        }
        
            // Inicializamos CredentialManager
            credentialManager = CredentialManager.create(requireContext())
    
            configurarRegistro()
            configurarLogin()
            configurarLoginGoogle()
    
            // SOLO observamos Firestore
            // porque sí es estado persistente UI
            observarUsuarioFirestore()
        }


    // ---------------------------------------------------
    // REGISTRO EMAIL/PASSWORD
    // ---------------------------------------------------

    private fun configurarRegistro() {

        binding.tVRegistro.setOnClickListener {

            val email = binding.etEmail.text.toString().trim()

            val password = binding.etPassword.text.toString().trim()


            // ---------------------------------------------------
            // VALIDACIONES
            // ---------------------------------------------------

            // Validar email
            if (!android.util.Patterns.EMAIL_ADDRESS
                    .matcher(email)
                    .matches()
            ) {

                Toast.makeText(
                    requireContext(),
                    "El email no es válido",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }


            // Validar contraseña
            if (password.length < 6) {

                Toast.makeText(
                    requireContext(),
                    "La contraseña debe tener al menos 6 caracteres",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }


            // ---------------------------------------------------
            // REGISTRO FIREBASE AUTH
            // ---------------------------------------------------


            // callback directo
            viewModel.registrarUsuario(
                email,
                password
            ) { usuarioFirebase, error ->

                // Registro correcto
                if (usuarioFirebase != null) {

                    // Ir a completar datos Firestore
                    findNavController().navigate(
                        R.id.registroFragment
                    )

                } else {

                    // Error Firebase Auth
                    Toast.makeText(
                        requireContext(),
                        error ?: "Error al registrarse",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }


    // ---------------------------------------------------
    // LOGIN EMAIL/PASSWORD
    // ---------------------------------------------------

    private fun configurarLogin() {

        binding.btnLogin.setOnClickListener {

            val email = binding.etEmail.text.toString().trim()

            val password = binding.etPassword.text.toString().trim()


            // Validar campos vacíos
            if (email.isEmpty() || password.isEmpty()) {

                Toast.makeText(
                    requireContext(),
                    "Completa los campos",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }


            // ---------------------------------------------------
            // LOGIN FIREBASE AUTH
            // ---------------------------------------------------

            viewModel.loginUsuario(
                email,
                password
            ) { usuarioFirebase, error ->

                // Login correcto
                if (usuarioFirebase != null) {
                    val preferencias =

                    requireActivity()
                        .getSharedPreferences(
                
                            "sesion",
                
                            AppCompatActivity.MODE_PRIVATE
                        )
                
                val editor = preferencias.edit()
                
                editor.putBoolean(
                
                    "mantener_sesion",
                
                    binding.checkMantenerSesion.isChecked
                )
                
                editor.commit()

                    // Obtener datos Firestore
                    viewModel.obtenerUsuario(
                        usuarioFirebase.uid
                    )

                } else {

                    // Error login
                    Toast.makeText(
                        requireContext(),
                        error ?: "Error login",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }


    // ---------------------------------------------------
    // LOGIN GOOGLE
    // ---------------------------------------------------

    private fun configurarLoginGoogle() {

        binding.btnGoogle.setOnClickListener {

            // Configuración Google Sign In
            val googleIdOption = GetGoogleIdOption.Builder()

                .setServerClientId(
                    getString(R.string.default_web_client_id)
                )

                .setFilterByAuthorizedAccounts(false)

                .setAutoSelectEnabled(false)

                .build()


            // Petición CredentialManager
            val request = GetCredentialRequest.Builder()

                .addCredentialOption(googleIdOption)

                .build()


            // Operación asíncrona
            lifecycleScope.launch {

                try {

                    val resultado = credentialManager.getCredential(

                        request = request,

                        context = requireContext()
                    )

                    procesarCredencialGoogle(
                        resultado.credential
                    )

                } catch (e: Exception) {

                    Log.e(
                        "GoogleSignIn",
                        "Error login Google",
                        e
                    )

                    Toast.makeText(
                        requireContext(),
                        "Error login Google",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }


    // ---------------------------------------------------
    // PROCESAR TOKEN GOOGLE
    // ---------------------------------------------------

    private fun procesarCredencialGoogle(
        credential: Credential
    ) {

        // Verificar tipo credencial
        if (
            credential is CustomCredential
            &&
                
            credential.type ==
            GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {

            // Obtener token Google
            val googleIdTokenCredential =

                GoogleIdTokenCredential.createFrom(
                    credential.data
                )

            val idToken =
                googleIdTokenCredential.idToken


            // ---------------------------------------------------
            // LOGIN FIREBASE GOOGLE
            // ---------------------------------------------------

            viewModel.loginConGoogle(
                idToken
            ) { usuarioFirebase, error ->

                // Login correcto
                if (usuarioFirebase != null) {
                    val preferencias =
                
                    requireActivity()
                        .getSharedPreferences(
                
                            "sesion",
                
                            AppCompatActivity.MODE_PRIVATE
                        )
                
                val editor = preferencias.edit()
                
                editor.putBoolean(
                
                    "mantener_sesion",
                
                    binding.checkMantenerSesion.isChecked
                )
                
                editor.commit()
                    // Buscar usuario Firestore
                    viewModel.obtenerUsuario(
                        usuarioFirebase.uid
                    )

                } else {

                    Toast.makeText(
                        requireContext(),
                        error ?: "Error Google",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }


    // ---------------------------------------------------
    // OBSERVAR USUARIO FIRESTORE
    // ---------------------------------------------------

    // SOLO observamos Firestore porque:
    // - rol
    // - aprobado
    // - datos usuario
    //
    // sí son estado persistente UI
    private fun observarUsuarioFirestore() {

        viewModel.usuario.observe(
            viewLifecycleOwner
        ) { usuario ->

            // ---------------------------------------------------
            // USUARIO EXISTE FIRESTORE
            // ---------------------------------------------------

            if (usuario != null) {

                // Usuario pendiente aprobación
                if (usuario.aprobado == false) {

                    Toast.makeText(
                        requireContext(),
                        "Usuario pendiente de aprobación",
                        Toast.LENGTH_LONG
                    ).show()

                    return@observe
                }


                // ---------------------------------------------------
                // NAVEGACIÓN SEGÚN ROL
                // ---------------------------------------------------

                when (usuario.rol) {

                    "cliente" -> {

                        startActivity(
                            Intent(
                                requireContext(),
                                ClienteHomeActivity::class.java
                            )
                        )
                    }

                    "empleado" -> {

                        startActivity(
                            Intent(
                                requireContext(),
                                EmpleadoHomeActivity::class.java
                            )
                        )
                    }

                    "admin" -> {

                        startActivity(
                            Intent(
                                requireContext(),
                                AdminHomeActivity::class.java
                            )
                        )
                    }
                }

                requireActivity().finish()

            } else {

                // ---------------------------------------------------
                // NO EXISTE FIRESTORE
                // ---------------------------------------------------
                // login Google nuevo
                // o usuario recién registrado

                findNavController().navigate(
                    R.id.registroFragment
                )
            }
        }
    }


    // ---------------------------------------------------
    // ON DESTROY VIEW
    // ---------------------------------------------------

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}
