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
import com.example.apppanadero.data.di.Injector
import com.example.apppanadero.databinding.FragmentAdminPedidosBinding
import com.example.apppanadero.ui.adapters.AdminPedidoAdapter
import com.example.apppanadero.viewmodel.PedidoViewModel
import com.example.apppanadero.viewmodel.UsuarioViewModel

class AdminPedidosFragment : Fragment() {

    // ------------------------------------------------
    // VIEW BINDING
    // ------------------------------------------------

    private var _binding:
            FragmentAdminPedidosBinding? = null

    private val binding get() = _binding!!

    // ------------------------------------------------
    // VIEWMODEL PEDIDOS
    // ------------------------------------------------

    private val pedidoViewModel:
            PedidoViewModel by viewModels {

        Injector.providePedidoViewModelFactory()
    }

    // ------------------------------------------------
    // VIEWMODEL USUARIOS
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
            AdminPedidoAdapter

    // ------------------------------------------------
    // MAPA CLIENTES
    // ------------------------------------------------

    // key   -> clienteId
    // value -> nombreComercio
    private val mapaClientes =

        mutableMapOf<String, String>()


    // ------------------------------------------------
    // ON CREATE VIEW
    // ------------------------------------------------

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        _binding =
            FragmentAdminPedidosBinding.inflate(

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

        usuarioViewModel.obtenerClientes()

        // ------------------------------------------------
        // ACTION BAR
        // ------------------------------------------------

        activity?.let {

            (it as? AppCompatActivity)
                ?.supportActionBar
                ?.title = "Gestion Pedidos"
        }

        // ------------------------------------------------
        // RECYCLERVIEW
        // ------------------------------------------------

        binding.recyclerPedidos.layoutManager =

            LinearLayoutManager(
                requireContext()
            )

        // ------------------------------------------------
        // OBSERVAR PEDIDOS
        // ------------------------------------------------

        pedidoViewModel.listaPedidos.observe(

            viewLifecycleOwner

        ) { listaPedidos ->

            // ------------------------------------------------
            // FILTRAR PENDIENTES
            // ------------------------------------------------

            val pedidosPendientes =

                listaPedidos.filter {

                    it.estado == "pendiente"
                }



            // ------------------------------------------------
            // CREAR ADAPTER
            // ------------------------------------------------

            adapter =
                AdminPedidoAdapter(

                    pedidosPendientes,

                    mapaClientes

                ) { pedido ->

                    // ------------------------------------------------
                    // CLICK DETALLE
                    // ------------------------------------------------

                    val action =

                        AdminPedidosFragmentDirections
                            .actionAdminPedidosFragmentToAdminDetallePedidoFragment(

                                pedidoId = pedido.id
                            )



                    findNavController()
                        .navigate(action)
                }

            binding.recyclerPedidos.adapter =
                adapter
        }

        // ------------------------------------------------
        // OBSERVAR USUARIO
        // ------------------------------------------------

        usuarioViewModel.listaClientes.observe(

            viewLifecycleOwner

        ) { listaClientes ->

            mapaClientes.clear()

            listaClientes.forEach { cliente ->

                mapaClientes[cliente.id] =

                    cliente.nombreComercio
                        ?: "Cliente"
            }

            if (::adapter.isInitialized) {

                adapter.notifyDataSetChanged()
            }
        }
        // ------------------------------------------------
        // CARGAR TODOS PEDIDOS
        // ------------------------------------------------

        pedidoViewModel.obtenerTodosPedidos()
    }

    // ------------------------------------------------
    // ON DESTROY VIEW
    // ------------------------------------------------

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}