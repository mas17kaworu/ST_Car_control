package com.longkai.stcarcontrol.st_exp.pbox

import android.app.Application
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.longkai.stcarcontrol.st_exp.STCarApplication
import com.longkai.stcarcontrol.st_exp.appPrefsDataStore
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPBox.CMDPBox
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList.CommandPBoxMock
import com.longkai.stcarcontrol.st_exp.mockMessage.MockMessageServiceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

data class TrackingViewState(
    val inReviewMode: Boolean = false,
    val inReplayMode: Boolean = false,
    val historyRecordData: HistoryRecordData? = null,
    val trackSettings: TrackSettings = TrackSettings(),
    val needRefreshTrack: Boolean = false
)

data class RecordingState(
    val isRecording: Boolean = false,
    val recordingPoint: RecordingPoint? = null
)

data class RecordingPoint(
    val realPoint: TrackingData? = null,
    val pboxPoint: TrackingData? = null
)

data class AlarmState(
    val ppsAlarm: Boolean = false,
    val antennaAlarm: Boolean = false,
    val wbiAlarm: Boolean = false,
    val nbiAlarm: Boolean = false,
    val spoofingAlarm: Boolean = false
)

@Serializable
data class TrackSettings(
    val hideRealTrackUI: Boolean = false,
    val labelInterval: Int = DEFAULT_LABEL_INTERVAL,
    val replaySpeed: Int = DEFAULT_REPLAY_SPEED,
    val replayCameraFollowCar: Boolean = false,
    val showRecordingLogs: Boolean = DEFAULT_SHOW_RECORDING_LOGS,
    val showPPSAlarm: Boolean = false,
    val showAntennaAlarm: Boolean = false,
    val showWBIAlarm: Boolean = false,
    val showNBIAlarm: Boolean = false,
    val showSpoofingAlarm: Boolean = false
)

val PREF_TRACK_SETTINGS = stringPreferencesKey("trackSettings")
const val DEFAULT_LABEL_INTERVAL = 10
const val DEFAULT_REPLAY_SPEED = 1
const val DEFAULT_SHOW_RECORDING_LOGS = false

// Control screen logs and max log number
const val SHOW_LOGS = true
const val LOG_MAX_LINES = 10

enum class RecordType {
    PBOX, REAL
}

class TrackingViewModel(private val application: Application) : AndroidViewModel(application) {
    private val TAG = this.javaClass.simpleName

    private var recordPath: String = ""
    private var recordDataReal: MutableList<String> = mutableListOf()
    private var recordDataPbox: MutableList<String> = mutableListOf()
    private val pboxLineRecordProcessor = LineRecordProcessor()
    private val realLineRecordProcessor = LineRecordProcessor()

    private val _uiState = MutableStateFlow(TrackingViewState())
    val uiState: StateFlow<TrackingViewState> = _uiState

    private val _showRealTrack = MutableStateFlow(true)
    val showRealTrack: StateFlow<Boolean> = _showRealTrack
    private val _showPboxTrack = MutableStateFlow(true)
    val showPboxTrack: StateFlow<Boolean> = _showPboxTrack

    private val _recordingState = MutableStateFlow(RecordingState())
    val recordingState: StateFlow<RecordingState> = _recordingState

    private val _alarmState = MutableStateFlow(AlarmState())
    val alarmState: StateFlow<AlarmState> = _alarmState

    private val _logs = MutableStateFlow(listOf<String>())
    val logs: StateFlow<List<String>> = _logs

    init {
        viewModelScope.launch {
            application.applicationContext.appPrefsDataStore.data.collectLatest { prefs ->
                val trackSettingsString = prefs[PREF_TRACK_SETTINGS]
                val trackSettings = trackSettingsString?.let { Json.decodeFromString<TrackSettings>(it) } ?: TrackSettings()
                _uiState.update {
                    it.copy(
                        trackSettings = trackSettings,
                        inReplayMode = false,
                        needRefreshTrack = true
                    )
                }
            }
        }
    }

    fun loadHistoryRecords(onComplete: (List<HistoryRecord>) -> Unit) {
        viewModelScope.launch {
            val historyRecords = Tracking.loadHistoryRecords()
            onComplete.invoke(historyRecords)
        }
    }

    fun loadRecord(historyRecord: HistoryRecord) {
        viewModelScope.launch {
            val historyRecordData = Tracking.load(historyRecord)
            _uiState.update {
                it.copy(
                    historyRecordData = historyRecordData,
                    needRefreshTrack = true
                )
            }
        }
    }

    fun clearRefreshFlag() {
        _uiState.update {
            it.copy(needRefreshTrack = false)
        }
    }

    fun enterReviewMode() {
        _uiState.update {
            it.copy(
                inReviewMode = true,
                inReplayMode = false
            )
        }
    }

    fun exitReviewMode() {
        _uiState.update {
            it.copy(
                inReviewMode = false,
                inReplayMode = false
            )
        }
    }

