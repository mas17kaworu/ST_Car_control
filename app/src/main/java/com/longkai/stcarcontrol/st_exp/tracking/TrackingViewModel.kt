package com.longkai.stcarcontrol.st_exp.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class TrackingViewState(
    val inReviewMode: Boolean = false,
    val isRecording: Boolean = false,
    val showRealTrack: Boolean = true,
    val showPboxTrack: Boolean = true,
    val historyRecordDataRefreshed: Boolean = false,
    val historyRecordData: HistoryRecordData? = null
)

class TrackingViewModel : ViewModel() {
    private var recordFilename: String = ""
    private var recordData: MutableList<String> = mutableListOf()

    private val _uiState = MutableStateFlow(TrackingViewState())
    val uiState: StateFlow<TrackingViewState> = _uiState


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
}