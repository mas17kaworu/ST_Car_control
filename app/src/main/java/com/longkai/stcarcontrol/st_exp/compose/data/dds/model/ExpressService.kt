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

enum class TriggerCondition(val description: String) {
    /**
     *  It initially functions by double click, and changed to manually send.
     */
    DoubleClick("Manually send service"),
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

