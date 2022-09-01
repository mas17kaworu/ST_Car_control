package com.longkai.stcarcontrol.st_exp.tracking

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

sealed class TrackingData(
    open val utcTime: LocalTime,
    open val latitude: Double,
    open val longitude: Double,
) {

    data class GprmcData(
        override val latitude: Double,
        override val longitude: Double,
        override val utcTime: LocalTime,
        val velocity: Double? = null,
        val direction: Double? = null,
        val utcDate: LocalDate? = null
    ) : TrackingData(utcTime, latitude, longitude)

    data class GpggaData(
        override val utcTime: LocalTime,
        override val latitude: Double,
        override val longitude: Double,
    ): TrackingData(utcTime, latitude, longitude)
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