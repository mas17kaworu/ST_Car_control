package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.longkai.stcarcontrol.st_exp.ConstantData
import com.longkai.stcarcontrol.st_exp.STCarApplication
import com.longkai.stcarcontrol.st_exp.Utils.SharedPreferencesUtil
import com.longkai.stcarcontrol.st_exp.ai.AbilityCallback
import com.longkai.stcarcontrol.st_exp.ai.EsrHelper
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDZCU.CMDZCU
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDZCU.CMDZCU.LinkStatus
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDZCU.CMDZCUEfuse
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter
import com.longkai.stcarcontrol.st_exp.compose.data.AIRepo
import com.longkai.stcarcontrol.st_exp.compose.data.AIRepoImpl.Companion.KEY_CN_KEY_WORDS
import com.longkai.stcarcontrol.st_exp.compose.data.AIRepoImpl.Companion.KEY_EN_KEY_WORDS
import com.longkai.stcarcontrol.st_exp.compose.data.dds.DdsRepo
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ExpressService
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ExpressServiceParam
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ServiceAction
import com.longkai.stcarcontrol.st_exp.compose.data.dds.test.ScreenLog
import com.longkai.stcarcontrol.st_exp.compose.data.successOr
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    val currentFlow: Flow<Float?> = emptyFlow(),
    val voltageFlow: Flow<Float?> = emptyFlow(),
    val deviceTempFlow: Flow<Float?> = emptyFlow(),
    val current: Float? = null,
    val voltage: Float? = null,
    val tempDevice: Float? = null,
    val tempMos: Float? = null,
    val link1Status: Boolean = false,
    val link2Status: Boolean = false,
    val link3Status: Boolean = false,
    val link4Status: Boolean = false,
    val loadStatus: Boolean = false,
    val cnKeyWords: List<String> = emptyList(),
    val enKeyWords: List<String> = emptyList(),
)

