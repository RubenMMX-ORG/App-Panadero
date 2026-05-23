package com.example.apppanadero.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.apppanadero.R
import com.example.apppanadero.data.di.Injector
import com.example.apppanadero.data.model.Precio
import com.example.apppanadero.data.model.Producto
import com.example.apppanadero.databinding.FragmentAdminNuevoProductoBinding
import com.example.apppanadero.viewmodel.PrecioViewModel
import com.example.apppanadero.viewmodel.ProductoViewModel
import com.google.firebase.Timestamp

class AdminNuevoProductoFragment : Fragment() {

    // ------------------------------------------------
    // VIEW BINDING
    // ------------------------------------------------

    private var _binding:
            FragmentAdminNuevoProductoBinding? = null

    private val binding get() = _binding!!

    // ------------------------------------------------
    // VIEWMODELS
    // ------------------------------------------------

    private val productoViewModel:
            ProductoViewModel by viewModels {

        Injector
            .provideProductoViewModelFactory()
    }

    private val precioViewModel:
            PrecioViewModel by viewModels {

        Injector
            .providePrecioViewModelFactory()
    }

    // ------------------------------------------------
    // VARIABLES
    // ------------------------------------------------

    // null -> nuevo producto
    // id   -> edición
    private var productoId: String? = null

    // ------------------------------------------------
    // ON CREATE VIEW
    // ------------------------------------------------

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        _binding =
            FragmentAdminNuevoProductoBinding.inflate(

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
        // RECUPERAR SAFEARGS
        // ------------------------------------------------

        val args =
            AdminNuevoProductoFragmentArgs
                .fromBundle(requireArguments())

        productoId =
            args.productoId

        // ------------------------------------------------
        // ACTION BAR
        // ------------------------------------------------

        activity?.let {

            (it as? AppCompatActivity)
                ?.supportActionBar
                ?.title =

                if (productoId.isNullOrEmpty()) {

                    "Nuevo producto"

                } else {

                    "Editar producto"
                }
        }

        // ------------------------------------------------
        // CAMBIAR TEXTO BOTÓN
        // ------------------------------------------------

        if (!productoId.isNullOrEmpty()) {

            binding.btnCrearProducto.text =
                "Actualizar producto"
        }

        // ------------------------------------------------
        // CONFIGURAR DROPDOWNS
        // ------------------------------------------------

        configurarDropdowns()

        // ------------------------------------------------
        // OBSERVERS
        // ------------------------------------------------

        observarProductoGuardado()

        observarPrecioActual()

        observarErrores()

        // ------------------------------------------------
        // MODO EDICIÓN
        // ------------------------------------------------

        if (!productoId.isNullOrEmpty()) {

            cargarProducto()
        }

        // ------------------------------------------------
        // BOTÓN GUARDAR
        // ------------------------------------------------

        binding.btnCrearProducto
            .setOnClickListener {

                guardarProducto()
            }
    }

    // ------------------------------------------------
    // CONFIGURAR DROPDOWNS
    // ------------------------------------------------

    private fun configurarDropdowns() {

        // ------------------------------------------------
        // IVA
        // ------------------------------------------------

        val listaIva = listOf(

            "4",

            "10",

            "21"
        )

        val adapterIva = ArrayAdapter(

            requireContext(),

            android.R.layout.simple_list_item_1,

            listaIva
        )

        binding.dropIva.setAdapter(
            adapterIva
        )

        // ------------------------------------------------
        // CATEGORÍAS
        // ------------------------------------------------

        val listaCategorias = listOf(

            "Pan",

            "Pastas",

            "Repostería",

            "Asados",

            "Especialidades",

            "Otros"
        )

        val adapterCategorias = ArrayAdapter(

            requireContext(),

            android.R.layout.simple_list_item_1,

            listaCategorias
        )

        binding.dropCategoria.setAdapter(
            adapterCategorias
        )
    }

    // ------------------------------------------------
    // CARGAR PRODUCTO PARA EDICIÓN
    // ------------------------------------------------

    private fun cargarProducto() {

        // ------------------------------------------------
        // CARGAR PRODUCTO
        // ------------------------------------------------

        productoViewModel.obtenerProductoPorId(
            productoId!!
        )

        // ------------------------------------------------
        // OBSERVAR PRODUCTO
        // ------------------------------------------------

        productoViewModel.productoDetalle.observe(

            viewLifecycleOwner

        ) { producto ->

            producto?.let {

                // ------------------------------------------------
                // NOMBRE
                // ------------------------------------------------

                binding.etNombreProducto.setText(
                    it.nombre
                )

                // ------------------------------------------------
                // CATEGORÍA
                // ------------------------------------------------

                binding.dropCategoria.setText(

                    it.categoria,

                    false
                )

                // ------------------------------------------------
                // IVA
                // ------------------------------------------------

                binding.dropIva.setText(

                    it.iva.toString(),

                    false
                )

                // ------------------------------------------------
                // CARGAR PRECIO VIGENTE
                // ------------------------------------------------

                precioViewModel.obtenerPrecioVigente(
                    it.id
                )
            }
        }
    }

