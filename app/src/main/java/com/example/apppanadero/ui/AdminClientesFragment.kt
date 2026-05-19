package com.example.apppanadero.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apppanadero.R
import com.example.apppanadero.data.di.Injector
import com.example.apppanadero.databinding.FragmentAdminClientesBinding
import com.example.apppanadero.ui.adapters.AdminClienteAdapter
import com.example.apppanadero.viewmodel.UsuarioViewModel

class AdminClientesFragment : Fragment() {

    // ------------------------------------------------
    // VIEW BINDING
    // ------------------------------------------------

    private var _binding:
            FragmentAdminClientesBinding? = null

    private val binding get() = _binding!!

    // ------------------------------------------------
    // VIEWMODEL
    // ------------------------------------------------

    private val usuarioViewModel:
            UsuarioViewModel by viewModels {

        Injector
            .provideUsuarioViewModelFactory()
    }

    // ------------------------------------------------
    // ADAPTER
    // ------------------------------------------------

    private lateinit var adapter:
            AdminClienteAdapter

    // ------------------------------------------------
    // ON CREATE VIEW
    // ------------------------------------------------

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        // Inflamos layout usando ViewBinding
        _binding =
            FragmentAdminClientesBinding.inflate(

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

        // ------------------------------------------------
        // ACTION BAR
        // ------------------------------------------------

        activity?.let {

            (it as? AppCompatActivity)
                ?.supportActionBar
                ?.title = "Gestion Clientes"
        }

        // ------------------------------------------------
        // RECYCLERVIEW
        // ------------------------------------------------

        binding.recyclerClientes.layoutManager =

            LinearLayoutManager(
                requireContext()
            )

        // ------------------------------------------------
        // OBSERVAR CLIENTES
        // ------------------------------------------------

        usuarioViewModel.usuario.observe(

            viewLifecycleOwner

        ) {  }

        // ------------------------------------------------
        // CARGAR CLIENTES
        // ------------------------------------------------

        usuarioViewModel.obtenerClientes()

        // ------------------------------------------------
        // OBSERVAR LISTA CLIENTES
        // ------------------------------------------------

        usuarioViewModel.listaClientes.observe(

            viewLifecycleOwner

        ) { listaClientes ->

            // ------------------------------------------------
            // CREAR ADAPTER
            // ------------------------------------------------

            adapter =
                AdminClienteAdapter(

                    listaClientes

                ) { cliente ->



                    // ------------------------------------------------
                    // NAVEGAR DETALLE CLIENTE
                    // ------------------------------------------------

                    val action =

                        AdminClientesFragmentDirections
                            .actionAdminClientesFragmentToAdminHistoricoClientesFragment(

                                clienteId = cliente.id
                            )

                    findNavController().navigate(
                        action
                    )
                }


            binding.recyclerClientes.adapter =
                adapter
        }
    }

    // ------------------------------------------------
    // ON DESTROY VIEW
    // ------------------------------------------------

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}