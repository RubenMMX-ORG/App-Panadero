package com.example.apppanadero.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.apppanadero.R
import com.example.apppanadero.data.di.Injector
import com.example.apppanadero.data.model.Usuario
import com.example.apppanadero.databinding.FragmentRegistroBinding
import com.example.apppanadero.viewmodel.UsuarioViewModel

class RegistroFragment : Fragment() {

    // ---------------------------------------------------
    // VIEW BINDING
    // ---------------------------------------------------

    // Binding nullable porque la vista puede destruirse
    private var _binding: FragmentRegistroBinding? = null

    // Acceso cómodo al binding
    private val binding get() = _binding!!


    // ---------------------------------------------------
    // VIEWMODEL
    // ---------------------------------------------------

    // Un ViewModel es una clase que guarda y gestiona
    // datos/lógica de la UI sin depender de la pantalla.
    //
    // Podríamos decir:
    // "el cerebro de la pantalla"
    private val viewModel: UsuarioViewModel by viewModels {

        // Injector construye las dependencias necesarias
        Injector.provideUsuarioViewModelFactory()
    }


    // ---------------------------------------------------
    // ROL SELECCIONADO
    // ---------------------------------------------------

    // Por defecto cliente
    private var rolSeleccionado = "cliente"



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inicializamos binding
        _binding = FragmentRegistroBinding.inflate(
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
        // Cambiar el título de la ActionBar
        activity?.let {
            (it as? AppCompatActivity)?.supportActionBar?.title = "Registro"
        }

        super.onViewCreated(view, savedInstanceState)

        configurarSelectorRol()
        configurarBotonRegistro()
    }


    // ---------------------------------------------------
    // BOTONES CLIENTE / EMPLEADO
    // ---------------------------------------------------

    private fun configurarSelectorRol() {

        // Botón CLIENTE
        binding.btnCliente.setOnClickListener {

            rolSeleccionado = "cliente"

            binding.formCliente.visibility = View.VISIBLE
            binding.formEmpleado.visibility = View.GONE
        }

        // Botón EMPLEADO
        binding.btnEmpleado.setOnClickListener {

            rolSeleccionado = "empleado"

            binding.formCliente.visibility = View.GONE
            binding.formEmpleado.visibility = View.VISIBLE
        }
    }


    // ---------------------------------------------------
    // BOTÓN REGISTRARSE
    // ---------------------------------------------------

    private fun configurarBotonRegistro() {

        binding.btnRegistrar.setOnClickListener {

            // Obtenemos usuario autenticado actual
            val usuarioFirebase = viewModel.getCurrentUser()

            // Seguridad extra por si no existe sesión
            if (usuarioFirebase == null) {

                Toast.makeText(
                    requireContext(),
                    "Debe iniciar sesión primero",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }


            // ---------------------------------------------------
            // CREAR OBJETO USUARIO SEGÚN ROL
            // ---------------------------------------------------

            val usuario = if (rolSeleccionado == "cliente") {

                Usuario(

                    // Datos comunes
                    nombre = binding.etNombreCliente.text.toString(),
                    apellidos = binding.etApellidosCliente.text.toString(),
                    email = usuarioFirebase.email ?: "",
                    rol = "cliente",

                    // Datos cliente
                    nombreComercio = binding.etComercio.text.toString(),
                    telefono = binding.etTelefono.text.toString(),
                    cif = binding.etCif.text.toString(),
                    direccion = binding.etDireccion.text.toString(),

                    // Pendiente aprobación admin
                    aprobado = false
                )

            } else {

                Usuario(

                    // Datos comunes
                    nombre = binding.etNombreEmpleado.text.toString(),
                    apellidos = binding.etApellidosEmpleado.text.toString(),
                    email = usuarioFirebase.email ?: "",
                    rol = "empleado",

                    // Datos empleado
                    cargo = binding.etCargo.text.toString(),

                    // Pendiente aprobación admin
                    aprobado = false
                )
            }


            // ---------------------------------------------------
            // GUARDAR USUARIO EN FIRESTORE
            // ---------------------------------------------------

            // Estilo "Puy du Fou":
            // callback directo
            // sin observers innecesarios
            viewModel.guardarUsuario(usuario) { guardadoCorrecto ->

                // Firestore guardado correctamente
                if (guardadoCorrecto) {

                    Toast.makeText(
                        requireContext(),
                        "Registro completado. Pendiente de aprobación",
                        Toast.LENGTH_LONG
                    ).show()

                    // Volvemos atrás al login
                    findNavController().navigate(
                        R.id.loginFragment
                    )

                } else {

                    // Error Firestore
                    Toast.makeText(
                        requireContext(),
                        "Error al guardar datos",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }


    // ---------------------------------------------------
    // ON DESTROY VIEW
    // ---------------------------------------------------

    override fun onDestroyView() {

        super.onDestroyView()

        // Evitamos memory leaks
        _binding = null
    }
}