package com.example.apppanadero.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.apppanadero.R
import com.example.apppanadero.data.di.Injector
import com.example.apppanadero.data.model.Pedido
import com.example.apppanadero.data.model.Usuario
import com.example.apppanadero.databinding.FragmentMapaBinding
import com.example.apppanadero.viewmodel.PedidoViewModel
import com.example.apppanadero.viewmodel.UsuarioViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MapaFragment : Fragment(), OnMapReadyCallback {

    // ------------------------------------------------
    // VIEWBINDING
    // ------------------------------------------------

    private var _binding: FragmentMapaBinding? = null

    private val binding get() = _binding!!

    // ------------------------------------------------
    // GOOGLE MAP
    // ------------------------------------------------

    private lateinit var googleMap: GoogleMap

    private lateinit var fusedLocationClient: //ubicacion actual
            FusedLocationProviderClient

    // ------------------------------------------------
    // VIEWMODEL PEDIDOS
    // ------------------------------------------------

    private val pedidoViewModel: PedidoViewModel by viewModels {

        Injector.providePedidoViewModelFactory(

        )
    }


    // ------------------------------------------------
    // VIEWMODEL USUARIOS
    // ------------------------------------------------

    private val usuarioViewModel: UsuarioViewModel by viewModels {

        Injector.provideUsuarioViewModelFactory(

        )
    }

    // ------------------------------------------------
    // LISTAS
    // ------------------------------------------------

    private val listaClientesPedidos = mutableListOf<Usuario>()

    // ------------------------------------------------
    // DATOS ACTUALES
    // ------------------------------------------------

    private var pedidosActuales =
        emptyList<Pedido>()

    private var clientesActuales =
        emptyList<Usuario>()

    // ------------------------------------------------
    // ON CREATE VIEW
    // ------------------------------------------------

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // ------------------------------------------------
        // INFLAR VISTA
        // ------------------------------------------------

        _binding = FragmentMapaBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    // ------------------------------------------------
    // ON VIEW CREATED
    // ------------------------------------------------

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        // INICIALIZACION
        fusedLocationClient =

            LocationServices
                .getFusedLocationProviderClient(

                    requireActivity()
                )

        // ------------------------------------------------
        // CARGAR DATOS FIRESTORE
        // ------------------------------------------------

        pedidoViewModel.obtenerTodosPedidos()

        usuarioViewModel.obtenerClientes()

        // ------------------------------------------------
        // TITULO ACTIONBAR
        // ------------------------------------------------

        activity?.let {

            (it as? AppCompatActivity)
                ?.supportActionBar
                ?.title = "Mapa reparto"
        }
        // ------------------------------------------------
        // OBSERVER PEDIDOS
        // ------------------------------------------------

        pedidoViewModel.listaPedidos.observe(

            viewLifecycleOwner

        ) { pedidos ->

            pedidosActuales = pedidos



            actualizarMapa()
        }

        // ------------------------------------------------
        // OBSERVER CLIENTES
        // ------------------------------------------------

        usuarioViewModel.listaClientes.observe(

            viewLifecycleOwner

        ) { clientes ->

            clientesActuales = clientes



            actualizarMapa()
        }

        // ------------------------------------------------
        // OBTENER MAPFRAGMENT
        // ------------------------------------------------

        val mapFragment = childFragmentManager
            .findFragmentById(
                R.id.mapFragment
            ) as SupportMapFragment

        // ------------------------------------------------
        // INICIAR MAPA ASINCRONO
        // ------------------------------------------------

        mapFragment.getMapAsync(this)
    }

    // ------------------------------------------------
    // MAPA LISTO
    // ------------------------------------------------

    override fun onMapReady(map: GoogleMap) {

        googleMap = map



        // ------------------------------------------------
        // PERMISOS LOCALIZACION
        // ------------------------------------------------

        checkLocationPermission()

        // ------------------------------------------------
        // TOAST MAPA LISTO
        // ------------------------------------------------

        Toast.makeText(

            requireContext(),

            "Mapa listo",

            Toast.LENGTH_SHORT

        ).show()

        // ------------------------------------------------
        // POSICION INICIAL
        // ------------------------------------------------

        if (

            ActivityCompat.checkSelfPermission(

                requireContext(),

                Manifest.permission.ACCESS_FINE_LOCATION

            )

            ==

            PackageManager.PERMISSION_GRANTED

        ) {

            fusedLocationClient
                .lastLocation

                .addOnSuccessListener {

                        location ->

                    if (location != null) {

                        // ----------------------------------------
                        // POSICION ACTUAL
                        // ----------------------------------------

                        val posicionActual = LatLng(

                            location.latitude,

                            location.longitude
                        )

                        // ----------------------------------------
                        // MOVER CAMARA
                        // ----------------------------------------

                        googleMap.moveCamera(

                            CameraUpdateFactory
                                .newLatLngZoom(

                                    posicionActual,

                                    14f
                                )
                        )
                    }
                }
        }

        // ------------------------------------------------
        // CONTROLES MAPA
        // ------------------------------------------------

        googleMap.uiSettings.apply {

            isZoomControlsEnabled = true

            isCompassEnabled = true

            isScrollGesturesEnabled = true

            isZoomGesturesEnabled = true

            isMyLocationButtonEnabled = true
        }


        // ------------------------------------------------
        // CLICK INFO WINDOW
        // ------------------------------------------------

        googleMap.setOnInfoWindowClickListener {

                marker ->

            // ------------------------------------------------
            // POSICION DESTINO
            // ------------------------------------------------

            val posicion = marker.position

            // ------------------------------------------------
            // URI GOOGLE MAPS
            // ------------------------------------------------

            val uri = Uri.parse(

                "google.navigation:q=" +

                        "${posicion.latitude}," +

                        "${posicion.longitude}"
            )

            // ------------------------------------------------
            // INTENT MAPS
            // ------------------------------------------------

            val intent = Intent(

                Intent.ACTION_VIEW,

                uri
            )

            // ------------------------------------------------
            // FORZAR GOOGLE MAPS
            // ------------------------------------------------

            intent.setPackage(

                "com.google.android.apps.maps"
            )

            // ------------------------------------------------
            // ABRIR NAVEGACION
            // ------------------------------------------------

            startActivity(intent)
        }
    }
    // ------------------------------------------------
