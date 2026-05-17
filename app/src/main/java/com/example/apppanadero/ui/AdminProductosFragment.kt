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
import com.example.apppanadero.R
import com.example.apppanadero.data.di.Injector
import com.example.apppanadero.databinding.FragmentAdminProductosBinding
import com.example.apppanadero.ui.adapters.AdminProductoAdapter
import com.example.apppanadero.viewmodel.ProductoViewModel

class AdminProductosFragment : Fragment() {

    // ------------------------------------------------
    // VIEW BINDING
    // ------------------------------------------------

    private var _binding:
            FragmentAdminProductosBinding? = null

    private val binding get() = _binding!!

    // ------------------------------------------------
    // VIEWMODEL
    // ------------------------------------------------

    private val productoViewModel:
            ProductoViewModel by viewModels {

        Injector
            .provideProductoViewModelFactory()
    }

    // ------------------------------------------------
    // ADAPTER
    // ------------------------------------------------

    private lateinit var adapter:
            AdminProductoAdapter

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
        // CONFIGURAR RECYCLERVIEW
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

            adapter =
                AdminProductoAdapter(

                    // Lista productos
                    listaProductos,

                    // ------------------------------------------------
                    // EDITAR PRODUCTO
                    // ------------------------------------------------

                    onEditarClick = { producto ->

                        // Navegamos hacia:
                        // AdminNuevoProductoFragment
                        //
                        // Más adelante podremos
                        // pasar productoId para edición.
                        findNavController().navigate(

                            R.id.action_adminProductosFragment_to_adminNuevoProductoFragment
                        )
                    },

                    // ------------------------------------------------
                    // ELIMINAR PRODUCTO
                    // ------------------------------------------------

                    onEliminarClick = { producto ->

                        // Eliminamos producto
                        productoViewModel
                            .eliminarProducto(

                                producto.id
                            )

                        Toast.makeText(

                            requireContext(),

                            "Producto eliminado",

                            Toast.LENGTH_SHORT

                        ).show()
                    }
                )

            // Asignamos adapter
            binding.recyclerProductos.adapter =
                adapter
        }

        // ------------------------------------------------
        // CARGAR PRODUCTOS
        // ------------------------------------------------

        productoViewModel.obtenerTodosProductos()

        // ------------------------------------------------
        // BOTÓN AÑADIR PRODUCTO
        // ------------------------------------------------

        binding.btnAddProducto
            .setOnClickListener {

                findNavController().navigate(

                    R.id.action_adminProductosFragment_to_adminNuevoProductoFragment
                )
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