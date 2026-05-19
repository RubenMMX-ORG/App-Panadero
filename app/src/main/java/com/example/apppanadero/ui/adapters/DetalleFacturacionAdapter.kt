package com.example.apppanadero.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apppanadero.data.model.LineaPedido
import com.example.apppanadero.data.model.Producto
import com.example.apppanadero.databinding.ItemDetalleProductoBinding

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
        val linea =
            listaLineas[position]

        // Producto relacionado
        val producto =

            mapaProductos[
                linea.productoId
            ]

        // ------------------------------------------------
        // NOMBRE PRODUCTO
        // ------------------------------------------------

        holder.binding.tvNombreProducto.text =

            producto?.nombre
                ?: "Producto"

        // ------------------------------------------------
        // CANTIDADES
        // ------------------------------------------------

        holder.binding.tvCantidad.text =

            "pedidas: ${linea.cantidadPedida} uds · " +
                    "devueltas: ${linea.cantidadDevuelta} uds"

        // ------------------------------------------------
        // DETALLE
        // ------------------------------------------------

        holder.binding.tvDetalle.text =

            "${linea.cantidadPedida} uds · " +
                    "%.2f€/u".format(
                        linea.precioUnitario
                    )

        // ------------------------------------------------
        // TOTAL
        // ------------------------------------------------

        val totalLinea =

            linea.cantidadPedida *
                    linea.precioUnitario

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