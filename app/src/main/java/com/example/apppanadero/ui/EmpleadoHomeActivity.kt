package com.example.apppanadero.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.apppanadero.databinding.ActivityEmpleadoHomeBinding


class EmpleadoHomeActivity: AppCompatActivity() {


    private lateinit var binding: ActivityEmpleadoHomeBinding




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEmpleadoHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}