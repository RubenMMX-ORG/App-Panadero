package com.example.apppanadero.ui.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.apppanadero.R
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
    // ------------------------------------------------
    // MAPA CLIENTES
    // ------------------------------------------------

    private val mapaClientes:
    Map<String, String>,

    // Callback click detalle
    private val onClickDetalle:
        (Pedido) -> Unit,

    // Callback click iniciar ruta
    private val onClickIniciarRuta:
        (Pedido) -> Unit,

    private val mostrarBotonRuta:
    Boolean,

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
        // NOMBRE COMERCIO
        // ------------------------------------------------

        holder.binding.tvNombreComercio.text =

            mapaClientes[
                pedido.clienteId
            ] ?: "Cliente"

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

        // ------------------------------------------------
        // BOTÓN INICIAR NAVEGACION, SOLO ROL REPARTIDOR
        // ------------------------------------------------
        if (mostrarBotonRuta) {

            holder.binding.btnIniciarRuta.visibility =
                View.VISIBLE

        } else {

            holder.binding.btnIniciarRuta.visibility =
                View.GONE
        }

        holder.binding.btnIniciarRuta
            .setOnClickListener {


                onClickIniciarRuta(pedido)
            }

        // ------------------------------------------------
        // ASIGNACION DE COLORES POR ESTADO
        // ------------------------------------------------

        if (pedido.estado == "pendiente") {

            holder.binding.chipEstado.chipBackgroundColor  = ColorStateList.valueOf(

                ContextCompat.getColor(holder.itemView.context, R.color.estado_pendiente_bg)
            )

        } else if (pedido.estado == "preparado") {

            holder.binding.chipEstado.chipBackgroundColor  = ColorStateList.valueOf(

                ContextCompat.getColor(holder.itemView.context, R.color.estado_camino_bg)
            )

        } else if (pedido.estado == "entregado") {

            holder.binding.chipEstado.chipBackgroundColor  = ColorStateList.valueOf(

                ContextCompat.getColor(holder.itemView.context, R.color.estado_entregado_bg)
            )

        } else if (pedido.estado == "finalizado") {

            holder.binding.chipEstado.chipBackgroundColor  = ColorStateList.valueOf(

                ContextCompat.getColor(holder.itemView.context, R.color.estado_finalizado_bg)
            )
        }

        if (pedido.estado == "pendiente") {

            holder.binding.chipEstado.setTextColor(

                ContextCompat.getColor(holder.itemView.context, R.color.estado_pendiente_text)
            )

        } else if (pedido.estado == "preparado") {

            holder.binding.chipEstado.setTextColor(

                ContextCompat.getColor(holder.itemView.context, R.color.estado_camino_text)
            )

        } else if (pedido.estado == "entregado") {

            holder.binding.chipEstado.setTextColor(

                ContextCompat.getColor(holder.itemView.context, R.color.estado_entregado_text)
            )

        } else if (pedido.estado == "finalizado") {

            holder.binding.chipEstado.setTextColor(

                ContextCompat.getColor(holder.itemView.context, R.color.estado_finalizado_text)
            )
        }
    }

    // ------------------------------------------------
    // TOTAL ITEMS
    // ------------------------------------------------

    override fun getItemCount(): Int {

        return listaPedidos.size
    }
}