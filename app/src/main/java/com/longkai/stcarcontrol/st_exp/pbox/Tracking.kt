package com.longkai.stcarcontrol.st_exp.pbox

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
        dataDir = File(context.getExternalFilesDir(null), DIR_HISTORY_RECORDS)
        if (!dataDir.exists()) dataDir.mkdir()
        println("Tracking data directory: $dataDir")
    }

    suspend fun saveRecording(
        filePath: String,
        pboxData: List<String>,
        realData: List<String>
    ) {
        withContext(Dispatchers.IO) {
            val recordPath = File(dataDir, filePath)
            recordPath.mkdir()

            val pboxFile = File(recordPath, FILE_PBOX)
            pboxFile.createNewFile()
            pboxFile.sink().buffer().use { sink ->
                pboxData.forEach {
                    sink.writeUtf8(it)
                    sink.writeUtf8("\n")
                }
            }
            val realFile = File(recordPath, FILE_REAL)
            realFile.createNewFile()
            realFile.sink().buffer().use { sink ->
                realData.forEach {
                    sink.writeUtf8(it)
                    sink.writeUtf8("\n")
                }
            }
        }
    }

    suspend fun loadHistoryRecords(): List<HistoryRecord> {
        return withContext(Dispatchers.IO) {
            dataDir.listFiles()
                ?.filter {
                    it.isDirectory && (File(it, FILE_REAL).exists() || File(it, FILE_PBOX).exists())
                }
                ?.map { HistoryRecord(it.name) }
                ?: emptyList()
        }
    }

    suspend fun load(historyRecord: HistoryRecord): HistoryRecordData {
        return withContext(Dispatchers.IO) {
            val recordDir = File(dataDir, historyRecord.recordName)
            val realFile = File(recordDir, FILE_REAL)
            val pboxFile = File(recordDir, FILE_PBOX)

            HistoryRecordData(
                recordName = historyRecord.recordName,
                realPoints = loadRecordFile(realFile),
                pboxPoints = loadRecordFile(pboxFile)
            )
        }
    }

    private fun loadRecordFile(recordFile: File): List<TrackingData> {
        if (recordFile.exists().not()) return emptyList()

        val records = mutableListOf<TrackingData>()
        val lineRecordProcessor = LineRecordProcessor()

        recordFile.source().use { source ->
            source.buffer().use { bufferedSource ->
                while (true) {
                    val line = bufferedSource.readUtf8Line() ?: break
                    lineRecordProcessor.processLine(line){ records.add(it) }
                }
            }
        }
        return records
    }

    private const val DIR_HISTORY_RECORDS = "HistoryRecords"
    private const val FILE_REAL = "real.txt"
    private const val FILE_PBOX = "pbox.txt"

}


class LineRecordProcessor() {
    private var rmcData: RmcData? = null
    private var ggaData: GgaData? = null

    fun processLine(
        line: String,
        onNewAlarmData: (AlarmData) -> Unit = {},
        onNewRecord: (TrackingData) -> Unit,
    ) {
        val fields = line.split(',', '*')
        val firstField = fields.first()

        if (firstField.isALARMFlag()) {
            val alarmData = parseAlarm(fields)
            alarmData?.let {
                onNewAlarmData(it)
            }
            return
        }

        if (firstField.isRMCFlag()) {
            rmcData = parseRmc(fields)
        } else if (firstField.isGGAFlag()) {
            ggaData = parseGga(fields)
        }

        // Requires both RMC data and GGA data to be present and at the same time, otherwise discard old data
        if (rmcData != null && ggaData != null) {
            if (rmcData!!.utcTime == ggaData!!.utcTime) {
                val record = mergeRmcAndGga(rmcData!!, ggaData!!)
                onNewRecord(record)
                rmcData = null
                ggaData = null
            } else if (rmcData!!.utcTime > ggaData!!.utcTime) {
                ggaData = null
            } else {
                rmcData = null
            }
        }
    }

    fun processLines(
        lines: List<String>,
        onNewAlarmData: (AlarmData) -> Unit = {},
        onNewRecord: (TrackingData) -> Unit,
    ) {
        lines.forEach {
            processLine(it, onNewAlarmData, onNewRecord)
        }
    }

    private fun mergeRmcAndGga(rmcData: RmcData, ggaData: GgaData): TrackingData {
        return TrackingData(
            utcTime = rmcData.utcTime,
            latitude = rmcData.latitude,
            longitude = rmcData.longitude,
            speed = rmcData.speed,
            direction = rmcData.direction,
            utcDate = rmcData.utcDate,
            gpsStatus = ggaData.gpsStatus,
            satelliteNumber = ggaData.satelliteNumber,
            hdop = ggaData.hdop,
            altitude = ggaData.altitude,
            geoidHeight = ggaData.geoidHeight
        )
    }

