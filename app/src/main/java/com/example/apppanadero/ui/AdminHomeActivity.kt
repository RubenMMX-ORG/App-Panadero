package com.example.apppanadero.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import com.example.apppanadero.data.di.Injector
import com.example.apppanadero.databinding.ActivityLoginBinding
import com.example.apppanadero.databinding.ItemPedidoBinding
import com.example.apppanadero.viewmodel.UsuarioViewModel
import kotlin.getValue

class AdminHomeActivity: AppCompatActivity() {


    private lateinit var binding: ItemPedidoBinding




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ItemPedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}