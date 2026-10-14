package com.longkai.stcarcontrol.st_exp.compose.ui.chassis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.*
import java.util.Collections
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChassisViewModel(private val repository: ChassisRepository) : ViewModel() {
    private val mutableUiState = MutableStateFlow(
        ChassisUiState(config = repository.config, isDemo = repository.isDemo)
    )
    val uiState = mutableUiState.asStateFlow()
    private var pageActive = false
    private val editedFields = mutableSetOf<ChassisControlField>()
    private var reception: Job? = null
    private var comparisonRequest: Job? = null

    /** Reception belongs to the page, not to a selected send mode or a Compose subscription. */
    fun onPageEntered() {
        if (pageActive) return
        pageActive = true
        reception = viewModelScope.launch {
            repository.telemetry.collect { appendTelemetry(it) }
        }
    }

    fun onPageExited() {
        pageActive = false
        onCompareClosed()
        lockControls()
        reception?.cancel()
        reception = null
    }

    fun lockControls() {
        editedFields.clear()
        mutableUiState.update { state ->
            state.copy(
                enabledControls = emptySet(),
                controlGenerations = ChassisControlTab.values().associateWith {
                    (state.controlGenerations[it] ?: 0L) + 1
                }
            )
        }
    }

    fun onControlTabSelected(tab: ChassisControlTab) {
        if (!pageActive || tab == ChassisControlTab.Epb) return
        editedFields.removeAll { it.mode == tab }
        mutableUiState.update { state ->
            state.copy(
                enabledControls = if (tab in state.enabledControls) state.enabledControls - tab
                    else state.enabledControls + tab,
                controlGenerations = state.controlGenerations + (tab to ((state.controlGenerations[tab] ?: 0L) + 1))
            )
        }
    }

    fun onEpbToggled() {
        if (!pageActive) return
        val enabled = !mutableUiState.value.epbEnabled
        viewModelScope.launch {
            when (val result = repository.setEpb(enabled)) {
                is ChassisCommandResult.Rejected -> mutableUiState.update { it.copy(error = result.error) }
                else -> mutableUiState.update { it.copy(epbEnabled = enabled) }
            }
        }
    }

    fun onCurrentOffsetChanged(enabled: Boolean) {
        if (!pageActive || mutableUiState.value.currentOffsetEnabled == enabled) return
        viewModelScope.launch {
            when (val result = repository.setCurrentOffset(enabled)) {
                is ChassisCommandResult.Rejected -> mutableUiState.update { it.copy(error = result.error) }
                else -> mutableUiState.update { it.copy(currentOffsetEnabled = enabled) }
            }
        }
    }

    fun onEmergencyStopToggled() {
        if (!pageActive) return
        val enabled = !mutableUiState.value.emergencyStopEnabled
        viewModelScope.launch {
            when (val result = repository.setEmergencyStop(enabled)) {
                is ChassisCommandResult.Rejected -> mutableUiState.update { it.copy(error = result.error) }
                else -> mutableUiState.update { it.copy(emergencyStopEnabled = enabled) }
            }
        }
    }

    fun onControlChanged(field: ChassisControlField, value: Int, interactionGeneration: Long) {
        val state = mutableUiState.value
        if (!pageActive || !state.canControl(field) || state.generation(field) != interactionGeneration) return
        if (!repository.config.isValid(ChassisControl(field, value))) {
            mutableUiState.update { it.copy(error = ChassisError.InvalidControl) }
            return
        }
        editedFields.add(field)
        mutableUiState.update { it.copy(controls = it.controls.withValue(field, value)) }
    }

    fun onControlCommitted(field: ChassisControlField, interactionGeneration: Long) {
        val state = mutableUiState.value
        if (!pageActive || !state.canControl(field) || state.generation(field) != interactionGeneration) return
        if (!editedFields.remove(field)) return
        val control = ChassisControl(field, state.controls.value(field))
        viewModelScope.launch {
            val result = repository.submitControl(control)
            if (result is ChassisCommandResult.Rejected) {
                lockControls()
                mutableUiState.update { it.copy(error = result.error) }
            }
        }
    }

    fun onControlInputSubmitted(field: ChassisControlField, text: String, interactionGeneration: Long) {
        val state = mutableUiState.value
        if (!pageActive || !state.canControl(field) || state.generation(field) != interactionGeneration) return
        val value = text.toIntOrNull()
        if (value == null || !repository.config.isValid(ChassisControl(field, value))) {
            mutableUiState.update { it.copy(error = ChassisError.InvalidControl) }
            return
        }
        onControlChanged(field, value, interactionGeneration)
        onControlCommitted(field, interactionGeneration)
    }

    fun onCombinedBrakesChanged(value: Int, interactionGeneration: Long) {
        val state = mutableUiState.value
        if (!pageActive || !state.canControl(ChassisControlField.Ehb) ||
            state.generation(ChassisControlField.Ehb) != interactionGeneration) return
        if (value !in repository.config.combinedBrakeForceN) {
            mutableUiState.update { it.copy(error = ChassisError.InvalidControl) }
            return
        }
        editedFields.addAll(listOf(ChassisControlField.Ehb, ChassisControlField.Emb))
        mutableUiState.update {
            it.copy(controls = it.controls.copy(ehbForceN = value, embForceN = value))
        }
    }

    fun onCombinedBrakesInputSubmitted(text: String, interactionGeneration: Long) {
        val state = mutableUiState.value
        if (!pageActive || !state.canControl(ChassisControlField.Ehb) ||
            state.generation(ChassisControlField.Ehb) != interactionGeneration) return
        val value = text.toIntOrNull()
        if (value == null || value !in repository.config.combinedBrakeForceN) {
            mutableUiState.update { it.copy(error = ChassisError.InvalidControl) }
            return
        }
        onCombinedBrakesChanged(value, interactionGeneration)
        editedFields.removeAll(listOf(ChassisControlField.Ehb, ChassisControlField.Emb))
        viewModelScope.launch {
            val result = repository.submitCombinedBrakes(value)
            if (result is ChassisCommandResult.Rejected) {
                lockControls()
                mutableUiState.update { it.copy(error = result.error) }
            }
        }
    }

    fun onCompareOpened() {
        if (!pageActive || mutableUiState.value.comparisonOpen) return
        mutableUiState.update { it.copy(comparisonOpen = true, comparisonResult = null) }
        val previousRequest = comparisonRequest
        comparisonRequest = viewModelScope.launch {
            // Finish unregistering an earlier request before registering the same command ID.
            previousRequest?.cancelAndJoin()
            val result = repository.requestComparison()
            ensureActive()
            mutableUiState.update { it.copy(comparisonResult = result) }
        }
    }

    fun onCompareClosed() {
        comparisonRequest?.cancel()
        mutableUiState.update { it.copy(comparisonOpen = false, comparisonResult = null) }
    }

    fun dismissError() {
        mutableUiState.update { it.copy(error = null) }
    }

    private fun appendTelemetry(sample: ChassisTelemetry) {
        mutableUiState.update { state ->
            if (state.telemetry != null && sample.timestampMillis < state.telemetry.timestampMillis) state
            else {
                val windowStart = sample.timestampMillis - HISTORY_WINDOW_MILLIS
                // Retain one sample before the window to connect the curve across its left edge.
                val firstRetained = state.history.indexOfLast {
                    it.timestampMillis < windowStart
                }.coerceAtLeast(0)
                val recent = state.history.subList(firstRetained, state.history.size).filter {
                    it.timestampMillis != sample.timestampMillis
                }
                state.copy(
                    telemetry = sample,
                    history = Collections.unmodifiableList(recent + sample),
                )
            }
        }
    }

    override fun onCleared() {
        onPageExited()
        super.onCleared()
    }

    companion object {
        const val HISTORY_WINDOW_SECONDS = 10
        const val HISTORY_WINDOW_MILLIS = HISTORY_WINDOW_SECONDS * 1_000L

        fun provideFactory(repository: ChassisRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    require(modelClass.isAssignableFrom(ChassisViewModel::class.java))
                    @Suppress("UNCHECKED_CAST")
                    return ChassisViewModel(repository) as T
                }
            }
    }
}
