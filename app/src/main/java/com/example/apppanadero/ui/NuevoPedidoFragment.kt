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
import com.example.apppanadero.data.model.LineaPedido
import com.example.apppanadero.data.model.Pedido
import com.example.apppanadero.data.model.Producto
import com.example.apppanadero.databinding.FragmentNuevoPedidoBinding
import com.example.apppanadero.ui.adapters.ProductoPedidoAdapter
import com.example.apppanadero.viewmodel.PedidoViewModel
import com.example.apppanadero.viewmodel.ProductoViewModel
import com.google.firebase.auth.FirebaseAuth

class NuevoPedidoFragment : Fragment() {

    private var _binding: FragmentNuevoPedidoBinding? = null
    private val binding get() = _binding!!

    // ------------------------------------------------
    // VIEWMODELS
    // ------------------------------------------------

    private val pedidoViewModel: PedidoViewModel by viewModels {

        Injector.providePedidoViewModelFactory()
    }

    private val productoViewModel: ProductoViewModel by viewModels {

        Injector.provideProductoViewModelFactory()
    }

    // ------------------------------------------------
    // VARIABLES
    // ------------------------------------------------

    // PedidoId si estamos editando.
    // Si es null:
    // estamos creando un pedido nuevo.
    private var pedidoId: String? = null

    // Número pedido actual.
    // Solo se reutiliza al editar.
    private var numeroPedidoActual = 0

    // Lista líneas pedido seleccionadas
    private val listaLineasPedido =
        mutableListOf<LineaPedido>()

    // Total pedido
    private var totalPedido = 0.0

    // Mapa cantidades iniciales.
    //
    // Se usa cuando editamos un pedido.
    //
    // key   -> productoId
    // value -> cantidadPedida
    private var cantidadesIniciales:
            Map<String, Int> = emptyMap()

    // Adapter RecyclerView
    private lateinit var adapter:
            ProductoPedidoAdapter

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

        super.onViewCreated(
            view,
            savedInstanceState
        )


        // Cambiar el título de la ActionBar
        activity?.let {
            (it as? AppCompatActivity)?.supportActionBar?.title = "Nuevo pedido"
        }
        super.onViewCreated(view, savedInstanceState)

        // ------------------------------------------------
        // RECUPERAR ARGUMENTOS
        // ------------------------------------------------

        // Si venimos desde:
        //
        // - Nuevo pedido:
        //      pedidoId = null
        //
        // - Modificar pedido:
        //      pedidoId tendrá valor
        val args =
            NuevoPedidoFragmentArgs
                .fromBundle(requireArguments())

        pedidoId = args.pedidoId

        // ------------------------------------------------
        // CONFIGURAR RECYCLERVIEW
        // ------------------------------------------------

        binding.recyclerProductos.layoutManager =
            LinearLayoutManager(requireContext())

        // ------------------------------------------------
        // OBSERVAR PRODUCTOS
        // ------------------------------------------------

