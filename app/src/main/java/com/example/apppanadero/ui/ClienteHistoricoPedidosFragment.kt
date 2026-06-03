package com.example.apppanadero.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apppanadero.data.di.Injector
import com.google.firebase.auth.FirebaseAuth
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.apppanadero.databinding.FragmentClienteHistoricoPedidosBinding
import com.example.apppanadero.ui.adapters.PedidoAdapter
import com.example.apppanadero.viewmodel.PedidoViewModel
import android.app.DatePickerDialog
import android.content.Intent
import com.google.firebase.Timestamp
import java.util.Calendar
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.apppanadero.data.model.Pedido
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// Fragment encargado de mostrar los pedidos del cliente actual
class ClienteHistoricoPedidosFragment : Fragment() {

    // ------------------------------------------------
    // BINDING
    // ------------------------------------------------
    private var _binding: FragmentClienteHistoricoPedidosBinding? = null
    private val binding get() = _binding!!

    // ------------------------------------------------
    // VIEWMODEL
    // ------------------------------------------------
    private val viewModel: PedidoViewModel by viewModels {

        Injector.providePedidoViewModelFactory()
    }

    //LISTA DE PEDIDOS DE CLIENTE

    private var listaPedidosGlobal = emptyList<Pedido>()

    //VARIABLES FECHAS
    private var fechaInicio: Timestamp? = null

    private var fechaFin: Timestamp? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Cambiar el título de la ActionBar
        activity?.let {
            (it as? AppCompatActivity)?.supportActionBar?.title = "Pedidos"
        }

        _binding = FragmentClienteHistoricoPedidosBinding.inflate(
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

        super.onViewCreated(view, savedInstanceState)

        //LLAMADAS A LOS METODOS UTILIZADOS
        configurarRecyclerView()

        configurarFechas()

        observarPedidos()

        cargarPedidosCliente()
        binding.cardHistorico.setOnClickListener {

            generarPdfHistorico()
        }
    }

    // ------------------------------------------------
    // CONFIGURAR RECYCLERVIEW
    // ------------------------------------------------

    private fun configurarRecyclerView() {

        binding.recyclerHistorico.layoutManager =
            LinearLayoutManager(requireContext())
    }

    // ------------------------------------------------
    // CONFIGURAR FECHAS
    // ------------------------------------------------
    private fun configurarFechas() {

        binding.etFechaInicio.setOnClickListener {

            val calendario = Calendar.getInstance()

            DatePickerDialog(

                requireContext(),

                { _, año, mes, dia ->

                    binding.etFechaInicio.setText(
                        "$dia/${mes + 1}/$año"
                    )

                    val cal = Calendar.getInstance()

                    cal.set(
                        año,
                        mes,
                        dia,
                        0,
                        0,
                        0
                    )

                    fechaInicio =
                        Timestamp(cal.time)

                    aplicarFiltro()
                },

                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)

            ).show()
        }

