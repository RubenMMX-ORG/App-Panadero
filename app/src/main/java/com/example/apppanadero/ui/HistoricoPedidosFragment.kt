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
import com.example.apppanadero.databinding.FragmentHistoricoPedidosBinding


class HistoricoPedidosFragment: Fragment() {


    private var _binding: FragmentHistoricoPedidosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Inflamos la vista con binding
        _binding = FragmentHistoricoPedidosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Cambiar el título de la ActionBar
        activity?.let {
            (it as? AppCompatActivity)?.supportActionBar?.title = "Historico de pedidos"
        }
        super.onViewCreated(view, savedInstanceState)




    }

}