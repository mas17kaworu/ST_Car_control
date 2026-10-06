package com.longkai.stcarcontrol.st_exp.compose.ui.chassis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.*
import java.util.Collections
import kotlinx.coroutines.Job
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
        lockControls()
        reception?.cancel()
        reception = null
    }

    fun lockControls() {
        editedFields.clear()
        mutableUiState.update { it.copy(selectedControl = null, controlGeneration = it.controlGeneration + 1) }
    }

    fun onControlTabSelected(tab: ChassisControlTab) {
        if (!pageActive || tab == ChassisControlTab.Epb) return
        val enable = mutableUiState.value.selectedControl != tab
        lockControls()
        if (enable) mutableUiState.update { it.copy(selectedControl = tab) }
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

    fun onControlChanged(field: ChassisControlField, value: Int, interactionGeneration: Long) {
        val state = mutableUiState.value
        if (!pageActive || !state.canControl(field) || state.controlGeneration != interactionGeneration) return
        if (!repository.config.isValid(ChassisControl(field, value))) {
            mutableUiState.update { it.copy(error = ChassisError.InvalidControl) }
            return
        }
        editedFields.add(field)
        mutableUiState.update { it.copy(controls = it.controls.withValue(field, value)) }
    }

    fun onControlCommitted(field: ChassisControlField, interactionGeneration: Long) {
        val state = mutableUiState.value
        if (!pageActive || !state.canControl(field) || state.controlGeneration != interactionGeneration) return
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

    fun dismissError() {
        mutableUiState.update { it.copy(error = null) }
    }

    private fun appendTelemetry(sample: ChassisTelemetry) {
        mutableUiState.update { state ->
            if (state.telemetry != null && sample.timestampMillis < state.telemetry.timestampMillis) state
            else {
                val recent = state.history.filter {
                    sample.timestampMillis - it.timestampMillis in 0L..HISTORY_WINDOW_MILLIS &&
                        it.timestampMillis != sample.timestampMillis
                }
                state.copy(
                    telemetry = sample,
                    history = Collections.unmodifiableList(recent.takeLast(HISTORY_MAX_POINTS - 1) + sample),
                )
            }
        }
    }

    override fun onCleared() {
        onPageExited()
        super.onCleared()
    }

    companion object {
        const val HISTORY_WINDOW_MILLIS = 30_000L
        const val HISTORY_MAX_POINTS = 300

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
