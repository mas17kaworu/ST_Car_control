package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.longkai.stcarcontrol.st_exp.ai.AbilityCallback
import com.longkai.stcarcontrol.st_exp.ai.EsrHelper
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDZCU.CMDZCU
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDZCU.CMDZCU.LinkStatus
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDZCU.CMDZCUEfuse
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter
import com.longkai.stcarcontrol.st_exp.compose.data.AIRepo
import com.longkai.stcarcontrol.st_exp.compose.data.dds.DdsRepo
import com.longkai.stcarcontrol.st_exp.compose.data.dds.fakeExpressServices
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ExpressService
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ExpressServiceParam
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ServiceAction
import com.longkai.stcarcontrol.st_exp.compose.data.successOr
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DdsUiState(
    val expressServices: List<ExpressService> = emptyList(),//fakeExpressServices,//emptyList(),
    val avasActions: List<ServiceAction.AvasAction> = emptyList(),
    val oledActions: List<ServiceAction.OledAction> = emptyList(),
    val actionOptions: List<ServiceAction> = emptyList(),
    val loading: Boolean = false,
    val focusedService: ExpressService? = null,
    val isAISDKInitSuccess: Boolean = false,
    val aiListenResult: String = "",
    val listening: Boolean = false,
    val aiLanguage: DdsViewModel.Language = DdsViewModel.Language.Chinese,
    val currentFlow: Flow<Float> = emptyFlow(),
    val voltageFlow: Flow<Float> = emptyFlow(),
    val deviceTempFlow: Flow<Float> = emptyFlow(),
    val current: Float? = null,
    val voltage: Float? = null,
    val tempDevice: Float? = null,
    val tempMos: Float? = null,
    val link1Status: Boolean = false,
    val link2Status: Boolean = false,
    val link3Status: Boolean = false,
    val link4Status: Boolean = false,
    val keyWords: List<String> = emptyList(),
)

