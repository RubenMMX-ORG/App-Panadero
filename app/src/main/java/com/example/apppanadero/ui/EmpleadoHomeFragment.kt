package com.example.apppanadero.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.apppanadero.R
import com.example.apppanadero.data.di.Injector
import com.example.apppanadero.databinding.FragmentRepartidorHomeBinding
import com.example.apppanadero.viewmodel.PedidoViewModel

class EmpleadoHomeFragment : Fragment() {

    // ------------------------------------------------
    // VIEW BINDING
    // ------------------------------------------------

    private var _binding:
            FragmentRepartidorHomeBinding? = null

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
            FragmentRepartidorHomeBinding.inflate(

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
                ?.title = "Home Empleado"
        }

        // ------------------------------------------------
        // CARGAR TODOS LOS PEDIDOS
        // ------------------------------------------------

        pedidoViewModel.obtenerTodosPedidos()

        // ------------------------------------------------
        // OBSERVAR PEDIDOS
        // ------------------------------------------------

        pedidoViewModel.listaPedidos.observe(

            viewLifecycleOwner

        ) { listaPedidos ->

            // ------------------------------------------------
            // FILTRAR SOLO PREPARADOS
            // ------------------------------------------------

            val pedidosPreparados =

                listaPedidos.filter {

                    it.estado == "preparado"
                }

            // ------------------------------------------------
            // MOSTRAR TOTAL PEDIDOS PENDIENTES
            // ------------------------------------------------

            binding.pedidosPendientesId.text =

                pedidosPreparados
                    .size
                    .toString()
        }

        // ------------------------------------------------
        // BOTÓN PEDIDOS HOY
        // ------------------------------------------------

        binding.btnPedidosHoy
            .setOnClickListener {

                navegarHacia(

                    R.id.action_empleadoHomeFragment_to_empleadoPedidosFragment
                )
            }

        // ------------------------------------------------
        // BOTÓN DEVOLUCIONES
        // ------------------------------------------------

        binding.btnDevoluciones
            .setOnClickListener {

                // Próximamente
            }

        // ------------------------------------------------
        // BOTÓN RUTA
        // ------------------------------------------------

        binding.btnRuta
            .setOnClickListener {

                // Próximamente Google Maps
            }
    }

    // ------------------------------------------------
    // NAVEGACIÓN GENÉRICA
    // ------------------------------------------------

    private fun navegarHacia(

        actionId: Int

    ) {

        findNavController()
            .navigate(actionId)
    }

    // ------------------------------------------------
    // ON DESTROY VIEW
    // ------------------------------------------------

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}