// ACTUALIZAR MAPA
// ------------------------------------------------

    private fun actualizarMapa() {

        if (!::googleMap.isInitialized) {

            return
        }

        googleMap.clear()

        val listaIdsClientes =

            pedidosActuales

                .filter {



                    it.estado
                        .trim()
                        .equals(

                            "preparado",

                            ignoreCase = true
                        )
                }

                .map {

                    it.clienteId
                }

                .distinct()

        val clientesPedidos =

            clientesActuales.filter {

                listaIdsClientes.contains(
                    it.id
                )
            }



        clientesPedidos.forEach { cliente ->

            try {

                if (

                    cliente.direccion
                        .isNullOrBlank()

                ) {

                    Log.e(

                        "MAPA",

                        "Direccion vacia"
                    )

                    return@forEach
                }

                val geocoder = Geocoder(

                    requireContext(),

                    Locale.getDefault()
                )

                val resultados =

                    geocoder.getFromLocationName(

                        cliente.direccion,

                        1
                    )


                if (

                    !resultados.isNullOrEmpty()

                ) {

                    val posicion = LatLng(

                        resultados[0].latitude,

                        resultados[0].longitude
                    )

                    val marker =

                        googleMap.addMarker(

                            MarkerOptions()

                                .position(
                                    posicion
                                )

                                .title(
                                    cliente.nombreComercio
                                )

                                .snippet(
                                    cliente.direccion +
                                            "\n\n🚚 iniciar navegación"
                                )

                                .icon(

                                    BitmapDescriptorFactory
                                        .defaultMarker(

                                            BitmapDescriptorFactory.HUE_ORANGE
                                        )
                                )
                        )

                    marker?.tag = cliente



                } else {

                    Log.e(

                        "MAPA",

                        "NO ENCONTRADA: ${cliente.direccion}"
                    )
                }

            } catch (e: Exception) {

                Log.e(

                    "MAPA",

                    "ERROR GEOCODER",

                    e
                )
            }
        }
    }

    // ------------------------------------------------
    // CODIGO PERMISO LOCALIZACION
    // ------------------------------------------------

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    // ------------------------------------------------
    // COMPROBAR PERMISO
    // ------------------------------------------------

    private fun checkLocationPermission() {

        if (

            ActivityCompat.checkSelfPermission(

                requireContext(),

                Manifest.permission.ACCESS_FINE_LOCATION

            )

            !=

            PackageManager.PERMISSION_GRANTED

        ) {

            // ------------------------------------------------
            // PEDIR PERMISO
            // ------------------------------------------------

            ActivityCompat.requestPermissions(

                requireActivity(),

                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),

                LOCATION_PERMISSION_REQUEST_CODE
            )

        } else {

            // ------------------------------------------------
            // ACTIVAR LOCALIZACION
            // ------------------------------------------------

            enableMyLocation()
        }
    }

    // ------------------------------------------------
    // ACTIVAR LOCALIZACION MAPA
    // ------------------------------------------------

    @RequiresPermission(
        allOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )

    private fun enableMyLocation() {

        // ------------------------------------------------
        // PUNTO AZUL USUARIO
        // ------------------------------------------------

        googleMap.isMyLocationEnabled = true

        // ------------------------------------------------
        // BOTON MI UBICACION
        // ------------------------------------------------

        googleMap.uiSettings
            .isMyLocationButtonEnabled = true
    }

    // ------------------------------------------------
    // ONDESTROY
    // ------------------------------------------------

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}