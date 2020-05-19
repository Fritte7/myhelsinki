package com.fritte.myhelsinki.ui.map

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.PolyUtil
import com.fritte.myhelsinki.R
import com.fritte.myhelsinki.model.Resource
import com.fritte.myhelsinki.model.event.Event
import com.fritte.myhelsinki.model.googlemapapi.Directions
import com.fritte.myhelsinki.ui.map.adapter.ImageAdapter
import com.fritte.myhelsinki.viewmodels.ViewModelEvents
import java.lang.StringBuilder

class ActivityMap  : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    companion object {
        private const val REQUEST_CODE_LOCATION = 1000
        private const val CHANNEL_ID = "My Helsinki"
    }

    private lateinit var txtTitle: TextView
    private lateinit var txtDesc: TextView
    private lateinit var txtDate: TextView
    private lateinit var txtUrl: TextView
    private lateinit var rvImages: RecyclerView

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var gMap: GoogleMap
    private var previousMarker: Marker? = null
    private var previousPolyline: Polyline? = null

    private lateinit var bottomSheet: BottomSheetBehavior<ConstraintLayout>
    private lateinit var viewModel: ViewModelEvents

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        txtTitle = findViewById(R.id.text_title)
        txtDesc  = findViewById(R.id.text_description)
        txtDate  = findViewById(R.id.text_date)
        txtUrl   = findViewById(R.id.text_url)
        rvImages = findViewById(R.id.recyclerview_image)

        checkPermissionLocation()
        createNotificationChannel()
        clearAllNotifications()

        viewModel = ViewModelProviders.of(this).get(ViewModelEvents::class.java)
        /** create markers on the map **/
        viewModel.events.observe(this, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    createMarkers(it.data)
                }
                Resource.Status.ERROR -> {
                    Snackbar.make(findViewById(R.id.main), it.data.toString(), Snackbar.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {

                }
            }
        })
        /** update the bottom sheet information **/
        viewModel.eventSelected.observe(this, Observer {
            if (it != null) {
                txtTitle.text = it.getName()
                txtDesc.text = Html.fromHtml(it.description.body, Html.FROM_HTML_MODE_COMPACT)
                txtDate.text = it.getEventDate()
                if (!it.info_url.isNullOrEmpty()) {
                    txtUrl.visibility = View.VISIBLE
                    txtUrl.text = it.info_url
                } else {
                    txtUrl.visibility = View.GONE
                }
                rvImages.layoutManager = GridLayoutManager(this, countColumn(it.description.images.size))
                rvImages.adapter = ImageAdapter(this, it.description.images)
            } else {
                txtTitle.text = null
                txtDesc.text = null
                txtDate.text = null
                txtUrl.text = null
                rvImages.adapter = null
            }
        })
        /** update direction map **/
        viewModel.directionEventSelected.observe(this, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    if (it.data?.status.equals("OK")) {
                        showDirection(it.data)
                    }
                }
                Resource.Status.ERROR -> {
                    Log.e("ActivityMap", "directionEventSelected( error: "+it.exception+" )")
                }
                Resource.Status.LOADING -> {

                }
            }
        })
        viewModel.notificationNearbyEvents.observe(this, Observer {
            it.forEachIndexed { index, event ->
                createNotification(event, 1000 + index)
            }
        })

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        bottomSheet = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet_map))
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {  }
                    BottomSheetBehavior.STATE_HIDDEN -> { }
                    BottomSheetBehavior.STATE_EXPANDED -> { }
                    BottomSheetBehavior.STATE_DRAGGING -> { }
                    BottomSheetBehavior.STATE_SETTLING -> { }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> { }
                }
            }
        })
    }

    /**************************************** Map ********************************************************/

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        configMap(gMap)
    }

    private fun createMarkers(data: List<Event>?) {
        gMap.clear()
        if (!data.isNullOrEmpty()) {
            data.forEach {
                val loc = LatLng(it.location.lat, it.location.lon)
                val markerOption = MarkerOptions()
                    .position(loc)

                val marker = gMap.addMarker(markerOption)
                marker.tag = it
            }
        }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        resetMap()

        if (marker != null) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            viewModel.selectedEvent(marker.tag as Event)
            previousMarker = marker

            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        return false
    }

    private fun showDirection(direction: Directions?) {
        if (direction != null) {
            val polylineOptions = PolylineOptions()
            polylineOptions.width(5f)
            polylineOptions.geodesic(true)
            polylineOptions.color(ContextCompat.getColor(this, R.color.colorAccent))

            val pointsList = PolyUtil.decode(direction?.routes?.get(0)!!.overview_polyline.points)
            for (point in pointsList) {
                polylineOptions.add(point)
            }
            previousPolyline = gMap.addPolyline(polylineOptions)
        }
    }

    /**************************************** Notification ********************************************************/

    private fun createNotification(event: Event, id: Int) {
        val intent = Intent(this, ActivityMap::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val strBuild = StringBuilder(event.getName()!!)
            .append(" ")
            .append(getString(R.string.notification_event_start))
            .append(" ")
            .append(event.getEventDate())

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_my_helsinki)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(strBuild.toString())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        NotificationManagerCompat.from(this).notify(id, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun clearAllNotifications() {
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    /**************************************** Permission ********************************************************/

    private fun checkPermissionLocation() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                val builder = AlertDialog.Builder(this)
                builder
                    .setTitle(getString(R.string.permission_location_title))
                    .setMessage(getString(R.string.permission_location_message))
                    .setPositiveButton(getString(R.string.OK)) { dialog, _ ->
                        permissionRequest()
                        dialog.dismiss()
                    }

                val dialog = builder.create()
                dialog.show()
            } else {
                permissionRequest()
            }
        }
    }

    private fun permissionRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_LOCATION -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(findViewById(R.id.main), getString(R.string.permission_location_title), Snackbar.LENGTH_SHORT).show()
                } else {
                    configMap(gMap)
                }
            }
        }
    }

    /**************************************** Utils ********************************************************/

    private fun configMap(mMap: GoogleMap) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    viewModel.updateCurrentLocation(currentLatLng)
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f))
                }
            }
        }
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isRotateGesturesEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = false
        mMap.setOnMarkerClickListener(this)

        mMap.setOnMapClickListener {
            if (previousMarker != null) {
                resetMap()
            }
        }
    }

    private fun resetMap() {
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        viewModel.selectedEvent(null)
        if (previousMarker != null) {
            previousMarker!!.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            previousMarker = null
        }
        if (previousPolyline != null) {
            previousPolyline!!.remove()
            previousPolyline = null
        }
    }

    private fun countColumn(size: Int): Int {
        val count: Int
        if (size == 0) {
            count = 0
        } else if (size == 1) {
            count = 1
        } else if (size == 2) {
            count = 2
        } else if (size % 3 == 0) {
            count = 3
        } else {
            count = 4
        }
        return count
    }

    override fun onBackPressed() {
        if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED || bottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED) {
            resetMap()
        } else {
            super.onBackPressed()
        }
    }
}