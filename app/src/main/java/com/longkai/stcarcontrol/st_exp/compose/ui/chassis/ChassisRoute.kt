package com.longkai.stcarcontrol.st_exp.compose.ui.chassis

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle

@Composable
fun ChassisRoute(
    viewModel: ChassisViewModel,
    modifier: Modifier = Modifier
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val stateFlow = remember(viewModel, lifecycle) {
        viewModel.uiState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
    }
    val state by stateFlow.collectAsState(initial = viewModel.uiState.value)

    ChassisScreen(
        state = state,
        onControlChanged = viewModel::onControlChanged,
        onControlCommitted = viewModel::onControlCommitted,
        onControlTabSelected = viewModel::onControlTabSelected,
        onEpbToggled = viewModel::onEpbToggled,
        onCurrentOffsetChanged = viewModel::onCurrentOffsetChanged,
        onDismissError = viewModel::dismissError,
        modifier = modifier
    )
}
