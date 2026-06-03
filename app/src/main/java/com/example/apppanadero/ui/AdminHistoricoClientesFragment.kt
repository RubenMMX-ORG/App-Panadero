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
import com.example.apppanadero.data.model.Producto
import com.example.apppanadero.databinding.FragmentAdminHistorialClienteBinding
import com.example.apppanadero.ui.adapters.DetalleFacturacionAdapter
import com.example.apppanadero.viewmodel.PedidoViewModel
import com.example.apppanadero.viewmodel.ProductoViewModel
import com.example.apppanadero.viewmodel.UsuarioViewModel
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Environment
import android.graphics.pdf.PdfDocument
import android.graphics.Paint
import android.widget.Toast
import com.example.apppanadero.data.model.Usuario
import com.google.firebase.Timestamp
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.core.content.FileProvider

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

    // ------------------------------------------------
    // FECHAS FILTRO
    // ------------------------------------------------

    // null = sin filtro
    private var fechaInicio:
            Timestamp? = null

    private var fechaFin:
            Timestamp? = null

    // ------------------------------------------------
    // PRODUCTOS CACHEADOS
    // ------------------------------------------------

    private val mapaProductos =
        mutableMapOf<String, Producto>()

    // ------------------------------------------------
    // VARIABLES GLOBALES PDF
    // ------------------------------------------------

    private lateinit var listaResumenGlobal:
            MutableList<LineaPedido>

    private var clienteGlobal:
            Usuario? = null


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
        // ARGUMENTOS
        // ------------------------------------------------

        arguments?.let {

            clienteId =

                AdminHistoricoClientesFragmentArgs
                    .fromBundle(it)
                    .clienteId
        }

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
        // FECHA INICIO
        // ------------------------------------------------

        binding.etFechaInicio.setOnClickListener {

            // ------------------------------------------------
            // CALENDARIO ACTUAL
            // ------------------------------------------------

            val calendario =
                Calendar.getInstance()

            val year =
                calendario.get(Calendar.YEAR)

            val month =
                calendario.get(Calendar.MONTH)

            val day =
                calendario.get(Calendar.DAY_OF_MONTH)

            // ------------------------------------------------
            // DATE PICKER
            // ------------------------------------------------

            val dialog =

                DatePickerDialog(

                    requireContext(),

                    { _, año, mes, dia ->

                        // ------------------------------------------------
                        // FORMATEAR TEXTO
                        // ------------------------------------------------

                        val fechaTexto =

                            "$dia/${mes + 1}/$año"

                        binding.etFechaInicio.setText(
                            fechaTexto
                        )

                        // ------------------------------------------------
                        // CONVERTIR A TIMESTAMP
                        // ------------------------------------------------

                        val calendarSeleccionado =
                            Calendar.getInstance()

                        calendarSeleccionado.set(

                            año,
                            mes,
                            dia,
                            0,
                            0,
                            0
                        )

                        fechaInicio =

                            Timestamp(

                                calendarSeleccionado.time
                            )

                        // ------------------------------------------------
                        // RECARGAR RESUMEN
                        // ------------------------------------------------

                        pedidoViewModel.obtenerPedidosCliente(
                            clienteId
                        )
                    },

                    year,
                    month,
                    day
                )

            dialog.show()
        }

        // ------------------------------------------------
        // FECHA FIN
        // ------------------------------------------------

        binding.etFechaFin.setOnClickListener {

            val calendario =
                Calendar.getInstance()

            val year =
                calendario.get(Calendar.YEAR)

            val month =
                calendario.get(Calendar.MONTH)

            val day =
                calendario.get(Calendar.DAY_OF_MONTH)

            val dialog =

                DatePickerDialog(

                    requireContext(),

                    { _, año, mes, dia ->

                        val fechaTexto =

                            "$dia/${mes + 1}/$año"

                        binding.etFechaFin.setText(
                            fechaTexto
                        )

                        // --------------------------------------
                        // ponemos 23:59:59
                        // para incluir TODO el día
                        // ------------------------------------------------

                        val calendarSeleccionado =
                            Calendar.getInstance()

                        calendarSeleccionado.set(

                            año,
                            mes,
                            dia,
                            23,
                            59,
                            59
                        )

                        fechaFin =

                            Timestamp(

                                calendarSeleccionado.time
                            )

                        // ------------------------------------------------
                        // RECARGAR RESUMEN
                        // ------------------------------------------------

                        pedidoViewModel.obtenerPedidosCliente(
                            clienteId
                        )
                    },

                    year,
                    month,
                    day
                )

            dialog.show()
        }

        //Boton imprimir pdf
        binding.btnImprimir.setOnClickListener {
            imprimirPdf()
        }

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
                clienteGlobal = it

                binding.tvNombreComercio.text =

                    it.nombreComercio

                binding.tvDireccion.text =

                    it.direccion

                binding.tvTelefono.text =

                    "📞 ${it.telefono}"

                binding.tvDescuento.text =

                    "${it.descuento.toInt()}% dto."
                pedidoViewModel.obtenerPedidosCliente(
                    clienteId
                )

                binding.dropdownDescuento.setText(

                    "${it.descuento.toInt()}%",

                    false
                )

                // ------------------------------------------------
                // SWITCH APROBADO
                // ------------------------------------------------

                binding.switchAprobado.isChecked =

                    it.aprobado == true

                // ------------------------------------------------
                // RUTA TEMPORAL
                // ------------------------------------------------

                binding.tvRuta.text =
                    "🚚 Ruta 1"

                // ------------------------------------------------
                // CAMBIO APROBADO
                // ------------------------------------------------

                binding.switchAprobado
                    .setOnCheckedChangeListener {

                            _,
                            isChecked ->

                        val usuarioActualizado =

                            it.copy(

                                aprobado = isChecked
                            )

                        usuarioViewModel.actualizarUsuario(

                            usuarioActualizado

                        ) { correcto ->

                            if (correcto) {

                                binding.switchAprobado.isChecked =
                                    isChecked
                            }
                        }
                    }

                // ------------------------------------------------
                // CAMBIO DESCUENTO
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

                        ) { correcto ->

                            if (correcto) {

                                binding.tvDescuento.text =

                                    "${descuentoSeleccionado.toInt()}% dto."
                            }
                        }
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
            // FILTRAR PEDIDOS FINALIZADOS
            // ------------------------------------------------

            val pedidosFinalizados =

                listaPedidos.filter {

                    // ------------------------------------------------
                    // SOLO FINALIZADOS
                    // ------------------------------------------------

                    it.estado == "finalizado"

                            &&

                            // ------------------------------------------------
                            // FECHA INICIO
                            // ------------------------------------------------

                            (fechaInicio == null ||

                                    it.fecha >= fechaInicio!!)

                            &&

                            // ------------------------------------------------
                            // FECHA FIN
                            // ------------------------------------------------

                            (fechaFin == null ||

                                    it.fecha <= fechaFin!!)
                }

            // ------------------------------------------------
            // AGRUPAR TODAS LAS LÍNEAS
            // ------------------------------------------------

            val listaResumen =

                mutableListOf<LineaPedido>()

            pedidosFinalizados.forEach { pedido ->

                listaResumen.addAll(

                    pedido.lineasPedido
                )
            }

            // ------------------------------------------------
            // GUARDAR GLOBAL PDF
            // ------------------------------------------------

            listaResumenGlobal =
                listaResumen

            // ------------------------------------------------
            // RESUMEN SIMPLE PANTALLA
            // ------------------------------------------------

            val totalPedidos =

                listaResumen.sumOf {

                    it.cantidadPedida
                }

            val totalDevueltos =

                listaResumen.sumOf {

                    it.cantidadDevuelta
                }



            binding.tvTotalPedidos.text =

                "Total pedidos: $totalPedidos uds"

            binding.tvTotalDevueltos.text =

                "Total devueltos: $totalDevueltos uds"



            // ------------------------------------------------
            // VARIABLES FACTURA
            // ------------------------------------------------

            var subtotal4 = 0.0
            var subtotal10 = 0.0
            var subtotal21 = 0.0

            var totalFactura = 0.0

            // ------------------------------------------------
            // RECORRER LÍNEAS FACTURA
            // ------------------------------------------------

            listaResumen.forEach { linea ->

                // ------------------------------------------------
                // OBTENER PRODUCTO
                // ------------------------------------------------

                val producto =

                    mapaProductos[
                        linea.productoId
                    ]

                // ------------------------------------------------
                // DATOS PRODUCTO
                // ------------------------------------------------

                val nombre =
                    producto?.nombre ?: ""

                val iva =
                    producto?.iva ?: 0.0

                val cantidad =
                    linea.cantidadFinal

                val precio =
                    linea.precioUnitario

                // ------------------------------------------------
                // SUBTOTAL LÍNEA
                // ------------------------------------------------

                val subtotal =
                    cantidad * precio

                // ------------------------------------------------
                // IVA LÍNEA
                // ------------------------------------------------

                val importeIva =

                    subtotal * (iva / 100)

                // ------------------------------------------------
                // TOTAL LÍNEA
                // ------------------------------------------------

                val totalLinea =

                    subtotal + importeIva

                // ------------------------------------------------
                // ACUMULAR POR IVA
                // ------------------------------------------------

                when (iva.toInt()) {

                    4 -> subtotal4 += subtotal

                    10 -> subtotal10 += subtotal

                    21 -> subtotal21 += subtotal
                }

                // ------------------------------------------------
                // ACUMULAR FACTURA TOTAL
                // ------------------------------------------------

                totalFactura += totalLinea
            }

            // ------------------------------------------------
            // IVA GRUPOS
            // ------------------------------------------------

            val iva4 =
                subtotal4 * 0.04

            val iva10 =
                subtotal10 * 0.10

            val iva21 =
                subtotal21 * 0.21

            // ------------------------------------------------
            // TOTAL CON IVA
            // ------------------------------------------------

            val totalConIva =

                subtotal4 + iva4 +

                        subtotal10 + iva10 +

                        subtotal21 + iva21

            // ------------------------------------------------
            // DESCUENTO CLIENTE
            // ------------------------------------------------

            val descuento =

                clienteGlobal?.descuento ?: 0.0

            // ------------------------------------------------
            // IMPORTE DESCUENTO
            // ------------------------------------------------

            val importeDescuento =

                totalConIva *

                        (descuento / 100)

            // ------------------------------------------------
            // TOTAL FINAL
            // ------------------------------------------------

            val totalFinal =

                totalConIva -

                        importeDescuento
            binding.tvTotalNeto.text =

                "Total neto: $totalFinal €"



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
    // IMPRIMIR PDF
    // ------------------------------------------------

    private fun imprimirPdf() {

        // ------------------------------------------------
        // DOCUMENTO PDF
        // ------------------------------------------------

        val pdfDocument =
            PdfDocument()

        // ------------------------------------------------
        // PÁGINA A4
        // ------------------------------------------------

        val pageInfo =

            PdfDocument.PageInfo.Builder(

                595,
                842,
                1

            ).create()

        // ------------------------------------------------
        // CREAR PÁGINA
        // ------------------------------------------------

        val page =

            pdfDocument.startPage(
                pageInfo
            )

        // ------------------------------------------------
        // CANVAS
        // ------------------------------------------------

        val canvas =
            page.canvas

        // ------------------------------------------------
        // PAINT
        // ------------------------------------------------

        val paint = Paint()

        paint.textSize = 12f

        // ------------------------------------------------
        // FORMATO DECIMALES
        // ------------------------------------------------

        val formato = DecimalFormat(
            "#0.00"
        )

        // ------------------------------------------------
        // POSICIÓN VERTICAL
        // ------------------------------------------------

        var y = 40f

        // ------------------------------------------------
        // NÚMERO FACTURA
        // ------------------------------------------------

        val numeroFactura =

            System.currentTimeMillis()

        // ------------------------------------------------
        // TÍTULO
        // ------------------------------------------------

        paint.textSize = 18f

        canvas.drawText(

            "FACTURA",

            40f,

            y,

            paint
        )

        y += 30f

        paint.textSize = 12f

        canvas.drawText(

            "Factura Nº: $numeroFactura",

            40f,

            y,

            paint
        )

        y += 40f

        // ------------------------------------------------
        // DATOS CLIENTE
        // ------------------------------------------------

        canvas.drawText(

            "Cliente: ${clienteGlobal?.nombre}",

            40f,

            y,

            paint
        )

        y += 25f

        canvas.drawText(

            "Comercio: ${clienteGlobal?.nombreComercio}",

            40f,

            y,

            paint
        )

        y += 25f

        canvas.drawText(

            "CIF: ${clienteGlobal?.cif}",

            40f,

            y,

            paint
        )

        y += 25f

        canvas.drawText(

            "Dirección: ${clienteGlobal?.direccion}",

            40f,

            y,

            paint
        )

        y += 40f

        // ------------------------------------------------
        // CABECERA TABLA
        // ------------------------------------------------

        paint.textSize = 11f

        canvas.drawText(
            "Producto",
            40f,
            y,
            paint
        )

        canvas.drawText(
            "Cant",
            220f,
            y,
            paint
        )

        canvas.drawText(
            "Precio",
            280f,
            y,
            paint
        )

        canvas.drawText(
            "IVA",
            370f,
            y,
            paint
        )

        canvas.drawText(
            "Total",
            470f,
            y,
            paint
        )

        y += 15f

        canvas.drawLine(

            40f,
            y,
            550f,
            y,
            paint
        )

        y += 25f

        // ------------------------------------------------
        // TOTALES IVA
        // ------------------------------------------------

        var subtotal4 = 0.0
        var subtotal10 = 0.0
        var subtotal21 = 0.0

        var totalFactura = 0.0

        // ------------------------------------------------
        // RECORRER LÍNEAS
        // ------------------------------------------------

        listaResumenGlobal.forEach { linea ->

            val producto =

                mapaProductos[
                    linea.productoId
                ]

            val nombre =
                producto?.nombre ?: ""

            val iva =
                producto?.iva ?: 0.0

            val cantidad =
                linea.cantidadFinal

            val precio =
                linea.precioUnitario

            val subtotal =
                cantidad * precio

            val totalLinea =

                subtotal +

                        (subtotal * iva / 100)

            // ------------------------------------------------
            // ACUMULAR POR IVA
            // ------------------------------------------------

            when (iva.toInt()) {

                4 -> subtotal4 += subtotal

                10 -> subtotal10 += subtotal

                21 -> subtotal21 += subtotal
            }

            totalFactura += totalLinea

            // ------------------------------------------------
            // DIBUJAR FILA
            // ------------------------------------------------

            canvas.drawText(

                nombre,

                40f,

                y,

                paint
            )

            canvas.drawText(

                cantidad.toString(),

                220f,

                y,

                paint
            )

            canvas.drawText(

                "${formato.format(precio)}€",

                280f,

                y,

                paint
            )

            canvas.drawText(

                "${iva.toInt()}%",

                370f,

                y,

                paint
            )

            canvas.drawText(

                "${formato.format(totalLinea)}€",

                470f,

                y,

                paint
            )

            y += 25f
        }

        // ------------------------------------------------
        // SEPARADOR
        // ------------------------------------------------

        y += 10f

        canvas.drawLine(

            40f,
            y,
            550f,
            y,
            paint
        )

        y += 30f

        // ------------------------------------------------
        // IVA 4
        // ------------------------------------------------

        val iva4 =
            subtotal4 * 0.04

        canvas.drawText(

            "IVA 4% -> Base: ${formato.format(subtotal4)}€  IVA: ${formato.format(iva4)}€",

            40f,

            y,

            paint
        )

        y += 25f

        // ------------------------------------------------
        // IVA 10
        // ------------------------------------------------

        val iva10 =
            subtotal10 * 0.10

        canvas.drawText(

            "IVA 10% -> Base: ${formato.format(subtotal10)}€  IVA: ${formato.format(iva10)}€",

            40f,

            y,

            paint
        )

        y += 25f

        // ------------------------------------------------
        // IVA 21
        // ------------------------------------------------

        val iva21 =
            subtotal21 * 0.21

        canvas.drawText(

            "IVA 21% -> Base: ${formato.format(subtotal21)}€  IVA: ${formato.format(iva21)}€",

            40f,

            y,

            paint
        )

        y += 35f

        // ------------------------------------------------
        // DESCUENTO CLIENTE
        // ------------------------------------------------

        val descuento =

            clienteGlobal?.descuento ?: 0.0

        val importeDescuento =

            totalFactura *

                    (descuento / 100)

        val totalFinal =

            totalFactura -

                    importeDescuento

        // ------------------------------------------------
        // TOTAL FACTURA
        // ------------------------------------------------

        paint.textSize = 16f

        canvas.drawText(

            "TOTAL: ${formato.format(totalFinal)}€",

            40f,

            y,

            paint
        )

        y += 30f

        paint.textSize = 12f

        canvas.drawText(

            "Descuento aplicado: ${descuento.toInt()}%",

            40f,

            y,

            paint
        )

        // ------------------------------------------------
        // TERMINAR PÁGINA
        // ------------------------------------------------

        pdfDocument.finishPage(page)

        // ------------------------------------------------
        // GUARDAR ARCHIVO
        // ------------------------------------------------

        val carpeta =

            Environment.getExternalStoragePublicDirectory(

                Environment.DIRECTORY_DOWNLOADS
            )

        val archivo = File(

            carpeta,

            "factura_$numeroFactura.pdf"
        )

        pdfDocument.writeTo(

            FileOutputStream(
                archivo
            )
        )

        // ------------------------------------------------
        // CERRAR PDF
        // ------------------------------------------------

        pdfDocument.close()
        val uri = FileProvider.getUriForFile(

            requireContext(),

            "${requireContext().packageName}.provider",

            archivo
        )

        val intent = Intent(

            Intent.ACTION_SEND
        ).apply {

            type = "application/pdf"

            putExtra(
                Intent.EXTRA_STREAM,
                uri
            )

            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        startActivity(

            Intent.createChooser(

                intent,

                "Enviar factura"
            )
        )

        Toast.makeText(

            requireContext(),

            "Factura guardada y lista para compartir",

            Toast.LENGTH_LONG

        ).show()
    }

    // ------------------------------------------------
    // ON DESTROY VIEW
    // ------------------------------------------------

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}