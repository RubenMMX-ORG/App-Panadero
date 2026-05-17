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
import com.example.apppanadero.databinding.FragmentAdminPedidosBinding
import com.example.apppanadero.ui.adapters.AdminPedidoAdapter
import com.example.apppanadero.viewmodel.PedidoViewModel

class AdminPedidosFragment : Fragment() {

    // ------------------------------------------------
    // VIEW BINDING
    // ------------------------------------------------

    private var _binding:
            FragmentAdminPedidosBinding? = null

    private val binding get() = _binding!!

    // ------------------------------------------------
    // VIEWMODEL
    // ------------------------------------------------

    private val pedidoViewModel:
            PedidoViewModel by viewModels {

        Injector.providePedidoViewModelFactory()
    }

    // ------------------------------------------------
    // ADAPTER
    // ------------------------------------------------

    private lateinit var adapter:
            AdminPedidoAdapter

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
            LinearLayoutManager(requireContext())

        // ------------------------------------------------
        // OBSERVAR PEDIDOS
        // ------------------------------------------------

        pedidoViewModel.listaPedidos.observe(
            viewLifecycleOwner
        ) { listaPedidos ->

            // Filtramos solo pedidos pendientes
            val pedidosPendientes =

                listaPedidos.filter {

                    it.estado == "pendiente"
                }

            adapter =
                AdminPedidoAdapter(

                    pedidosPendientes

                ) { pedido ->

                    // CLICK DETALLE
                    val action =

                        AdminPedidosFragmentDirections
                            .actionAdminPedidosFragmentToAdminDetallePedidoFragment(

                                pedidoId = pedido.id
                            )

                    findNavController().navigate(action)

                }

            binding.recyclerPedidos.adapter =
                adapter
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