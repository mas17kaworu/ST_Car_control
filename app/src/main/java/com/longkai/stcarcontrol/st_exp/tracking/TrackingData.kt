package com.longkai.stcarcontrol.st_exp.tracking

import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.abs

data class RmcData(
    val utcTime: LocalTime,
    val latitude: Double,
    val longitude: Double,
    val velocity: Double? = null,
    val course: Double? = null,
    val utcDate: LocalDate? = null
)

data class GgaData(
    val utcTime: LocalTime,
    val latitude: Double,
    val longitude: Double,
    val gpsStatus: Int? = null,
    val satelliteNumber: Int? = null,
    val hdop: Double? = null,
    val altitude: Double? = null,
    val geoidHeight: Double? = null
)

class TrackingData(
    val utcTime: LocalTime,
    val latitude: Double,
    val longitude: Double,
    val velocity: Double? = null,
    val course: Double? = null,
    val utcDate: LocalDate? = null,
    val gpsStatus: Int? = null,
    val satelliteNumber: Int? = null,
    val hdop: Double? = null,
    val altitude: Double? = null,
    val geoidHeight: Double? = null
) {
    fun formatTime() = utcTime.toString()

    fun formatDateTime(): String {
        return "${utcDate.toString()} $utcTime"
    }

    fun formatLocation(): String {
        val latStr = String.format("%.6f N", latitude)
        val lngStr = String.format("%.6f E", longitude)
        val altStr = String.format("%.6f m", altitude)
        return "$latStr, $lngStr, $altStr"
    }

    fun isSameTime(other: TrackingData): Boolean {
        return utcTime == other.utcTime
    }

    /**
     * Time difference in seconds
     */
    fun timeDiff(otherPoint: TrackingData): Int {
        return abs(utcTime.toSecondOfDay() - otherPoint.utcTime.toSecondOfDay())
    }
}

enum class LatitudeHemisphere(val key: String) {
    North("N"),
    South("S");

    companion object {
        fun from(key: String): LatitudeHemisphere {
            return values().first { it.key == key }
        }
    }
}

enum class LongitudeHemisphere(val key: String) {
    East("E"),
    West("W");

    companion object {
        fun from(key: String): LongitudeHemisphere {
            return values().first { it.key == key }
        }
    }
}