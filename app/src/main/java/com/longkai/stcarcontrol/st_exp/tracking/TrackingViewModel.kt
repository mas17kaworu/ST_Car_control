package com.longkai.stcarcontrol.st_exp.tracking

import android.app.Application
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.longkai.stcarcontrol.st_exp.STCarApplication
import com.longkai.stcarcontrol.st_exp.appPrefsDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

data class TrackingViewState(
    val inReviewMode: Boolean = false,
    val inReplayMode: Boolean = false,
    val isRecording: Boolean = false,
    val historyRecordData: HistoryRecordData? = null,
    val trackSettings: TrackSettings = TrackSettings(),
    val needRefreshTrack: Boolean = false,
)

data class TrackSettings(
    val hideRealTrackUI: Boolean = false,
    val labelInterval: Int = DEFAULT_LABEL_INTERVAL,
    val replaySpeed: Int = DEFAULT_REPLAY_SPEED,
    val replayCameraFollowCar: Boolean = false
)

val PREF_HIDE_REAL_TRACK_UI = booleanPreferencesKey("hideRealTrackUI")
val PREF_LABEL_INTERVAL = intPreferencesKey("labelInterval")
val PREF_REPLAY_SPEED = intPreferencesKey("replaySpeed")
val PREF_REPLAY_CAMERA_FOLLOW_CAR = booleanPreferencesKey("replayCameraFollowCar")
const val DEFAULT_LABEL_INTERVAL = 10
const val DEFAULT_REPLAY_SPEED = 1

enum class RECORD_TYPE {
    PBOX, REAL
}

class TrackingViewModel(application: Application) : AndroidViewModel(application) {
    private var recordPath: String = ""
    private var recordDataReal: MutableList<String> = mutableListOf()
    private var recordDataPbox: MutableList<String> = mutableListOf()

    private val _uiState = MutableStateFlow(TrackingViewState())
    val uiState: StateFlow<TrackingViewState> = _uiState
    private val _showRealTrack = MutableStateFlow(true)
    val showRealTrack: StateFlow<Boolean> = _showRealTrack
    private val _showPboxTrack = MutableStateFlow(true)
    val showPboxTrack: StateFlow<Boolean> = _showPboxTrack

    init {
        viewModelScope.launch {
            application.applicationContext.appPrefsDataStore.data.collectLatest { prefs ->
                val hideRealTrackUI = prefs[PREF_HIDE_REAL_TRACK_UI] ?: false
                val labelInterval = prefs[PREF_LABEL_INTERVAL] ?: DEFAULT_LABEL_INTERVAL
                val replaySpeed = prefs[PREF_REPLAY_SPEED] ?: DEFAULT_REPLAY_SPEED
                val replayCameraFollowCar = prefs[PREF_REPLAY_CAMERA_FOLLOW_CAR] ?: false
                _uiState.update {
                    it.copy(
                        trackSettings = TrackSettings(
                            hideRealTrackUI = hideRealTrackUI,
                            labelInterval = labelInterval,
                            replaySpeed = replaySpeed,
                            replayCameraFollowCar = replayCameraFollowCar
                        ),
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
        _uiState.update {
            it.copy(isRecording = true)
        }
        recordPath = "${LocalDateTime.now()}"
    }

    fun saveRecord(record: String, recordType: RECORD_TYPE) {
        when (recordType) {
            RECORD_TYPE.PBOX -> recordDataPbox.add(record)
            RECORD_TYPE.REAL -> recordDataReal.add(record)
        }
    }

    fun stopRecording(onComplete: (fileName: String?) -> Unit) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isRecording = false)
            }
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
                    prefs[PREF_HIDE_REAL_TRACK_UI] = trackSettings.hideRealTrackUI
                    prefs[PREF_LABEL_INTERVAL] = trackSettings.labelInterval
                    prefs[PREF_REPLAY_SPEED] = trackSettings.replaySpeed
                    prefs[PREF_REPLAY_CAMERA_FOLLOW_CAR] = trackSettings.replayCameraFollowCar
                }
            }
        }
    }
}