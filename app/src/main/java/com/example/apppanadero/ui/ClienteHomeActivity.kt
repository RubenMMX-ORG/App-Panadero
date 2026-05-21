package com.example.apppanadero.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.apppanadero.R
import com.example.apppanadero.databinding.ActivityClienteHomeBinding




class ClienteHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClienteHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityClienteHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Actionbar
        setSupportActionBar(binding.toolbarCliente)

        //Navegacion atras de la toolbar
        binding.toolbarCliente.setNavigationOnClickListener {

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
            .findFragmentById(R.id.nav_host_fragment_cliente) as NavHostFragment

        val navController = navHostFragment.navController



        return when (item.itemId) {

            R.id.menu_home -> {
                // Evitar navegar si ya estás en Home
                if (navController.currentDestination?.id != R.id.clienteHomeFragment) {
                    navController.popBackStack(R.id.clienteHomeFragment, false)
                }
                true
            }

            R.id.menu_perfil -> {
                // navController.navigate(R.id.perfilFragment)
                true
            }

            R.id.menu_logout -> {

            // ------------------------------------------------
            // FIREBASE LOGOUT
            // ------------------------------------------------
        
            val repository = UsuarioRepository()
        
            repository.logout()
        
            // ------------------------------------------------
            // SHARED PREFERENCES
            // ------------------------------------------------
        
            val preferencias = getSharedPreferences(
        
                "sesion",
        
                MODE_PRIVATE
            )
        
            val editor = preferencias.edit()
        
            editor.putBoolean(
        
                "mantener_sesion",
        
                false
            )
        
            editor.commit()
        
            // ------------------------------------------------
            // VOLVER LOGIN
            // ------------------------------------------------
        
            startActivity(
        
                Intent(
        
                    this,
        
                    LoginActivity::class.java
                )
            )
        
            finish()
        
            true
        }
            }

            else -> super.onOptionsItemSelected(item)
        }


    }


}
