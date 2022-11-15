package com.longkai.stcarcontrol.st_exp.compose.data.dds.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object TriggerConditionSerializer : KSerializer<TriggerCondition> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TriggerCondition", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: TriggerCondition) {
        val string = value.name
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): TriggerCondition {
        val string = decoder.decodeString()
        return when (string) {
            TriggerCondition.DoubleClick.name -> TriggerCondition.ManuallySend // replaced by ManuallySend
            TriggerCondition.ManuallySend.name -> TriggerCondition.ManuallySend
            TriggerCondition.DigitalKeyLock.name -> TriggerCondition.DigitalKeyLock
            TriggerCondition.DigitalKeyUnlock.name -> TriggerCondition.DigitalKeyUnlock
            else -> TriggerCondition.ManuallySend
        }
    }
}