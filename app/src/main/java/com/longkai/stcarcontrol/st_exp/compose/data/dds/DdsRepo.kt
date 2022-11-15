package com.longkai.stcarcontrol.st_exp.compose.data.dds

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.longkai.stcarcontrol.st_exp.appPrefsDataStore
import com.longkai.stcarcontrol.st_exp.compose.data.Result
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.*
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ServiceAction.AvasAction
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ServiceAction.OledAction
import com.longkai.stcarcontrol.st_exp.compose.data.dds.service.DdsService
import com.longkai.stcarcontrol.st_exp.compose.data.dds.service.DdsService.DigitalKeyState
import com.longkai.stcarcontrol.st_exp.compose.data.dds.service.TopicData
import com.longkai.stcarcontrol.st_exp.compose.data.dds.test.ScreenLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

interface DdsRepo {
    fun expressServices(): Flow<List<ExpressService>>
    val avasActions: StateFlow<Result<List<AvasAction>>>
    val oledActions: StateFlow<Result<List<OledAction>>>
    val digitalKeyState: StateFlow<DigitalKeyState>
    suspend fun createExpressService(serviceParam: ExpressServiceParam): ExpressService
    suspend fun updateExpressService(service: ExpressService)
    suspend fun deleteExpressService(service: ExpressService)
    suspend fun executeExpressService(service: ExpressService)
    suspend fun trySendSomething()
}

class DdsRepoImpl(
    private val context: Context,
    private val ddsService: DdsService
) : DdsRepo {

    private val EXPRESS_SERVICES = stringPreferencesKey("express_services")

    private val _avasActions = MutableStateFlow<Result<List<AvasAction>>>(Result.Loading)
    override val avasActions: StateFlow<Result<List<AvasAction>>> = _avasActions

    private val _oledActions = MutableStateFlow<Result<List<OledAction>>>(Result.Loading)
    override val oledActions: StateFlow<Result<List<OledAction>>> = _oledActions

    private val _digitalKeyState = MutableStateFlow(DigitalKeyState.Reset)
    override val digitalKeyState: StateFlow<DigitalKeyState> = _digitalKeyState

    init {
        ddsService.start()
        ddsService.registerTopicListener(object : DdsService.TopicListener {
            override fun onAvasDataAvailable(topicData: TopicData) {
                ScreenLog.log( "onAvasDataAvailable")
                _avasActions.update { Result.Success(topicData.toAvasActions()) }
            }

            override fun onOledDataAvailable(topicData: TopicData) {
                ScreenLog.log( "onOledDataAvailable")
                _oledActions.update { Result.Success(topicData.toOledActions()) }
            }

            override fun onDigitalKeyStateChanged(keyState: DigitalKeyState) {
                ScreenLog.log( "onDigitalKeyStateChanged: $keyState")
                _digitalKeyState.update { keyState }
            }

        })
    }

    override fun expressServices(): Flow<List<ExpressService>> = context.appPrefsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            val servicesString = prefs[EXPRESS_SERVICES]
            servicesString?.let {
                Json.decodeFromString<List<ExpressService>>(it)
            } ?: emptyList()
        }

    override suspend fun createExpressService(serviceParam: ExpressServiceParam): ExpressService {
        return withContext(Dispatchers.IO) {
            lateinit var newService: ExpressService
            context.appPrefsDataStore.edit { prefs ->
                val oldServicesString = prefs[EXPRESS_SERVICES]
                val oldServices = oldServicesString?.let {
                    Json.decodeFromString<List<ExpressService>>(oldServicesString)
                }
                val newServiceId = oldServices?.lastOrNull()?.id?.plus(1) ?: 0
                newService = ExpressService(
                    id = newServiceId,
                    name = serviceParam.name,
                    triggerCondition = serviceParam.triggerCondition,
                    actions = serviceParam.actions,
                    imageUri = serviceParam.imageUri
                )
                val newServices = oldServices?.let { it + newService } ?: listOf(newService)
                prefs[EXPRESS_SERVICES] = Json.encodeToString(newServices)
            }
            newService
        }
    }

    override suspend fun updateExpressService(service: ExpressService) {
        withContext(Dispatchers.IO) {
            context.appPrefsDataStore.edit { prefs ->
                val oldServicesString = prefs[EXPRESS_SERVICES]
                if (oldServicesString != null) {
                    val oldServices = Json.decodeFromString<List<ExpressService>>(oldServicesString)
                    val index = oldServices.indexOfFirst { it.id == service.id }
                    val mutableList = oldServices.toMutableList()
                    if (index != -1) {
                        mutableList.removeAt(index)
                        mutableList.add(index, service)
                    } else {
                        mutableList.add(service)
                    }
                    prefs[EXPRESS_SERVICES] = Json.encodeToString(mutableList)
                }
            }
        }
    }

    override suspend fun deleteExpressService(service: ExpressService) {
        withContext(Dispatchers.IO) {
            context.appPrefsDataStore.edit { prefs ->
                val oldServicesString = prefs[EXPRESS_SERVICES]
                if (oldServicesString != null) {
                    val oldServices = Json.decodeFromString<List<ExpressService>>(oldServicesString)
                    val index = oldServices.indexOfFirst { it.id == service.id }
                    val mutableList = oldServices.toMutableList()
                    if (index != -1) {
                        mutableList.removeAt(index)
                    }
                    prefs[EXPRESS_SERVICES] = Json.encodeToString(mutableList)
                }
            }
        }
    }

    override suspend fun executeExpressService(service: ExpressService) {
        withContext(Dispatchers.IO) {
            service.actions.forEach { serviceAction ->
                when (serviceAction) {
                    is AvasAction -> ddsService.sendAvasAction(serviceAction.action.toByteArray())
                    is OledAction -> ddsService.sendOledAction(serviceAction.action.toByteArray())
                    is ServiceAction.Delay -> delay(serviceAction.seconds.toLong().times(1000))
                }
            }
        }
    }

    override suspend fun trySendSomething() {
        withContext(Dispatchers.IO) {
            ScreenLog.log("test")
            ddsService.sendAvasAction(ByteArray(3).apply {
                this[0] = 5
                this[1] = 6
            })
            ddsService.sendOledAction(ByteArray(2).apply {
                this[0] = 7
                // this[1] = 4
            })
        }
    }

    private var requestCount = 0
    private fun shouldRandomlyFail(): Boolean = ++requestCount % 5 == 0
}