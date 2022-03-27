package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.longkai.stcarcontrol.st_exp.compose.data.dds.DdsRepo
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ExpressService
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ExpressServiceParam
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ServiceAction
import com.longkai.stcarcontrol.st_exp.compose.data.successOr
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception

data class DdsUiState(
    val expressServices: List<ExpressService> = emptyList(),
    val avasActions: List<ServiceAction.AvasAction> = emptyList(),
    val oledActions: List<ServiceAction.OledAction> = emptyList(),
    val actionOptions: List<ServiceAction> = emptyList(),
    val loading: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class DdsViewModel(
    private val ddsRepo: DdsRepo
): ViewModel() {

    private val _uiState = MutableStateFlow(DdsUiState(loading = true))
    val uiState: StateFlow<DdsUiState> = _uiState.asStateFlow()

    init {
        refreshServiceActions()

        viewModelScope.launch {
            try {
                ddsRepo.expressServices().collect { expressServices ->
                    _uiState.update { it.copy(expressServices = expressServices) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun refreshServiceActions() {
        _uiState.update { it.copy(loading = true) }

        viewModelScope.launch {
            val avasActionsDeferred = async { ddsRepo.avasActions() }
            val oledActionsDeferred = async { ddsRepo.oledActions() }

            val avasActions = avasActionsDeferred.await().successOr(emptyList())
            val oledActions = oledActionsDeferred.await().successOr(emptyList())
            val actionOptions = avasActions + ServiceAction.Delay(seconds = 5) + oledActions

            _uiState.update {
                it.copy(
                    avasActions = avasActions,
                    oledActions = oledActions,
                    actionOptions = actionOptions,
                    loading = false
                )
            }
        }
    }

    fun createExpressService(serviceParam: ExpressServiceParam) {
        viewModelScope.launch {
            ddsRepo.createExpressService(serviceParam)
        }
    }

    fun updateExpressService(service: ExpressService) {
        viewModelScope.launch {
            ddsRepo.updateExpressService(service)
        }
    }

    fun deleteExpressService(service: ExpressService) {
        viewModelScope.launch {
            ddsRepo.deleteExpressService(service)
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