package com.example.apppanadero.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apppanadero.R
import com.example.apppanadero.data.di.Injector
import com.example.apppanadero.databinding.FragmentAdminProductosBinding
import com.example.apppanadero.ui.adapters.AdminProductoAdapter
import com.example.apppanadero.viewmodel.PrecioViewModel
import com.example.apppanadero.viewmodel.ProductoViewModel

class AdminProductosFragment : Fragment() {

    // ------------------------------------------------
    // VIEW BINDING
    // ------------------------------------------------

    private var _binding:
            FragmentAdminProductosBinding? = null

    private val binding get() = _binding!!

    // ------------------------------------------------
    // VIEWMODEL PRODUCTOS
    // ------------------------------------------------

    private val productoViewModel:
            ProductoViewModel by viewModels {

        Injector
            .provideProductoViewModelFactory()
    }

    // ------------------------------------------------
    // VIEWMODEL PRECIOS
    // ------------------------------------------------

    private val precioViewModel:
            PrecioViewModel by viewModels {

        Injector
            .providePrecioViewModelFactory()
    }

    // ------------------------------------------------
    // ADAPTER
    // ------------------------------------------------

    private lateinit var adapter:
            AdminProductoAdapter

    // ------------------------------------------------
    // MAPA PRECIOS
    // ------------------------------------------------

    private val mapaPrecios =
        mutableMapOf<String, Double>()


    //Variables para asegurar que carga antes de mostrar

    private var listaProductosActual =
        emptyList<com.example.apppanadero.data.model.Producto>()

    private var productosCargados = false

    private var preciosCargados = false

    // ------------------------------------------------
    // ON CREATE VIEW
    // ------------------------------------------------

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        _binding =
            FragmentAdminProductosBinding.inflate(

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
                ?.title = "Gestion Productos"
        }

        // ------------------------------------------------
        // RECYCLERVIEW
        // ------------------------------------------------

        binding.recyclerProductos.layoutManager =

            LinearLayoutManager(
                requireContext()
            )



        // ------------------------------------------------
        // OBSERVAR PRODUCTOS
        // ------------------------------------------------

        productoViewModel.listaProductos.observe(

            viewLifecycleOwner

        ) { listaProductos ->

            listaProductosActual =
                listaProductos

            productosCargados = true

            intentarCrearAdapter()
        }

        // ------------------------------------------------
        // OBSERVAR PRECIO ACTUAL
        // ------------------------------------------------

        precioViewModel.listaPrecios.observe(

            viewLifecycleOwner


        ) { listaPrecios ->


            mapaPrecios.clear()

            listaPrecios.forEach {

                mapaPrecios[
                    it.productoId
                ] = it.precio
            }

            preciosCargados = true

            intentarCrearAdapter()
        }

        // ------------------------------------------------
        // CARGAR PRODUCTOS
        // ------------------------------------------------

        productoViewModel.obtenerTodosProductos()

        // ------------------------------------------------
        // CARGAR PRECIOS VIGENTES
        // ------------------------------------------------
        precioViewModel.obtenerPreciosVigentes()

        // ------------------------------------------------
        // BOTÓN NUEVO PRODUCTO
        // ------------------------------------------------

        binding.btnAddProducto
            .setOnClickListener {

                findNavController().navigate(

                    R.id.action_adminProductosFragment_to_adminNuevoProductoFragment
                )
            }
    }
    private fun intentarCrearAdapter() {

        if (

            productosCargados &&
            preciosCargados

        ) {

            adapter =

                AdminProductoAdapter(

                    listaProductosActual,

                    mapaPrecios,

                    onEditarClick = { producto ->

                        val action =

                            AdminProductosFragmentDirections
                                .actionAdminProductosFragmentToAdminNuevoProductoFragment(

                                    productoId = producto.id
                                )

                        findNavController()
                            .navigate(action)
                    },

                    onEliminarClick = { producto ->

                        productoViewModel
                            .eliminarProducto(
                                producto.id
                            )

                        productoViewModel
                            .obtenerTodosProductos()// llamamos para refresca el reycler

                        Toast.makeText(

                            requireContext(),

                            "Producto eliminado",

                            Toast.LENGTH_SHORT

                        ).show()
                    }
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