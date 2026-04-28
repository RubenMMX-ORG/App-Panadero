package com.example.apppanadero.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.apppanadero.R
import com.example.apppanadero.databinding.FragmentClienteHomeBinding
import com.example.apppanadero.databinding.FragmentNuevoPedidoBinding


class NuevoPedidoFragment: Fragment() {


    private var _binding: FragmentNuevoPedidoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Inflamos la vista con binding
        _binding = FragmentNuevoPedidoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Cambiar el título de la ActionBar
        activity?.let {
            (it as? AppCompatActivity)?.supportActionBar?.title = "Nuevo pedido de Cliente"
        }
        super.onViewCreated(view, savedInstanceState)



    }

}