    private fun parseRmc(fields: List<String>): RmcData? {
        return try {
            val utcTime = parseUtcTime(fields[1])
            val isValid = (fields[2] == "A")
            return if (isValid) {
                var latitude = parseLatitude(fields[3])
                val latitudeHemisphere = LatitudeHemisphere.from(fields[4])
                if (latitudeHemisphere == LatitudeHemisphere.South) {
                    latitude *= -1
                }
                var longitude = parseLongitude(fields[5])
                val longitudeHemisphere = LongitudeHemisphere.from(fields[6])
                if (longitudeHemisphere == LongitudeHemisphere.West) {
                    longitude *= -1
                }
                val speed = parseSpeed(fields[7])
                val direction = parseDouble(fields[8])
                val utcDate = parseUtcDate(fields[9])
                RmcData(
                    utcTime = utcTime,
                    latitude = latitude,
                    longitude = longitude,
                    speed = speed,
                    direction = direction,
                    utcDate = utcDate
                )
            } else null
        } catch (e: Exception) {
            Log.e("zcf", "$fields", e)
            null
        }
    }

    private fun parseGga(fields: List<String>): GgaData? {
        return try {
            val utcTime = parseUtcTime(fields[1])
            var latitude = parseLatitude(fields[2])
            val latitudeHemisphere = LatitudeHemisphere.from(fields[3])
            if (latitudeHemisphere == LatitudeHemisphere.South) {
                latitude *= -1
            }
            var longitude = parseLongitude(fields[4])
            val longitudeHemisphere = LongitudeHemisphere.from(fields[5])
            if (longitudeHemisphere == LongitudeHemisphere.West) {
                longitude *= -1
            }
            val gpsStatus = parseInt(fields[6])
            val satelliteNumber = parseInt(fields[7])
            val hdop = parseDouble(fields[8])
            val altitude = parseDouble(fields[9])
            val geoidHeight = parseDouble(fields[11])
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
            Log.e("zcf", "$fields", e)
            null
        }
    }

    private fun parseAlarm(fields: List<String>): AlarmData? {
        return try {
            val utcTime = parseUtcTime(fields[1])
            val antennaStatus = parseInt(fields[5])
            val antennaSign = parseInt(fields[6])
            val wbiSign = parseInt(fields[7])
            val nbiSign = parseInt(fields[8])
            val spoofingSign = parseInt(fields[9])
            AlarmData(
                utcTime = utcTime,
                antennaStatus = antennaStatus,
                antennaSign = antennaSign,
                wbiSign = wbiSign,
                nbiSign = nbiSign,
                spoofingSign = spoofingSign
            )
        } catch (e: Exception) {
            Log.e("zcf", "$fields", e)
            null
        }
    }

    /**
     * UTC ime in [UTC_TIME_PATTERN] format
     */
    private fun parseUtcTime(input: String): LocalTime {
        val (timeStr, timePattern) = when (input.length) {
            UTC_TIME_PATTERN_SSS.length -> input to UTC_TIME_PATTERN_SSS
            UTC_TIME_PATTERN_SS.length -> input to UTC_TIME_PATTERN_SS
            UTC_DATE_PATTERN.length -> input to UTC_TIME_PATTERN
            else -> input.substringBefore('.') to UTC_TIME_PATTERN
        }
        return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern(timePattern))
    }

    /**
     * UTC date in [UTC_DATE_PATTERN] format
     */
    private fun parseUtcDate(input: String): LocalDate {
        return LocalDate.parse(input, DateTimeFormatter.ofPattern(UTC_DATE_PATTERN))
    }

    /**
     * @param input In 'ddmm.mmmm' format
     */
    private fun parseLatitude(input: String): Double {
        val dd = input.substring(0, 2)
        val mms = input.substring(2)
        return dd.toInt() + mms.toDouble()/60.0
    }

    /**
     * @param input In 'dddmm.mmmm' format
     */
    private fun parseLongitude(input: String): Double {
        val dd = input.substring(0, 3)
        val mms = input.substring(3)
        return dd.toInt() + mms.toDouble()/60.0
    }

    private fun parseSpeed(input: String): Double? {
        val rawSpeed = parseDouble(input)
        return rawSpeed?.let { it * 1.852 / 3.6  }
    }

    private fun parseDouble(input: String): Double? {
        return if (input.isBlank()) null else input.toDouble()
    }

    private fun parseInt(input: String): Int? {
        return if(input.isBlank()) null else input.toInt()
    }

    private fun String.isRMCFlag() = startsWith('$') && endsWith("RMC")
    private fun String.isGGAFlag() = startsWith('$') && endsWith("GGA")
    private fun String.isALARMFlag() = startsWith('$') && endsWith("ALARM")


    companion object {
        const val UTC_TIME_PATTERN = "HHmmss"
        const val UTC_TIME_PATTERN_SSS = "HHmmss.SSS"
        const val UTC_TIME_PATTERN_SS = "HHmmss.SS"
        const val UTC_DATE_PATTERN = "ddMMyy"
    }
}
