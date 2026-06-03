package com.example.apppanadero.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apppanadero.data.model.LineaPedido
import com.example.apppanadero.data.model.Producto
import com.example.apppanadero.databinding.ItemDetalleProductoBinding
import android.util.Log
// ------------------------------------------------
// ADAPTER DETALLE FACTURACIÓN CLIENTE
// ------------------------------------------------

// Este adapter muestra:
//
// - productos agrupados
// - cantidad pedida
// - cantidad devuelta
// - total neto
// - precio unitario
// - total producto
//
// Más adelante permitirá:
//
// - exportar PDF
// - estadísticas
// - IVA real
// - descuentos
class DetalleFacturacionAdapter(

    // Lista líneas agrupadas
    private val listaLineas:
    List<LineaPedido>,

    // Mapa productos
    //
    // key   -> productoId
    // value -> Producto
    private val mapaProductos:
    Map<String, Producto>

) : RecyclerView.Adapter<
        DetalleFacturacionAdapter.DetalleViewHolder>() {

    // ------------------------------------------------
    // VIEW HOLDER
    // ------------------------------------------------

    class DetalleViewHolder(

        val binding:
        ItemDetalleProductoBinding

    ) : RecyclerView.ViewHolder(
        binding.root
    )

    // ------------------------------------------------
    // CREAR ITEM
    // ------------------------------------------------

    override fun onCreateViewHolder(

        parent: ViewGroup,
        viewType: Int

    ): DetalleViewHolder {

        val binding =

            ItemDetalleProductoBinding.inflate(

                LayoutInflater.from(
                    parent.context
                ),

                parent,

                false
            )

        return DetalleViewHolder(
            binding
        )
    }

    // ------------------------------------------------
    // BIND DATOS
    // ------------------------------------------------

    override fun onBindViewHolder(

        holder: DetalleViewHolder,
        position: Int

    ) {

        // Línea actual
        val lineaPedido =
            listaLineas[position]

        // Producto relacionado
        val producto =

            mapaProductos[
                lineaPedido.productoId
            ]
        Log.d(
            "FACTURA",
            "productoId linea = ${lineaPedido.productoId}"
        )

        Log.d(
            "FACTURA",
            "producto encontrado = ${producto?.nombre}"
        )

        // ------------------------------------------------
        // NOMBRE PRODUCTO
        // ------------------------------------------------

        holder.binding.tvNombreProducto.text =

            producto?.nombre
                ?: "Producto"

        // ------------------------------------------------
        // CANTIDADES
        // ------------------------------------------------

        if (lineaPedido.cantidadDevuelta != 0) {

            // como en el else quitamos visibilidad
            // y el item se recicla
            // hay que volverle a dar visibilidad

            holder.binding.tvCantidad.visibility =
                View.VISIBLE

            holder.binding.tvCantidad.text =

                "Pedidas: ${lineaPedido.cantidadPedida} uds / Devueltas: ${lineaPedido.cantidadDevuelta} uds"

        } else {

            holder.binding.tvCantidad.visibility =
                View.GONE
        }

        // ------------------------------------------------
        // DETALLE
        // ------------------------------------------------

        holder.binding.tvDetalle.text =

            "${lineaPedido.cantidadPedida} uds · " +
                    "%.2f€/u".format(
                        lineaPedido.precioUnitario
                    )

        // ------------------------------------------------
        // TOTAL
        // ------------------------------------------------

        val totalLinea =

            lineaPedido.cantidadPedida *
                    lineaPedido.precioUnitario

        holder.binding.tvTotal.text =

            "€%.2f".format(
                totalLinea
            )
    }

    // ------------------------------------------------
    // TOTAL ITEMS
    // ------------------------------------------------

    override fun getItemCount(): Int {

        return listaLineas.size
    }
}