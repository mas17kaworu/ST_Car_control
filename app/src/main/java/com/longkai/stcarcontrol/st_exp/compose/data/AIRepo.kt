package com.longkai.stcarcontrol.st_exp.compose.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

interface AIRepo {
    val initStateFlow: StateFlow<Boolean>

    val abilityResult: Flow<String>

    val keyWordList: List<List<String>>

    fun initSuccess()

    fun emitAbilityResult(result: String)

    fun updateKeyword(index: Int, value: String)
}

class AIRepoImpl : AIRepo {
    private val _initStateFlow = MutableStateFlow(false)
    private val _abilityResult = MutableStateFlow("")
    private val _keyWordList: MutableList<List<String>> = arrayListOf(
        listOf("打开第一个服务", "我无聊了", "I am tired"), // 第一个服务默认的命令
        listOf("打开第二个务"),
        listOf("打开第三个务"),
        listOf("打开第四个务"),
    )

    override val initStateFlow: StateFlow<Boolean> = _initStateFlow
    override val abilityResult: Flow<String> = _abilityResult
    override val keyWordList: List<List<String>>
        get() = _keyWordList.toList()

    override fun initSuccess() {
        _initStateFlow.update {
            true
        }
    }

    override fun emitAbilityResult(result: String) {
        _abilityResult.update { result }
    }

    override fun updateKeyword(index: Int, value: String) {
        _keyWordList[index] = _keyWordList[index].toMutableList().apply { add(value) }.toList()
    }
}
