package com.longkai.stcarcontrol.st_exp.activity


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.util.component1
import com.amap.api.location.AMapLocationClient
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.maps.utils.SpatialRelationUtil
import com.amap.api.maps.utils.overlay.SmoothMoveMarker
import com.google.android.material.snackbar.Snackbar
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.Utils.dp2px
import com.longkai.stcarcontrol.st_exp.databinding.FragmentTrackingBinding
import com.longkai.stcarcontrol.st_exp.tracking.Tracking
import com.longkai.stcarcontrol.st_exp.tracking.TrackingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class TrackingActivity : ComponentActivity() {

    private lateinit var binding: FragmentTrackingBinding
    private val mapView get() = binding.mapView
    private val aMap get() = mapView.map
    private val smoothMarker by lazy { SmoothMoveMarker(aMap) }

    private var points: MutableList<LatLng> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this, true);
        mapView.onCreate(savedInstanceState)

        initTracking()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    private fun initTracking() {
        Tracking.init(this)

        aMap.animateCamera(CameraUpdateFactory.zoomTo(10f))

        smoothMarker.apply {
            setDescriptor(
                BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(resources, R.drawable.ic_car_hatchback_blue)
                        .rotate(-90f)
                )
            )
            setRotate(90f)
        }

        binding.loadRecordBtn.setOnClickListener { loadRecord() }
        binding.showTrackBtn.setOnClickListener { showTrack() }
        binding.moveCarBtn.setOnClickListener { moveCar() }
        binding.clearBtn.setOnClickListener { clearTrack() }
        binding.showTrackProgressBtn.setOnClickListener { showTrackProgress() }
    }

    private fun loadRecord() {
        points = Tracking.load().map { it.toLatLng() }.toMutableList()
        showSnackbar("Load record complete")
    }

    private fun showTrack() {
        ensureRecordLoaded()

        val bounds = LatLngBounds(points[0], points[points.size - 1])
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50.dp2px(this)))
        aMap.addMarker(
            MarkerOptions()
                .position(points[0])
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(
                            resources,
                            R.drawable.ic_car_hatchback_blue
                        )
                    )
                )

        )
        aMap.addPolyline(PolylineOptions().addAll(points).width(10f).color(Color.CYAN))
    }

    private fun moveCar() {
        ensureRecordLoaded()

        val drivePoint = points[0]
        val (first) = SpatialRelationUtil.calShortestDistancePoint(points, drivePoint)
        points[first] = drivePoint
        val subList: List<LatLng> = points.subList(first, points.size)

        smoothMarker.setPoints(subList)
        smoothMarker.setTotalDuration(40)
        smoothMarker.startSmoothMove()
    }

    private fun clearTrack() {
        aMap.clear()
    }

    private fun showTrackProgress() {
        ensureRecordLoaded()

        CoroutineScope(Dispatchers.Main).launch {
            points.forEachIndexed { index, point ->
                if (index == 0) {
                    aMap.animateCamera(CameraUpdateFactory.newLatLng(point))
                } else {
                    aMap.moveCamera(CameraUpdateFactory.newLatLng(point))
                }
                val previousPoint = if (index == 0) null else points[index - 1]

                if (previousPoint != null) {
                    val polylineOptions = PolylineOptions().width(10f).color(Color.CYAN)
                    aMap.addPolyline(polylineOptions.add(previousPoint, point))
                }

                delay(2)
            }
        }
    }

    private fun ensureRecordLoaded() {
        if (points.isNullOrEmpty()) {
            showSnackbar("Record is empty, load record first.")
        }
    }

    private fun showSnackbar(msg: String) {
        Snackbar.make(binding.root, msg, Toast.LENGTH_SHORT).show()
    }
}

const val mockPoint = true
private fun TrackingData.toLatLng() =
    if (mockPoint) LatLng(latitude + 7, longitude + 94) else LatLng(latitude, longitude)

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}