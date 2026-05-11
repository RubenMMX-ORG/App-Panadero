package com.example.apppanadero.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apppanadero.data.di.Injector
import com.example.apppanadero.data.model.LineaPedido
import com.example.apppanadero.data.model.Pedido
import com.example.apppanadero.data.model.Producto
import com.example.apppanadero.databinding.FragmentNuevoPedidoBinding
import com.example.apppanadero.ui.adapters.ProductoPedidoAdapter
import com.example.apppanadero.viewmodel.PedidoViewModel
import com.google.firebase.auth.FirebaseAuth

class NuevoPedidoFragment : Fragment() {

    private var _binding: FragmentNuevoPedidoBinding? = null
    private val binding get() = _binding!!

    // ViewModel pedidos
    private val pedidoViewModel: PedidoViewModel by viewModels {
        Injector.providePedidoViewModelFactory()
    }

    // Lista carrito
    private val listaLineasPedido =
        mutableListOf<LineaPedido>()

    // Total pedido
    private var totalPedido = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentNuevoPedidoBinding.inflate(
                inflater,
                container,
                false
            )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        // Cambiar el título de la ActionBar
        activity?.let {
            (it as? AppCompatActivity)?.supportActionBar?.title = "Nuevo pedido "
        }

        super.onViewCreated(view, savedInstanceState)

        // Productos temporales
        val listaProductos = listOf(

            Producto(
                id = "1",
                nombre = "Barra normal",
                categoria = "Pan",
                iva = 4.0
            ),

            Producto(
                id = "2",
                nombre = "Croissant",
                categoria = "Bollería",
                iva = 10.0
            ),

            Producto(
                id = "3",
                nombre = "Pan integral",
                categoria = "Pan",
                iva = 4.0
            )
        )

        // Adapter productos
        val adapter = ProductoPedidoAdapter(

            listaProductos

        ) { producto, cantidad ->

            actualizarLineaPedido(
                producto,
                cantidad
            )
        }

        binding.recyclerProductos.layoutManager =
            LinearLayoutManager(requireContext())

        binding.recyclerProductos.adapter =
            adapter

        // Confirmar pedido
        binding.btnConfirmarPedido.setOnClickListener {

            confirmarPedido()
        }

        observarPedidoGuardado()
    }

    // ------------------------------------------------
    // ACTUALIZAR CARRITO
    // ------------------------------------------------

    private fun actualizarLineaPedido(

        producto: Producto,
        cantidad: Int

    ) {

        // Eliminamos si existe
        listaLineasPedido.removeAll {

            it.productoId == producto.id
        }

        // Si cantidad > 0 añadimos
        if (cantidad > 0) {

            val linea = LineaPedido(

                productoId = producto.id,

                nombreProducto = producto.nombre,

                cantidadPedida = cantidad,

                cantidadDevuelta = 0,

                cantidadFinal = cantidad,

                precioUnitario = 1.20 // temporal
            )

            listaLineasPedido.add(linea)
        }

        recalcularTotal()
    }

    // ------------------------------------------------
    // RECALCULAR TOTAL
    // ------------------------------------------------

    private fun recalcularTotal() {

        totalPedido =
            listaLineasPedido.sumOf {

                it.cantidadPedida * it.precioUnitario
            }

        binding.tvTotal.text =
            "Total: €%.2f".format(totalPedido)
    }

    // ------------------------------------------------
    // CONFIRMAR PEDIDO
    // ------------------------------------------------

    private fun confirmarPedido() {

        val clienteId =
            FirebaseAuth
                .getInstance()
                .currentUser
                ?.uid

        if (clienteId == null) {

            Toast.makeText(
                requireContext(),
                "Usuario no autenticado",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (listaLineasPedido.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Añade productos al pedido",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val pedido = Pedido(

            clienteId = clienteId,

            lineasPedido = listaLineasPedido,

            estado = "pendiente",

            precioTotal = totalPedido
        )

        pedidoViewModel.guardarPedido(pedido)
    }

    // ------------------------------------------------
    // OBSERVAR PEDIDO GUARDADO
    // ------------------------------------------------

    private fun observarPedidoGuardado() {

        pedidoViewModel.pedidoGuardado.observe(
            viewLifecycleOwner
        ) { guardado ->

            if (guardado) {

                Toast.makeText(
                    requireContext(),
                    "Pedido realizado correctamente",
                    Toast.LENGTH_SHORT
                ).show()

                pedidoViewModel.limpiarPedidoGuardado()
            }
        }
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}
