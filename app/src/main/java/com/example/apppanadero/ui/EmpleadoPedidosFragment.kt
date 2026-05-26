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
import com.example.apppanadero.databinding.FragmentRepartidorPedidosBinding
import com.example.apppanadero.ui.adapters.AdminPedidoAdapter
import com.example.apppanadero.viewmodel.PedidoViewModel
import com.example.apppanadero.viewmodel.UsuarioViewModel

class EmpleadoPedidosFragment : Fragment() {

    // ------------------------------------------------
    // VIEW BINDING
    // ------------------------------------------------

    private var _binding:
            FragmentRepartidorPedidosBinding? = null

    private val binding get() = _binding!!

    // ------------------------------------------------
    // VIEWMODEL PEDIDOS
    // ------------------------------------------------

    private val pedidoViewModel:
            PedidoViewModel by viewModels {

        Injector
            .providePedidoViewModelFactory()
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

        // ------------------------------------------------
        // INFLAR VIEW BINDING
        // ------------------------------------------------

        _binding =
            FragmentRepartidorPedidosBinding.inflate(

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
                ?.title = "Pedidos de hoy"
        }

        // ------------------------------------------------
        // CARGAR CLIENTES
        // ------------------------------------------------

        usuarioViewModel.obtenerClientes()

        // ------------------------------------------------
        // CARGAR TODOS LOS PEDIDOS
        // ------------------------------------------------

        pedidoViewModel.obtenerTodosPedidos()

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
            // FILTRAR PARA PREPARADOS Y ENTREGADOS
            // ------------------------------------------------
            val args =

                EmpleadoPedidosFragmentArgs
                    .fromBundle(requireArguments())

            val filtroEstado =
                args.estadoFiltro

            val pedidosfiltrados =

                listaPedidos.filter {

                    it.estado == filtroEstado
                }


            // ------------------------------------------------
            // CREAR ADAPTER
            // ------------------------------------------------

            adapter =

                AdminPedidoAdapter(

                    // Lista pedidos
                    pedidosfiltrados,

                    // Mapa clientes
                    mapaClientes,

                    // ------------------------------------------------
                    // CLICK DETALLE
                    // ------------------------------------------------

                    onClickDetalle = { pedido ->
                        val action =

                            EmpleadoPedidosFragmentDirections
                                .actionEmpleadoPedidosFragmentToEmpleadoDetallePedidoFragment(

                                    pedidoId = pedido.id
                                )

                        findNavController()
                            .navigate(action)
                    },

                    // ------------------------------------------------
                    // CLICK INICIAR RUTA
                    // ------------------------------------------------

                    onClickIniciarRuta = { pedido ->

                        // Próximamente:
                        // Google Maps
                    },

                    // ------------------------------------------------
                    // MOSTRAR BOTÓN RUTA
                    // ------------------------------------------------

                    mostrarBotonRuta = true
                )

            // ------------------------------------------------
            // ASIGNAR ADAPTER
            // ------------------------------------------------

            binding.recyclerPedidos.adapter =
                adapter
        }

        // ------------------------------------------------
        // OBSERVAR CLIENTES
        // ------------------------------------------------

        usuarioViewModel.listaClientes.observe(

            viewLifecycleOwner

        ) { listaClientes ->

            // ------------------------------------------------
            // LIMPIAR MAPA
            // ------------------------------------------------

            mapaClientes.clear()

            // ------------------------------------------------
            // RELLENAR MAPA
            // ------------------------------------------------

            listaClientes.forEach { cliente ->

                mapaClientes[cliente.id] =

                    cliente.nombreComercio
                        ?: "Cliente"
            }


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