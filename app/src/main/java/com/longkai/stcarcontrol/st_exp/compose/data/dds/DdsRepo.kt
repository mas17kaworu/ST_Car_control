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
import com.longkai.stcarcontrol.st_exp.compose.data.dds.service.TopicData
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
    suspend fun createExpressService(serviceParam: ExpressServiceParam)
    suspend fun updateExpressService(service: ExpressService)
    suspend fun deleteExpressService(service: ExpressService)
    suspend fun executeExpressService(service: ExpressService)
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

    private val _digitalKeyUnlocked = MutableStateFlow<Boolean>(false)
    val digitalKeyUnlocked: StateFlow<Boolean> = _digitalKeyUnlocked

    init {
        ddsService.start()
        ddsService.registerTopicListener(object : DdsService.TopicListener {
            override fun onAvasDataAvailable(topicData: TopicData) {
                _avasActions.update { Result.Success(topicData.toAvasActions()) }
            }

            override fun onOledDataAvailable(topicData: TopicData) {
                _oledActions.update { Result.Success(topicData.toOledActions()) }
            }

            override fun onDigitalKeyStateChanged(unlocked: Boolean) {
                _digitalKeyUnlocked.update { unlocked }
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

//    override suspend fun avasActions(): Result<List<AvasAction>> {
//        return withContext(Dispatchers.IO) {
//            delay(800)
//            if (shouldRandomlyFail()) {
//                Result.Error(IllegalStateException())
//            } else {
//                Result.Success(fakeAvasActions)
//            }
//        }
//    }
//
//    override suspend fun oledActions(): Result<List<OledAction>> {
//        return withContext(Dispatchers.IO) {
//            delay(800)
//            if (shouldRandomlyFail()) {
//                Result.Error(IllegalStateException())
//            } else {
//                Result.Success(fakeOledActions)
//            }
//        }
//    }

    override suspend fun createExpressService(serviceParam: ExpressServiceParam) {
        withContext(Dispatchers.IO) {
            context.appPrefsDataStore.edit { prefs ->
                val oldServicesString = prefs[EXPRESS_SERVICES]
                val newServices = if (oldServicesString != null) {
                    val oldServices = Json.decodeFromString<List<ExpressService>>(oldServicesString)
                    oldServices + ExpressService(
                        id = oldServices.lastOrNull()?.id?.plus(1) ?: 0,
                        name = serviceParam.name,
                        triggerCondition = serviceParam.triggerCondition,
                        actions = serviceParam.actions
                    )
                } else {
                    listOf(
                        ExpressService(
                            id = 0,
                            name = serviceParam.name,
                            triggerCondition = serviceParam.triggerCondition,
                            actions = serviceParam.actions
                        )
                    )
                }
                prefs[EXPRESS_SERVICES] = Json.encodeToString(newServices)
            }
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

    private var requestCount = 0
    private fun shouldRandomlyFail(): Boolean = ++requestCount % 5 == 0
}