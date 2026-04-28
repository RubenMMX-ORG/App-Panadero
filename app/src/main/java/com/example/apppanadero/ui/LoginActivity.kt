package com.example.apppanadero.ui


import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.apppanadero.databinding.ActivityAdminHomeBinding
import com.example.apppanadero.databinding.ActivityClienteHomeBinding

import com.example.apppanadero.databinding.ActivityLoginBinding
import com.example.apppanadero.databinding.FragmentAdminPedidosBinding
import com.example.apppanadero.databinding.FragmentClienteHomeBinding

import com.example.apppanadero.databinding.FragmentDetallePedidoBinding
import com.example.apppanadero.databinding.FragmentNuevoPedidoBinding
import com.example.apppanadero.databinding.FragmentRegistroBinding
import com.example.apppanadero.databinding.ItemPedidoAdminBinding
import com.example.apppanadero.databinding.ItemPedidoBinding
import com.example.apppanadero.databinding.ItemProductoAdminBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClienteHomeBinding//Declaracion binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Iniciamos bindin
        binding = ActivityClienteHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}