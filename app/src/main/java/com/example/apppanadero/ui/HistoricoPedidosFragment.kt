package com.example.apppanadero.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apppanadero.data.di.Injector
import com.example.apppanadero.databinding.FragmentHistoricoPedidosBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.apppanadero.ui.adapters.PedidoAdapter
import com.example.apppanadero.viewmodel.PedidoViewModel


// Fragment encargado de mostrar los pedidos del cliente actual
class HistoricoPedidosFragment : Fragment() {

    // ------------------------------------------------
    // BINDING
    // ------------------------------------------------
    private var _binding: FragmentHistoricoPedidosBinding? = null
    private val binding get() = _binding!!

    // ------------------------------------------------
    // VIEWMODEL
    // ------------------------------------------------
    private val viewModel: PedidoViewModel by viewModels {

        Injector.providePedidoViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Cambiar el título de la ActionBar
        activity?.let {
            (it as? AppCompatActivity)?.supportActionBar?.title = "Historico de pedidos"
        }

        _binding = FragmentHistoricoPedidosBinding.inflate(
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

        super.onViewCreated(view, savedInstanceState)

        configurarRecyclerView()

        observarPedidos()

        cargarPedidosCliente()
    }

    // ------------------------------------------------
    // CONFIGURAR RECYCLERVIEW
    // ------------------------------------------------

    private fun configurarRecyclerView() {

        binding.recyclerHistorico.layoutManager =
            LinearLayoutManager(requireContext())
    }

    // ------------------------------------------------
    // CARGAR PEDIDOS CLIENTE
    // ------------------------------------------------

    private fun cargarPedidosCliente() {

        // Obtenemos usuario autenticado actual
        val uid = FirebaseAuth
            .getInstance()
            .currentUser
            ?.uid

        // Si existe usuario
        if (uid != null) {

            // Pedimos pedidos al ViewModel
            viewModel.obtenerPedidosCliente(uid)

        } else {

            Toast.makeText(
                requireContext(),
                "Usuario no autenticado",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ------------------------------------------------
    // OBSERVAR PEDIDOS
    // ------------------------------------------------

    private fun observarPedidos() {

        // Observamos lista pedidos
        viewModel.listaPedidos.observe(viewLifecycleOwner) { listaPedidos ->

            // Creamos adapter
            val adapter = PedidoAdapter(listaPedidos) { pedido ->

                // CLICK ITEM PEDIDO
                Toast.makeText(
                    requireContext(),
                    "Pedido seleccionado: ${pedido.id}",
                    Toast.LENGTH_SHORT
                ).show()


                val action =

                    HistoricoPedidosFragmentDirections.actionHistoricoPedidosFragmentToDetallePedidoFragment(
                        pedidoId = pedido.id
                    )

                findNavController().navigate(action)


            }

            // Asignamos adapter al RecyclerView
            binding.recyclerHistorico.adapter = adapter
        }

        // ------------------------------------------------
        // OBSERVAR ERRORES
        // ------------------------------------------------

        viewModel.error.observe(viewLifecycleOwner) { error ->

            if (error.isNotEmpty()) {

                Toast.makeText(
                    requireContext(),
                    error,
                    Toast.LENGTH_LONG
                ).show()

                viewModel.limpiarError()
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