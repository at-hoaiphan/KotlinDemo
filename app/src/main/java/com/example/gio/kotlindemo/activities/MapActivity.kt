package com.example.gio.kotlindemo.activities

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.gio.kotlindemo.R
import com.example.gio.kotlindemo.datas.BusStopDatabaseJava
import com.example.gio.kotlindemo.models.bus_stops.PlaceStop
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_map.*

class MapActivity(private var mPlaceStops: ArrayList<PlaceStop> = arrayListOf()) : AppCompatActivity(), LocationListener {
    val DEFAULT_CARRIAGE = "0"
    //    val CARRIAGE_1 = "1"
//    val CARRIAGE_2 = "2"
//    val CARRIAGE_3 = "3"
    val REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100
    var cameraPosition: CameraPosition? = null
    var sPositionCarriage = DEFAULT_CARRIAGE
    lateinit var mMyProgress: ProgressDialog
    private var mMyMap: GoogleMap? = null
    private var mCurrentMarker: Marker? = null
    private var mBusStopDatabase: BusStopDatabaseJava? = null
    private var mListMarkers: ArrayList<Marker> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Create Progress Bar
        mMyProgress = ProgressDialog(this)
        mMyProgress.setTitle(R.string.title_dialog_map_loading)
        mMyProgress.setMessage(getString(R.string.message_dialog_please_wait))
        mMyProgress.setCancelable(true)

        // Display Progress Bar
        mMyProgress.show()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        // Put event when GoogleMap is ready.
        mapFragment.getMapAsync { googleMap -> onMyMapReady(googleMap) }

        sPositionCarriage = spBusCarriage.selectedItemPosition.toString()
    }

    fun onMyMapReady(googleMap: GoogleMap) {
        // Get GoogleMap Object
        mMyMap = googleMap

        mMyMap!!.setOnMapLoadedCallback {
            // Dismiss Dialog Progress when downloading finished
            mMyProgress.dismiss()
            // Get data from database
            mBusStopDatabase = BusStopDatabaseJava(baseContext)
            if (sPositionCarriage == DEFAULT_CARRIAGE) {
                mPlaceStops.addAll(mBusStopDatabase!!.allPlaces)
            } else {
                mPlaceStops.addAll(mBusStopDatabase!!.getPlacesByIdCarriage(sPositionCarriage))
            }

            if (mPlaceStops.size > 0) {
                // Show default Bus Carriage
                for (placeStop in mPlaceStops) {
                    val option = MarkerOptions()
                    option.title(placeStop.name)
                    option.snippet(placeStop.latitude.toString() + ";" + placeStop.longitude.toString())
                    option.position(LatLng(placeStop.latitude, placeStop.longitude))
                    option.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_stop24))
                    val marker = mMyMap!!.addMarker(option)
                    mListMarkers.add(marker)
                }

            } else {
                Toast.makeText(baseContext, R.string.error_message_load_data_fail, Toast.LENGTH_SHORT).show()
            }
            // Draw all carriage

            // Show User's Location

            askPermissionsAndShowMyLocation()
        }
        mMyMap!!.mapType = GoogleMap.MAP_TYPE_SATELLITE
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mMyMap!!.isMyLocationEnabled = true
    }

    fun askPermissionsAndShowMyLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            val accessCoarsePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            val accessFinePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

            if (accessCoarsePermission != PackageManager.PERMISSION_GRANTED || accessFinePermission != PackageManager.PERMISSION_GRANTED) {

                // Permissions.
                val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

                // Dialog.
                ActivityCompat.requestPermissions(this, permissions,
                        REQUEST_ID_ACCESS_COURSE_FINE_LOCATION)
                return
            }
        }
        // Show Current Location
        showMyLocation()
    }

    // Find a Location Provider
    private fun getEnabledLocationProvider(): String? {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()

        // Find the best LocationProvider
        // ==> "gps", "network",...
        val bestProvider = locationManager.getBestProvider(criteria, true)
        val enabled = locationManager.isProviderEnabled(bestProvider)

        if (!enabled) {
            Toast.makeText(this, R.string.error_message_no_location_provider, Toast.LENGTH_LONG).show()
            return null
        }
        return bestProvider
    }

    fun showMyLocation() {
        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationProvider = this.getEnabledLocationProvider() ?: return

        // Millisecond
        val MIN_TIME_BW_UPDATES: Long = 1000
        // Met
        val MIN_DISTANCE_CHANGE_FOR_UPDATES = 1f

        val myLocation: Location?
        try {
            locationManager.requestLocationUpdates(
                    locationProvider,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this)

            // Get location.
            myLocation = locationManager.getLastKnownLocation(locationProvider)
        } catch (e: SecurityException) {
            Toast.makeText(this, R.string.error_message_show_location_error, Toast.LENGTH_LONG).show()
            return
        }

        // Android API >= 23 catch SecurityException.
        if (myLocation != null) {
            val latLng = LatLng(myLocation.latitude, myLocation.longitude)
            mMyMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f))
            // Add MyLocation on Map:
            val option = MarkerOptions()
            option.title(getString(R.string.title_marker_my_location))
            option.snippet(latLng.latitude.toString() + "+" + latLng.longitude)
            option.position(LatLng(latLng.latitude, latLng.longitude))
            option.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_start_marker))
            mCurrentMarker = mMyMap?.addMarker(option)
            mCurrentMarker?.showInfoWindow()

            cameraPosition = CameraPosition.Builder()
                    .target(latLng)             // Sets the center of the map to location user
                    .zoom(16f)                   // Sets the zoom
                    .bearing(90f)                // Sets the orientation of the camera to east
                    .tilt(40f)                   // Sets the tilt of the camera to 30 degrees
                    .build()
        }
    }

    override fun onLocationChanged(p0: Location?) {
        // No-op
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        // No-op
    }

    override fun onProviderEnabled(p0: String?) {
        // No-op
    }

    override fun onProviderDisabled(p0: String?) {
        // No-op
    }

}
