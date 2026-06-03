package com.example.apppanadero.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.apppanadero.data.di.Injector
import com.example.apppanadero.databinding.FragmentPerfilBinding
import com.example.apppanadero.viewmodel.UsuarioViewModel
import com.google.firebase.auth.FirebaseAuth

class PerfilFragment : Fragment() {


// ------------------------------------------------
// BINDING
// ------------------------------------------------

    private var _binding: FragmentPerfilBinding? = null

    private val binding
        get() = _binding!!

// ------------------------------------------------
// VIEWMODEL
// ------------------------------------------------

    private val usuarioViewModel:
            UsuarioViewModel by viewModels {

        Injector.provideUsuarioViewModelFactory()
    }

// ------------------------------------------------
// ON CREATE VIEW
// ------------------------------------------------

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        activity?.let {

            (it as? AppCompatActivity)
                ?.supportActionBar
                ?.title = "Mi Perfil"
        }

        _binding =

            FragmentPerfilBinding.inflate(

                inflater,
                container,
                false
            )

        return binding.root
    }

// ------------------------------------------------
// ON VIEW CREATED
// ------------------------------------------------

    override fun onViewCreated(

        view: View,
        savedInstanceState: Bundle?

    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        cargarUsuario()

        observarUsuario()
    }

// ------------------------------------------------
// CARGAR USUARIO
// ------------------------------------------------

    private fun cargarUsuario() {

        val uid = FirebaseAuth
            .getInstance()
            .currentUser
            ?.uid

        uid?.let {

            usuarioViewModel
                .obtenerUsuarioPorId(it)
        }
    }

// ------------------------------------------------
// OBSERVAR USUARIO
// ------------------------------------------------

    private fun observarUsuario() {

        usuarioViewModel.usuario.observe(

            viewLifecycleOwner

        ) { usuario ->

            usuario ?: return@observe

            // ------------------------
            // DATOS COMUNES
            // ------------------------

            binding.tvNombre.text =
                usuario.nombre

            binding.tvApellidos.text =
                usuario.apellidos

            binding.tvEmail.text =
                usuario.email

            binding.tvRol.text =
                usuario.rol

            // ------------------------
            // CLIENTE
            // ------------------------

            if (usuario.rol == "cliente") {

                binding.layoutCliente.visibility =
                    View.VISIBLE

                binding.layoutEmpleado.visibility =
                    View.GONE

                binding.tvComercio.text =
                    usuario.nombreComercio ?: ""

                binding.tvDireccion.text =
                    usuario.direccion ?: ""

                binding.tvTelefono.text =
                    usuario.telefono ?: ""

                binding.tvCif.text =
                    usuario.cif ?: ""
            }

            // ------------------------
            // EMPLEADO
            // ------------------------

            else if (usuario.rol == "empleado") {

                binding.layoutCliente.visibility =
                    View.GONE

                binding.layoutEmpleado.visibility =
                    View.VISIBLE

                binding.tvCargo.text =
                    usuario.cargo ?: ""
            }

            // ------------------------
            // ADMIN
            // ------------------------

            else {

                binding.layoutCliente.visibility =
                    View.GONE

                binding.layoutEmpleado.visibility =
                    View.GONE
            }
        }
    }

// ------------------------------------------------
// DESTROY
// ------------------------------------------------

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }


}
