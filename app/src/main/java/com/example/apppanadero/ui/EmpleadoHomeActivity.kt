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
