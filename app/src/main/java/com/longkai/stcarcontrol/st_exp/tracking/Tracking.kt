package com.longkai.stcarcontrol.st_exp.tracking

import android.content.Context
import android.util.Log
import okio.buffer
import okio.source
import java.io.File
import java.lang.Exception
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object Tracking {

    private lateinit var dataDir: File

    fun init(context: Context) {
        dataDir = context.getExternalFilesDir(null)!!
    }

    fun load(fileName: String = "LB_Limited.txt"): List<TrackingData> {
        val recordsFile = File(dataDir, fileName)
        println("zcf recordsFile: $recordsFile, ${recordsFile.exists()}")

        if (recordsFile.exists().not()) return emptyList()

        val records = mutableListOf<TrackingData>()
        recordsFile.source().use { source ->
            source.buffer().use { bufferedSource ->
                while (true) {
                    val line = bufferedSource.readUtf8Line() ?: break

                    val fields = line.split(',')
                    val result: TrackingData? = when (fields.first()) {
                        TYPE_GPRMC -> parseGPRMC(fields)
                        TYPE_GPGGA -> parseGPGGA(fields)
                        else -> null
                    }
                    result?.let { records.add(it) }
                }
            }
        }
        return records
    }

    private fun parseGPRMC(fields: List<String>): TrackingData.GprmcData? {
        return try {
            val utcTime = parseUtcTime(fields[1])
            val isValid = (fields[2] == "A")
            return if (isValid) {
                var latitude = parseDegree(fields[3])
                val latitudeHemisphere = LatitudeHemisphere.from(fields[4])
                if (latitudeHemisphere == LatitudeHemisphere.South) {
                    latitude *= -1
                }
                var longitude = parseDegree(fields[5])
                val longitudeHemisphere = LongitudeHemisphere.from(fields[6])
                if (longitudeHemisphere == LongitudeHemisphere.West) {
                    longitude *= -1
                }
                TrackingData.GprmcData(
                    utcTime = utcTime,
                    latitude = latitude,
                    longitude = longitude
                )
            } else null
        } catch (e: Exception) {
            Log.i("zcf", "$fields", e)
            null
        }
    }

    private fun parseGPGGA(fields: List<String>): TrackingData.GpggaData? {
        return try {
            val utcTime = parseUtcTime(fields[1])
            var latitude = parseDegree(fields[2])
            val latitudeHemisphere = LatitudeHemisphere.from(fields[3])
            if (latitudeHemisphere == LatitudeHemisphere.South) {
                latitude *= -1
            }
            var longitude = parseDegree(fields[4])
            val longitudeHemisphere = LongitudeHemisphere.from(fields[5])
            if (longitudeHemisphere == LongitudeHemisphere.West) {
                longitude *= -1
            }
            TrackingData.GpggaData(
                utcTime = utcTime,
                latitude = latitude,
                longitude = longitude
            )
        } catch (e: Exception) {
            Log.i("zcf", "$fields", e)
            null
        }
    }

    /**
     * UTC ime in "HHmmss.SSS" format
     */
    private fun parseUtcTime(input: String): LocalTime {
        return LocalTime.parse(input, DateTimeFormatter.ofPattern(UTC_TIME_PATTERN))
    }

    /**
     * @param input In 'ddmm.mmmm' format
     */
    private fun parseDegree(input: String): Double {
        val dd = input.substring(0, 2)
        val mms = input.substring(2)
        return dd.toInt() + mms.toDouble()/60.0
    }

    const val TYPE_GPRMC = "\$GPRMC"
    const val TYPE_GPGGA = "\$GPGGA"

    const val UTC_TIME_PATTERN = "HHmmss.SSS"
}
