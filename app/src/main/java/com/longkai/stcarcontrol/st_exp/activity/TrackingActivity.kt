package com.longkai.stcarcontrol.st_exp.activity


import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.util.component1
import com.amap.api.location.AMapLocationClient
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.maps.utils.SpatialRelationUtil
import com.amap.api.maps.utils.overlay.MovingPointOverlay
import com.google.android.material.snackbar.Snackbar
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.Utils.dp2px
import com.longkai.stcarcontrol.st_exp.Utils.rotate
import com.longkai.stcarcontrol.st_exp.databinding.FragmentTrackingBinding
import com.longkai.stcarcontrol.st_exp.tracking.Tracking
import com.longkai.stcarcontrol.st_exp.tracking.TrackingData
import kotlinx.coroutines.*
import kotlin.math.atan2


class TrackingActivity : ComponentActivity() {

    private lateinit var binding: FragmentTrackingBinding
    private val mapView get() = binding.mapView
    private val aMap get() = mapView.map

    private var points: MutableList<LatLng> = mutableListOf()
    private val progressiveTrackScope = CoroutineScope(Dispatchers.Main)

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
        progressiveTrackScope.cancel()
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

        binding.loadRecordBtn.setOnClickListener { loadRecord() }
        binding.showTrackBtn.setOnClickListener { showTrack() }
        binding.moveCarBtn.setOnClickListener { moveCar() }
        binding.clearBtn.setOnClickListener { clearTrack() }
        binding.showTrackProgressBtn.setOnClickListener { showTrackProgress() }
    }

    private fun loadRecord() {
        points = Tracking.load().map { it.toLatLng() }.toMutableList()

        val bounds = LatLngBounds(points[0], points[points.size - 2])
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50.dp2px(this)))

        showSnackbar("Load record complete")
    }

    private fun showTrack() {
        ensureRecordLoaded()

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

        val movingMarker = createMovingMarker(-90f)

        val drivePoint = points[0]
        val (first) = SpatialRelationUtil.calShortestDistancePoint(points, drivePoint)
        points[first] = drivePoint
        val subList: List<LatLng> = points.subList(first, points.size)

        movingMarker.setPoints(subList)
        movingMarker.setTotalDuration(40)
        movingMarker.startSmoothMove()
    }

    private fun clearTrack() {
        progressiveTrackScope.coroutineContext.cancelChildren()
        aMap.clear()
    }

    private fun showTrackProgress() {
        ensureRecordLoaded()

        val pointDelay: Long = 1

        val movingMarker = createMovingMarker(-90f)
        val polyOptions = PolylineOptions().width(10f).color(Color.CYAN)
        val polyline = aMap.addPolyline(polyOptions)

        progressiveTrackScope.launch {
            points.forEachIndexed { index, point ->
                if (index == 0) {
                    aMap.animateCamera(CameraUpdateFactory.newLatLng(point))
                } else {
                    aMap.moveCamera(CameraUpdateFactory.newLatLng(point))
                }

                val previousPoint = if (index == 0) null else points[index - 1]

                polyOptions.add(point)
                polyline.points = polyOptions.points

                movingMarker.setPoints(mutableListOf(previousPoint, point))
                previousPoint?.let {
                    movingMarker.setRotate(calculateAngle(previousPoint, point).toFloat())
                }
                movingMarker.setTotalDuration(pointDelay.toInt())
                movingMarker.startSmoothMove()

                delay(pointDelay)
            }
        }
    }

    private fun createMovingMarker(rotation: Float = 0f): MovingPointOverlay {
        val marker = aMap.addMarker(
            MarkerOptions()
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(resources, R.drawable.ic_car_hatchback_blue)
                            .rotate(rotation)
                    )
                )
                .anchor(0.5f, 0.5f)
        )
        return MovingPointOverlay(aMap, marker)
    }

    private fun calculateAngle(from: LatLng, to: LatLng): Double {
        val diffX = to.longitude - from.longitude
        val diffY = to.latitude - from.latitude
        return 360 * atan2(diffY, diffX) / (2 * Math.PI) + 90
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
