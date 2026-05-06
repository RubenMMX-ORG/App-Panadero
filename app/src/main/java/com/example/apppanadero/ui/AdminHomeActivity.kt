package com.example.apppanadero.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.apppanadero.databinding.ActivityAdminHomeBinding
import com.example.apppanadero.databinding.FragmentAdminHomeBinding
class AdminHomeActivity: AppCompatActivity() {


    private lateinit var binding: ActivityAdminHomeBinding




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}