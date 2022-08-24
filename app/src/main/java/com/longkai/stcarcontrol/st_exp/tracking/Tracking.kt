package com.longkai.stcarcontrol.st_exp.tracking

import android.content.Context
import android.icu.text.SimpleDateFormat
import okio.FileSystem
import okio.Path
import okio.buffer
import okio.source
import java.io.File
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

object Tracking {

    private lateinit var dataDir: File

    fun init(context: Context) {
        dataDir = context.getExternalFilesDir(null)!!
    }

    fun load(fileName: String = "LB_Limited.text"): List<TrackingData> {
        val recordsFile = File(dataDir, fileName)
        println("zcf dir: $dataDir, ${recordsFile.exists()}")

        val records = mutableListOf<TrackingData>()
        recordsFile.source().use { source ->
            source.buffer().use { bufferedSource ->
                while (true) {
                    val line = bufferedSource.readUtf8Line() ?: break

                    val fields = line.split(',')
                    val result: TrackingData? = when (fields.first()) {
                        TYPE_GPGGA -> parseGPRMC(fields)
                        TYPE_GPRMC -> parseGPGGA(fields)
                        else -> null
                    }
                    result?.let { records.add(it) }
                }
            }
        }
        return records
    }

    fun parseGPGGA(fields: List<String>): TrackingData.GprmcData? {

        val isValid = (fields[2] == "A")
//        return TrackingData.GprmcData(
//            utcTime = LocalTime.parse(fields[1], DateTimeFormatter.ofPattern("HHmmss")),
//            latitude =
//        )
        return null
    }

    fun parseGPRMC(fields: List<String>): TrackingData.GpggaData? {

//        return TrackingData.GpggaData(
//
//        )
        return null
    }

    const val TYPE_GPGGA = "#GPGGA"
    const val TYPE_GPRMC = "#GPRMC"
}
