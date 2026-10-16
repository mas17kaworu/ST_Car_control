package com.longkai.stcarcontrol.st_exp.compose.ui.chassis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.ArrayDeque
import java.util.Collections
import java.util.concurrent.atomic.AtomicReference

class ChassisViewModel(
    private val repository: ChassisRepository,
    private val clockMillis: () -> Long = { System.nanoTime() / 1_000_000L },
    private val receptionDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(
        ChassisUiState(config = repository.config, isDemo = repository.isDemo)
    )
    val uiState = mutableUiState.asStateFlow()
    private val mutableTelemetryState = MutableStateFlow(ChassisTelemetryState())
    val telemetryState = mutableTelemetryState.asStateFlow()
    private var pageActive = false
    private val editedFields = mutableSetOf<ChassisControlField>()
    private var reception: Job? = null
    private val receptionMutex = Mutex()
    private var comparisonRequest: Job? = null
    private var brakeResponseRequest: Job? = null
    private val brakeResponseRecording = AtomicReference<ChassisBrakeResponse?>()

    /** Reception belongs to the page, not to a selected send mode or a Compose subscription. */
    fun onPageEntered() {
        if (pageActive) return
        pageActive = true
        reception = viewModelScope.launch(receptionDispatcher) {
            // A rapid exit/re-entry must finish unregistering before the next subscription starts.
            receptionMutex.withLock {
                val history = ArrayDeque(mutableTelemetryState.value.history)
                val historyMutex = Mutex()
                repository.telemetry.onEach { sample ->
                    historyMutex.withLock { appendTelemetry(history, sample) }
                }.conflate().collect {
                    // Only display notifications are conflated; every received sample is stored first.
                    val snapshot = historyMutex.withLock { history.toList() }
                    val display = ChassisTelemetryState.fromHistory(snapshot)
                    ensureActive()
                    mutableTelemetryState.value = display
                    delay(DISPLAY_INTERVAL_MILLIS)
                }
            }
        }
    }

    fun onPageExited() {
        pageActive = false
        onCompareClosed()
        onBrakeResponseClosed()
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

    fun onBrakeChartMaxSubmitted(text: String) {
        if (!pageActive) return
        val maximum = text.toIntOrNull()
        if (maximum == null || maximum <= 0) {
            mutableUiState.update { it.copy(error = ChassisError.InvalidControl) }
            return
        }
        mutableUiState.update { it.copy(brakeChartMaxN = maximum) }
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
        onBrakeResponseClosed()
        brakeResponseRequest = viewModelScope.launch {
            val startedAtMillis = clockMillis()
            // Start before submission so an immediate report is not lost.
            brakeResponseRecording.set(ChassisBrakeResponse(startedAtMillis))
            val result = repository.submitCombinedBrakes(value)
            ensureActive()
            when (result) {
                is ChassisCommandResult.Rejected -> {
                    brakeResponseRecording.set(null)
                    lockControls()
                    mutableUiState.update { it.copy(error = result.error) }
                }
                ChassisCommandResult.PreviewOnly -> brakeResponseRecording.set(null)
                ChassisCommandResult.Submitted -> {
                    delay((startedAtMillis + BRAKE_RESPONSE_WINDOW_MILLIS - clockMillis()).coerceAtLeast(0L))
                    val response = checkNotNull(brakeResponseRecording.getAndSet(null))
                    mutableUiState.update { it.copy(brakeResponse = response) }
                }
            }
        }
    }

    fun onBrakeResponseClosed() {
        brakeResponseRequest?.cancel()
        brakeResponseRequest = null
        brakeResponseRecording.set(null)
        mutableUiState.update { it.copy(brakeResponse = null) }
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

    private fun appendTelemetry(history: ArrayDeque<ChassisTelemetry>, sample: ChassisTelemetry) {
        brakeResponseRecording.updateAndGet { recording ->
            if (recording != null && sample.timestampMillis in recording.startedAtMillis until
                (recording.startedAtMillis + BRAKE_RESPONSE_WINDOW_MILLIS)) {
                recording.copy(samples = Collections.unmodifiableList(recording.samples + sample))
            } else recording
        }
        val latest = history.peekLast()
        if (latest != null && sample.timestampMillis < latest.timestampMillis) return
        if (latest?.timestampMillis == sample.timestampMillis) history.removeLast()
        history.addLast(sample)
        val windowStart = sample.timestampMillis - HISTORY_WINDOW_MILLIS
        var boundary: ChassisTelemetry? = null
        while (history.isNotEmpty() && history.first.timestampMillis < windowStart) {
            boundary = history.removeFirst()
        }
        // Keep the predecessor so the line reaches the window's left edge.
        boundary?.let { history.addFirst(it) }
    }

    override fun onCleared() {
        onPageExited()
        super.onCleared()
    }

    companion object {
        const val HISTORY_WINDOW_SECONDS = 10
        const val HISTORY_WINDOW_MILLIS = HISTORY_WINDOW_SECONDS * 1_000L
        const val BRAKE_WINDOW_SECONDS = 1
        const val DISPLAY_INTERVAL_MILLIS = 34L
        const val BRAKE_RESPONSE_WINDOW_MILLIS = 2_000L

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
