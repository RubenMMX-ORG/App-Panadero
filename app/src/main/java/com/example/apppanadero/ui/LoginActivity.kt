package com.example.apppanadero.ui

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.apppanadero.databinding.ActivityHomeBinding
import com.example.apppanadero.databinding.ActivityLoginBinding
import com.example.apppanadero.databinding.FragmentDetallePedidoBinding
import com.example.apppanadero.databinding.FragmentNuevoPedidoBinding
import com.example.apppanadero.databinding.FragmentRegistroBinding
import com.example.apppanadero.databinding.ItemPedidoBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: FragmentDetallePedidoBinding//Declaracion binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Iniciamos bindin
        binding = FragmentDetallePedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}