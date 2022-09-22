package com.longkai.stcarcontrol.st_exp.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

enum class SignalType {
    Real, PBox, Other3, Other4
}

data class TrackingViewState(
    val inReviewMode: Boolean = false,
    val isRecording: Boolean = false,
    val signalType: SignalType = SignalType.Real
)

class TrackingViewModel : ViewModel() {
    private var recordFilename: String = ""
    private var recordData: MutableList<String> = mutableListOf()

    private val _uiState = MutableStateFlow(TrackingViewState())
    val uiState: StateFlow<TrackingViewState> = _uiState


    suspend fun loadHistoryRecords(): List<HistoryRecord> {
        return Tracking.loadHistoryRecords()
    }

    suspend fun loadRecord(historyRecord: HistoryRecord): List<TrackingData> {
        return Tracking.load(historyRecord)
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

    fun changeSignal(signalType: SignalType) {
        _uiState.update { it.copy(signalType = signalType) }
    }


}