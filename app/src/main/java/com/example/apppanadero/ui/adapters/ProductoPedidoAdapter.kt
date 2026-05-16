package com.example.apppanadero.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apppanadero.data.model.Producto
import com.example.apppanadero.databinding.ItemProductoBinding


// Adapter encargado de mostrar productos
// para crear o modificar un pedido
class ProductoPedidoAdapter(

    // Lista productos obtenidos desde Firestore
    private val listaProductos: List<Producto>,

    // ------------------------------------------------
    // NUEVO 😄🔥
    // ------------------------------------------------
    // Mapa con precios vigentes.
    //
    // key   -> productoId
    // value -> precio actual
    //
    // Este mapa viene desde el Fragment,
    // NO desde Firestore directamente.
    //
    // El Adapter solo pinta datos.
    private val mapaPrecios:
    Map<String, Double>,

    // ------------------------------------------------
    // YA EXISTÍA
    // ------------------------------------------------

    // Mapa con cantidades iniciales.
    // Se usa principalmente al modificar un pedido.
    //
    // key   -> productoId
    // value -> cantidad seleccionada
    //
    // Si es un pedido nuevo:
    // se enviará emptyMap()
    //
    // Si es modificar pedido:
    // se enviarán las cantidades reales
    // recuperadas desde Firestore
    private val cantidadesIniciales:
    Map<String, Int>,

    // Callback para comunicar al Fragment
    // que una cantidad ha cambiado.
    //
    // El Adapter NO debe hablar con Firestore.
    // Solo pinta datos y comunica eventos.
    //
    // La lógica real se gestiona en:
    // Fragment -> ViewModel -> Repository
    private val onCantidadCambiada:
        (Producto, Int) -> Unit

) : RecyclerView.Adapter<
        ProductoPedidoAdapter.ProductoViewHolder>() {

    // ------------------------------------------------
    // MAPA CANTIDADES
    // ------------------------------------------------

    // Guarda las cantidades actuales
    // seleccionadas por el usuario.
    //
    // Este mapa se inicializa usando:
    // cantidadesIniciales
    //
    // Así conseguimos que al modificar
    // un pedido el RecyclerView ya aparezca
    // con los productos previamente añadidos.
    private val cantidadesSeleccionadas =
        mutableMapOf<String, Int>()

    // ------------------------------------------------
    // INIT
    // ------------------------------------------------

    init {

        // Copiamos las cantidades iniciales
        // al mapa interno del adapter
        cantidadesSeleccionadas.putAll(
            cantidadesIniciales
        )
    }

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

        // ------------------------------------------------
        // NUEVO 😄🔥
        // ------------------------------------------------
        // Recuperamos precio actual usando:
        //
        // producto.id
        //
        // como FK lógica.
        //
        // Si no existe:
        // usamos 0.0 por seguridad.
        val precioActual =

            mapaPrecios[
                producto.id
            ] ?: 0.0

        // Recuperamos cantidad actual
        //
        // Si el producto no existe en el mapa:
        // usamos 0 por defecto
        var cantidad =
            cantidadesSeleccionadas[
                producto.id
            ] ?: 0

        // ------------------------------------------------
        // DATOS PRODUCTO
        // ------------------------------------------------

        // Nombre producto
        holder.binding.tvNombreProducto.text =
            producto.nombre

        // Categoría
        holder.binding.tvCategoria.text =
            producto.categoria

        // ------------------------------------------------
        // MODIFICADO 😄🔥
        // ------------------------------------------------
        // Antes:
        //
        // producto.precio
        //
        // Ahora:
        //
        // precioActual
        //
        // porque el precio ya NO pertenece
        // directamente a Producto.
        holder.binding.tvPrecioProducto.text =

            "precio/unidad: %.2f €"
                .format(precioActual)

        // Cantidad seleccionada
        holder.binding.tvCantidad.text =
            cantidad.toString()

        // ------------------------------------------------
        // BOTÓN SUMAR
        // ------------------------------------------------

        holder.binding.btnSumar.setOnClickListener {

            cantidad++

            // Actualizamos mapa interno
            cantidadesSeleccionadas[
                producto.id
            ] = cantidad

            // Actualizamos UI
            holder.binding.tvCantidad.text =
                cantidad.toString()

            // Comunicamos cambio al Fragment
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

                // Actualizamos mapa interno
                cantidadesSeleccionadas[
                    producto.id
                ] = cantidad

                // Actualizamos UI
                holder.binding.tvCantidad.text =
                    cantidad.toString()

                // Comunicamos cambio al Fragment
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