package com.longkai.stcarcontrol.st_exp.ai

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.iflytek.aikit.core.AiHelper
import com.iflytek.aikit.core.BaseLibrary
import com.iflytek.aikit.core.ErrType
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.STCarApplication
import com.longkai.stcarcontrol.st_exp.compose.data.AIRepo
import kotlin.concurrent.thread

/**
 * @Desc: 讯飞语音初始化辅助类
 * @Author leon
 * @Date 2023/5/11-17:24
 * Copyright 2023 iFLYTEK Inc. All Rights Reserved.
 */
class IFlytekAbilityManager private constructor() {

    companion object {

        //在线授权校验间隔时长，默认为300s，可自定义设置，最短为60s，单位 秒
        private const val AUTH_INTERVAL = 333

        @Volatile
        private var instance: IFlytekAbilityManager? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: IFlytekAbilityManager().also { instance = it }
            }
    }

    /**
     * 初始化sdk
     * 只需要初始化一次
     */
    fun initializeSdk(
        context: Context,
        aiRepo: AIRepo,
    ) {
        val externalPath = STCarApplication.CONTEXT.getExternalFilesDir(null)?.path
        Log.i("IFlytekAbilityManager", "externalPath $externalPath" )
        val params = BaseLibrary.Params.builder()
            .appId(context.resources.getString(R.string.appId))
            .apiKey(context.resources.getString(R.string.apiKey))
            .apiSecret(context.resources.getString(R.string.apiSecret))
//            .workDir("${externalPath}/ai")
            .workDir("sdcard/ai")
            .iLogMaxCount(1)
            .authInterval(AUTH_INTERVAL)
            .ability(engineIds())
            .build()
        //鉴权
        AiHelper.getInst().registerListener { type, code ->
            when (type) {
                ErrType.AUTH -> {
                    Log.d(
                        "IFlytekAbilityManager",
                        "引擎初始化状态 ${type == ErrType.AUTH && code == 0} ${code}"
                    )
                    if (code == 0) {
                        aiRepo.initSuccess()
                    }
                }
                ErrType.HTTP -> {

                }
                else -> {

                }
            }
        }
        thread {
            AiHelper.getInst().init(context, params)
        }
    }

    /**
     * 添加所需的能力引擎id,多个能力用;隔开，如"xxx;xxx"
     */
    private fun engineIds() = listOf(
        AbilityConstant.ESR_ID,
    ).joinToString(separator = ";")
}