    fun enterReplayMode() {
        if (_uiState.value.inReviewMode) {
            _uiState.update {
                it.copy(inReplayMode = true)
            }
        }
    }

    fun exitReplayMode() {
        if (_uiState.value.inReviewMode) {
            _uiState.update {
                it.copy(inReplayMode = false)
            }
        }
    }

    fun startRecording() {
        _recordingState.update {
            it.copy(isRecording = true, recordingPoint = null)
        }
        if (uiState.value.trackSettings.showRecordingLogs) {
            _logs.update { listOf() }
        }

        recordPath = "${LocalDateTime.now()}"

        fun CMDPBox.DataType.toRecordType() = when (this) {
            CMDPBox.DataType.Real -> RecordType.REAL
            CMDPBox.DataType.PBox -> RecordType.PBOX
            CMDPBox.DataType.Reserved1 -> throw java.lang.IllegalArgumentException()
            CMDPBox.DataType.Reserved2 -> throw java.lang.IllegalArgumentException()
        }

        val command = CMDPBox()
        ServiceManager.getInstance().sendCommandToCar(command, CommandListenerAdapter<CMDPBox.Response>())
        ServiceManager.getInstance().registerRegularlyCommand(
            command,
            object : CommandListenerAdapter<CMDPBox.Response>() {
                override fun onSuccess(response: CMDPBox.Response?) {
                    Log.i(TAG, "onSuccess, response: ${response?.dataType}, ${response?.content}")
                    response?.also {
                        saveRecord(response.content, response.dataType.toRecordType())
                    }
                }

                override fun onTimeout() { }
            }
        )
        MockMessageServiceImpl.getService().StopService(CommandPBoxMock::class.java.toString())
        MockMessageServiceImpl.getService().StartService(CommandPBoxMock::class.java.toString(), application)
    }

    private fun saveRecord(record: String, recordType: RecordType) {
        if (!_recordingState.value.isRecording) return

        System.lineSeparator()

        val lines = record.split("\r\n")
        when (recordType) {
            RecordType.PBOX -> {
                recordDataPbox.add(record)
                pboxLineRecordProcessor.processLines(
                    lines = lines,
                    onNewAlarmData = { updateAlarmState(it) },
                    onNewRecord = { trackingData ->
                        _recordingState.update {
                            it.copy(
                                recordingPoint = RecordingPoint(pboxPoint = trackingData)
                            )
                        }
                    },
                )
            }
            RecordType.REAL -> {
                recordDataReal.add(record)
                realLineRecordProcessor.processLines(
                    lines = lines,
                    onNewAlarmData = { updateAlarmState(it) },
                    onNewRecord = { trackingData ->
                        _recordingState.update {
                            it.copy(
                                recordingPoint = RecordingPoint(realPoint = trackingData)
                            )
                        }
                    }
                )
            }
        }

        if (uiState.value.trackSettings.showRecordingLogs) {
            val log = "$recordType: $record"
            _logs.update { it.plus(log).takeLast(LOG_MAX_LINES) }
        }
    }

    private fun updateAlarmState(alarmData: AlarmData) {
        _alarmState.update {
            it.copy(
                ppsAlarm = alarmData.ppsAlarm?.equals(0)?.not() ?: false,
                antennaAlarm = alarmData.antennaAlarm?.equals(0)?.not() ?: false,
                wbiAlarm = alarmData.wbiAlarm?.equals(0)?.not() ?: false,
                nbiAlarm = alarmData.nbiAlarm?.equals(0)?.not() ?: false,
                spoofingAlarm = alarmData.spoofingAlarm?.equals(0)?.not() ?: false
            )
        }
    }

    fun stopRecording(onComplete: (fileName: String?) -> Unit) {
        viewModelScope.launch {
            _recordingState.update {
                it.copy(isRecording = false, recordingPoint = null)
            }
            ServiceManager.getInstance().unregisterRegularlyCommand(CMDPBox())
            MockMessageServiceImpl.getService().StopService(CommandPBoxMock::class.java.toString())
            val filePath = recordPath
            if (filePath.isNotBlank() && recordDataPbox.isNotEmpty()) {
                Tracking.saveRecording(filePath, recordDataPbox, recordDataReal)
                recordPath = ""
                recordDataPbox.clear()
                recordDataReal.clear()
                onComplete.invoke(filePath)
            } else {
                onComplete.invoke(null)
            }
        }
    }

    fun switchRealTrack() {
        _showRealTrack.update { !it }
    }

    fun switchPboxTrack() {
        _showPboxTrack.update { !it }
    }

    fun saveSettings(trackSettings: TrackSettings) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getApplication<STCarApplication>().applicationContext.appPrefsDataStore.edit { prefs ->
                    prefs[PREF_TRACK_SETTINGS] = Json.encodeToString(trackSettings)
                }
            }
        }
    }
}