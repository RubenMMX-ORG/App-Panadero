package com.example.apppanadero.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.apppanadero.databinding.FragmentAdminHomeBinding


class AdminHomeFragment: Fragment() {


    private var _binding: FragmentAdminHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Inflamos la vista con binding
        _binding = FragmentAdminHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Cambiar el título de la ActionBar
        activity?.let {
            (it as? AppCompatActivity)?.supportActionBar?.title = "Admin"
        }
        super.onViewCreated(view, savedInstanceState)





    }
    //Funcion comun para navegar hacia un fragment
    private fun navegarHacia(actionId: Int) {
        findNavController().navigate(actionId)
    }
}