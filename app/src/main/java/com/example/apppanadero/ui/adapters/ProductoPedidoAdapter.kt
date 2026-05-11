package com.example.apppanadero.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apppanadero.data.model.Producto
import com.example.apppanadero.databinding.ItemProductoBinding


// Adapter encargado de mostrar productos
// para crear un nuevo pedido
class ProductoPedidoAdapter(

    // Lista productos
    private val listaProductos: List<Producto>,

    // Callback cuando cambia cantidad
    private val onCantidadCambiada:
        (Producto, Int) -> Unit

) : RecyclerView.Adapter<
        ProductoPedidoAdapter.ProductoViewHolder>() {

    // ------------------------------------------------
    // MAPA CANTIDADES
    // ------------------------------------------------

    // Guarda cantidades seleccionadas
    // key = productoId
    // value = cantidad
    private val cantidadesSeleccionadas =
        mutableMapOf<String, Int>()

    // ------------------------------------------------
    // VIEW HOLDER
    // ------------------------------------------------

    class ProductoViewHolder(

        val binding: ItemProductoBinding

    ) : RecyclerView.ViewHolder(binding.root)

    // ------------------------------------------------
    // CREAR ITEM
    // ------------------------------------------------

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductoViewHolder {

        val binding =
            ItemProductoBinding.inflate(

                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return ProductoViewHolder(binding)
    }

    // ------------------------------------------------
    // UNIR DATOS
    // ------------------------------------------------

    override fun onBindViewHolder(
        holder: ProductoViewHolder,
        position: Int
    ) {

        // Producto actual
        val producto =
            listaProductos[position]

        // Recuperamos cantidad actual
        var cantidad =
            cantidadesSeleccionadas[producto.id] ?: 0

        // Nombre producto
        holder.binding.tvNombreProducto.text =
            producto.nombre

        // Precio temporal
        holder.binding.tvPrecioProducto.text =
            "€1.20"

        // Cantidad
        holder.binding.tvCantidad.text =
            cantidad.toString()

        // ------------------------------------------------
        // BOTÓN SUMAR
        // ------------------------------------------------

        holder.binding.btnSumar.setOnClickListener {

            cantidad++

            cantidadesSeleccionadas[producto.id] =
                cantidad

            holder.binding.tvCantidad.text =
                cantidad.toString()

            onCantidadCambiada(
                producto,
                cantidad
            )
        }

        // ------------------------------------------------
        // BOTÓN RESTAR
        // ------------------------------------------------

        holder.binding.btnRestar.setOnClickListener {

            if (cantidad > 0) {

                cantidad--

                cantidadesSeleccionadas[producto.id] =
                    cantidad

                holder.binding.tvCantidad.text =
                    cantidad.toString()

                onCantidadCambiada(
                    producto,
                    cantidad
                )
            }
        }
    }

    // ------------------------------------------------
    // TAMAÑO LISTA
    // ------------------------------------------------

    override fun getItemCount(): Int {

        return listaProductos.size
    }
}