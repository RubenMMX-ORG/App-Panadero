package com.example.apppanadero.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.apppanadero.databinding.FragmentAdminProductosBinding
import com.example.apppanadero.databinding.FragmentClienteHomeBinding

class AdminProductosFragment : Fragment() {

    // ------------------------------------------------
    // VIEW BINDING
    // ------------------------------------------------

    private var _binding:
            FragmentAdminProductosBinding? = null

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
            FragmentAdminProductosBinding.inflate(
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
                ?.title = "Gestion-Productos"
        }
    }
}