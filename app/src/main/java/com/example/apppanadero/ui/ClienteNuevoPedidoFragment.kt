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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apppanadero.data.di.Injector
import com.example.apppanadero.data.model.LineaPedido
import com.example.apppanadero.data.model.Pedido
import com.example.apppanadero.data.model.Producto
import com.example.apppanadero.databinding.FragmentClienteNuevoPedidoBinding
import com.example.apppanadero.ui.adapters.ProductoPedidoAdapter
import com.example.apppanadero.viewmodel.PedidoViewModel
import com.example.apppanadero.viewmodel.PrecioViewModel
import com.example.apppanadero.viewmodel.ProductoViewModel

class ClienteNuevoPedidoFragment : Fragment() {

    // ------------------------------------------------
    // VIEW BINDING
    // ------------------------------------------------

    private var _binding:
            FragmentClienteNuevoPedidoBinding? = null

    private val binding get() = _binding!!

    // ------------------------------------------------
    // SAFE ARGS
    // ------------------------------------------------

    private val args:
            ClienteNuevoPedidoFragmentArgs
            by navArgs()

    // ------------------------------------------------
    // VIEWMODELS
    // ------------------------------------------------

    private val productoViewModel:
            ProductoViewModel by viewModels {

        Injector
            .provideProductoViewModelFactory()
    }

    private val pedidoViewModel:
            PedidoViewModel by viewModels {

        Injector
            .providePedidoViewModelFactory()
    }

    private val precioViewModel:
            PrecioViewModel by viewModels {

        Injector
            .providePrecioViewModelFactory()
    }

    // ------------------------------------------------
    // ADAPTER
    // ------------------------------------------------

    private lateinit var adapter:
            ProductoPedidoAdapter

    // ------------------------------------------------
    // VARIABLES
    // ------------------------------------------------

    // Pedido edición
    private var pedidoId: String? = null

    // Líneas pedido
    private val listaLineasPedido =
        mutableListOf<LineaPedido>()

    // Cantidades iniciales
    private val cantidadesIniciales =
        mutableMapOf<String, Int>()

    // Precios reales
    private var mapaPrecios =
        mapOf<String, Double>()

    // ------------------------------------------------
    // ON CREATE VIEW
    // ------------------------------------------------

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        _binding =
            FragmentClienteNuevoPedidoBinding.inflate(

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
                ?.title = "Nuevo Pedido"
        }

        // ------------------------------------------------
        // SAFE ARGS
        // ------------------------------------------------

        pedidoId = args.pedidoId

        // ------------------------------------------------
        // RECYCLER
        // ------------------------------------------------

        binding.recyclerProductos.layoutManager =

            LinearLayoutManager(
                requireContext()
            )

        // ------------------------------------------------
        // CARGAR PRODUCTOS
        // ------------------------------------------------

        productoViewModel.obtenerTodosProductos()

        // ------------------------------------------------
        // CARGAR PRECIOS
        // ------------------------------------------------

        precioViewModel.obtenerPreciosVigentes()

        // ------------------------------------------------
        // OBSERVAR PRECIOS
        // ------------------------------------------------

        precioViewModel.listaPrecios.observe(

            viewLifecycleOwner

        ) { listaPrecios ->

            mapaPrecios =

                listaPrecios.associate {

                    it.productoId to it.precio
                }

            // ------------------------------------------------
            // RECARGAR ADAPTER
            // ------------------------------------------------

            productoViewModel.listaProductos.value?.let {

                cargarAdapterProductos(
                    it
                )
            }
        }

        // ------------------------------------------------
        // OBSERVAR PRODUCTOS
        // ------------------------------------------------

        productoViewModel.listaProductos.observe(

            viewLifecycleOwner

        ) { listaProductos ->

            cargarAdapterProductos(
                listaProductos
            )
        }

        // ------------------------------------------------
        // BOTÓN CREAR PEDIDO
        // ------------------------------------------------

        binding.btnCrearPedido
            .setOnClickListener {

                guardarPedido()
            }
    }

    // ------------------------------------------------
    // CARGAR ADAPTER
    // ------------------------------------------------

    private fun cargarAdapterProductos(

        listaProductos: List<Producto>

    ) {

        adapter =

            ProductoPedidoAdapter(

                listaProductos,

                mapaPrecios,

                cantidadesIniciales

            ) { producto, cantidad ->

                actualizarLineaPedido(

                    producto,

                    cantidad
                )
            }

        binding.recyclerProductos.adapter =
            adapter
    }

    // ------------------------------------------------
    // ACTUALIZAR LÍNEA PEDIDO
    // ------------------------------------------------

    private fun actualizarLineaPedido(

        producto: Producto,
        cantidad: Int

    ) {

        // ------------------------------------------------
        // ELIMINAR SI CANTIDAD 0
        // ------------------------------------------------

        if (cantidad == 0) {

            listaLineasPedido.removeAll {

                it.productoId == producto.id
            }

            cantidadesIniciales.remove(
                producto.id
            )

            actualizarTotal()

            return
        }

        // ------------------------------------------------
        // GUARDAR CANTIDAD
        // ------------------------------------------------

        cantidadesIniciales[
            producto.id
        ] = cantidad

        // ------------------------------------------------
        // PRECIO REAL
        // ------------------------------------------------

        val precioUnitario =

            mapaPrecios[
                producto.id
            ] ?: 0.0

        // ------------------------------------------------
        // CREAR LÍNEA
        // ------------------------------------------------

        val lineaPedido =

            LineaPedido(

                productoId = producto.id,

                nombreProducto =
                    producto.nombre,

                cantidadPedida =
                    cantidad,

                cantidadFinal =
                    cantidad,

                cantidadDevuelta = 0,

                precioUnitario =
                    precioUnitario
            )

        // ------------------------------------------------
        // REEMPLAZAR SI EXISTE
        // ------------------------------------------------

        listaLineasPedido.removeAll {

            it.productoId == producto.id
        }

        listaLineasPedido.add(
            lineaPedido
        )

        actualizarTotal()
    }

    // ------------------------------------------------
    // ACTUALIZAR TOTAL
    // ------------------------------------------------

    private fun actualizarTotal() {

        val total =

            listaLineasPedido.sumOf {

                it.cantidadPedida *
                        it.precioUnitario
            }

        binding.tvTotalPedido.text =

            "Total: %.2f €".format(
                total
            )
    }

    // ------------------------------------------------
    // GUARDAR PEDIDO
    // ------------------------------------------------

    private fun guardarPedido() {

        // ------------------------------------------------
        // VALIDAR
        // ------------------------------------------------

        if (listaLineasPedido.isEmpty()) {

            Toast.makeText(

                requireContext(),

                "Añade productos",

                Toast.LENGTH_SHORT

            ).show()

            return
        }

        // ------------------------------------------------
        // TOTAL
        // ------------------------------------------------

        val total =

            listaLineasPedido.sumOf {

                it.cantidadPedida *
                        it.precioUnitario
            }

        // ------------------------------------------------
        // CREAR PEDIDO
        // ------------------------------------------------

        val pedido =

            Pedido(

                id = pedidoId ?: "",

                lineasPedido =
                    listaLineasPedido,

                precioTotal =
                    total
            )

        // ------------------------------------------------
        // GUARDAR FIREBASE
        // ------------------------------------------------

        pedidoViewModel.guardarPedido(
            pedido
        )

        Toast.makeText(

            requireContext(),

            "Pedido guardado",

            Toast.LENGTH_SHORT

        ).show()

        findNavController()
            .popBackStack()
    }

    // ------------------------------------------------
    // ON DESTROY VIEW
    // ------------------------------------------------

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}