class DdsViewModel(
    private val ddsRepo: DdsRepo,
    private val aiRepo: AIRepo,
): ViewModel() {

    private val _uiState = MutableStateFlow(DdsUiState(loading = true))
    val uiState: StateFlow<DdsUiState> = _uiState.asStateFlow()

    private val _currentFlow = MutableStateFlow(0f)
    private val _deviceTempFlow = MutableStateFlow(0f)
    private val _voltageFlow = MutableStateFlow(0f)

    private val zcuCommand = CMDZCU()
    private val zcuEfuseCommand = CMDZCUEfuse()

    private val esrAbilityCallback: AbilityCallback = object : AbilityCallback {
        override fun onAbilityBegin() {
            Log.i("Longkai", "onAbilityBegin ")
            _uiState.update {
                it.copy(
                    aiListenResult = "",
                    listening = true,
                )
            }
        }

        override fun onAbilityResult(result: String) {
            Log.i("Longkai", "onAbilityResult $result")
            aiRepo.emitAbilityResult(result)
            val index = result.getTargetServiceIndex()

            Log.i("Longkai", "Going to execute service $index")
            index?.let {
                if (_uiState.value.expressServices.size > index) {
                    val service = _uiState.value.expressServices[index]
                    _uiState.update {
                        it.copy(focusedService = service)
                    }
                    executeExpressService(service)
                    aiRepo.emitAbilityResult(
                        "$result Service ${index + 1} sent!"
                    )
                }
            }
        }

        private fun String.getTargetServiceIndex(): Int? {
            var serviceIndex : Int? = null
            aiRepo.keyWordList.onEachIndexed { index , serviceCommandList ->
                val foundString = serviceCommandList.find { keyWord ->
                    this.contains(keyWord)
                }
                if (foundString != null) {
                    serviceIndex =  index
                }
            }
            return serviceIndex
        }

        override fun onAbilityError(code: Int, error: Throwable?) {
            Log.i("Longkai", "onAbilityError $code ${error?.message}")
            _uiState.update {
                it.copy(
                    aiListenResult = "Error: ${error?.message}",
                    listening = false,
                )
            }
        }

        override fun onAbilityEnd() {
            Log.i("Longkai", "onAbilityEnd ")
            _uiState.update {
                it.copy(
                    listening = false,
                )
            }
        }
    }

    private val esrHelper = EsrHelper(esrAbilityCallback)

    init {
        aiRepo.initKeyWordsList()
        viewModelScope.launch {
            try {
                combine(
                    ddsRepo.expressServices(),
                    ddsRepo.avasActions,
                    ddsRepo.oledActions,
                    aiRepo.initStateFlow,
                ) { expressServices, avasActionsResult, oledActionsResult, isAISDKInitSuccess ->
                    val avasActions = avasActionsResult.successOr(emptyList())
                    val oledActions = oledActionsResult.successOr(emptyList())
                    val actionOptions = avasActions + ServiceAction.Delay(seconds = 5) + oledActions

                    _uiState.update {
                        it.copy(
                            expressServices = expressServices,
                            avasActions = avasActions,
                            oledActions = oledActions,
                            actionOptions = actionOptions,
                            loading = false,
                            isAISDKInitSuccess = isAISDKInitSuccess,
                            currentFlow = _currentFlow.asStateFlow(),
                            deviceTempFlow = _deviceTempFlow.asStateFlow(),
                            voltageFlow = _voltageFlow.asStateFlow(),
                            keyWords = aiRepo.getKeyWordFromPrefs(),
                        )
                    }
                }.flatMapMerge {
                    aiRepo.abilityResult.map { result ->
                        _uiState.update {
                            it.copy(
                                aiListenResult = result
                            )
                        }
                    }
                }
                    .collect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createExpressService(serviceParam: ExpressServiceParam) {
        viewModelScope.launch {
            val newService = ddsRepo.createExpressService(serviceParam)
            onSelectService(newService)
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
            onSelectService(null)
        }
    }

    fun executeExpressService(service: ExpressService) {
        viewModelScope.launch {
            ddsRepo.executeExpressService(service)
        }
    }

    fun onSelectService(service: ExpressService?) {
        _uiState.update { it.copy(focusedService = service) }
    }

    fun selectAiLanguage(language: Language) {
        _uiState.update { it.copy(aiLanguage = language) }
    }

    fun trySendSomething() {
        viewModelScope.launch {
            ddsRepo.trySendSomething()
        }
    }

    fun startListen() {
        if (_uiState.value.listening) {
            esrHelper.stopAudioRecord()
        } else {
            esrHelper.startAudioRecord(_uiState.value.aiLanguage.ordinal)
        }
    }

    fun updateRecordKeyWords(index: Int, value: String) {
        aiRepo.updateKeyword(index, value)
    }

    fun updateRecordKeyWords(list: List<String>) {
        _uiState.update {
            it.copy(
                keyWords = list
            )
        }
        aiRepo.updateKeyword(list)
    }

    fun registerZCUCommandListener() {
        ServiceManager.getInstance().registerRegularlyCommand(
            zcuEfuseCommand, object : CommandListenerAdapter<CMDZCUEfuse.Response>() {
                override fun onSuccess(response: CMDZCUEfuse.Response?) {
                    _currentFlow.update { response?.current ?: 0f }
                    _deviceTempFlow.update { response?.tempDevice ?: 0f }
                    _voltageFlow.update { response?.voltage ?: 0f }
                    _uiState.update {
                        it.copy(
                            current = response?.current,
                            voltage = response?.voltage,
                            tempMos = response?.tempMos,
                            tempDevice = response?.tempDevice,
                        )
                    }
                }
            }
        )

        ServiceManager.getInstance().registerRegularlyCommand(
            zcuCommand, object : CommandListenerAdapter<CMDZCU.Response>() {
                override fun onSuccess(response: CMDZCU.Response?) {
                    _uiState.update {
                        it.copy(
                            link1Status = response?.link1Status == LinkStatus.OK,
                            link2Status = response?.link2Status == LinkStatus.OK,
                            link3Status = response?.link3Status == LinkStatus.OK,
                            link4Status = response?.link4Status == LinkStatus.OK,
                        )
                    }
                }
            }
        )
    }

    fun unregisterZCUCommandListener() {
        ServiceManager.getInstance().unregisterRegularlyCommand(zcuEfuseCommand)
        ServiceManager.getInstance().unregisterRegularlyCommand(zcuCommand)
    }

    enum class Language {
        Chinese,
        English,
        ;
    }

    companion object {
        fun provideFactory(
            ddsRepo: DdsRepo,
            aiRepo: AIRepo,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DdsViewModel(ddsRepo, aiRepo) as T
            }
        }
    }
}