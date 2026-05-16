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
import com.example.apppanadero.data.di.Injector
import com.example.apppanadero.data.model.Precio
import com.example.apppanadero.data.model.Producto
import com.example.apppanadero.databinding.FragmentAdminCrearProductoBinding
import com.example.apppanadero.viewmodel.PrecioViewModel
import com.example.apppanadero.viewmodel.ProductoViewModel
import com.google.firebase.Timestamp

class NuevoProductoFragment : Fragment() {

    // ------------------------------------------------
    // VIEW BINDING
    // ------------------------------------------------

    private var _binding:
            FragmentAdminCrearProductoBinding? = null

    private val binding get() = _binding!!

    // ------------------------------------------------
    // VIEWMODELS
    // ------------------------------------------------

    private val productoViewModel:
            ProductoViewModel by viewModels {

        Injector.provideProductoViewModelFactory()
    }

    private val precioViewModel:
            PrecioViewModel by viewModels {

        Injector.providePrecioViewModelFactory()
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
            FragmentAdminCrearProductoBinding.inflate(

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
                ?.title = "Nuevo producto"
        }

        // ------------------------------------------------
        // CONFIGURAR DROPDOWNS
        // ------------------------------------------------

        configurarDropdowns()

        // ------------------------------------------------
        // OBSERVERS
        // ------------------------------------------------

        observarProductoGuardado()

        observarErrores()

        // ------------------------------------------------
        // BOTÓN CREAR PRODUCTO
        // ------------------------------------------------

        binding.btnCrearProducto
            .setOnClickListener {

                crearProducto()
            }
    }

    // ------------------------------------------------
    // CONFIGURAR DROPDOWNS
    // ------------------------------------------------

    private fun configurarDropdowns() {

        // ------------------------------------------------
        // LISTA IVA
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
        // LISTA CATEGORÍAS
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
    // CREAR PRODUCTO
    // ------------------------------------------------

    private fun crearProducto() {

        // ------------------------------------------------
        // RECUPERAR DATOS FORMULARIO
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

                "Selecciona una categoría",

                Toast.LENGTH_SHORT

            ).show()

            return
        }

        if (ivaTexto.isEmpty()) {

            Toast.makeText(

                requireContext(),

                "Selecciona un IVA",

                Toast.LENGTH_SHORT

            ).show()

            return
        }

        if (precioTexto.isEmpty()) {

            Toast.makeText(

                requireContext(),

                "Introduce un precio",

                Toast.LENGTH_SHORT

            ).show()

            return
        }

        // ------------------------------------------------
        // CONVERSIONES
        // ------------------------------------------------

        val iva = ivaTexto.toDouble()

        val precio = precioTexto.toDouble()

        // ------------------------------------------------
        // CREAR PRODUCTO
        // ------------------------------------------------

        // IMPORTANTE 😄🔥
        //
        // Producto ya NO tiene:
        // val precio
        //
        // El precio se guarda
        // en entidad independiente.
        val producto = Producto(

            nombre = nombre,

            categoria = categoria,

            iva = iva
        )

        // ------------------------------------------------
        // GUARDAR PRODUCTO
        // ------------------------------------------------

        productoViewModel.guardarProducto(
            producto
        )
    }

    // ------------------------------------------------
    // OBSERVAR PRODUCTO GUARDADO
    // ------------------------------------------------

    private fun observarProductoGuardado() {

        // ------------------------------------------------
        // OBSERVAMOS PRODUCTO CREADO
        // ------------------------------------------------

        productoViewModel.productoGuardado.observe(

            viewLifecycleOwner

        ) { producto ->

            // Si producto != null:
            // guardado correcto
            if (producto != null) {

                // ------------------------------------------------
                // RECUPERAR PRECIO FORMULARIO
                // ------------------------------------------------

                val precioTexto =

                    binding.etPrecio
                        .text
                        .toString()

                val precioDouble =
                    precioTexto.toDouble()

                // ------------------------------------------------
                // CREAR ENTIDAD PRECIO
                // ------------------------------------------------

                // El producto ya existe.
                //
                // Ahora creamos el
                // precio inicial asociado.
                val precio = Precio(

                    // FK lógica
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

                // Volvemos atrás
                findNavController()
                    .popBackStack()

                // Limpiamos LiveData
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