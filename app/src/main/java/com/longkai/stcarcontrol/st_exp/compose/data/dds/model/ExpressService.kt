package com.longkai.stcarcontrol.st_exp.compose.data.dds.model

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

//sealed class ServiceAction {
//    data class AvasAction(val soundEffect: Int) : ServiceAction() {
//        override fun name(): String {
//            return "Sound Effect $soundEffect"
//        }
//    }
//
//    data class OledAction(val lightEffect: Int) : ServiceAction() {
//        override fun name(): String {
//            return "Light Effect $lightEffect"
//        }
//    }
//
//    data class Delay(val seconds: Int): ServiceAction() {
//        override fun name(): String {
//            return "Delay"
//        }
//    }
//
//    abstract fun name(): String
//
//    companion object {
//        fun from(name: String): ServiceAction {
//            ServiceAction.
//        }
//    }
//}

sealed class ServiceAction {
    data class AvasAction(
        val name: String,
        val action: String? = null
    ): ServiceAction() {
        override fun toString(): String {
            return name
        }
    }

    data class OledAction(
        val name: String,
        val action: String? = null
    ): ServiceAction() {
        override fun toString(): String {
            return name
        }
    }

    data class Delay(
        val name: String = "Delay",
        val seconds: Int
    ): ServiceAction() {
        override fun toString(): String {
            return name
        }
    }
}

