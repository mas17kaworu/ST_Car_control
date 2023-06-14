package com.longkai.stcarcontrol.st_exp.pbox

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
import com.amap.api.maps.CoordinateConverter
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
    private val coordinateConverter =
        CoordinateConverter(context).apply { from(CoordinateConverter.CoordType.GPS) }
    private val mainScope = CoroutineScope(Dispatchers.Main)

    private var historyRecordData: HistoryRecordData? = null
    private var trackSettings: TrackSettings = TrackSettings()
    private var realTrackPolyline: Polyline? = null
    private var pboxTrackPolyline: Polyline? = null

    //For replay and recording
    private var playingRealLine: Polyline? = null
    private var playingPboxLine: Polyline? = null
    private var playingCar: MovingPointOverlay? = null
    //For replay
    private var replayRealIndex: Int = 0
    private var replayPboxIndex: Int = 0
    private var replayPaused = false
    private var isReplaying = false

    //For recording
    private var recordingData: RecordingData = RecordingData()

    data class RecordingData(
        val pboxTrackPoints: MutableList<TrackingData> = mutableListOf(),
        val realTrackPoints: MutableList<TrackingData> = mutableListOf(),
        val pboxMapPoints: MutableList<LatLng> = mutableListOf(),
        val realMapPoints: MutableList<LatLng> = mutableListOf()
    )

    fun init() {
        aMap.mapType = AMap.MAP_TYPE_SATELLITE
        aMap.animateCamera(CameraUpdateFactory.zoomTo(10f))
    }

    fun setHistoryRecordData(historyRecordData: HistoryRecordData) {
        this.historyRecordData = historyRecordData
        historyRecordData.calculateErrors()
        clearAllTracks()
    }

    fun setConfig(trackSettings: TrackSettings) {
        if (this.trackSettings.labelInterval != trackSettings.labelInterval) {
            clearAllTracks()
        }
        this.trackSettings = trackSettings
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

    fun showTracks() {
        clearAllTracks()
        ensureRecordLoaded {
            if (!trackSettings.hideRealTrackUI) {
                realTrackPolyline = showTrack(it.realPoints, REAL_TRACK_COLOR)
            }
            pboxTrackPolyline = showTrack(it.pboxPoints, PBOX_TRACK_COLOR, updateBounds = true)

            addPoints(it.pboxPoints)

            if (trackSettings.labelInterval > 0) {
                addLabelMarkers(it.pboxPoints, trackSettings.labelInterval)
                updateTrackPointInfo(it.pboxPoints[0])
            }
        }
    }

    fun switchRealTrackVisibility(showRealTrack: Boolean) {
        realTrackPolyline?.let { it.isVisible = showRealTrack }
    }

    fun switchPboxTrackVisibility(showPboxTrack: Boolean) {
        pboxTrackPolyline?.let { it.isVisible = showPboxTrack }
    }

    private fun showTrack(
        trackPoints: List<TrackingData>,
        @ColorRes trackColor: Int = PBOX_TRACK_COLOR,
        updateBounds: Boolean = false
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

        return aMap.addPolyline(
            PolylineOptions().addAll(mapPoints).width(LINE_WIDTH)
                .color(context.getColor(trackColor))
        )
    }

    private fun addPoints(trackPoints: List<TrackingData>) {
        trackPoints.forEachIndexed { index, trackingData ->
            addMarker(index, trackingData, R.drawable.ic_tracking_point, R.color.colorWhite)
        }
    }

    private fun addLabelMarkers(trackPoints: List<TrackingData>, labelInterval: Int) {
        var lastPoint: TrackingData? = null
        trackPoints.forEachIndexed { index, trackingData ->
            val timeDiff = lastPoint?.let { trackingData.timeDiff(it) } ?: Int.MAX_VALUE
            if (index == 0 || index == trackPoints.size - 1 || timeDiff >= labelInterval) {
                addLabelMarker(trackingData)
                lastPoint = trackingData
            }
        }
    }

    private val colorDots = listOf(
        R.color.colorRed,
        R.color.colorBlue,
        R.color.colorGreen,
        R.color.colorYellow,
        R.color.colorCyan,
        R.color.transparent
    )
        .map { colorRes ->
            BitmapDescriptorFactory.fromBitmap(
                context.getBitmapFromVectorDrawable(
                    R.drawable.ic_tracking_point,
                    colorRes
                )
            )
        }

    private fun addMarker(
        pointIndex: Int,
        trackingData: TrackingData,
        @DrawableRes drawableResId: Int = R.drawable.ic_tracking_point,
        @ColorRes colorResId: Int = R.color.colorWhite
    ) {
        val (colorDot, text) = when (trackingData.gpsStatus) {
            1 -> Pair(colorDots[0], "S")
            2 -> Pair(colorDots[1], "D")
            4 -> Pair(colorDots[2], "F")
            5 -> Pair(colorDots[3], "FL")
            6 -> Pair(colorDots[4], "I")
            else -> Pair(colorDots[5], "")
        }

        aMap.addMarker(
            MarkerOptions()
                .position(trackingData.toLatLng())
                .icon(colorDot)
                .title("POINT $pointIndex")
                .snippet("TIME: ${trackingData.formatTime()}\nSPEED: ${trackingData.formatSpeed()}\nLLA: ${trackingData.formatLocation()}\nERROR: ${trackingData.formatError()}")
        )
    }

    private fun addLabelMarker(
        trackingData: TrackingData,
        @DrawableRes drawableResId: Int = R.drawable.ic_tracking,
        @ColorRes colorResId: Int = R.color.colorWhite
    ) {
        val (backgroundColor, text) = when (trackingData.gpsStatus) {
            1 -> Pair(R.color.colorRed, "S")
            2 -> Pair(R.color.colorBlue, "D")
            4 -> Pair(R.color.colorGreen, "F")
            5 -> Pair(R.color.colorYellow, "FL")
            6 -> Pair(R.color.colorCyan, "I")
            else -> Pair(R.color.transparent, "")
        }
        val fontColor = R.color.colorBlack
        if (text.isNotBlank()) {
            aMap.addText(
                TextOptions()
                    .position(trackingData.toLatLng())
                    .text("${text}${trackingData.formatErrorWithoutUnit()}")
                    .fontColor(context.getColor(fontColor))
            )
        }
    }

    fun pauseReplay() {
        replayPaused = true
    }

    fun continueReplay() {
        if (replayPaused) {
            replayPaused = false
            replayTrack(restart = false)
        }
    }

    fun exitReplay() {
        replayPaused = false
        clearAllTracks()
    }

    fun clearReplay() {
        mainScope.launch {
            replayPaused = true
            while (isReplaying) {
                delay(10)
            }
            clearReplayedTracks()
            continueReplay()
        }
    }

    fun replayTrack(restart: Boolean = true) {
        if (restart) {
            clearAllTracks()
        }
        ensureRecordLoaded { historyRecordData ->
            // Add real track
            val realTrackPoints = historyRecordData.realPoints
            val realMapPoints = realTrackPoints.map { it.toLatLng() }
            val realPolyline = playingRealLine ?: aMap.addPolyline(
                PolylineOptions().width(LINE_WIDTH).color(context.getColor(REAL_TRACK_COLOR))
            )

            // Add pbox track
            val pboxTrackPoints = historyRecordData.pboxPoints
            val pboxMapPoints = pboxTrackPoints.map { it.toLatLng() }
            val pboxPolyline = playingPboxLine ?: aMap.addPolyline(
                PolylineOptions().width(LINE_WIDTH).color(context.getColor(PBOX_TRACK_COLOR))
            )
            val pboxCar = playingCar ?: createMovingMarker(90f)

            var realIndex = replayRealIndex
            var pboxIndex = replayPboxIndex

            fun saveStateWhenPaused() {
                playingRealLine = realPolyline
                playingPboxLine = pboxPolyline
                playingCar = pboxCar
                replayRealIndex = realIndex
                replayPboxIndex = pboxIndex
                isReplaying = false
            }

            mainScope.launch {
                isReplaying = true
                val pointDelay: Long = ONE_SECOND / trackSettings.replaySpeed

                // Add real track points which are before pbox start
                while (realIndex < realTrackPoints.size && realTrackPoints[realIndex].isEarlyThan(
                        pboxTrackPoints[0]
                    )
                ) {
                    if (replayPaused) {
                        saveStateWhenPaused()
                        break
                    }
                    realPolyline.options = realPolyline.options.add(realMapPoints[realIndex])
                    realIndex++
                    delay(pointDelay)
                }

                var lastPoint: TrackingData? = null
                while (pboxIndex < pboxTrackPoints.size) {
                    if (replayPaused) {
                        saveStateWhenPaused()
                        break
                    }

                    val pboxPoint = pboxMapPoints[pboxIndex]
                    val pboxTrackPoint = pboxTrackPoints[pboxIndex]
                    updateTrackPointInfo(pboxTrackPoints[pboxIndex])

                    // Move camera
                    if (trackSettings.replayCameraFollowCar) {
                        if (pboxIndex == 0) {
                            aMap.animateCamera(CameraUpdateFactory.newLatLng(pboxPoint))
                        } else {
                            aMap.moveCamera(CameraUpdateFactory.newLatLng(pboxPoint))
                        }
                    }
                    // Move car forward
                    val previousPoint =
                        if (pboxCar.position == null) null else pboxMapPoints[pboxIndex - 1]
                    pboxCar.setPoints(mutableListOf(previousPoint, pboxPoint))
//                    previousPoint?.let {
//                        val angle = calculateAngle(previousPoint, pboxPoint).toFloat()
//                        pboxCar.setRotate(360 - angle)
//                    }
                    pboxTrackPoint.direction?.let {
                        pboxCar.setRotate(it.toFloat() - 90)
                    }
                    pboxCar.setTotalDuration(pointDelay.toInt())
                    pboxCar.startSmoothMove()

                    // Move forward pbox track
                    pboxPolyline.options = pboxPolyline.options.add(pboxPoint)
                    // Move forward real track
                    while (realIndex < realTrackPoints.size && realTrackPoints[realIndex].isEarlyOrEqual(
                            pboxTrackPoint
                        )
                    ) {
                        realPolyline.options = realPolyline.options.add(realMapPoints[realIndex])
                        realIndex++
                    }

                    addMarker(
                        pboxIndex,
                        pboxTrackPoint,
                        R.drawable.ic_tracking_point,
                        R.color.colorWhite
                    )

                    // Add label markers on pbox track
                    if (trackSettings.labelInterval > 0) {
                        val timeDiff =
                            lastPoint?.let { pboxTrackPoint.timeDiff(it) } ?: Int.MAX_VALUE
                        if (pboxIndex == 0 || pboxIndex == pboxTrackPoints.size - 1 || timeDiff >= trackSettings.labelInterval) {
                            addLabelMarker(pboxTrackPoint)
                            lastPoint = pboxTrackPoint
                        }
                    }

                    pboxIndex++
                    delay(pointDelay)
                }
                // Add real track points which are after pbox end
                while (realIndex < realTrackPoints.size) {
                    if (replayPaused) {
                        saveStateWhenPaused()
                        break
                    }
                    realPolyline.options = realPolyline.options.add(realMapPoints[realIndex])
                    realIndex++
                    delay(pointDelay)
                }
            }
        }
    }

    fun updateRecording(recordingPoint: RecordingPoint) {
        recordingPoint.pboxPoint?.let {
            recordingData.addPboxPoint(it, it.toLatLng())
            updateRecordingForPBox(recordingData.pboxTrackPoints, recordingData.pboxMapPoints)
        }
        recordingPoint.realPoint?.let {
            recordingData.addRealPoint(it, it.toLatLng())
            updateRecordingForReal(recordingData.realTrackPoints, recordingData.realMapPoints)
        }
    }

    private fun updateRecordingForPBox(
        pboxTrackPoints: List<TrackingData>,
        pboxMapPoints: List<LatLng>
    ) {
        val isFirstPoint = pboxTrackPoints.size == 1

        val pboxTrackPoint = pboxTrackPoints.last()
        val pboxMapPoint = pboxMapPoints.last()

        val bounds = calcBounds(pboxMapPoints)
        aMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                50.dp2px(context)
            )
        )

        val pboxPolyline = playingPboxLine ?: aMap.addPolyline(
            PolylineOptions().width(LINE_WIDTH).color(context.getColor(PBOX_TRACK_COLOR))
        ).also { playingPboxLine = it }
        val pboxCar = playingCar ?: createMovingMarker(90f).also { playingCar = it }

        updateTrackPointInfo(pboxTrackPoint)

        // Move camera
        if (isFirstPoint) {
            aMap.animateCamera(CameraUpdateFactory.newLatLng(pboxMapPoint))
        } else {
            aMap.moveCamera(CameraUpdateFactory.newLatLng(pboxMapPoint))
        }

        // Move car forward
        val previousPoint = if (pboxCar.position == null) null else pboxMapPoints[pboxMapPoints.size - 2]
        pboxCar.setPoints(mutableListOf(previousPoint, pboxMapPoint))
        pboxTrackPoint.direction?.let {
            pboxCar.setRotate(it.toFloat() - 90)
        }
        pboxCar.setTotalDuration(10)
        pboxCar.startSmoothMove()

        // Move forward pbox track
        pboxMapPoint.let {
            pboxPolyline.options = pboxPolyline.options.add(pboxMapPoint)
        }

        pboxTrackPoint.let {
            addMarker(
                pboxTrackPoints.size,
                pboxTrackPoint,
                R.drawable.ic_tracking_point,
                R.color.colorWhite
            )
            // Add label markers on pbox track
            if (trackSettings.labelInterval > 0) {
                if (isFirstPoint || pboxTrackPoints.size.mod(trackSettings.labelInterval) == 0) {
                    addLabelMarker(pboxTrackPoint)
                }
            }
        }
    }

    private fun updateRecordingForReal(
        realTrackPoints: List<TrackingData>,
        realMapPoints: List<LatLng>
    ) {
        val realMapPoint = realMapPoints.last()

        val realPolyline = playingRealLine ?: aMap.addPolyline(
            PolylineOptions().width(LINE_WIDTH).color(context.getColor(REAL_TRACK_COLOR))
        ).also { playingRealLine = it }

        // Move forward real track
        realPolyline.options = realPolyline.options.add(realMapPoint)
    }

    private fun clearReplayedTracks() {
        mainScope.coroutineContext.cancelChildren()
        aMap.clear()
        realTrackPolyline = null
        pboxTrackPolyline = null
        playingRealLine = null
        playingPboxLine = null
        playingCar = null
    }

    fun clearAllTracks() {
        mainScope.coroutineContext.cancelChildren()
        aMap.clear()
        realTrackPolyline = null
        pboxTrackPolyline = null
        playingRealLine = null
        playingPboxLine = null
        playingCar = null
        replayRealIndex = 0
        replayPboxIndex = 0
        replayPaused = false
    }

    /**
     * Animate car moving on track
     */
    private fun moveCar(trackPoints: List<TrackingData>) {
        val movingMarker = createMovingMarker()

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

        val movingMarker = createMovingMarker()
        val polyOptions = PolylineOptions().width(LINE_WIDTH).color(Color.CYAN)
        val polyline = aMap.addPolyline(polyOptions)

        val pointDelay: Long = 1
        mainScope.launch {
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
        val carWidth = dp2px(context, 30f)
        val carHeight = dp2px(context, 50f)
        val marker = aMap.addMarker(
            MarkerOptions()
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        context.getBitmapFromVectorDrawable(
                            CAR_ICON,
                            CAR_COLOR,
                            carWidth,
                            carHeight
                        )
                            .rotate(rotation)
                    )
                )
                .anchor(0.5f, 0.5f)
                .zIndex(100f)
        )
        return MovingPointOverlay(aMap, marker)
    }

    private fun calculateAngle(from: LatLng, to: LatLng): Double {
        val diffX = to.longitude - from.longitude
        val diffY = to.latitude - from.latitude

        return atan2(diffY, diffX) * 180 / Math.PI
//        var angle = atan2(abs(diffY), abs(diffX))
//        if (diffX >= 0) {
//            if (diffY >= 0) {
//
//            } else {
//                angle = Math.PI - angle
//            }
//        } else {
//            if (diffY >= 0) {
//                angle = 2 * Math.PI - angle
//            } else {
//                angle = Math.PI + angle
//            }
//        }
//        return angle * 180.0 / Math.PI
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

    private fun ensureRecordLoaded(action: (HistoryRecordData) -> Unit) {
        historyRecordData?.let {
            action.invoke(it)
        } ?: showMessage("Record is empty, load record first.")
    }

    private fun TrackingData.toLatLng(): LatLng {
        coordinateConverter.coord(LatLng(latitude, longitude))
        return coordinateConverter.convert()
    }

    private fun HistoryRecordData.calculateErrors() {
        pboxPoints.forEachIndexed { index, pboxPoint ->
            val matchPointIndex = realPoints.findMatchPointIndex(pboxPoint, index)
            val error = matchPointIndex?.let {
                val matchPoint = realPoints[matchPointIndex]
                calculatePointError(pboxPoint.toLatLng(), matchPoint.toLatLng())
            }
            pboxPoint.error = error
        }
    }

    private fun calculatePointError(pboxMapPoint: LatLng, realMapPoint: LatLng): Float {
        return AMapUtils.calculateLineDistance(pboxMapPoint, realMapPoint)
    }

    private fun RecordingData.addPboxPoint(trackPoint: TrackingData, mapPoint: LatLng) {
        pboxTrackPoints.add(trackPoint)
        pboxMapPoints.add(mapPoint)
        calculateLastPointError()
    }

    private fun RecordingData.addRealPoint(trackPoint: TrackingData, mapPoint: LatLng) {
        realTrackPoints.add(trackPoint)
        realMapPoints.add(mapPoint)
        calculateLastPointError()
    }

    private fun RecordingData.calculateLastPointError() {
        val lastPboxPoint = pboxTrackPoints.lastOrNull()
        val lastRealPoint = realTrackPoints.lastOrNull()

        if (lastPboxPoint != null && lastRealPoint != null) {
            if (lastPboxPoint.isEarlyOrEqual(lastRealPoint)) {
                val matchPointIndex = realTrackPoints.findMatchPointIndex(lastPboxPoint, realTrackPoints.size)
                if (matchPointIndex != null) {
                    lastPboxPoint.error = calculatePointError(pboxMapPoints.last(), realMapPoints[matchPointIndex])
                }
            }
        }
    }

    private fun List<TrackingData>.findMatchPointIndex(point: TrackingData, startIndex: Int): Int? {
        if (startIndex < 0 || startIndex > this.size) return null
        var iterator = this.listIterator(startIndex)
        while(iterator.hasPrevious()) {
            val element = iterator.previous()
            if (element.isSameTime(point)) return iterator.nextIndex()
            if (element.isEarlyThan(point)) break
        }
        iterator = this.listIterator(startIndex)
        while(iterator.hasNext()) {
            val element = iterator.next()
            if (element.isSameTime(point)) return iterator.previousIndex()
            if (element.isLaterThan(point)) break
        }
        return null
    }

    companion object {
        private const val LINE_WIDTH = 4f
        private const val ONE_SECOND = 1000L //milliseconds
        private const val REAL_TRACK_COLOR = R.color.colorWhite
        private const val PBOX_TRACK_COLOR = R.color.colorMagenta
        private const val CAR_ICON = R.drawable.ic_car_top_view_white
        private const val CAR_COLOR = R.color.colorWhite
    }
}