        binding.etFechaFin.setOnClickListener {

            val calendario = Calendar.getInstance()

            DatePickerDialog(

                requireContext(),

                { _, año, mes, dia ->

                    binding.etFechaFin.setText(
                        "$dia/${mes + 1}/$año"
                    )

                    val cal = Calendar.getInstance()

                    cal.set(
                        año,
                        mes,
                        dia,
                        23,
                        59,
                        59
                    )

                    fechaFin =
                        Timestamp(cal.time)

                    aplicarFiltro()
                },

                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)

            ).show()
        }
    }

    // ------------------------------------------------
    // CARGAR PEDIDOS CLIENTE
    // ------------------------------------------------

    private fun cargarPedidosCliente() {

        // Obtenemos usuario autenticado actual
        val uid = FirebaseAuth
            .getInstance()
            .currentUser
            ?.uid

        // Si existe usuario
        if (uid != null) {

            // Pedimos pedidos al ViewModel
            viewModel.obtenerPedidosCliente(uid)

        } else {

            Toast.makeText(
                requireContext(),
                "Usuario no autenticado",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ------------------------------------------------
    // OBSERVAR PEDIDOS
    // ------------------------------------------------


    private fun observarPedidos() {

        // Observamos lista pedidos
        viewModel.listaPedidos.observe(viewLifecycleOwner) { listaPedidos ->

            listaPedidosGlobal = listaPedidos

            aplicarFiltro()

        }
    }

    private fun aplicarFiltro() {

        val pedidosFiltrados =

            listaPedidosGlobal.filter {

                (fechaInicio == null ||
                        it.fecha >= fechaInicio!!)

                        &&

                        (fechaFin == null ||
                                it.fecha <= fechaFin!!)
            }

        val pedidoAdapter =

            PedidoAdapter(
                pedidosFiltrados
            ) { pedido ->

                val action =

                    ClienteHistoricoPedidosFragmentDirections
                        .actionClienteHistoricoPedidosFragmentToClienteDetallePedidoFragment(
                            pedido.id
                        )

                findNavController().navigate(
                    action
                )
            }

        binding.recyclerHistorico.adapter =
            pedidoAdapter
    }

    // ------------------------------------------------
    // METODO QUE DIBUJA EL PDF
    // ------------------------------------------------
    //No es compose ni nada de eso es PdfDocument de Android
    private fun generarPdfHistorico() {

        val pdfDocument = PdfDocument()

        val pageInfo =

            PdfDocument.PageInfo.Builder(

                595,
                842,
                1

            ).create()

        val page =

            pdfDocument.startPage(
                pageInfo
            )

        val canvas =
            page.canvas

        val paint =
            Paint()

        paint.textSize = 12f

        var y = 40f

        val formatoFecha =

            SimpleDateFormat(

                "dd/MM/yyyy",

                Locale.getDefault()
            )

        // --------------------------------------
        // TITULO
        // --------------------------------------

        paint.textSize = 18f

        canvas.drawText(

            "HISTORICO DE PEDIDOS",

            40f,

            y,

            paint
        )

        y += 40f

        paint.textSize = 12f

        canvas.drawText(

            "Generado: ${
                formatoFecha.format(
                    Date()
                )
            }",

            40f,

            y,

            paint
        )

        y += 40f

        // --------------------------------------
        // CABECERA TABLA
        // --------------------------------------

        canvas.drawText(
            "Pedido",
            40f,
            y,
            paint
        )

        canvas.drawText(
            "Fecha",
            180f,
            y,
            paint
        )

        canvas.drawText(
            "Estado",
            350f,
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

        // --------------------------------------
        // FILTRAR FECHAS
        // --------------------------------------

        val pedidosFiltrados =

            listaPedidosGlobal.filter {

                (fechaInicio == null ||

                        it.fecha >= fechaInicio!!)

                        &&

                        (fechaFin == null ||

                                it.fecha <= fechaFin!!)
            }

        // --------------------------------------
        // PEDIDOS
        // --------------------------------------

        pedidosFiltrados.forEach { pedido ->

            val fechaTexto =

                formatoFecha.format(
                    pedido.fecha.toDate()
                )

            canvas.drawText(

                pedido.numeroPedido.toString(),

                40f,

                y,

                paint
            )

            canvas.drawText(

                fechaTexto,

                180f,

                y,

                paint
            )

            canvas.drawText(

                pedido.estado,

                350f,

                y,

                paint
            )

            y += 25f
        }

        // --------------------------------------
        // RESUMEN
        // --------------------------------------

        y += 20f

        canvas.drawLine(
            40f,
            y,
            550f,
            y,
            paint
        )

        y += 30f

        canvas.drawText(

            "Total pedidos: ${pedidosFiltrados.size}",

            40f,

            y,

            paint
        )

        pdfDocument.finishPage(page)

        // --------------------------------------
        // GUARDAR
        // --------------------------------------

        val carpeta =

            Environment
                .getExternalStoragePublicDirectory(

                    Environment.DIRECTORY_DOWNLOADS
                )

        val archivo = File(

            carpeta,

            "historico_pedidos.pdf"
        )

        pdfDocument.writeTo(

            FileOutputStream(
                archivo
            )
        )

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

                "Enviar Historico"
            )
        )

        Toast.makeText(

            requireContext(),

            "Histórico descargado y listo para compartir",

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