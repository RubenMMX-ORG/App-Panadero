package com.example.apppanadero.ui

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.apppanadero.databinding.ActivityAdminHomeBinding
import com.example.apppanadero.databinding.ActivityHomeBinding
import com.example.apppanadero.databinding.ActivityLoginBinding
import com.example.apppanadero.databinding.FragmentAdminPedidosBinding
import com.example.apppanadero.databinding.FragmentCrearProductoBinding
import com.example.apppanadero.databinding.FragmentDetallePedidoBinding
import com.example.apppanadero.databinding.FragmentNuevoPedidoBinding
import com.example.apppanadero.databinding.FragmentRegistroBinding
import com.example.apppanadero.databinding.ItemPedidoAdminBinding
import com.example.apppanadero.databinding.ItemPedidoBinding
import com.example.apppanadero.databinding.ItemProductoAdminBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: FragmentCrearProductoBinding//Declaracion binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Iniciamos bindin
        binding = FragmentCrearProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}