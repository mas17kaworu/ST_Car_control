package com.longkai.stcarcontrol.st_exp.tracking

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.lang.Exception
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.Path

object Tracking {

    private lateinit var dataDir: File

    fun init(context: Context) {
        dataDir = context.getExternalFilesDir(null)!!
    }

    suspend fun saveRecording(fileName: String, data: List<String>) {
        withContext(Dispatchers.IO) {
            val recordFile = File(dataDir, fileName)
            recordFile.sink().buffer().use { sink ->
                data.forEach {
                    sink.writeUtf8(it)
                    sink.writeUtf8("\n")
                }
            }
        }
    }

    suspend fun loadHistoryRecords(): List<HistoryRecord> {
        return withContext(Dispatchers.IO) {
            dataDir
                .listFiles()
                .filter { it.isFile }
                .map { HistoryRecord(it.name) }
        }
    }

    suspend fun load(historyRecord: HistoryRecord): List<TrackingData> {
        return withContext(Dispatchers.IO) {
            val recordsFile = File(dataDir, historyRecord.fileName)
            println("zcf recordsFile: $recordsFile, ${recordsFile.exists()}")

            if (recordsFile.exists().not()) return@withContext emptyList()

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
            records
        }
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
