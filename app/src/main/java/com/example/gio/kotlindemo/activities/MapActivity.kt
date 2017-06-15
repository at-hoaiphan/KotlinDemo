package com.example.gio.kotlindemo.activities

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.example.gio.kotlindemo.R
import com.example.gio.kotlindemo.datas.BusStopDatabaseJava
import com.example.gio.kotlindemo.datas.CarriagePolyline
import com.example.gio.kotlindemo.models.bus_stops.PlaceStop
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_map.*

class MapActivity(private var mPlaceStops: ArrayList<PlaceStop> = arrayListOf()) : AppCompatActivity(), LocationListener {
    val DEFAULT_CARRIAGE = "0"
    val CARRIAGE_1 = "1"
    val CARRIAGE_2 = "2"
    val CARRIAGE_3 = "3"
    val REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100
    var cameraPosition: CameraPosition? = null
    var sPositionCarriage = DEFAULT_CARRIAGE
    lateinit var mMyProgress: ProgressDialog
    private var mMyMap: GoogleMap? = null
    private var mCurrentMarker: Marker? = null
    private var mBusMarker: Marker? = null
    private var mBusStopDatabase: BusStopDatabaseJava? = null
    private var mListMarkers: ArrayList<Marker> = arrayListOf()
    private var mAllCarriagePolyline: Polyline? = null
    private var mCarriagePolyline: Polyline? = null
    private var mCoundownTimer: CountDownTimer? = null

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
            drawAllCarriagePoly()
            // Show bus carriage polyline by choose spinner
            spBusCarriage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                    //                    mViewPager.setVisibility(View.GONE)
                    //                    mIsViewpagerVisibility = false

                    sPositionCarriage = i.toString()
                    // Reload map
                    mMyMap!!.clear()
                    //                    mIsDirected = false

                    // draw carriage
                    if (sPositionCarriage == 0.toString()) {
                        drawAllCarriagePoly()
                    } else {
                        drawCarriagePoly(sPositionCarriage)
                    }
                    //                    // Remove previousSelectedMarker
                    //                    if (mPreviousSelectedMarker != null) {
                    //                        mPreviousSelectedMarker.remove()
                    //                    }

                    showMyLocation()
                    mListMarkers.clear()
                    mPlaceStops.clear()
                    if (sPositionCarriage == 0.toString()) {
                        mPlaceStops.addAll(mBusStopDatabase!!.allPlaces)
                    } else {
                        mPlaceStops.addAll(mBusStopDatabase!!.getPlacesByIdCarriage(sPositionCarriage))
                    }

                    if (mPlaceStops.size > 0) {
                        for (placeStop in mPlaceStops) {
                            val option = MarkerOptions()
                            option.title(placeStop.name)
                            option.snippet(placeStop.latitude.toString() + ";" + placeStop.longitude.toString())
                            option.position(LatLng(placeStop.latitude, placeStop.longitude))
                            option.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_stop24))
                            val marker = mMyMap!!.addMarker(option)
                            mListMarkers.add(marker)
                        }
                    }
//                    mViewPager.setAdapter(null)
//                    mAdapter = ViewPagerMarkerAdapter(baseContext, supportFragmentManager, mPlaceStops)
//                    mViewPager.setAdapter(mAdapter)
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {

                }
            }
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

            if (accessCoarsePermission != PackageManager.PERMISSION_GRANTED
                    || accessFinePermission != PackageManager.PERMISSION_GRANTED) {
                // Permissions.
                val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                // Dialog.
                ActivityCompat.requestPermissions(this, permissions, REQUEST_ID_ACCESS_COURSE_FINE_LOCATION)
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

    private fun drawCarriagePoly(carriage: String) {
        // points: overview_polyline
        val carriagePolyOption = PolylineOptions().geodesic(true).width(25f)
        val arrCarriageDecode = java.util.ArrayList<LatLng>()
        when (carriage) {
            CARRIAGE_1 -> {
                // Bus Carriage 1
                arrCarriageDecode.addAll(CarriagePolyline.carriagePoly1)
                carriagePolyOption.color(Color.parseColor("#99FF373E"))
            }
            CARRIAGE_2 -> {
                // Bus Carriage 2
                arrCarriageDecode.addAll(CarriagePolyline.carriagePoly2)
                carriagePolyOption.color(Color.parseColor("#88FFF837"))
            }
            CARRIAGE_3 -> {
                // Bus Carriage 3
                arrCarriageDecode.addAll(CarriagePolyline.carriagePoly3)
                carriagePolyOption.color(Color.parseColor("#7337FF37"))
            }
        }

        val option = MarkerOptions()
        option.title(getString(R.string.title_marker_bus_here))
        option.position(LatLng(arrCarriageDecode[0].latitude, arrCarriageDecode[0].longitude))
        option.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_marker))
        // Cancel old moving-bus and create new one
        mCoundownTimer?.cancel()
        mBusMarker = mMyMap?.addMarker(option)
        mCoundownTimer = object : CountDownTimer((arrCarriageDecode.size * 5000).toLong(), 5000) {
            internal var index = 0
            internal var busPosition = LatLng(arrCarriageDecode[0].latitude, arrCarriageDecode[0].longitude)

            override fun onTick(millisUntilFinished: Long) {
                if (index == arrCarriageDecode.size - 1) {
                    cancel()
                } else {
                    index++
                    busPosition = LatLng(arrCarriageDecode[index].latitude, arrCarriageDecode[index].longitude)
                    mBusMarker?.setPosition(busPosition)
                }
            }

            override fun onFinish() {
                mBusMarker?.remove()
            }
        }.start()

        // Draw polyline
        for (arrCarriage in arrCarriageDecode) {
            carriagePolyOption.add(arrCarriage)
        }
        // Clear old Polyline
        if (mCarriagePolyline != null) {
            mCarriagePolyline?.remove()
        }
        if (mAllCarriagePolyline != null) {
            mAllCarriagePolyline?.remove()
        }
        mCarriagePolyline = mMyMap?.addPolyline(carriagePolyOption)
    }

    private fun drawAllCarriagePoly() {
        // points: overview_polyline
        val arrCarriageDecode1 = java.util.ArrayList<LatLng>()
        val arrCarriageDecode2 = java.util.ArrayList<LatLng>()
        val arrCarriageDecode3 = java.util.ArrayList<LatLng>()

        arrCarriageDecode1.addAll(CarriagePolyline.carriagePoly1)
        arrCarriageDecode2.addAll(CarriagePolyline.carriagePoly2)
        arrCarriageDecode3.addAll(CarriagePolyline.carriagePoly3)
        // Draw polyline
        val carriagePolyOption1 = PolylineOptions().geodesic(true).color(Color.parseColor("#99FF373E")).width(30f)
        val carriagePolyOption2 = PolylineOptions().geodesic(true).color(Color.parseColor("#88FFF837")).width(23f)
        val carriagePolyOption3 = PolylineOptions().geodesic(true).color(Color.parseColor("#7337FF37")).width(15f)

        for (arrCarriage in arrCarriageDecode1) {
            carriagePolyOption1.add(arrCarriage)
        }
        for (arrCarriage in arrCarriageDecode2) {
            carriagePolyOption2.add(arrCarriage)
        }
        for (arrCarriage in arrCarriageDecode3) {
            carriagePolyOption3.add(arrCarriage)
        }

        // Clear old direction
        mAllCarriagePolyline = mMyMap?.addPolyline(carriagePolyOption1)
        mAllCarriagePolyline = mMyMap?.addPolyline(carriagePolyOption2)
        mAllCarriagePolyline = mMyMap?.addPolyline(carriagePolyOption3)
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
