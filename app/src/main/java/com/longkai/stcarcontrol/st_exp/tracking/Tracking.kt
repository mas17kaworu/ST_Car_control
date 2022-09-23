package com.longkai.stcarcontrol.st_exp.tracking

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.lang.Exception
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object Tracking {

    private lateinit var dataDir: File

    fun init(context: Context) {
        dataDir = context.getExternalFilesDir(null)!!
        println("Tracking data directory: $dataDir")
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
            dataDir.listFiles()
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
                    var rmcData: RmcData? = null
                    var ggaData: GgaData? = null

                    val handleDataPoint = {
                        rmcData?.let {
                            val record = mergeRmcAndGga(it, ggaData)
                            records.add(record)
                            rmcData = null
                            ggaData = null
                        }
                    }

                    while (true) {
                        val line = bufferedSource.readUtf8Line() ?: break
                        val fields = line.split(',')

                        // This assumes RMC data always comes before GGA.
                        when(fields.first()) {
                            TYPE_GNRMC -> {
                                handleDataPoint.invoke()
                                rmcData = parseRmc(fields)
                            }
                            TYPE_GNGGA -> {
                                ggaData = parseGga(fields)
                            }
                        }
                    }
                    handleDataPoint.invoke()
                }
            }
            records
        }
    }

    private fun mergeRmcAndGga(rmcData: RmcData, ggaData: GgaData?): TrackingData {
        return TrackingData(
            utcTime = rmcData.utcTime,
            latitude = rmcData.latitude,
            longitude = rmcData.longitude,
            velocity = rmcData.velocity,
            course = rmcData.course,
            utcDate = rmcData.utcDate,
            gpsStatus = ggaData?.gpsStatus,
            satelliteNumber = ggaData?.satelliteNumber,
            hdop = ggaData?.hdop,
            altitude = ggaData?.altitude,
            geoidHeight = ggaData?.geoidHeight
        )
    }

    private fun parseRmc(fields: List<String>): RmcData? {
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
                val velocity = fields[7].toDouble()
                val course = fields[8].toDouble()
                val utcDate = parseUtcDate(fields[9])
                RmcData(
                    utcTime = utcTime,
                    latitude = latitude,
                    longitude = longitude,
                    velocity = velocity,
                    course = course,
                    utcDate = utcDate
                )
            } else null
        } catch (e: Exception) {
            Log.i("zcf", "$fields", e)
            null
        }
    }

    private fun parseGga(fields: List<String>): GgaData? {
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
            val gpsStatus = fields[6].toInt()
            val satelliteNumber = fields[7].toInt()
            val hdop = fields[8].toDouble()
            val altitude = fields[9].toDouble()
            val geoidHeight = fields[10].toDouble()
            GgaData(
                utcTime = utcTime,
                latitude = latitude,
                longitude = longitude,
                gpsStatus = gpsStatus,
                satelliteNumber = satelliteNumber,
                hdop = hdop,
                altitude = altitude,
                geoidHeight = geoidHeight
            )
        } catch (e: Exception) {
            Log.i("zcf", "$fields", e)
            null
        }
    }

    /**
     * UTC ime in [UTC_TIME_PATTERN] format
     */
    private fun parseUtcTime(input: String): LocalTime {
        return LocalTime.parse(input, DateTimeFormatter.ofPattern(UTC_TIME_PATTERN))
    }

    /**
     * UTC date in [UTC_DATE_PATTERN] format
     */
    private fun parseUtcDate(input: String): LocalDate {
        return LocalDate.parse(input, DateTimeFormatter.ofPattern(UTC_TIME_PATTERN))
    }


    /**
     * @param input In 'ddmm.mmmm' format
     */
    private fun parseDegree(input: String): Double {
        val dd = input.substring(0, 2)
        val mms = input.substring(2)
        return dd.toInt() + mms.toDouble()/60.0
    }

    const val TYPE_GNRMC = "\$GNRMC"
    const val TYPE_GNGGA = "\$GNGGA"

    const val UTC_TIME_PATTERN = "HHmmss.SSS"
    const val UTC_DATE_PATTERN = "ddMMyy"
}
