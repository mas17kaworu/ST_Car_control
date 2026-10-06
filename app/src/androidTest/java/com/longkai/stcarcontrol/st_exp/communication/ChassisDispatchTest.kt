package com.longkai.stcarcontrol.st_exp.communication

import android.os.Bundle
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList.CMDChassisControl
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList.CMDChassisReport
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.*
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

/** Exercises the actual dispatcher without opening a socket or sending vehicle commands. */
class ChassisDispatchTest {
    @Test
    fun existingSendApiWritesSingleFieldCommandWithoutRegisteringAnAcknowledgment() {
        val connection = RecordingConnection()
        val dispatcher = ProtocolMessageDispatch(connection)
        val control = CMDChassisControl.setSpeed(20)
        dispatcher.sendCommand(control, null)
        assertArrayEquals(control.toRawData(), connection.writes.single())
        assertEquals(0, dispatcher.sentCommandList.size())
    }

    @Test
    fun existingRegularSubscriptionReceivesWithoutSendingUntilUnregistered() {
        val connection = RecordingConnection()
        val dispatcher = ProtocolMessageDispatch(connection)
        val command = CMDChassisReport()
        val listener = Reports()
        dispatcher.registerRegularCommand(command, listener)
        assertTrue(connection.writes.isEmpty())
        dispatcher.onReceive(frame(), 0, 21)
        dispatcher.onReceive(frame(), 0, 21)
        assertEquals(2, listener.samples.size)
        assertEquals(0, listener.errors)
        dispatcher.unregisterRegularCommand(command)
        dispatcher.onReceive(frame(), 0, 21)
        assertEquals(2, listener.samples.size)
        assertTrue(connection.writes.isEmpty())
    }

    @Test
    fun invalidChassisChecksumDoesNotStopSubsequentReports() {
        val dispatcher = ProtocolMessageDispatch(RecordingConnection())
        val listener = Reports()
        dispatcher.registerRegularCommand(CMDChassisReport(), listener)
        val corrupt = frame().apply { this[20] = (this[20].toInt() xor 1).toByte() }
        dispatcher.onReceive(corrupt, 0, corrupt.size)
        assertEquals(1, listener.errors)
        assertTrue(listener.samples.isEmpty())
        dispatcher.onReceive(frame(), 0, 21)
        assertEquals(1, listener.samples.size)
    }

    @Test
    fun absentServiceProducesNoSyntheticReportsAndRejectsSubmission() = runBlocking {
        // Isolated Compose tests do not initialize the application's communication service.
        assertNull(ServiceManager.getInstance().messageDispatcher)
        val repository = DefaultChassisRepository()
        assertTrue(repository.telemetry.toList().isEmpty())
        assertEquals(
            ChassisCommandResult.Rejected(ChassisError.NotReady),
            repository.submitControl(ChassisControl(ChassisControlField.Speed, 0))
        )
        for (enabled in listOf(true, false)) {
            assertEquals(ChassisCommandResult.Rejected(ChassisError.NotReady), repository.setEpb(enabled))
            assertEquals(ChassisCommandResult.Rejected(ChassisError.NotReady), repository.setCurrentOffset(enabled))
        }
    }

    private fun frame(): ByteArray = byteArrayOf(
        0x5A, 0x3C, 0x12, 0x3E,
        20, 0, 0, 0, 0x10, 0x27, 0, 0,
        0x20, 0x4E, 0, 0, 0x10, 0x27, 0, 0, 0,
    ).apply { this[20] = CheckSumBit.checkSum(this, 20) }

    private class Reports : CommandListenerAdapter<CMDChassisReport.Response>() {
        val samples = mutableListOf<CMDChassisReport.Response>()
        var errors = 0
        override fun onSuccess(response: CMDChassisReport.Response) { samples += response }
        override fun onTimeout() { errors++ }
    }

    private class RecordingConnection : ConnectionInterface {
        val writes = mutableListOf<ByteArray>()
        override fun open(parameter: Bundle?, listener: ConnectionListener?) = false
        override fun close() {}
        override fun writeDataBlock(data: ByteArray): Int {
            writes += data.copyOf()
            return data.size
        }
        override fun setReceiveListener(listener: MessageReceivedListener) {}
    }
}
