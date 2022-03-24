package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import com.longkai.stcarcontrol.st_exp.compose.data.Result
import com.longkai.stcarcontrol.st_exp.compose.data.dds.fakeAvasActions
import com.longkai.stcarcontrol.st_exp.compose.data.dds.fakeExpressServices
import com.longkai.stcarcontrol.st_exp.compose.data.dds.fakeOledActions
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ExpressService
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ServiceAction.AvasAction
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ServiceAction.OledAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

interface DdsRepo {
    suspend fun expressServices(): Result<List<ExpressService>>
    suspend fun avasActions(): Result<List<AvasAction>>
    suspend fun oledActions(): Result<List<OledAction>>
}

class DdsRepoImpl() : DdsRepo {
    override suspend fun expressServices(): Result<List<ExpressService>> {
        return withContext(Dispatchers.IO) {
            delay(200)
            Result.Success(fakeExpressServices)
        }
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

    private var requestCount = 0
    private fun shouldRandomlyFail(): Boolean = ++requestCount % 5 == 0
}