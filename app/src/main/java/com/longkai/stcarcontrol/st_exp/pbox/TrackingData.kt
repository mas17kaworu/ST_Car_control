package com.longkai.stcarcontrol.st_exp.pbox

import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.abs

data class RmcData(
    val utcTime: LocalTime,
    val latitude: Double,
    val longitude: Double,
    val speed: Double? = null,
    val direction: Double? = null,
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

data class AlarmData(
    val utcTime: LocalTime,
    val alarmMask: Int?,
    val ppsAlarm: Int?,
    val antennaAlarm: Int?,
    val wbiAlarm: Int?,
    val nbiAlarm: Int?,
    val spoofingAlarm: Int?
)

class TrackingData(
    val utcTime: LocalTime,
    val latitude: Double,
    val longitude: Double,
    val speed: Double? = null,
    val direction: Double? = null,
    val utcDate: LocalDate? = null,
    val gpsStatus: Int? = null,
    val satelliteNumber: Int? = null,
    val hdop: Double? = null,
    val altitude: Double? = null,
    val geoidHeight: Double? = null
) {
    var error: Float? = null

    fun formatTime() = utcTime.toString()

    fun formatDateTime(): String {
        return "${utcDate.toString()} $utcTime"
    }

    fun formatSpeed(): String {
        return speed?.let { String.format("%.2f m/s", it) } ?: ""
    }

    fun formatLocation(): String {
        val latStr = String.format("%.7f N", latitude)
        val lngStr = String.format("%.7f E", longitude)
        val altStr = String.format("%.3f m", altitude)
        return "$latStr, $lngStr, $altStr"
    }

    fun formatError(): String {
        return error?.let { String.format("%.2fcm", it * 100) } ?: ""
    }

    fun formatErrorWithoutUnit(): String {
        return error?.let { String.format("%.2f", it * 100) } ?: ""
    }

    fun isSameTime(other: TrackingData): Boolean {
        return utcTime == other.utcTime
    }

    fun isEarlyThan(other: TrackingData): Boolean {
        return utcTime < other.utcTime
    }

    fun isEarlyOrEqual(other: TrackingData): Boolean {
        return utcTime <= other.utcTime
    }

    fun isLaterThan(other: TrackingData): Boolean {
        return utcTime > other.utcTime
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