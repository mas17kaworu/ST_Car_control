package com.longkai.stcarcontrol.st_exp.tracking

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.util.component1
import com.amap.api.maps.AMap
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.maps.utils.SpatialRelationUtil
import com.amap.api.maps.utils.overlay.MovingPointOverlay
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.Utils.dp2px
import com.longkai.stcarcontrol.st_exp.Utils.getBitmapFromVectorDrawable
import com.longkai.stcarcontrol.st_exp.Utils.rotate
import com.longkai.stcarcontrol.st_exp.databinding.ViewMarkerInfoBinding
import kotlinx.coroutines.*
import kotlin.math.atan2

class AMapHelper(
    val context: Context,
    val aMap: AMap,
    val updateTrackPointInfo: (TrackingData) -> Unit,
    val showMessage: (String) -> Unit
) {
    private val replayTrackScope = CoroutineScope(Dispatchers.Main)

    private var historyRecordData: HistoryRecordData? = null
    private var realTrackPolyline: Polyline? = null
    private var pboxTrackPolyline: Polyline? = null

    fun init(){
        aMap.mapType = AMap.MAP_TYPE_SATELLITE
        aMap.animateCamera(CameraUpdateFactory.zoomTo(10f))
    }

    fun setHistoryRecordData(historyRecordData: HistoryRecordData) {
        aMap.clear()
        this.historyRecordData = historyRecordData
        realTrackPolyline = null
        pboxTrackPolyline = null
    }

    fun setupInfoWindow() {
        aMap.setInfoWindowAdapter(object : AMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker?): View? {
                return marker?.let {
                    val markerBinding = ViewMarkerInfoBinding.inflate(LayoutInflater.from(context))
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

    fun showTracks(showRealTrack: Boolean, showPboxTrack: Boolean) {
        ensureRecordLoaded() {
            if (showRealTrack) {
                if (realTrackPolyline == null) {
                    realTrackPolyline = showTrack(it.realPoints, R.color.colorCyan)
                }
            } else {
                realTrackPolyline?.let { it.isVisible = false }
            }
            if (showPboxTrack) {
                if (pboxTrackPolyline == null) {
                    showTrack(it.pboxPoints, R.color.colorBlue, updateBounds = true, showPoints = true, showLabelMarkers = true)
                }
            } else {
                pboxTrackPolyline?.let { it.isVisible = false }
            }
        }
    }

    private fun showTrack(
        trackPoints: List<TrackingData>,
        @ColorRes trackColor: Int = R.color.colorCyan,
        updateBounds: Boolean = false,
        showPoints: Boolean = false,
        showLabelMarkers: Boolean = false
    ): Polyline {
        val mapPoints = trackPoints.map { it.toLatLng() }

        if (updateBounds) {
            val bounds = calcBounds(mapPoints)
            aMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    50.dp2px(context)
                )
            )
        }

        val polyline = aMap.addPolyline(PolylineOptions().addAll(mapPoints).width(10f).color(context.getColor(trackColor)))

        if (showPoints) {
            addPoints(trackPoints)
        }

        if (showLabelMarkers) {
            addLabelMarkers(trackPoints)
            updateTrackPointInfo(trackPoints[0])
        }

        return polyline
    }

    private fun addPoints(trackPoints: List<TrackingData>) {
        trackPoints.forEachIndexed { index, trackingData ->
            addMarker(trackingData, R.drawable.ic_tracking_point, R.color.colorWhite)
        }
    }

    private fun addLabelMarkers(trackPoints: List<TrackingData>) {
        var lastPoint: TrackingData? = null
        trackPoints.forEachIndexed { index, trackingData ->
            val timeDiff = lastPoint?.let { trackingData.timeDiff(it) } ?: Int.MAX_VALUE
            if (index == 0 || index == trackPoints.size - 1 || timeDiff >= MARKER_INTERVAL) {
                addLabelMarker(trackingData)
                lastPoint = trackingData
            }
        }
    }

    private fun addMarker(
        trackingData: TrackingData,
        @DrawableRes drawableResId: Int = R.drawable.ic_tracking,
        @ColorRes colorResId: Int = R.color.colorWhite
    ) {
        aMap.addMarker(
            MarkerOptions()
                .position(trackingData.toLatLng())
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        context.getBitmapFromVectorDrawable(drawableResId, colorResId)
                    )
                )
                .title(trackingData.formatTime())
                .snippet("${trackingData.calcDistanceError()?.formatDistance()}, ${trackingData.gpsStatus}")
        )
    }

    private fun addLabelMarker(
        trackingData: TrackingData,
        @DrawableRes drawableResId: Int = R.drawable.ic_tracking,
        @ColorRes colorResId: Int = R.color.colorWhite
    ) {
        val distanceError = trackingData.calcDistanceError()
        aMap.addText(
            TextOptions()
                .position(trackingData.toLatLng())
                .text("${distanceError?.formatDistance()}, ${trackingData.gpsStatus}")
        )
    }

    fun replayTrack() {
        ensureRecordLoaded {
            val trackPoints = it.pboxPoints
            val mapPoints = trackPoints.map { it.toLatLng() }

            val pointDelay: Long = 1
            val movingMarker = createMovingMarker(-90f)
            val polyOptions = PolylineOptions().width(10f).color(Color.CYAN)
            val polyline = aMap.addPolyline(polyOptions)

            replayTrackScope.launch {
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
    }

    fun clearTrack() {
        replayTrackScope.coroutineContext.cancelChildren()
        aMap.clear()
    }

    /**
     * Animate car moving on track
     */
    private fun moveCar(trackPoints: List<TrackingData>) {
        val movingMarker = createMovingMarker(-90f)

        val mapPoints = trackPoints.map { it.toLatLng() }
        val mutablePoints = mapPoints.toMutableList()
        val drivePoint = mutablePoints[0]
        val (first) = SpatialRelationUtil.calShortestDistancePoint(mutablePoints, drivePoint)
        mutablePoints[first] = drivePoint
        val subList: List<LatLng> = mutablePoints.subList(first, mutablePoints.size)

        movingMarker.setPoints(subList)
        movingMarker.setTotalDuration(40)
        movingMarker.startSmoothMove()
    }

    /**
     * Update car track by stepping points
     */
    private fun showTrackProgress(trackPoints: List<TrackingData>) {
        val mapPoints = trackPoints.map { it.toLatLng() }

        val movingMarker = createMovingMarker(-90f)
        val polyOptions = PolylineOptions().width(10f).color(Color.CYAN)
        val polyline = aMap.addPolyline(polyOptions)

        val pointDelay: Long = 1
        replayTrackScope.launch {
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

    private fun createMovingMarker(rotation: Float = 0f): MovingPointOverlay {
        val marker = aMap.addMarker(
            MarkerOptions()
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        context.getBitmapFromVectorDrawable(R.drawable.ic_car_hatchback_blue)
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

    private fun calcBounds(mapPoints: List<LatLng>): LatLngBounds {
        var latLow: Double = mapPoints.first().latitude
        var latHigh: Double = mapPoints.last().latitude
        var longLow: Double = mapPoints.first().longitude
        var longHigh: Double = mapPoints.last().longitude
        mapPoints.forEach {
            if (it.latitude < latLow) latLow = it.latitude
            if (it.latitude > latHigh) latHigh = it.latitude
            if (it.longitude < longLow) longLow = it.longitude
            if (it.longitude > longHigh) longHigh = it.longitude
        }
        return LatLngBounds(LatLng(latLow, longLow), LatLng(latHigh, longHigh))
    }

    private fun TrackingData.calcDistanceError(): Float? {
        return ensureRecordLoaded<Float> {
            val refPoints = it.realPoints
            val matchPoint = refPoints.firstOrNull {
                this.isSameTime(it)
            }
            matchPoint?.let {
                AMapUtils.calculateLineDistance(this.toLatLng(), it.toLatLng())
            }
        }
    }

    private fun ensureRecordLoaded(action: (HistoryRecordData) -> Unit) {
        historyRecordData?.let {
            action.invoke(it)
        } ?: showMessage("Record is empty, load record first.")
    }

    private fun <T> ensureRecordLoaded(action: (HistoryRecordData) -> T?): T? {
        return historyRecordData?.let {
            action.invoke(it)
        }
    }

    private fun Float.formatDistance() = String.format("%.2f", this) + "m"

    companion object {
        private const val MARKER_INTERVAL = 10 // in seconds
    }
}

const val mockPoint = true
fun TrackingData.toLatLng() =
    if (mockPoint) LatLng(latitude + 7, longitude + 94) else LatLng(latitude, longitude)
