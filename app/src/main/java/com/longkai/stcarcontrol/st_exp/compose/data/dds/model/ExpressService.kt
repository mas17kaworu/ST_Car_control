package com.longkai.stcarcontrol.st_exp.compose.data.dds.model

import kotlinx.serialization.Serializable

@Serializable
data class ExpressService(
    val name: String,
    val triggerCondition: TriggerCondition,
    val actions: List<ServiceAction>
)

enum class TriggerCondition(val description: String) {
    DoubleClick("Double click service card"),
    DigitalKeyUnlock("Digital key unlock door");

    override fun toString(): String {
        return description
    }
}

@Serializable
sealed class ServiceAction {

    @Serializable
    data class AvasAction(
        val name: String,
        val action: String? = null
    ): ServiceAction() {
        override fun toString(): String {
            return name
        }
    }

    @Serializable
    data class OledAction(
        val name: String,
        val action: String? = null
    ): ServiceAction() {
        override fun toString(): String {
            return name
        }
    }

    @Serializable
    data class Delay(
        val name: String = "Delay",
        val seconds: Int
    ): ServiceAction() {
        override fun toString(): String {
            return name
        }
    }
}

