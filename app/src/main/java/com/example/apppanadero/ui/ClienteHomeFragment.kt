package com.example.apppanadero.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.apppanadero.R
import com.example.apppanadero.data.di.Injector
import com.example.apppanadero.databinding.FragmentClienteHomeBinding
import com.example.apppanadero.viewmodel.UsuarioViewModel
import kotlin.getValue
import kotlin.text.replace


class ClienteHomeFragment: Fragment() {


    private var _binding: FragmentClienteHomeBinding? = null
    private val binding get() = _binding!!

    // ------------------------------------------------
    // VIEWMODELS
    // ------------------------------------------------

    private val usuarioViewModel:
            UsuarioViewModel by viewModels {

        Injector
            .provideUsuarioViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Inflamos la vista con binding
        _binding = FragmentClienteHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Cambiar el título de la ActionBar
        activity?.let {
            (it as? AppCompatActivity)?.supportActionBar?.title = "Cliente"
        }
        super.onViewCreated(view, savedInstanceState)

        binding.btnNuevoPedido.setOnClickListener {
            navegarHacia(R.id.action_clienteHomeFragment_to_clienteNuevoPedidoFragment)
        }

        binding.btnHistorico.setOnClickListener {
            navegarHacia(R.id.action_clienteHomeFragment_to_clienteHistoricoPedidosFragment)
        }




    }
    //Funcion comun para navegar hacia un fragment
    private fun navegarHacia(actionId: Int) {
        findNavController().navigate(actionId)
    }
}