package com.longkai.stcarcontrol.st_exp.compose.ui.chassis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisCommandResult
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisControlState
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisControlTab
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisError
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisRepository
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisTelemetry
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisTelemetryException
import java.util.Collections
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChassisViewModel(
    private val repository: ChassisRepository,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(
        ChassisUiState(config = repository.config, isDemo = repository.isDemo)
    )
    val uiState: StateFlow<ChassisUiState> = mutableUiState.asStateFlow()

    // One in-flight submission and at most one pending target; never replayed by UI collection.
    private val controlRequests = Channel<ChassisControlState>(Channel.CONFLATED)
    private var requestedControls = ChassisControlState()

    init {
        viewModelScope.launch {
            for (controls in controlRequests) {
                when (val result = repository.submitControls(controls)) {
                    is ChassisCommandResult.Rejected ->
                        mutableUiState.update { it.copy(error = result.error) }
                    ChassisCommandResult.PreviewOnly, ChassisCommandResult.Submitted -> Unit
                }
            }
        }
        viewModelScope.launch {
            mutableUiState.subscriptionCount
                .map { it > 0 }
                .distinctUntilChanged()
                .collectLatest { observed ->
                    if (observed) {
                        try {
                            repository.telemetry.collect { appendTelemetry(it) }
                        } catch (failure: ChassisTelemetryException) {
                            mutableUiState.update { it.copy(error = failure.error) }
                        }
                    }
                }
        }
    }

    fun onSpeedStepChanged(value: Int) = updateDraft { copy(speedStep = value) }

    fun onSteeringStepChanged(value: Int) = updateDraft { copy(steeringStep = value) }

    fun onEhbLevelChanged(value: Float) = updateDraft { copy(ehbLevel = value) }

    fun onEmbLevelChanged(value: Float) = updateDraft { copy(embLevel = value) }

    fun commitControls() {
        requestControls(mutableUiState.value.controls)
    }

    fun onCurrentOffsetChanged(enabled: Boolean) {
        updateDraft { copy(currentOffsetEnabled = enabled) }
        // An offset toggle must not also submit axes that are still being dragged.
        requestControls(requestedControls.copy(currentOffsetEnabled = enabled))
    }

    fun onControlTabSelected(tab: ChassisControlTab) {
        mutableUiState.update { it.copy(selectedControl = tab) }
    }

    fun dismissError() {
        mutableUiState.update { it.copy(error = null) }
    }

    private fun updateDraft(update: ChassisControlState.() -> ChassisControlState) {
        mutableUiState.update { state ->
            val controls = state.controls.update()
            if (state.config.isValid(controls)) {
                state.copy(controls = controls)
            } else {
                state.copy(error = ChassisError.InvalidControl)
            }
        }
    }

    private fun requestControls(controls: ChassisControlState) {
        if (!repository.config.isValid(controls)) {
            mutableUiState.update { it.copy(error = ChassisError.InvalidControl) }
            return
        }
        requestedControls = controls
        if (controlRequests.trySend(controls).isFailure) {
            mutableUiState.update { it.copy(error = ChassisError.NotReady) }
        }
    }

    private fun appendTelemetry(telemetry: ChassisTelemetry) {
        mutableUiState.update { state ->
            val lastTimestamp = state.telemetry?.timestampMillis
            if (lastTimestamp != null && telemetry.timestampMillis < lastTimestamp) {
                state
            } else {
                val recent = state.history.filter {
                    telemetry.timestampMillis - it.timestampMillis in 0L..HISTORY_WINDOW_MILLIS &&
                        it.timestampMillis != telemetry.timestampMillis
                }
                state.copy(
                    telemetry = telemetry,
                    history = Collections.unmodifiableList(
                        recent.takeLast(HISTORY_MAX_POINTS - 1) + telemetry
                    ),
                )
            }
        }
    }

    override fun onCleared() {
        controlRequests.cancel()
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
