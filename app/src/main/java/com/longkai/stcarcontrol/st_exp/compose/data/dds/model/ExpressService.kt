package com.longkai.stcarcontrol.st_exp.compose.data.dds.model

import android.net.Uri
import kotlinx.serialization.Serializable

@Serializable
data class ExpressService(
    val id: Long,
    val name: String,
    val triggerCondition: TriggerCondition,
    val actions: List<ServiceAction>,
    val imageUri: String? = null
)

@Serializable(with = TriggerConditionSerializer::class)
enum class TriggerCondition(val description: String) {
    /**
     * Replaced by ManuallySend
     */
    DoubleClick("Double click to execute service"),
    ManuallySend("Manually send service"),
    DigitalKeyLock("Digital key lock door"),
    DigitalKeyUnlock("Digital key unlock door");

    override fun toString(): String {
        return description
    }

    companion object {
        fun availableOptions() = values().filter { it != DoubleClick }.toList()
    }
}

@Serializable
sealed class ServiceAction {

    @Serializable
    data class AvasAction(
        val name: String,
        val action: String
    ): ServiceAction() {
        override fun toString(): String {
            return name
        }
    }

    @Serializable
    data class OledAction(
        val name: String,
        val action: String
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

