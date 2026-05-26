package com.example.apppanadero.ui

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apppanadero.R
import com.example.apppanadero.data.di.Injector
import com.example.apppanadero.databinding.FragmentAdminDetallePedidoBinding
import com.example.apppanadero.ui.adapters.LineaPedidoAdapter
import com.example.apppanadero.viewmodel.PedidoViewModel
import com.example.apppanadero.viewmodel.UsuarioViewModel

class EmpleadoDetallePedidoFragment : Fragment() {

    // ------------------------------------------------
    // VIEW BINDING
    // ------------------------------------------------

    private var _binding:
            FragmentAdminDetallePedidoBinding? = null

    private val binding get() = _binding!!

    // ------------------------------------------------
    // VIEWMODEL PEDIDOS
    // ------------------------------------------------


    private val pedidoViewModel:
            PedidoViewModel by viewModels {

        Injector.providePedidoViewModelFactory()
    }

    //ASIGNACION DE ESTADO DE PEDIOD
    private var estadoPedido: String = ""

    // ------------------------------------------------
    // VIEWMODEL USUARIOS
    // ------------------------------------------------

    private val usuarioViewModel:
            UsuarioViewModel by viewModels {

        Injector
            .provideUsuarioViewModelFactory()
    }

    // ------------------------------------------------
    // VARIABLES
    // ------------------------------------------------

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
        // RECYCLER
        // ------------------------------------------------

        binding.recyclerDetalle.layoutManager =

            LinearLayoutManager(
                requireContext()
            )

        // ------------------------------------------------
        // VALIDAR PEDIDO
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
        // OBSERVAR PEDIDO
        // ------------------------------------------------

        pedidoViewModel.pedidoDetalle.observe(

            viewLifecycleOwner

        ) { pedido ->

            if (pedido != null) {

                estadoPedido = pedido.estado

                // ------------------------------------------------
                // CLIENTE
                // ------------------------------------------------

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
                // FECHA
                // ------------------------------------------------

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
                // ADAPTER
                // ------------------------------------------------

                adapter =

                    LineaPedidoAdapter(

                        pedido.lineasPedido,

                        pedido.estado,

                        // ------------------------------------------------
                        // CLICK ITEM DEVOLUCIÓN
                        // ------------------------------------------------

                        onClickLinea = { lineaPedido ->

                            // ------------------------------------------------
                            // SOLO PEDIDOS ENTREGADOS
                            // ------------------------------------------------

                            if (pedido.estado == "entregado") {

                                // ------------------------------------------------
                                // EDITTEXT NUMÉRICO
                                // ------------------------------------------------

                                val editText =

                                    EditText(
                                        requireContext()
                                    )

                                editText.inputType =

                                    InputType.TYPE_CLASS_NUMBER

                                // ------------------------------------------------
                                // ALERT DIALOG
                                // ------------------------------------------------

                                AlertDialog.Builder(
                                    requireContext()
                                )

                                    .setTitle(
                                        "Cantidad devuelta"
                                    )

                                    .setMessage(
                                        "Introduce cantidad devuelta"
                                    )

                                    .setView(editText)

                                    // ------------------------------------------------
                                    // BOTÓN ACEPTAR
                                    // ------------------------------------------------

                                    .setPositiveButton(

                                        "Aceptar"

                                    ) { _, _ ->

                                        val cantidadDevuelta =

                                            editText.text
                                                .toString()
                                                .toIntOrNull() ?: 0

                                        // ------------------------------------------------
                                        // ACTUALIZAR DEVOLUCIÓN
                                        // ------------------------------------------------

                                        pedidoViewModel
                                            .actualizarCantidadDevuelta(

                                                pedido.id,

                                                lineaPedido.productoId,

                                                cantidadDevuelta
                                            )


                                        Toast.makeText(

                                            requireContext(),

                                            "Devolución actualizada",

                                            Toast.LENGTH_SHORT

                                        ).show()
                                    }

                                    // ------------------------------------------------
                                    // BOTÓN CANCELAR
                                    // ------------------------------------------------

                                    .setNegativeButton(

                                        "Cancelar",

                                        null
                                    )

                                    .show()
                            }
                        }
                    )

                // ------------------------------------------------
                // COLORES ESTADO
                // ------------------------------------------------

                if (pedido.estado == "pendiente") {

                    binding.chipEstado.chipBackgroundColor =
                        ColorStateList.valueOf(

                            ContextCompat.getColor(

                                requireContext(),

                                R.color.estado_pendiente_bg
                            )
                        )

                } else if (pedido.estado == "preparado") {

                    binding.chipEstado.chipBackgroundColor =
                        ColorStateList.valueOf(

                            ContextCompat.getColor(

                                requireContext(),

                                R.color.estado_camino_bg
                            )
                        )

                } else if (pedido.estado == "entregado") {

                    binding.chipEstado.chipBackgroundColor =
                        ColorStateList.valueOf(

                            ContextCompat.getColor(

                                requireContext(),

                                R.color.estado_entregado_bg
                            )
                        )

                } else if (pedido.estado == "finalizado") {

                    binding.chipEstado.chipBackgroundColor =
                        ColorStateList.valueOf(

                            ContextCompat.getColor(

                                requireContext(),

                                R.color.estado_finalizado_bg
                            )
                        )
                }

                // ------------------------------------------------
                // COLOR TEXTO ESTADO
                // ------------------------------------------------

                if (pedido.estado == "pendiente") {

                    binding.chipEstado.setTextColor(

                        ContextCompat.getColor(

                            requireContext(),

                            R.color.estado_pendiente_text
                        )
                    )

                } else if (pedido.estado == "preparado") {

                    binding.chipEstado.setTextColor(

                        ContextCompat.getColor(

                            requireContext(),

                            R.color.estado_camino_text
                        )
                    )

                } else if (pedido.estado == "entregado") {

                    binding.chipEstado.setTextColor(

                        ContextCompat.getColor(

                            requireContext(),

                            R.color.estado_entregado_text
                        )
                    )

                } else if (pedido.estado == "finalizado") {

                    binding.chipEstado.setTextColor(

                        ContextCompat.getColor(

                            requireContext(),

                            R.color.estado_finalizado_text
                        )
                    )
                }

                // ------------------------------------------------
                // ASIGNAR ADAPTER
                // ------------------------------------------------

                binding.recyclerDetalle.adapter =
                    adapter
            }
        }

        // ------------------------------------------------
        // OBSERVAR CLIENTE
        // ------------------------------------------------

        usuarioViewModel.usuario.observe(

            viewLifecycleOwner

        ) { usuario ->

            usuario?.let {

                binding.tvNombreComercio.text =

                    it.nombreComercio
                        ?: "Cliente"
            }
        }

        // ------------------------------------------------
        // BOTÓN LISTO
        // ------------------------------------------------

        binding.btnListo.setOnClickListener {

            val nuevoEstado =

                if (estadoPedido == "preparado") {

                    "entregado"

                } else {

                    "finalizado"
                }

            pedidoViewModel.actualizarEstadoPedido(

                pedidoId!!,

                nuevoEstado
            )

            val mensaje =

                if (estadoPedido == "preparado") {

                    "Pedido entregado"

                } else {

                    "Pedido finalizado"
                }

            Toast.makeText(

                requireContext(),

                mensaje,

                Toast.LENGTH_SHORT

            ).show()

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