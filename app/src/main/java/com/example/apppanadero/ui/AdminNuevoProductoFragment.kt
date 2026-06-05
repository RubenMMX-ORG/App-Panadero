package com.example.apppanadero.ui

import android.net.Uri
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
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileOutputStream

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

    //VARIABLE PARA IMAGEN
    private var imagenUri: Uri? = null

    //LAUNCHE PARA PERMISOS
    // Launcher moderno para pedir el permiso de cámara
    private val requestCameraPermissionLauncher =

        registerForActivityResult(

            ActivityResultContracts.RequestPermission()

        ) { isGranted ->

            if (isGranted) {

                abrirCamara()

            } else {

                Toast.makeText(

                    requireContext(),

                    "Permiso de cámara denegado",

                    Toast.LENGTH_SHORT

                ).show()
            }
        }


    // ------------------------------------------------
    // LAUNCHER GALERIA
    // ------------------------------------------------


    private val seleccionarImagenLauncher =

        registerForActivityResult(

            ActivityResultContracts.GetContent()

        ) { uri ->

            uri?.let {

                val ruta =

                    copiarImagenAlmacenInterno(
                        it
                    )

                ruta?.let { rutaGuardada ->

                    imagenUri = Uri.fromFile(

                        File(
                            rutaGuardada
                        )
                    )

                    binding.imgProducto.setImageURI(
                        imagenUri
                    )
                }
            }
        }

    // ------------------------------------------------
    // LAUNCHER CAMMARA
    // ------------------------------------------------

    private val takePicturePreviewLauncher =

        registerForActivityResult(

            ActivityResultContracts.TakePicturePreview()

        ) { bitmap ->
            //SE LLAMA AL METODO DE GUARDAR SI NO ES NULL
            bitmap?.let {

                binding.imgProducto.setImageBitmap(
                    it
                )

                imagenUri =
                    guardarBitmapInterno(it)

                Toast.makeText(

                    requireContext(),

                    "Foto guardada",

                    Toast.LENGTH_SHORT

                ).show()
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

        //BOTON PARA CARGAR IMAGENES AL IMAGEVIEW
        binding.btnGaleria.setOnClickListener {

            seleccionarImagenLauncher.launch(
                "image/*"
            )
        }

        //BOTON PARA CAMARA
        binding.btnCamara.setOnClickListener {

            comprobarPermisoCamara()
        }

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

                // ------------------------------------------------
                // CARGAR IMAGEN
                // ------------------------------------------------

                if (it.imagenUri.isNotEmpty()) {

                    imagenUri = Uri.parse(
                        it.imagenUri
                    )

                    binding.imgProducto.setImageURI(
                        imagenUri
                    )
                }
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
            iva = iva,
            imagenUri = imagenUri?.toString() ?: ""
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
    // METODO COMPROBAR PERMISO
    // ------------------------------------------------
    /**
     * Comprueba el permiso de cámara y, si está concedido, lanza la cámara.
     * Si no, solicita el permiso usando la API moderna.
     */
    private fun comprobarPermisoCamara() {

        when {
            // Permiso ya concedido
            ContextCompat.checkSelfPermission(

                requireContext(),

                Manifest.permission.CAMERA

            ) == PackageManager.PERMISSION_GRANTED -> {

                abrirCamara()
            }
            // Debemos mostrar una explicación al usuario
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Toast.makeText(
                    requireContext(),
                    "La aplicación necesita acceder a la cámara para poder hacer fotos.",
                    Toast.LENGTH_LONG
                ).show()
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            // Pedimos el permiso directamente
            else -> {

                requestCameraPermissionLauncher.launch(

                    Manifest.permission.CAMERA
                )
            }
        }
    }



    // ------------------------------------------------
    // METODO PARA ABRIR CAMARA
    // ------------------------------------------------
    /**
     * Lanza la cámara usando el contrato TakePicturePreview.
     * Devuelve un Bitmap (normalmente una miniatura de la foto).
     */
    private fun abrirCamara() {

        takePicturePreviewLauncher.launch(
            null
        )
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

    //TENEMOS QUE COPIAR LA IMAGEN EN LOCAL PORQUE LA URI ES TEMPORAL Y LO QUE GUARDAMOS EN FIRESTORE NO NOS SIRVE
    private fun copiarImagenAlmacenInterno(
        uriOriginal: Uri
    ): String? {

        return try {

            val carpeta = File(

                requireContext().filesDir,

                "productos"
            )

            if (!carpeta.exists()) {

                carpeta.mkdirs()
            }

            val archivoDestino = File(

                carpeta,

                "producto_${System.currentTimeMillis()}.jpg"
            )

            requireContext()
                .contentResolver
                .openInputStream(uriOriginal)
                ?.use { input ->

                    FileOutputStream(
                        archivoDestino
                    ).use { output ->

                        input.copyTo(output)
                    }
                }

            archivoDestino.absolutePath

        } catch (e: Exception) {

            e.printStackTrace()

            null
        }
    }

    //TENEMOS QUE COPIAR LA IMAGEN EN LOCAL PORQUE LA URI ES TEMPORAL Y LO QUE GUARDAMOS EN FIRESTORE NO NOS SIRVE
    private fun guardarBitmapInterno(
        bitmap: Bitmap
    ): Uri? {

        return try {

            val carpeta = File(

                requireContext().filesDir,

                "productos"
            )

            if (!carpeta.exists()) {

                carpeta.mkdirs()
            }

            val archivo = File(

                carpeta,

                "producto_${System.currentTimeMillis()}.jpg"
            )

            FileOutputStream(
                archivo
            ).use { output ->

                bitmap.compress(

                    Bitmap.CompressFormat.JPEG,

                    90,

                    output
                )
            }

            Uri.fromFile(
                archivo
            )

        } catch (e: Exception) {

            e.printStackTrace()

            null
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