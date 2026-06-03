package com.example.apppanadero.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apppanadero.data.model.Usuario
import com.example.apppanadero.databinding.ItemAdminClientesBinding

// ------------------------------------------------
// ADAPTER CLIENTES ADMIN
// ------------------------------------------------

// Este adapter muestra:
//
// - todos los clientes
// - nombre comercio
// - dirección
// - teléfono
// - descuento
// - estado aprobación
//
// Más adelante permitirá:
//
// - ver facturación
// - generar PDF
// - filtrar fechas
// - ver pedidos finalizados
// - rutas reales
class AdminClienteAdapter(

    // Lista clientes
    private val listaClientes:
    List<Usuario>,

    // Click detalle cliente
    private val onDetalleClick:
        (Usuario) -> Unit

) : RecyclerView.Adapter<
        AdminClienteAdapter.ClienteViewHolder>() {

    // ------------------------------------------------
    // VIEW HOLDER
    // ------------------------------------------------

    class ClienteViewHolder(

        val binding:
        ItemAdminClientesBinding

    ) : RecyclerView.ViewHolder(
        binding.root
    )

    // ------------------------------------------------
    // CREAR ITEM
    // ------------------------------------------------

    override fun onCreateViewHolder(

        parent: ViewGroup,
        viewType: Int

    ): ClienteViewHolder {

        val binding =

            ItemAdminClientesBinding.inflate(

                LayoutInflater.from(
                    parent.context
                ),

                parent,

                false
            )

        return ClienteViewHolder(
            binding
        )
    }

    // ------------------------------------------------
    // BIND DATOS
    // ------------------------------------------------

    override fun onBindViewHolder(

        holder: ClienteViewHolder,
        position: Int

    ) {

        // Cliente actual
        val cliente =
            listaClientes[position]

        // ------------------------------------------------
        // NOMBRE COMERCIO
        // ------------------------------------------------

        holder.binding.tvNombreComercio.text =

            cliente.nombreComercio
                ?: "Sin nombre"

        // ------------------------------------------------
        // DIRECCIÓN
        // ------------------------------------------------

        holder.binding.tvDireccion.text =

            cliente.direccion
                ?: "Sin dirección"

        // ------------------------------------------------
        // TELÉFONO
        // ------------------------------------------------

        holder.binding.tvTelefono.text =

            "📞 ${cliente.telefono ?: "---"}"

        // ------------------------------------------------
        // DESCUENTO
        // ------------------------------------------------

       /* holder.binding.tvDescuento.text =

            "${cliente.descuento.toInt()}% dto."*/

        // ------------------------------------------------
        // APROBADO
        // ------------------------------------------------

        if (cliente.aprobado == true) {

            holder.binding.chipAprobado.text =
                "Aprobado"

        } else {

            holder.binding.chipAprobado.text =
                "No Aprobado"
        }



        // ------------------------------------------------
        // BOTÓN DETALLE
        // ------------------------------------------------

        holder.binding.btnDetalleCliente
            .setOnClickListener {

                onDetalleClick(cliente)
            }
    }

    // ------------------------------------------------
    // TOTAL ITEMS
    // ------------------------------------------------

    override fun getItemCount(): Int {

        return listaClientes.size
    }
}
