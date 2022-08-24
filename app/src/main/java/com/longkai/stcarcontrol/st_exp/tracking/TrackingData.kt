package com.longkai.stcarcontrol.st_exp.tracking

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

sealed class TrackingData {

    data class GprmcData(
        val utcTime: LocalTime,
        val latitude: Float,
        val latitudeHemisphere: LatitudeHemisphere,
        val longitude: Float,
        val longitudeHemisphere: LongitudeHemisphere,
        val velocity: Float,
        val direction: Float,
        val utcDate: LocalDate
    ) : TrackingData()

    data class GpggaData(
        val utcTime: LocalDateTime,
        val latitude: Float,
        val latitudeHemisphere: LatitudeHemisphere,
        val longitude: Float,
        val longitudeHemisphere: LongitudeHemisphere,
    ): TrackingData()
}

enum class LatitudeHemisphere(key: Char) {
    North('N'),
    South('S')
}

enum class LongitudeHemisphere(key: Char) {
    East('E'),
    West('W')
}