    // ------------------------------------------------
    // OBSERVAR PRECIO ACTUAL
    // ------------------------------------------------

    private fun observarPrecioActual() {

        precioViewModel.precioActual.observe(

            viewLifecycleOwner

        ) { precio ->

            precio?.let {

                binding.etPrecio.setText(

                    it.precio.toString()
                )
            }
        }
    }

    // ------------------------------------------------
    // GUARDAR PRODUCTO
    // ------------------------------------------------

    private fun guardarProducto() {

        // ------------------------------------------------
        // RECUPERAR DATOS
        // ------------------------------------------------

        val nombre =

            binding.etNombreProducto
                .text
                .toString()
                .trim()

        val categoria =

            binding.dropCategoria
                .text
                .toString()
                .trim()

        val ivaTexto =

            binding.dropIva
                .text
                .toString()
                .trim()

        val precioTexto =

            binding.etPrecio
                .text
                .toString()
                .trim()

        // ------------------------------------------------
        // VALIDACIONES
        // ------------------------------------------------

        if (nombre.isEmpty()) {

            Toast.makeText(

                requireContext(),

                "Introduce un nombre",

                Toast.LENGTH_SHORT

            ).show()

            return
        }

        if (categoria.isEmpty()) {

            Toast.makeText(

                requireContext(),

                "Selecciona categoría",

                Toast.LENGTH_SHORT

            ).show()

            return
        }

        if (ivaTexto.isEmpty()) {

            Toast.makeText(

                requireContext(),

                "Selecciona IVA",

                Toast.LENGTH_SHORT

            ).show()

            return
        }

        if (precioTexto.isEmpty()) {

            Toast.makeText(

                requireContext(),

                "Introduce precio",

                Toast.LENGTH_SHORT

            ).show()

            return
        }

        // ------------------------------------------------
        // CONVERSIONES
        // ------------------------------------------------

        val iva =
            ivaTexto.toDouble()

        val precioDouble =
            precioTexto.toDouble()

        // ------------------------------------------------
        // CREAR PRODUCTO
        // ------------------------------------------------

        val producto = Producto(

            id = productoId ?: "",

            nombre = nombre,

            categoria = categoria,

            iva = iva
        )

        // ------------------------------------------------
        // NUEVO PRODUCTO
        // ------------------------------------------------

        if (productoId.isNullOrEmpty()) {

            productoViewModel.guardarProducto(
                producto
            )

        } else {

            // ------------------------------------------------
            // ACTUALIZAR PRODUCTO
            // ------------------------------------------------

            productoViewModel.actualizarProducto(
                producto
            )

            precioViewModel.actualizarPrecio(

                producto.id,

                precioDouble
            )

            Toast.makeText(

                requireContext(),

                "Producto actualizado",

                Toast.LENGTH_SHORT

            ).show()

            findNavController()
                .popBackStack()
        }
    }

    // ------------------------------------------------
    // OBSERVAR PRODUCTO GUARDADO
    // ------------------------------------------------

    private fun observarProductoGuardado() {

        productoViewModel.productoGuardado.observe(

            viewLifecycleOwner

        ) { producto ->

            producto?.let {

                // ------------------------------------------------
                // RECUPERAR PRECIO
                // ------------------------------------------------

                val precioTexto =

                    binding.etPrecio
                        .text
                        .toString()

                val precioDouble =
                    precioTexto.toDouble()

                // ------------------------------------------------
                // CREAR PRECIO INICIAL
                // ------------------------------------------------

                val precio = Precio(

                    productoId = producto.id,

                    precio = precioDouble,

                    fechaInicio =
                        Timestamp.now(),

                    vigente = true
                )

                // ------------------------------------------------
                // GUARDAR PRECIO
                // ------------------------------------------------

                precioViewModel.guardarPrecio(
                    precio
                )

                Toast.makeText(

                    requireContext(),

                    "Producto creado correctamente",

                    Toast.LENGTH_SHORT

                ).show()

                // ------------------------------------------------
                // VOLVER ATRÁS
                // ------------------------------------------------

                findNavController()
                    .popBackStack()

                // ------------------------------------------------
                // LIMPIAR LIVEDATA
                // ------------------------------------------------

                productoViewModel
                    .limpiarProductoGuardado()
            }
        }
    }

    // ------------------------------------------------
    // OBSERVAR ERRORES
    // ------------------------------------------------

    private fun observarErrores() {

        productoViewModel.error.observe(

            viewLifecycleOwner

        ) { error ->

            if (error.isNotEmpty()) {

                Toast.makeText(

                    requireContext(),

                    error,

                    Toast.LENGTH_SHORT

                ).show()

                productoViewModel
                    .limpiarError()
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