class DdsViewModel(
    private val ddsRepo: DdsRepo,
    private val aiRepo: AIRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DdsUiState(loading = true))
    val uiState: StateFlow<DdsUiState> = _uiState.asStateFlow()

    private val _currentFlow = MutableStateFlow<Float?>(null)
    private val _deviceTempFlow = MutableStateFlow<Float?>(null)
    private val _voltageFlow = MutableStateFlow<Float?>(null)

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
            var serviceIndex: Int? = null
            aiRepo.keyWordList.onEachIndexed { index, serviceCommandList ->
                val foundString = serviceCommandList.find { keyWord ->
                    this.contains(keyWord)
                }
                if (foundString != null) {
                    serviceIndex = index
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
                    val languageIndex = SharedPreferencesUtil.get(
                        STCarApplication.CONTEXT,
                        SHAREDP_PREF_KEY_LANGUAGE,
                        0
                    ) as Int
                    val language = Language.values()[languageIndex]
                    _uiState.update {
                        it.copy(
                            expressServices = expressServices,
                            avasActions = avasActions,
                            oledActions = oledActions,
                            actionOptions = actionOptions,
                            loading = false,
                            aiLanguage = language,
                            isAISDKInitSuccess = isAISDKInitSuccess,
                            currentFlow = _currentFlow.asStateFlow(),
                            deviceTempFlow = _deviceTempFlow.asStateFlow(),
                            voltageFlow = _voltageFlow.asStateFlow(),
                            cnKeyWords = aiRepo.getKeyWordFromPrefs(KEY_CN_KEY_WORDS),
                            enKeyWords = aiRepo.getKeyWordFromPrefs(KEY_EN_KEY_WORDS),
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

    fun trySendSomething() {
        viewModelScope.launch {
            ddsRepo.trySendSomething()
        }
    }

    fun selectAiLanguage(language: Language) {
        SharedPreferencesUtil.put(
            STCarApplication.CONTEXT, SHAREDP_PREF_KEY_LANGUAGE, language.ordinal
        )
        _uiState.update { it.copy(aiLanguage = language) }
    }

    fun startListen() {
        ScreenLog.log("start Listen")
        if (_uiState.value.listening) {
            esrHelper.stopAudioRecord()
        } else {
            esrHelper.startAudioRecord(_uiState.value.aiLanguage.ordinal)
        }
    }

    fun updateRecordKeyWords(index: Int, value: String) {
        aiRepo.updateKeyword(index, value)
    }

    fun updateRecordKeyWords(listCN: List<String>, listEN: List<String>) {
        _uiState.update {
            it.copy(
                cnKeyWords = listCN,
                enKeyWords = listEN,
            )
        }
        aiRepo.updateKeyword(listCN, listEN)
        Toast.makeText(STCarApplication.CONTEXT, "saved", Toast.LENGTH_SHORT).show()
    }

    fun registerZCUCommandListener() {
        ServiceManager.getInstance().registerRegularlyCommand(
            zcuEfuseCommand, object : CommandListenerAdapter<CMDZCUEfuse.Response>() {
                override fun onSuccess(response: CMDZCUEfuse.Response?) {
                    _currentFlow.update { response?.current }
                    _deviceTempFlow.update { response?.tempDevice}
                    _voltageFlow.update { response?.voltage}
                    _uiState.update {
                        it.copy(
                            current = response?.current,
                            voltage = response?.voltage,
                            tempMos = response?.tempMos,
                            tempDevice = response?.tempDevice,
                            loadStatus = response?.loadStatus?.toBoolean() ?: it.loadStatus,
                        )
                    }
                }
            }
        )

        ServiceManager.getInstance().registerRegularlyCommand(
            zcuCommand, object : CommandListenerAdapter<CMDZCU.Response>() {
                private var job: Job? = null
                private var link1Job : Job? = null
                private var link2Job : Job? = null
                private var link3Job : Job? = null
                private var link4Job : Job? = null
                override fun onSuccess(response: CMDZCU.Response?) {
                    Log.i("Longkai", "${response?.link1Status?.toBoolean()} ${response?.link2Status?.toBoolean()} ${response?.link3Status?.toBoolean()} ${response?.link4Status?.toBoolean()}")
                    _uiState.update {
                        it.copy(
                            link1Status = if (response?.link1Status?.toBoolean() == true) {
                                link1Job?.cancel()
                                link1Job = viewModelScope.launch {
                                    delay(2000)
                                    _uiState.value = _uiState.value.copy(link1Status = false)
                                    Log.i("Longkai", "link1Job false")
                                }
                                true
                            } else it.link1Status,
                            link2Status = if (response?.link2Status?.toBoolean() == true) {
                                link2Job?.cancel()
                                link2Job = viewModelScope.launch {
                                    delay(2000)
                                    _uiState.value = _uiState.value.copy(link2Status = false)
                                    Log.i("Longkai", "link2Job false")
                                }
                                true
                            } else it.link2Status,
                            link3Status = if (response?.link3Status?.toBoolean() == true) {
                                link3Job?.cancel()
                                link3Job = viewModelScope.launch {
                                    delay(2000)
                                    _uiState.value = _uiState.value.copy(link3Status = false)
                                    Log.i("Longkai", "link3Job false")
                                }
                                true
                            } else it.link3Status,
                            link4Status = if (response?.link4Status?.toBoolean() == true) {
                                link4Job?.cancel()
                                link4Job = viewModelScope.launch {
                                    delay(2000)
                                    _uiState.value = _uiState.value.copy(link4Status = false)
                                    Log.i("Longkai", "link4Job false")
                                }
                                true
                            } else it.link4Status,
                        )
                    }
                    job?.cancel()
                    job = viewModelScope.launch {
                        delay(2000)
                        _uiState.value = _uiState.value.copy(
                            link1Status = false,
                            link2Status = false,
                            link3Status = false,
                            link4Status = false
                        )
                    }
                }
            }
        )
    }

    private fun LinkStatus.toBoolean(): Boolean? {
        return if (this == LinkStatus.OK) true
        else if (this == LinkStatus.Fail) false
        else null
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
        private val SHAREDP_PREF_KEY_LANGUAGE = "SHARED_PREF_KEY_LANGUAGE"

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