package com.longkai.stcarcontrol.st_exp.tracking

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
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
    val isRecording: Boolean = false,
    val showRealTrack: Boolean = true,
    val showPboxTrack: Boolean = true,
    val historyRecordDataRefreshed: Boolean = false,
    val historyRecordData: HistoryRecordData? = null,
    val hideRealTrackUI: Boolean = false,
    val labelInterval: Int = DEFAULT_LABEL_INTERVAL
)

val PREF_HIDE_REAL_TRACK_UI = booleanPreferencesKey("hideRealTrackUI")
val PREF_LABEL_INTERVAL = intPreferencesKey("labelInterval")
const val DEFAULT_LABEL_INTERVAL = 10

class TrackingViewModel(application: Application) : AndroidViewModel(application) {
    private var recordFilename: String = ""
    private var recordData: MutableList<String> = mutableListOf()

    private val _uiState = MutableStateFlow(TrackingViewState())
    val uiState: StateFlow<TrackingViewState> = _uiState

    init {
        viewModelScope.launch {
            application.applicationContext.appPrefsDataStore.data.collectLatest { prefs ->
                val hideRealTrackUI = prefs[PREF_HIDE_REAL_TRACK_UI] ?: false
                val labelInterval = prefs[PREF_LABEL_INTERVAL] ?: DEFAULT_LABEL_INTERVAL
                _uiState.update {
                    it.copy(
                        hideRealTrackUI = hideRealTrackUI,
                        labelInterval = labelInterval
                    )
                }
            }
        }
    }

    suspend fun loadHistoryRecords(): List<HistoryRecord> {
        return Tracking.loadHistoryRecords()
    }

    fun loadRecord(historyRecord: HistoryRecord) {
        viewModelScope.launch {
            val historyRecordData = Tracking.load(historyRecord)
            _uiState.update {
                it.copy(
                    historyRecordDataRefreshed = true,
                    historyRecordData = historyRecordData
                )
            }
        }
    }

    fun enterReviewMode() {
        _uiState.update {
            it.copy(inReviewMode = true)
        }
    }

    fun exitReviewMode() {
        _uiState.update {
            it.copy(inReviewMode = false)
        }
    }

    fun startRecording() {
        _uiState.update {
            it.copy(isRecording = true)
        }
        recordFilename = LocalDateTime.now().toString()
    }

    fun saveRecord(record: String) {
        recordData.add(record)
    }

    fun stopRecording(onComplete: (fileName: String?) -> Unit) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isRecording = false)
            }
            val fileName = recordFilename
            if (fileName.isNotBlank() && recordData.isNotEmpty()) {
                Tracking.saveRecording(fileName, recordData)
                recordFilename = ""
                recordData.clear()
                onComplete.invoke(fileName)
            } else {
                onComplete.invoke(null)
            }
        }
    }

    fun switchRealTrack() {
        _uiState.update { it.copy(showRealTrack = !it.showRealTrack) }
    }

    fun switchPboxTrack() {
        _uiState.update { it.copy(showPboxTrack = !it.showPboxTrack) }
    }

    fun saveSettings(hideRealTrack: Boolean, labelInterval: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getApplication<STCarApplication>().applicationContext.appPrefsDataStore.edit { prefs ->
                    prefs[PREF_HIDE_REAL_TRACK_UI] = hideRealTrack
                    prefs[PREF_LABEL_INTERVAL] = labelInterval
                }
            }
        }
    }
}