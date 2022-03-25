package com.longkai.stcarcontrol.st_exp.compose.data.dds

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.longkai.stcarcontrol.st_exp.appPrefsDataStore
import com.longkai.stcarcontrol.st_exp.compose.data.Result
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ExpressService
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ServiceAction.AvasAction
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ServiceAction.OledAction
import com.longkai.stcarcontrol.st_exp.fragment.SoundFragment
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
    suspend fun avasActions(): Result<List<AvasAction>>
    suspend fun oledActions(): Result<List<OledAction>>
    suspend fun saveExpressService(service: ExpressService)
}

class DdsRepoImpl(
    private val context: Context
) : DdsRepo {

    private val EXPRESS_SERVICES = stringPreferencesKey("express_services")

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

    override suspend fun avasActions(): Result<List<AvasAction>> {
        return withContext(Dispatchers.IO) {
            delay(800)
            if (shouldRandomlyFail()) {
                Result.Error(IllegalStateException())
            } else {
                Result.Success(fakeAvasActions)
            }
        }
    }

    override suspend fun oledActions(): Result<List<OledAction>> {
        return withContext(Dispatchers.IO) {
            delay(800)
            if (shouldRandomlyFail()) {
                Result.Error(IllegalStateException())
            } else {
                Result.Success(fakeOledActions)
            }
        }
    }

    override suspend fun saveExpressService(service: ExpressService) {
        withContext(Dispatchers.IO) {
            context.appPrefsDataStore.edit { prefs ->
                val oldServicesString = prefs[EXPRESS_SERVICES]
                val newServices = if (oldServicesString != null) {
                    val oldServices = Json.decodeFromString<List<ExpressService>>(oldServicesString)
                    oldServices + service
                } else {
                    listOf(service)
                }
                prefs[EXPRESS_SERVICES] = Json.encodeToString(newServices)
            }
        }
    }

    private var requestCount = 0
    private fun shouldRandomlyFail(): Boolean = ++requestCount % 5 == 0
}