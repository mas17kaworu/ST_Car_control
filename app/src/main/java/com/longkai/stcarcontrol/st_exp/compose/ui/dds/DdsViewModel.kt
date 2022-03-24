package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ExpressService
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ServiceAction
import com.longkai.stcarcontrol.st_exp.compose.data.successOr
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DdsUiState(
    val expressServices: List<ExpressService> = emptyList(),
    val avasActions: List<ServiceAction.AvasAction> = emptyList(),
    val oledActions: List<ServiceAction.OledAction> = emptyList(),
    val actionOptions: List<ServiceAction> = emptyList(),
    val loading: Boolean = false
)

class DdsViewModel(
    private val ddsRepo: DdsRepo
): ViewModel() {

    private val _uiState = MutableStateFlow(DdsUiState(loading = true))
    val uiState: StateFlow<DdsUiState> = _uiState.asStateFlow()

    init {
        refreshAll()
    }

    private fun refreshAll() {
        _uiState.update { it.copy(loading = true) }

        viewModelScope.launch {
            val expressServiceDeferred = async { ddsRepo.expressServices() }
            val avasActionsDeferred = async { ddsRepo.avasActions() }
            val oledActionsDeferred = async { ddsRepo.oledActions() }

            val expressServices = expressServiceDeferred.await().successOr(emptyList())
            val avasActions = avasActionsDeferred.await().successOr(emptyList())
            val oledActions = oledActionsDeferred.await().successOr(emptyList())
            val actionOptions = avasActions + ServiceAction.Delay(seconds = 5) + oledActions

            _uiState.update {
                it.copy(
                    expressServices = expressServices,
                    avasActions = avasActions,
                    oledActions = oledActions,
                    actionOptions = actionOptions,
                    loading = false
                )
            }
        }
    }

    companion object {
        fun provideFactory(
            ddsRepo: DdsRepo
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return DdsViewModel(ddsRepo) as T
            }
        }
    }
}