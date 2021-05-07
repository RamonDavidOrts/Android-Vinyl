package com.ramonorts.discossqlite

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.ramonorts.discossqlite.databinding.ActivityMapsBinding
import kotlin.properties.Delegates

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocation: FusedLocationProviderClient
    private var ultimoMarcador : Marker? = null
    private var editar = false
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocation = LocationServices.getFusedLocationProviderClient(this)

        editar = intent.getBooleanExtra("editar", false)
        binding.etMapaEstudio.setText(intent.getStringExtra("estudio").toString())
        latitud = intent.getDoubleExtra("latitud", 0.0)
        longitud = intent.getDoubleExtra("longitud", 0.0)

        if (!editar)
            binding.etMapaEstudio.isEnabled = false

        binding.btAceptar.setOnClickListener {
            val intentResultado = Intent().apply {
                putExtra("estudio", binding.etMapaEstudio.text.toString())
                putExtra("latitud", latitud)
                putExtra("longitud", longitud)
            }
            setResult(Activity.RESULT_OK, intentResultado)
            finish()
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        if (latitud != 0.0 && longitud != 0.0) {
            val ubicacion = LatLng(latitud, longitud)
            colocarMarcador(ubicacion)
        }

        if (editar) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
                return
            }

            mMap.isMyLocationEnabled = true

            fusedLocation.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val ubicacion = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 12f))
                }
            }

            mMap.setOnMapLongClickListener { location ->
                colocarMarcador(location)

                latitud = location.latitude
                longitud = location.longitude
            }

        }
    }

    private fun colocarMarcador(ubicacion: LatLng) {
        val markerOptions = MarkerOptions().position(ubicacion)
        markerOptions.icon(
            BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_GREEN
            )
        )
        markerOptions.title(binding.etMapaEstudio.text.toString())
        if (ultimoMarcador != null)
            ultimoMarcador!!.remove()
        ultimoMarcador = mMap.addMarker(markerOptions)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 12f))
    }
}