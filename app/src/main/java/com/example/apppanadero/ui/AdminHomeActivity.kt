package com.example.apppanadero.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.apppanadero.R
import com.example.apppanadero.databinding.ActivityAdminHomeBinding
import com.example.apppanadero.databinding.ActivityClienteHomeBinding
import com.example.apppanadero.databinding.FragmentAdminHomeBinding
class AdminHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.toolbarAdmin)

        //Navegacion atras de la toolbar
        binding.toolbarAdmin.setNavigationOnClickListener {

            onBackPressedDispatcher.onBackPressed()
        }

    }

    // CREAR MENÚ
    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_home, menu)
        return true
    }

    // MANEJAR CLICKS

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        //  Obtener NavController correctamente
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_admin) as NavHostFragment

        val navController = navHostFragment.navController




        return when (item.itemId) {

            R.id.menu_home -> {
                // Evitar navegar si ya estás en Home
                if (navController.currentDestination?.id != R.id.adminHomeFragment) {
                    navController.popBackStack(R.id.adminHomeFragment, false)
                }
                true
            }

            R.id.menu_perfil -> {
                // navController.navigate(R.id.perfilFragment)
                true
            }

            R.id.menu_logout -> {
                // logout()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}