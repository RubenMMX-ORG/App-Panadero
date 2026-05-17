package com.example.apppanadero.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apppanadero.data.model.Pedido
import com.example.apppanadero.databinding.ItemPedidoAdminBinding

// ------------------------------------------------
// ADAPTER PEDIDOS ADMIN
// ------------------------------------------------

// Este adapter muestra TODOS los pedidos
// de TODOS los clientes.
//
// Solo se mostrarán pedidos:
// estado = pendiente
//
// Más adelante podremos:
// - cambiar estado
// - entrar al detalle
// - gestionar pedidos
class AdminPedidoAdapter(

    // Lista pedidos
    private val listaPedidos: List<Pedido>,

    // Callback click detalle
    private val onClickDetalle:
        (Pedido) -> Unit

) : RecyclerView.Adapter<
        AdminPedidoAdapter.AdminPedidoViewHolder>() {

    // ------------------------------------------------
    // VIEW HOLDER
    // ------------------------------------------------

    class AdminPedidoViewHolder(

        val binding: ItemPedidoAdminBinding

    ) : RecyclerView.ViewHolder(binding.root)

    // ------------------------------------------------
    // CREAR ITEM
    // ------------------------------------------------

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdminPedidoViewHolder {

        val binding =
            ItemPedidoAdminBinding.inflate(

                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return AdminPedidoViewHolder(binding)
    }

    // ------------------------------------------------
    // BIND DATOS
    // ------------------------------------------------

    override fun onBindViewHolder(
        holder: AdminPedidoViewHolder,
        position: Int
    ) {

        // Pedido actual
        val pedido =
            listaPedidos[position]

        // ------------------------------------------------
        // NOMBRE CLIENTE
        // ------------------------------------------------

        // De momento mostramos clienteId.
        //
        // Más adelante podremos
        // cargar nombre real del cliente.
        holder.binding.tvNombreCliente.text =
            pedido.clienteId

        // ------------------------------------------------
        // NUMERO PEDIDO
        // ------------------------------------------------

        holder.binding.tvNumeroPedido.text =
            "Pedido #${pedido.numeroPedido}"

        // ------------------------------------------------
        // ESTADO
        // ------------------------------------------------

        holder.binding.chipEstado.text =
            pedido.estado

        // ------------------------------------------------
        // FECHA
        // ------------------------------------------------

        // Temporal hasta implementar fechas reales
        holder.binding.tvFecha.text =
            "📅 Próximamente"

        holder.binding.tvHora.text =
            "⏰ --:--"

        // ------------------------------------------------
        // CANTIDAD PRODUCTOS
        // ------------------------------------------------

        val totalProductos =
            pedido.lineasPedido.sumOf {

                it.cantidadPedida
            }

        holder.binding.tvCantidad.text =
            "$totalProductos productos"

        // ------------------------------------------------
        // PRECIO TOTAL
        // ------------------------------------------------

        holder.binding.tvPrecio.text =
            "€%.2f".format(
                pedido.precioTotal
            )

        // ------------------------------------------------
        // BOTÓN DETALLE
        // ------------------------------------------------

        holder.binding.btnDetalle
            .setOnClickListener {

                onClickDetalle(pedido)
            }
    }

    // ------------------------------------------------
    // TOTAL ITEMS
    // ------------------------------------------------

    override fun getItemCount(): Int {

        return listaPedidos.size
    }
}