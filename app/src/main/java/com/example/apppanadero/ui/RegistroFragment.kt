package com.example.apppanadero.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.apppanadero.data.di.Injector
import com.example.apppanadero.data.model.Usuario
import com.example.apppanadero.databinding.FragmentRegistroBinding
import com.example.apppanadero.viewmodel.UsuarioViewModel
import com.google.firebase.auth.FirebaseAuth

class RegistroFragment : Fragment() {

    private var _binding: FragmentRegistroBinding? = null
    private val binding get() = _binding!!

    // ViewModel con Injector
    private val viewModel: UsuarioViewModel by viewModels {
        Injector.provideUsuarioViewModelFactory()
    }

    //  Rol seleccionado (por defecto cliente)
    private var rolSeleccionado = "cliente"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegistroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarSelectorRol()
        configurarBotonRegistro()
        observarGuardado()
    }

    // BOTONES CLIENTE / EMPLEADO
    private fun configurarSelectorRol() {

        binding.btnCliente.setOnClickListener {
            rolSeleccionado = "cliente"

            binding.formCliente.visibility = View.VISIBLE
            binding.formEmpleado.visibility = View.GONE
        }

        binding.btnEmpleado.setOnClickListener {
            rolSeleccionado = "empleado"

            binding.formCliente.visibility = View.GONE
            binding.formEmpleado.visibility = View.VISIBLE
        }
    }

    // BOTÓN REGISTRARSE
    private fun configurarBotonRegistro() {

        binding.btnRegistrar.setOnClickListener {
            // Obtenemos instancia de Auth para agregar por ejemplo el email directamente
            val firebaseUser = FirebaseAuth.getInstance().currentUser

            // Creamos un objeto usuario dependiendo del rol
            val usuario = if (rolSeleccionado == "cliente") {

                Usuario(
                    nombre = binding.etNombreCliente.text.toString(),
                    apellidos = binding.etApellidosCliente.text.toString(),
                    email = firebaseUser?.email ?: "",
                    rol = "cliente",

                    nombreComercio = binding.etComercio.text.toString(),
                    telefono = binding.etTelefono.text.toString(),
                    cif = binding.etCif.text.toString(),
                    direccion = binding.etDireccion.text.toString(),
                    aprobado = true
                )

            } else {

                Usuario(
                    nombre = binding.etNombreEmpleado.text.toString(),
                    apellidos = binding.etApellidosEmpleado.text.toString(),
                    email = firebaseUser?.email ?: "",
                    rol = "empleado",

                    cargo = binding.etCargo.text.toString(),
                    aprobado = true
                )
            }

            viewModel.guardarUsuario(usuario)
        }
    }

    //  OBSERVADOR: escucha si el usuario se ha guardado en Firestore
    private fun observarGuardado() {

        viewModel.usuarioGuardado.observe(viewLifecycleOwner) { success ->

            if (success) {

                Toast.makeText(
                    requireContext(),
                    "Registro completado. Pendiente de aprobación",
                    Toast.LENGTH_LONG
                ).show()

                //  Volver a login (mejor práctica)
                requireActivity().onBackPressedDispatcher.onBackPressed()

            } else {

                Toast.makeText(
                    requireContext(),
                    "Error al guardar",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}