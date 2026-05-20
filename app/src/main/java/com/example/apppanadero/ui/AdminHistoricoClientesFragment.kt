package com.example.apppanadero.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apppanadero.data.di.Injector
import com.example.apppanadero.data.model.LineaPedido
import com.example.apppanadero.data.model.Pedido
import com.example.apppanadero.data.model.Producto
import com.example.apppanadero.databinding.FragmentAdminHistorialClienteBinding
import com.example.apppanadero.ui.adapters.DetalleFacturacionAdapter
import com.example.apppanadero.viewmodel.PedidoViewModel
import com.example.apppanadero.viewmodel.ProductoViewModel
import com.example.apppanadero.viewmodel.UsuarioViewModel

class AdminHistoricoClientesFragment : Fragment() {

    // ------------------------------------------------
    // VIEW BINDING
    // ------------------------------------------------

    private var _binding:
            FragmentAdminHistorialClienteBinding? = null

    private val binding get() = _binding!!

    // ------------------------------------------------
    // VIEWMODELS
    // ------------------------------------------------

    private val usuarioViewModel:
            UsuarioViewModel by viewModels {

        Injector
            .provideUsuarioViewModelFactory()
    }

    private val pedidoViewModel:
            PedidoViewModel by viewModels {

        Injector
            .providePedidoViewModelFactory()
    }

    private val productoViewModel:
            ProductoViewModel by viewModels {

        Injector
            .provideProductoViewModelFactory()
    }

    // ------------------------------------------------
    // ADAPTER
    // ------------------------------------------------

    private lateinit var adapter:
            DetalleFacturacionAdapter

    // ------------------------------------------------
    // VARIABLES
    // ------------------------------------------------

    private var clienteId: String = ""

    // Productos cacheados
    private val mapaProductos =
        mutableMapOf<String, Producto>()

    // ------------------------------------------------
    // ON CREATE
    // ------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        arguments?.let {

            clienteId =

                AdminHistoricoClientesFragmentArgs
                    .fromBundle(it)
                    .clienteId
        }
    }

    // ------------------------------------------------
    // ON CREATE VIEW
    // ------------------------------------------------

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        _binding =
            FragmentAdminHistorialClienteBinding.inflate(

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
                ?.title = "Histórico Cliente"
        }

        // ------------------------------------------------
        // RECYCLER
        // ------------------------------------------------

        binding.recyclerProductos.layoutManager =

            LinearLayoutManager(
                requireContext()
            )

        // ------------------------------------------------
        // SPINNER DESCUENTOS
        // ------------------------------------------------

        val descuentos = listOf(

            "0%",
            "5%",
            "10%",
            "15%",
            "20%"
        )

        val descuentoAdapter =

            ArrayAdapter(

                requireContext(),

                android.R.layout.simple_list_item_1,

                descuentos
            )

        binding.dropdownDescuento.setAdapter(
            descuentoAdapter
        )

        // ------------------------------------------------
        // CARGAR CLIENTE
        // ------------------------------------------------

        usuarioViewModel.obtenerUsuario(
            clienteId
        )

        // ------------------------------------------------
        // OBSERVAR CLIENTE
        // ------------------------------------------------

        usuarioViewModel.usuario.observe(

            viewLifecycleOwner

        ) { cliente ->

            cliente?.let {

                // ------------------------------------------------
                // DATOS CLIENTE
                // ------------------------------------------------

                binding.tvNombreComercio.text =

                    it.nombreComercio

                binding.tvDireccion.text =

                    it.direccion

                binding.tvTelefono.text =

                    "📞 ${it.telefono}"

                binding.tvDescuento.text =

                    "${it.descuento.toInt()}% dto."

                binding.dropdownDescuento.setText(

                    "${it.descuento.toInt()}%",

                    false
                )

                // ------------------------------------------------
                // APROBADO
                // ------------------------------------------------

                binding.switchAprobado.isChecked =

                    it.aprobado == true

                // ------------------------------------------------
                // RUTA TEMPORAL
                // ------------------------------------------------

                binding.tvRuta.text =
                    "🚚 Ruta pendiente"

                // ------------------------------------------------
                // SWITCH APROBADO
                // ------------------------------------------------

                binding.switchAprobado
                    .setOnCheckedChangeListener {

                            _,
                            isChecked ->

                        val usuarioActualizado =

                            it.copy(

                                aprobado = isChecked
                            )

                        usuarioViewModel.guardarUsuario(

                            usuarioActualizado
                        ) { }
                    }

                // ------------------------------------------------
                // CAMBIAR DESCUENTO
                // ------------------------------------------------

                binding.dropdownDescuento
                    .setOnItemClickListener {

                            _,
                            _,
                            position,
                            _ ->

                        val descuentoSeleccionado =

                            descuentos[position]
                                .replace("%", "")
                                .toDouble()

                        val usuarioActualizado =

                            it.copy(

                                descuento = descuentoSeleccionado
                            )

                        usuarioViewModel.actualizarUsuario(

                            usuarioActualizado
                        ) { }
                    }
            }
        }

        // ------------------------------------------------
        // CARGAR PRODUCTOS
        // ------------------------------------------------

        productoViewModel.obtenerTodosProductos()

        // ------------------------------------------------
        // OBSERVAR PRODUCTOS
        // ------------------------------------------------

        productoViewModel.listaProductos.observe(

            viewLifecycleOwner

        ) { listaProductos ->

            mapaProductos.clear()

            listaProductos.forEach { producto ->

                mapaProductos[
                    producto.id
                ] = producto
            }
        }

        // ------------------------------------------------
        // CARGAR PEDIDOS CLIENTE
        // ------------------------------------------------

        pedidoViewModel.obtenerPedidosCliente(

            clienteId
        )

        // ------------------------------------------------
        // OBSERVAR PEDIDOS
        // ------------------------------------------------

        pedidoViewModel.listaPedidos.observe(

            viewLifecycleOwner

        ) { listaPedidos ->

            // ------------------------------------------------
            // FILTRAR FINALIZADOS
            // ------------------------------------------------

            val pedidosFinalizados =

                listaPedidos.filter {

                    it.estado == "finalizado"
                }

            // ------------------------------------------------
            // AGRUPAR LÍNEAS
            // ------------------------------------------------

            val listaResumen =

                mutableListOf<LineaPedido>()

            pedidosFinalizados.forEach { pedido ->

                listaResumen.addAll(
                    pedido.lineasPedido
                )
            }

            // ------------------------------------------------
            // RESUMEN
            // ------------------------------------------------

            val totalPedidos =

                listaResumen.sumOf {

                    it.cantidadPedida
                }

            val totalDevueltos =

                listaResumen.sumOf {

                    it.cantidadDevuelta
                }

            val totalNeto =

                listaResumen.sumOf {

                    it.cantidadFinal
                }

            binding.tvTotalPedidos.text =

                "Total pedidos: $totalPedidos uds"

            binding.tvTotalDevueltos.text =

                "Total devueltos: $totalDevueltos uds"

            binding.tvTotalNeto.text =

                "Total neto: $totalNeto uds"

            // ------------------------------------------------
            // ADAPTER
            // ------------------------------------------------

            adapter =
                DetalleFacturacionAdapter(

                    listaResumen,

                    mapaProductos
                )

            binding.recyclerProductos.adapter =
                adapter
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
