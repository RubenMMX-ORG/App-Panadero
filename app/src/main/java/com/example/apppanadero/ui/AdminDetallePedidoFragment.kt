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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apppanadero.data.di.Injector
import com.example.apppanadero.databinding.FragmentAdminDetallePedidoBinding
import com.example.apppanadero.ui.adapters.LineaPedidoAdapter
import com.example.apppanadero.viewmodel.PedidoViewModel
import com.example.apppanadero.viewmodel.UsuarioViewModel

class AdminDetallePedidoFragment : Fragment() {

    // ------------------------------------------------
    // VIEW BINDING
    // ------------------------------------------------

    private var _binding:
            FragmentAdminDetallePedidoBinding? = null

    private val binding get() = _binding!!

    // ------------------------------------------------
    // VIEWMODEL
    // ------------------------------------------------

    private val pedidoViewModel:
            PedidoViewModel by viewModels {

        Injector.providePedidoViewModelFactory()
    }
    // ------------------------------------------------
    // VIEWMODEL USUARIO
    // ------------------------------------------------

    private val usuarioViewModel:
            UsuarioViewModel by viewModels {

        Injector
            .provideUsuarioViewModelFactory()
    }

    // ------------------------------------------------
    // VARIABLES
    // ------------------------------------------------

    // PedidoId recibido por navegación
    private var pedidoId: String? = null

    // ------------------------------------------------
    // ADAPTER
    // ------------------------------------------------

    private lateinit var adapter:
            LineaPedidoAdapter

    // ------------------------------------------------
    // ON CREATE VIEW
    // ------------------------------------------------

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        // Inflamos layout con ViewBinding
        _binding =
            FragmentAdminDetallePedidoBinding.inflate(

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
                ?.title = "Detalle Pedido"
        }

        // ------------------------------------------------
        // RECUPERAR ARGUMENTOS
        // ------------------------------------------------

        val args =
            AdminDetallePedidoFragmentArgs
                .fromBundle(requireArguments())

        pedidoId = args.pedidoId

        // ------------------------------------------------
        // CONFIGURAR RECYCLER
        // ------------------------------------------------

        binding.recyclerDetalle.layoutManager =
            LinearLayoutManager(requireContext())

        // ------------------------------------------------
        // VALIDAR PEDIDO ID
        // ------------------------------------------------

        if (pedidoId == null) {

            Toast.makeText(

                requireContext(),

                "Pedido no encontrado",

                Toast.LENGTH_SHORT

            ).show()

            findNavController().popBackStack()

            return
        }

        // ------------------------------------------------
        // CARGAR PEDIDO
        // ------------------------------------------------

        pedidoViewModel.obtenerPedidoPorId(
            pedidoId!!
        )

        // ------------------------------------------------
        // OBSERVAR DETALLE PEDIDO
        // ------------------------------------------------

        pedidoViewModel.pedidoDetalle.observe(
            viewLifecycleOwner
        ) { pedido ->

            if (pedido != null) {

                // ------------------------------------------------
                // CLIENTE
                // ------------------------------------------------

                // De momento mostramos clienteId.
                // Más adelante podremos cargar
                // el nombre real del cliente.
                usuarioViewModel.obtenerUsuarioPorId(

                    pedido.clienteId
                )
                // ------------------------------------------------
                // NUMERO PEDIDO
                // ------------------------------------------------

                binding.tvNumeroPedido.text =
                    "Pedido #${pedido.numeroPedido}"

                // ------------------------------------------------
                // ESTADO
                // ------------------------------------------------

                binding.chipEstado.text =
                    pedido.estado

                // ------------------------------------------------
                // FECHA Y HORA
                // ------------------------------------------------

                // Temporal hasta implementar fechas reales
                binding.tvFecha.text =
                    "📅 Próximamente"

                binding.tvHora.text =
                    "⏰ --:--"

                // ------------------------------------------------
                // TOTAL PRODUCTOS
                // ------------------------------------------------

                val totalProductos =

                    pedido.lineasPedido.sumOf {

                        it.cantidadPedida
                    }

                binding.tvCantidad.text =
                    "$totalProductos productos"

                // ------------------------------------------------
                // PRECIO TOTAL
                // ------------------------------------------------

                binding.tvPrecio.text =
                    "€%.2f".format(

                        pedido.precioTotal
                    )

                // ------------------------------------------------
                // RECYCLER DETALLE
                // ------------------------------------------------

                // Reutilizamos el adapter ya creado
                // para mostrar líneas pedido.
                //
                // Como el pedido todavía está:
                // pendiente/preparado
                //
                // usamos cantidadPedida.
                adapter =
                    LineaPedidoAdapter(

                        pedido.lineasPedido,

                        pedido.estado
                    )

                binding.recyclerDetalle.adapter =
                    adapter
            }
        }

        // ------------------------------------------------
        // OBSERVAR USUARIO
        // ------------------------------------------------

        usuarioViewModel.usuario.observe(

            viewLifecycleOwner

        ) { usuario ->

            usuario?.let {

                binding.tvNombreCliente.text =

                    it.nombreComercio
                        ?: "Cliente"
            }
        }


        // ------------------------------------------------
        // BOTÓN LISTO
        // ------------------------------------------------

        binding.btnListo.setOnClickListener {

            // Cambiamos estado:
            //
            // pendiente -> preparado
            pedidoViewModel.actualizarEstadoPedido(

                pedidoId!!,

                "preparado"
            )

            Toast.makeText(

                requireContext(),

                "Pedido marcado como preparado",

                Toast.LENGTH_SHORT

            ).show()

            // Volvemos atrás
            findNavController().popBackStack()
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