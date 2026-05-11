package com.example.apppanadero.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apppanadero.data.model.Pedido
import com.example.apppanadero.databinding.ItemPedidoBinding
import java.text.SimpleDateFormat
import java.util.Locale

// Adapter encargado de pintar pedidos
class PedidoAdapter(

    // Lista pedidos
    private val listaPedidos: List<Pedido>,

    // Click item
    private val onClick: (Pedido) -> Unit

) : RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    // ------------------------------------------------
    // VIEW HOLDER
    // ------------------------------------------------

    class PedidoViewHolder(

        val binding: ItemPedidoBinding

    ) : RecyclerView.ViewHolder(binding.root)

    // ------------------------------------------------
    // CREAR ITEM
    // ------------------------------------------------

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PedidoViewHolder {

        val binding = ItemPedidoBinding.inflate(

            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PedidoViewHolder(binding)
    }

    // ------------------------------------------------
    // UNIR DATOS CON VISTA
    // ------------------------------------------------

    override fun onBindViewHolder(
        holder: PedidoViewHolder,
        position: Int
    ) {

        val pedido = listaPedidos[position]

        // Número pedido
        holder.binding.tvNumeroPedido.text = "Pedido #${pedido.numeroPedido}"

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

        holder.binding.tvFecha.text =
            fechaFormateada

        // Estado
        holder.binding.chipEstado.text =
            pedido.estado

        // Total productos
        val totalProductos = pedido.lineasPedido.sumOf {

            it.cantidadPedida
        }

        holder.binding.tvCantidad.text =
            "$totalProductos productos"

        // Precio
        holder.binding.tvPrecio.text =
            "€${pedido.precioTotal}"

        // Click item
        holder.itemView.setOnClickListener {

            onClick(pedido)
        }
    }

    // ------------------------------------------------
    // TAMAÑO LISTA
    // ------------------------------------------------

    override fun getItemCount(): Int {

        return listaPedidos.size
    }
}