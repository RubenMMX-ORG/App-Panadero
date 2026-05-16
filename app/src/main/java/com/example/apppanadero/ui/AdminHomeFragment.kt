package com.example.apppanadero.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.apppanadero.R
import com.example.apppanadero.databinding.FragmentAdminHomeBinding

class AdminHomeFragment : Fragment() {

    // ------------------------------------------------
    // VIEW BINDING
    // ------------------------------------------------

    private var _binding:
            FragmentAdminHomeBinding? = null

    private val binding get() = _binding!!

    // ------------------------------------------------
    // ON CREATE VIEW
    // ------------------------------------------------

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        // Inflamos layout usando ViewBinding
        _binding =
            FragmentAdminHomeBinding.inflate(
                inflater,
                container,
                false
            )

        return binding.root
    }

    // ------------------------------------------------
    // ON VIEW CREATED
    // ------------------------------------------------

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        // ------------------------------------------------
        // ACTION BAR
        // ------------------------------------------------

        activity?.let {

            (it as? AppCompatActivity)
                ?.supportActionBar
                ?.title = "Admin"
        }

        // ------------------------------------------------
        // BOTÓN ADMIN PEDIDOS
        // ------------------------------------------------

        binding.cardPedidos
            .setOnClickListener {

                navegarHacia(
                    R.id.action_adminHomeFragment_to_adminPedidosFragment
                )
            }

        // ------------------------------------------------
        // BOTÓN ADMIN CLIENTES
        // ------------------------------------------------

        binding.cardClientes
            .setOnClickListener {

                navegarHacia(
                    R.id.action_adminHomeFragment_to_adminClientesFragment
                )
            }

        // ------------------------------------------------
        // BOTÓN ADMIN PRODUCTOS
        // ------------------------------------------------

        binding.cardProductos
            .setOnClickListener {

                navegarHacia(
                    R.id.action_adminHomeFragment_to_adminProductosFragment
                )
            }
    }

    // ------------------------------------------------
    // FUNCIÓN COMÚN NAVEGACIÓN
    // ------------------------------------------------

    // Evita repetir código.
    // Recibe el actionId definido
    // en navigation graph.
    private fun navegarHacia(
        actionId: Int
    ) {

        findNavController()
            .navigate(actionId)
    }

    // ------------------------------------------------
    // ON DESTROY VIEW
    // ------------------------------------------------

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}