package com.example.apppanadero.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.apppanadero.R
import com.example.apppanadero.data.model.LineaPedido
import com.example.apppanadero.databinding.ItemDetalleProductoBinding


// Adapter encargado de pintar líneas de pedido
class LineaPedidoAdapter(

    // Lista líneas pedido
    private val listaLineasPedido: List<LineaPedido>,


    private val estadoPedido: String

) : RecyclerView.Adapter<LineaPedidoAdapter.LineaPedidoViewHolder>() {

    // ------------------------------------------------
    // VIEW HOLDER
    // ------------------------------------------------

    class LineaPedidoViewHolder(

        val binding: ItemDetalleProductoBinding

    ) : RecyclerView.ViewHolder(binding.root)

    // ------------------------------------------------
    // CREAR ITEM
    // ------------------------------------------------

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LineaPedidoViewHolder {

        val binding = ItemDetalleProductoBinding.inflate(

            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return LineaPedidoViewHolder(binding)
    }

    // ------------------------------------------------
    // UNIR DATOS CON VISTA
    // ------------------------------------------------

    override fun onBindViewHolder(
        holder: LineaPedidoViewHolder,
        position: Int
    ) {


        // Línea actual
        val lineaPedido = listaLineasPedido[position]
        // Cantidad de productos a mostrar segun estado

        val cantidadMostrar =

            if (estadoPedido == "finalizado") {

                lineaPedido.cantidadFinal

            } else {

                lineaPedido.cantidadPedida
            }
        //Condicion para que solo pinte cantidad devuelta si es distina de cero
        if (lineaPedido.cantidadDevuelta != 0) {
            //como en el else quitamos visibilidad y el item se recicla hay que volverle a dar visibilidad al dato
            holder.binding.tvCantidad.visibility = View.VISIBLE

            holder.binding.tvCantidad.text =
                "Pedidas: ${lineaPedido.cantidadPedida} uds / Devueltas: ${lineaPedido.cantidadDevuelta} uds"

        } else {
            holder.binding.tvCantidad.visibility = View.GONE
        }


        // Nombre producto
        holder.binding.tvNombreProducto.text = lineaPedido.nombreProducto

        // Texto detalle
        holder.binding.tvDetalle.text = "$cantidadMostrar uds -> ${lineaPedido.precioUnitario} €/u"

        // Subtotal
        val subtotal = cantidadMostrar * lineaPedido.precioUnitario
        holder.binding.tvTotal.text = "%.2f €".format(subtotal)

        holder.itemView.setOnClickListener {

            holder.itemView.setBackgroundColor(

                ContextCompat.getColor(

                    holder.itemView.context,

                    R.color.background_click_item
                )
            )

        }


    }

    // ------------------------------------------------
    // TAMAÑO LISTA
    // ------------------------------------------------

    override fun getItemCount(): Int {

        return listaLineasPedido.size
    }
}
