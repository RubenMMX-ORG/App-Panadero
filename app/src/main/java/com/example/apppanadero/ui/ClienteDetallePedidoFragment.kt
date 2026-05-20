package com.example.apppanadero.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apppanadero.R
import com.example.apppanadero.data.model.Pedido
import com.example.apppanadero.ui.adapters.LineaPedidoAdapter
import com.example.apppanadero.viewmodel.PedidoViewModel
import com.example.apppanadero.data.di.Injector
import com.example.apppanadero.databinding.FragmentClienteDetallePedidoBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ClienteDetallePedidoFragment : Fragment() {

    private var _binding: FragmentClienteDetallePedidoBinding? = null
    private val binding get() = _binding!!

    //Variable para recuperar el pedidoId recibido
    private var pedidoId: String? = null

    // ViewModel con Injector
    private val pedidoViewModel: PedidoViewModel by viewModels {
        Injector.providePedidoViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClienteDetallePedidoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)
        // Cambiar el título de la ActionBar
        activity?.let {
            (it as? AppCompatActivity)?.supportActionBar?.title = "Detalle Pedido"
        }
       

        // Recuperamos el pedidoId de los argumentos
        val args = ClienteDetallePedidoFragmentArgs.fromBundle(requireArguments())
        pedidoId = args.pedidoId


        //  Observa el LiveData
        pedidoViewModel.pedidoDetalle.observe(viewLifecycleOwner) { pedido ->
            if (pedido != null) {
                mostrarDatosPedido(pedido)
            } else {
                Toast.makeText(requireContext(), "No se encontró el pedido", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        //  Pide al ViewModel que cargue el pedido, si no es nulo pasa el pedidoid como parametro dela funcion obtenerpedidoPOrId,
        //  que en la clase viewModel es  quien guarda ese pedido en el liveData 
    
        pedidoId?.let {

            pedidoViewModel.obtenerPedidoPorId(it)
        }

        configurarBotones()
    }

    private fun mostrarDatosPedido(pedido: Pedido) {
        // Número pedido
        binding.tvNumeroPedido.text = "Pedido # ${pedido.numeroPedido}"

        // Fecha
        val formato =
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            )

        val fechaFormateada =
            formato.format(
                pedido.fecha.toDate()
            )

        binding.tvFecha.text =
            fechaFormateada

        // Estado
        binding.chipEstado.text = pedido.estado

        // Cantidad total de productos
        val totalProductos = pedido.lineasPedido.sumOf { it.cantidadPedida }
        binding.tvNumProductos.text = "$totalProductos productos"

        // Precio total
        binding.tvTotal.text = "%.2f €".format(pedido.precioTotal)

        // ------------------------------------------------
        // ASIGNACION DE COLORES POR ESTADO
        // ------------------------------------------------

        if (pedido.estado == "pendiente") {

            binding.chipEstado.chipBackgroundColor  = ColorStateList.valueOf(

                ContextCompat.getColor(requireContext(), R.color.estado_pendiente_bg)
            )

        } else if (pedido.estado == "preparado") {

            binding.chipEstado.chipBackgroundColor  = ColorStateList.valueOf(

                ContextCompat.getColor(requireContext(), R.color.estado_camino_bg)
            )

        } else if (pedido.estado == "entregado") {

            binding.chipEstado.chipBackgroundColor  = ColorStateList.valueOf(

                ContextCompat.getColor(requireContext(), R.color.estado_entregado_bg)
            )

        } else if (pedido.estado == "finalizado") {

            binding.chipEstado.chipBackgroundColor  = ColorStateList.valueOf(

                ContextCompat.getColor(requireContext(), R.color.estado_finalizado_bg)
            )
        }

        if (pedido.estado == "pendiente") {

            binding.chipEstado.setTextColor(

                ContextCompat.getColor(requireContext(), R.color.estado_pendiente_text)
            )

        } else if (pedido.estado == "preparado") {

            binding.chipEstado.setTextColor(

                ContextCompat.getColor(requireContext(), R.color.estado_camino_text)
            )

        } else if (pedido.estado == "entregado") {

            binding.chipEstado.setTextColor(

                ContextCompat.getColor(requireContext(), R.color.estado_entregado_text)
            )

        } else if (pedido.estado == "finalizado") {

            binding.chipEstado.setTextColor(

                ContextCompat.getColor(requireContext(), R.color.estado_finalizado_text)
            )
        }

        // RecyclerView con las líneas de pedido
        val adapter = LineaPedidoAdapter(pedido.lineasPedido, pedido.estado)
        binding.recyclerDetalle.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerDetalle.adapter = adapter
    }

    private fun configurarBotones() {
        binding.btnCancelar.setOnClickListener {
            // Observamos el pedido actual
            val pedido = pedidoViewModel.pedidoDetalle.value

            if (pedido != null) {
                // Solo cancelar si está pendiente 
                if (pedido.estado == "pendiente") {
                    pedidoViewModel.eliminarPedido(pedido.id)

                    Toast.makeText(requireContext(), "Pedido cancelado", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "No se puede cancelar un pedido finalizado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(requireContext(), "Pedido no cargado aún", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnModificar.setOnClickListener {

            val action =
                ClienteDetallePedidoFragmentDirections
                    .actionClienteDetallePedidoFragmentToClienteNuevoPedidoFragment(
                        pedidoId = pedidoId//asignamos al action el pedidoId recibido y lo pasamos al siguiente fragment
                    )

            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