        // Este observer se ejecutará cuando
        // Firestore devuelva la lista productos.
        //
        // Aquí es donde realmente se crea
        // el Adapter.
        productoViewModel.listaProductos.observe(
            viewLifecycleOwner
        ) { listaProductos ->

            adapter =
                ProductoPedidoAdapter(

                    // Lista productos Firestore
                    listaProductos,

                    // Cantidades iniciales
                    //
                    // Si es nuevo pedido:
                    // emptyMap()
                    //
                    // Si es edición:
                    // contendrá cantidades reales
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
        // CARGAR PRODUCTOS
        // ------------------------------------------------

        productoViewModel.obtenerTodosProductos()

        // ------------------------------------------------
        // MODO EDICIÓN
        // ------------------------------------------------

        // Si pedidoId != null:
        // estamos modificando pedido.
        if (pedidoId != null) {

            pedidoViewModel.obtenerPedidoPorId(
                pedidoId!!
            )

            // Observamos pedido recuperado
            pedidoViewModel.pedidoDetalle.observe(
                viewLifecycleOwner
            ) { pedido ->

                if (pedido != null) {

                    // ------------------------------------------------
                    // RECUPERAR LINEAS PEDIDO
                    // ------------------------------------------------

                    listaLineasPedido.clear()

                    listaLineasPedido.addAll(
                        pedido.lineasPedido
                    )

                    // ------------------------------------------------
                    // RECUPERAR TOTAL
                    // ------------------------------------------------

                    totalPedido =
                        pedido.precioTotal

                    // ------------------------------------------------
                    // RECUPERAR NÚMERO PEDIDO
                    // ------------------------------------------------

                    numeroPedidoActual =
                        pedido.numeroPedido

                    // ------------------------------------------------
                    // ACTUALIZAR TOTAL UI
                    // ------------------------------------------------

                    binding.tvTotal.text = "Total: %.2f€".format(totalPedido)

                    // ------------------------------------------------
                    // CREAR MAPA CANTIDADES
                    // ------------------------------------------------

                    // Convertimos:
                    //
                    // List<LineaPedido>
                    //
                    // en:
                    //
                    // Map<productoId, cantidad>
                    //
                    // Esto permitirá que el
                    // RecyclerView aparezca
                    // ya cargado al editar.
                    cantidadesIniciales =

                        pedido.lineasPedido.associate {

                            it.productoId to
                                    it.cantidadPedida
                        }

                    // ------------------------------------------------
                    // RECARGAR ADAPTER
                    // ------------------------------------------------

                    // Cuando ya tenemos:
                    //
                    // - productos
                    // - cantidades
                    //
                    // volvemos a asignar adapter
                    // para refrescar RecyclerView.
                    adapter =
                        ProductoPedidoAdapter(

                            productoViewModel
                                .listaProductos
                                .value ?: emptyList(),

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
            }
        }

        // ------------------------------------------------
        // BOTÓN CONFIRMAR
        // ------------------------------------------------

        binding.btnConfirmarPedido
            .setOnClickListener {

                confirmarPedido()
            }

        // ------------------------------------------------
        // OBSERVAR PEDIDO GUARDADO
        // ------------------------------------------------

        observarPedidoGuardado()
    }

    // ------------------------------------------------
    // ACTUALIZAR LINEA PEDIDO
    // ------------------------------------------------

    private fun actualizarLineaPedido(

        producto: Producto,
        cantidad: Int

    ) {

        // Eliminamos línea previa
        // para evitar duplicados.
        listaLineasPedido.removeAll {

            it.productoId == producto.id
        }

        // ------------------------------------------------
        // AÑADIR NUEVA LINEA
        // ------------------------------------------------

        if (cantidad > 0) {

            val linea = LineaPedido(

                productoId = producto.id,

                nombreProducto = producto.nombre,

                cantidadPedida = cantidad,

                cantidadDevuelta = 0,

                // Inicialmente:
                // cantidadFinal = cantidadPedida
                cantidadFinal = cantidad,

                precioUnitario =
                    producto.precio
            )

            listaLineasPedido.add(linea)
        }

        // Recalculamos total
        recalcularTotal()
    }

    // ------------------------------------------------
    // RECALCULAR TOTAL
    // ------------------------------------------------

    private fun recalcularTotal() {

        totalPedido =
            listaLineasPedido.sumOf {

                it.cantidadPedida *
                        it.precioUnitario
            }

        binding.tvTotal.text = "Total: %.2f €".format(totalPedido)
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

        // ------------------------------------------------
        // VALIDAR USUARIO
        // ------------------------------------------------

        if (clienteId == null) {

            Toast.makeText(
                requireContext(),
                "Usuario no autenticado",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // ------------------------------------------------
        // VALIDAR PRODUCTOS
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
        // MODO EDITAR
        // ------------------------------------------------

        if (pedidoId != null) {

            val pedidoActualizado = Pedido(

                id = pedidoId!!,

                numeroPedido = numeroPedidoActual,

                clienteId = clienteId,

                lineasPedido = listaLineasPedido,

                estado = "pendiente",

                precioTotal = totalPedido
            )

            // Actualizamos pedido existente
            pedidoViewModel.actualizarPedido(
                pedidoActualizado
            )

        } else {

            // ------------------------------------------------
            // NUEVO PEDIDO
            // ------------------------------------------------

            val nuevoPedido = Pedido(

                clienteId = clienteId,

                lineasPedido = listaLineasPedido,

                estado = "pendiente",

                precioTotal = totalPedido
            )

            // Guardamos nuevo pedido
            pedidoViewModel.guardarPedido(
                nuevoPedido
            )
        }
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
                    "Pedido guardado correctamente",
                    Toast.LENGTH_SHORT
                ).show()

                // Volvemos atrás
                findNavController().popBackStack()

                // Limpiamos LiveData
                // para evitar bucles infinitos
                pedidoViewModel.limpiarPedidoGuardado()
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