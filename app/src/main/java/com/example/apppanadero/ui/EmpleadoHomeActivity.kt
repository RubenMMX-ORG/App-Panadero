package com.example.apppanadero.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.apppanadero.R
import com.example.apppanadero.data.di.Injector
import com.example.apppanadero.databinding.ActivityEmpleadoHomeBinding
import com.example.apppanadero.viewmodel.UsuarioViewModel
import kotlin.getValue


class EmpleadoHomeActivity : AppCompatActivity() {


    private lateinit var binding: ActivityEmpleadoHomeBinding

    private val usuarioViewModel:
            UsuarioViewModel by viewModels {

        Injector
            .provideUsuarioViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEmpleadoHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarEmpleado)

        //Navegacion atras de la toolbar
        binding.toolbarEmpleado.setNavigationOnClickListener {

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
                if (navController.currentDestination?.id != R.id.empleadoHomeFragment) {
                    navController.popBackStack(R.id.empleadoHomeFragment, false)
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

                usuarioViewModel.logout()

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


            else -> super.onOptionsItemSelected(item)
        }


    }

}
