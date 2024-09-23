package com.longkai.stcarcontrol.st_exp.compose.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface ZCUDataRepo { // 要不要？
    val currentFlow: Flow<Float>
    val voltageFlow: Flow<Float>
    val deviceTempFlow: Flow<Float>
    val mosfetTempFlow: Flow<Float>

    fun updateCurrentValue(value: Float)
    fun updateVoltageValue(value: Float)
    fun updateDeviceTempValue(value: Float)
    fun updateMosfetValue(value: Float)
}

class ZCUDataRepoImpl: ZCUDataRepo {
    private val _currentFlow = MutableStateFlow(0f)
    private val _voltageFlow = MutableStateFlow(0f)
    private val _deviceTempFlow = MutableStateFlow(0f)
    private val _mosfetTempFlow = MutableStateFlow(0f)

    override val currentFlow: Flow<Float> = _currentFlow.asStateFlow()
    override val voltageFlow: Flow<Float> = _voltageFlow.asStateFlow()
    override val deviceTempFlow: Flow<Float> = _deviceTempFlow.asStateFlow()
    override val mosfetTempFlow: Flow<Float> = _mosfetTempFlow.asStateFlow()
    override fun updateCurrentValue(value: Float) {
        _currentFlow.update { value }
    }

    override fun updateVoltageValue(value: Float) {
        _voltageFlow.update { value }
    }

    override fun updateDeviceTempValue(value: Float) {
        _deviceTempFlow.update { value }
    }

    override fun updateMosfetValue(value: Float) {
        _mosfetTempFlow.update { value }
    }
}
