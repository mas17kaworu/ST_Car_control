package com.longkai.stcarcontrol.st_exp.tracking


import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.util.component1
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.amap.api.location.AMapLocationClient
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.maps.utils.SpatialRelationUtil
import com.amap.api.maps.utils.overlay.MovingPointOverlay
import com.google.android.material.snackbar.Snackbar
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.Utils.dp2px
import com.longkai.stcarcontrol.st_exp.Utils.getBitmapFromVectorDrawable
import com.longkai.stcarcontrol.st_exp.Utils.rotate
import com.longkai.stcarcontrol.st_exp.databinding.FragmentTrackingBinding
import com.longkai.stcarcontrol.st_exp.databinding.ViewMarkerInfoBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.atan2


class TrackingActivity : ComponentActivity() {

    private val viewModel: TrackingViewModel by viewModels()

    private lateinit var binding: FragmentTrackingBinding
    private val mapView get() = binding.mapView
    private val aMap get() = mapView.map

    private var trackPoints: List<TrackingData> = emptyList()
    private var mapPoints: List<LatLng> = emptyList()
    private val progressiveTrackScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Tracking.init(this)

        binding = FragmentTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this, true);

//        MapsInitializer.setTerrainEnable(true)
        mapView.onCreate(savedInstanceState)
        aMap.mapType = AMap.MAP_TYPE_SATELLITE

        initUI()
        initHistoryRecords()
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

    private fun initHistoryRecords() {
        fun hideHistoryRecordsLayout() {
            binding.historyRecordsLayout.isVisible = false
        }

        binding.historyRecordsBtn.setOnClickListener {
            lifecycleScope.launch {
                val historyRecords = viewModel.loadHistoryRecords()
                binding.historyRecords.adapter = HistoryRecordsAdapter(historyRecords) {
                    viewModel.enterReviewMode()
                    loadRecord(it)
                    hideHistoryRecordsLayout()
                }
                binding.historyRecordsLayout.apply { isVisible = isVisible.not() }
            }
        }
    }


    private fun initUI() {
        aMap.animateCamera(CameraUpdateFactory.zoomTo(10f))

//        binding.moveCarBtn.setOnClickListener { moveCar() }
//        binding.clearBtn.setOnClickListener { clearTrack() }
//        binding.showTrackProgressBtn.setOnClickListener { showTrackProgress() }

        fun updateRecordBtnUI(isRecording: Boolean) {
            binding.recordBtn.text = if (isRecording) "Stop Recording" else "Start Recording"
            val tintColor =
                if (isRecording) android.R.color.holo_red_light else android.R.color.holo_green_dark
            binding.recordBtn.compoundDrawableTintList = ColorStateList.valueOf(getColor(tintColor))
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { uiState ->
                    binding.apply {
                        reviewModeBtns.isInvisible = uiState.inReviewMode.not()
                        recordBtn.isInvisible = uiState.inReviewMode
                        updateRecordBtnUI(uiState.isRecording)
                        trackPointInfo.isInvisible = uiState.inReviewMode.not()

                        signalReal.isSelected = uiState.signalType == SignalType.Real
                        signalPbox.isSelected = uiState.signalType == SignalType.PBox
                        signal3.isSelected = uiState.signalType == SignalType.Other3
                        signal4.isSelected = uiState.signalType == SignalType.Other4
                    }
                }
            }
        }

        binding.signalReal.setOnClickListener { viewModel.changeSignal(SignalType.Real) }
        binding.signalPbox.setOnClickListener { viewModel.changeSignal(SignalType.PBox) }
        binding.signal3.setOnClickListener { viewModel.changeSignal(SignalType.Other3) }
        binding.signal4.setOnClickListener { viewModel.changeSignal(SignalType.Other4) }

        binding.replayBtn.setOnClickListener { replayTrack() }
        binding.exitReviewBtn.setOnClickListener {
            clearTrack()
            viewModel.exitReviewMode()
        }
        binding.recordBtn.setOnClickListener {
            if (viewModel.uiState.value.isRecording) {
                viewModel.stopRecording { fileName ->
                    if (fileName != null) {
                        showSnackbar("Recording successfully saved to $fileName")
                    } else {
                        showSnackbar("No data saved")
                    }
                }
            } else {
                viewModel.startRecording()
            }
        }

        aMap.setInfoWindowAdapter(object : AMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker?): View? {
                return marker?.let {
                    val markerBinding = ViewMarkerInfoBinding.inflate(layoutInflater)
                    markerBinding.title.text = marker.title
                    markerBinding.details.text = marker.snippet
                    markerBinding.root
                }
            }

            override fun getInfoContents(p0: Marker?): View? {
                return null
            }
        })

        aMap.setOnMarkerClickListener { marker ->
            if (marker.isInfoWindowShown) {
                marker.hideInfoWindow()
            } else {
                marker.showInfoWindow()
            }
            return@setOnMarkerClickListener true
        }

        aMap.setOnInfoWindowClickListener { marker ->
            marker.hideInfoWindow()
        }
    }

    private fun loadRecord(historyRecord: HistoryRecord) {
        lifecycleScope.launch {
            trackPoints = viewModel.loadRecord(historyRecord)
            mapPoints = trackPoints.map { it.toLatLng() }
            if (mapPoints.isEmpty()) {
                showSnackbar("Load record error!")
            } else {
                val bounds = LatLngBounds(mapPoints[0], mapPoints[mapPoints.size - 2])
                aMap.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        bounds,
                        50.dp2px(this@TrackingActivity)
                    )
                )

                showTrack()
                binding.reviewModeBtns.isVisible = true
            }
        }
    }

    private fun showTrack() {
        ensureRecordLoaded()

        val firstPoint = trackPoints.first()
        val lastPoint = trackPoints.last()
        aMap.addMarker(
            MarkerOptions()
                .position(mapPoints[0])
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        getBitmapFromVectorDrawable(R.drawable.ic_tracking, R.color.colorWhite)
                    )
                )
                .title(firstPoint.formatTime())
                .snippet("${firstPoint.formatLat()} ${firstPoint.formatLng()}")
        )
        aMap.addMarker(
            MarkerOptions()
                .position(mapPoints.last())
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        getBitmapFromVectorDrawable(R.drawable.ic_tracking, R.color.colorWhite)
                    )
                )
                .title(lastPoint.formatTime())
                .snippet("${lastPoint.formatLat()} ${lastPoint.formatLng()}")
        )
        updateTrackPointInfo(trackPoints[0])
        aMap.addPolyline(PolylineOptions().addAll(mapPoints).width(10f).color(Color.CYAN))
    }

    private fun moveCar() {
        ensureRecordLoaded()

        hideTrackPointInfo()

        val movingMarker = createMovingMarker(-90f)

        val mutablePoints = mapPoints.toMutableList()
        val drivePoint = mutablePoints[0]
        val (first) = SpatialRelationUtil.calShortestDistancePoint(mutablePoints, drivePoint)
        mutablePoints[first] = drivePoint
        val subList: List<LatLng> = mutablePoints.subList(first, mutablePoints.size)

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
            mapPoints.forEachIndexed { index, point ->
                if (index == 0) {
                    aMap.animateCamera(CameraUpdateFactory.newLatLng(point))
                } else {
                    aMap.moveCamera(CameraUpdateFactory.newLatLng(point))
                }

                polyOptions.add(point)
                polyline.points = polyOptions.points
                updateTrackPointInfo(trackPoints[index])

                val previousPoint = if (index == 0) null else mapPoints[index - 1]
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

    private fun replayTrack() {
        ensureRecordLoaded()

        val pointDelay: Long = 1

        val movingMarker = createMovingMarker(-90f)
        val polyOptions = PolylineOptions().width(10f).color(Color.CYAN)
        val polyline = aMap.addPolyline(polyOptions)

        progressiveTrackScope.launch {
            mapPoints.forEachIndexed { index, point ->
                updateTrackPointInfo(trackPoints[index])

                val previousPoint = if (index == 0) null else mapPoints[index - 1]
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

    private fun updateTrackPointInfo(trackingData: TrackingData) {
        binding.trackPointInfo.visibility = View.VISIBLE
        binding.trackPointInfo.setData(trackingData)
    }

    private fun hideTrackPointInfo() {
        binding.trackPointInfo.visibility = View.INVISIBLE
    }

    private fun createMovingMarker(rotation: Float = 0f): MovingPointOverlay {
        val marker = aMap.addMarker(
            MarkerOptions()
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        getBitmapFromVectorDrawable(R.drawable.ic_car_hatchback_blue)
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
        if (mapPoints.isNullOrEmpty()) {
            showSnackbar("Record is empty, load record first.")
        }
    }

    private fun showSnackbar(msg: String) {
        Snackbar.make(binding.root, msg, Toast.LENGTH_SHORT).show()
    }
}

const val mockPoint = true
fun TrackingData.toLatLng() =
    if (mockPoint) LatLng(latitude + 7, longitude + 94) else LatLng(latitude, longitude)
