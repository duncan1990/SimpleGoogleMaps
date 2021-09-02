package com.ahmety.hackhatongooglemaps

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST = 1

    private var lati=0.0
    private var longi=0.0
    var gpsStatus: Boolean = false
    var isConsideredLocation: Boolean = false

    private fun getLocationAccess() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //mMap.isMyLocationEnabled = true
                if (gpsStatus){
          //  Toast.makeText(this, "ssssss", Toast.LENGTH_LONG).show()
            fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                var location: Location? = task.result
                if (location != null) {
                    lati = location.latitude
                    longi = location.longitude
                    addCurrentLocationOnMap()
                }
            }
                }
            else{
                    Toast.makeText(this, "Konum Servisleri kapalı", Toast.LENGTH_LONG).show()
                    showAlertDialog()
            }
        }
        else
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
    }

    private fun addCurrentLocationOnMap() {
        // To zoom my map:
        val zoomLevel = 15f

        // Add a marker in Sydney and move the camera
        val myLocation = LatLng(lati, longi)
        // To add change the marker, add an image, marker.jpg to drawable folder and :
        //  mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)))
        // To the map’s addmarker’s method add following line:
        //   mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
        mMap.addMarker(MarkerOptions().position(myLocation).title("My Place"))
        //  mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
        // To zoom my map:
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoomLevel))
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
         //   Toast.makeText(this, "reqcode", Toast.LENGTH_LONG).show()
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                if (gpsStatus){
                    getLocationAccess()
                }
                else{
                   // openLocationServices()
                  showAlertDialog()
                }
            }
            else {
                Toast.makeText(this, "Uygulama için konum servisi izni verilmedi!", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun showAlertDialog() {
        // build alert dialog
        val dialogBuilder = AlertDialog.Builder(this)

        // set message of alert dialog
        dialogBuilder.setMessage("Konum servisiniz açık degil. Konum servisini açmak ister misiniz?")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Evet", DialogInterface.OnClickListener { dialog, id ->
                    finish()
                    openLocationServices()
                })
                // negative button text and action
                .setNegativeButton("Hayır", DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Konum Servisi")
        // show alert dialog
        alert.show()
    }

    override fun onRestart() {
        super.onRestart()
        if (isConsideredLocation){
            isConsideredLocation = false
            checkLocationStatus()
        }
    }

    private fun openLocationServices() {
        isConsideredLocation = true
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)


    }
    private fun checkLocationStatus() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (gpsStatus) {
            getLocationAccess()
        }
        else {
            Toast.makeText(this, "Konum Servisi kapalı", Toast.LENGTH_LONG).show()
        }
    }

    private fun getLocationStatus() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getLocationStatus()
        getLocationAccess()
        }
}