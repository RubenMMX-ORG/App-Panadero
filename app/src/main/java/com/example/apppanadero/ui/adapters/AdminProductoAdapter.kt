package com.example.apppanadero.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apppanadero.data.model.Producto
import com.example.apppanadero.databinding.ItemProductoAdminBinding

// ------------------------------------------------
// ADAPTER PRODUCTOS ADMIN
// ------------------------------------------------

// Adapter encargado de mostrar
// productos para gestión admin.
//
// Permitirá:
// - editar
// - eliminar
// - buscar
class AdminProductoAdapter(

    // Lista productos
    private val listaProductos:
    List<Producto>,

    // Callback editar
    private val onEditarClick:
        (Producto) -> Unit,

    // Callback eliminar
    private val onEliminarClick:
        (Producto) -> Unit

) : RecyclerView.Adapter<
        AdminProductoAdapter.ProductoViewHolder>() {

    // ------------------------------------------------
    // VIEW HOLDER
    // ------------------------------------------------

    class ProductoViewHolder(

        val binding:
        ItemProductoAdminBinding

    ) : RecyclerView.ViewHolder(
        binding.root
    )

    // ------------------------------------------------
    // CREAR ITEM
    // ------------------------------------------------

    override fun onCreateViewHolder(

        parent: ViewGroup,
        viewType: Int

    ): ProductoViewHolder {

        val binding =

            ItemProductoAdminBinding.inflate(

                LayoutInflater.from(
                    parent.context
                ),

                parent,

                false
            )

        return ProductoViewHolder(
            binding
        )
    }

    // ------------------------------------------------
    // BIND DATOS
    // ------------------------------------------------

    override fun onBindViewHolder(

        holder: ProductoViewHolder,
        position: Int

    ) {

        // Producto actual
        val producto =
            listaProductos[position]

        // ------------------------------------------------
        // NOMBRE
        // ------------------------------------------------

        holder.binding.tvNombreProducto.text =
            producto.nombre

        // ------------------------------------------------
        // CATEGORÍA
        // ------------------------------------------------

        holder.binding.chipCategoria.text =
            producto.categoria

        // ------------------------------------------------
        // PRECIO
        // ------------------------------------------------

        // Temporal hasta cargar
        // entidad Precio real
        holder.binding.tvPrecio.text =
            "Consultar precio"

        // ------------------------------------------------
        // BOTÓN EDITAR
        // ------------------------------------------------

        holder.binding.btnEditar
            .setOnClickListener {

                onEditarClick(producto)
            }

        // ------------------------------------------------
        // BOTÓN ELIMINAR
        // ------------------------------------------------

        holder.binding.btnEliminar
            .setOnClickListener {

                onEliminarClick(producto)
            }
    }

    // ------------------------------------------------
    // TOTAL ITEMS
    // ------------------------------------------------

    override fun getItemCount(): Int {

        return listaProductos.size
    }
}