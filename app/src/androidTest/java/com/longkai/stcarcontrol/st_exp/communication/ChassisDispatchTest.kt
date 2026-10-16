package com.longkai.stcarcontrol.st_exp.communication

import android.os.Bundle
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList.CMDChassisControl
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList.CMDChassisCompare
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList.CMDChassisEmergencyStop
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
    fun existingSendApiWritesChassisCommandsWithoutRegisteringAnAcknowledgment() {
        val connection = RecordingConnection()
        val dispatcher = ProtocolMessageDispatch(connection)
        val timeCommand = CMDChassisControl.setBrakeTimes(1, 255)
        val timeFrame = byteArrayOf(
            0x5A, 0x3C, 0x1A, 0x3D, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 1, 0xFF.toByte(), 0xA7.toByte()
        )
        assertArrayEquals(timeFrame, timeCommand.toRawData())
        for (invalid in listOf(-1, 256)) {
            assertTrue(runCatching { CMDChassisControl.setBrakeTimes(invalid, 0) }
                .exceptionOrNull() is IllegalArgumentException)
            assertTrue(runCatching { CMDChassisControl.setBrakeTimes(0, invalid) }
                .exceptionOrNull() is IllegalArgumentException)
        }
        val commands = listOf(timeCommand, CMDChassisControl.setSpeed(20), CMDChassisEmergencyStop(true),
            CMDChassisEmergencyStop(false), CMDChassisCompare())
        for (command in commands) {
            dispatcher.sendCommand(command, null)
            assertArrayEquals(command.toRawData(), connection.writes.last())
            assertEquals(0, dispatcher.sentCommandList.size())
        }
        assertEquals(5, connection.writes.size)
        assertArrayEquals(byteArrayOf(0x5A, 0x3C, 0x02, 0x40, 0xBD.toByte()), connection.writes.last())
    }

    @Test
    fun existingRegularSubscriptionReceivesWithoutSendingUntilUnregistered() {
        val connection = RecordingConnection()
        val dispatcher = ProtocolMessageDispatch(connection)
        val command = CMDChassisReport()
        val listener = Reports()
        dispatcher.registerRegularCommand(command, listener)
        assertTrue(connection.writes.isEmpty())
        dispatcher.onReceive(frame(), 0, 25)
        dispatcher.onReceive(frame(), 0, 25)
        assertEquals(2, listener.samples.size)
        assertEquals(0x8034, listener.samples.last().rampRaw)
        assertEquals(0xCDEF, listener.samples.last().voltageRaw)
        assertArrayEquals(frame(), listener.samples.last().mockResponse())
        assertEquals(0, listener.errors)
        dispatcher.unregisterRegularCommand(command)
        dispatcher.onReceive(frame(), 0, 25)
        assertEquals(2, listener.samples.size)
        assertTrue(connection.writes.isEmpty())
    }

    @Test
    fun invalidChassisChecksumDoesNotStopSubsequentReports() {
        val dispatcher = ProtocolMessageDispatch(RecordingConnection())
        val listener = Reports()
        dispatcher.registerRegularCommand(CMDChassisReport(), listener)
        val corrupt = frame().apply { this[24] = (this[24].toInt() xor 1).toByte() }
        dispatcher.onReceive(corrupt, 0, corrupt.size)
        assertEquals(1, listener.errors)
        assertTrue(listener.samples.isEmpty())
        dispatcher.onReceive(frame(), 0, 25)
        assertEquals(1, listener.samples.size)

        val comparison = CMDChassisCompare()
        val comparisonFrame = CMDChassisCompare.Response(List(50) { 0x8034 }, List(50) { 0xFFFF }).mockResponse()
        assertEquals(205, comparisonFrame.size)
        assertEquals(0xCA, comparisonFrame[2].toInt() and 0xFF)
        assertEquals(0x34, comparisonFrame[4].toInt() and 0xFF)
        assertEquals(0x80, comparisonFrame[5].toInt() and 0xFF)
        assertEquals(List(50) { 0x8034 }, comparison.toResponse(comparisonFrame).withoutOffsetN)
        assertEquals(List(50) { 0xFFFF }, comparison.toResponse(comparisonFrame).withOffsetN)
        for (invalid in listOf(
            comparisonFrame.copyOf(204),
            comparisonFrame.copyOf(206),
            comparisonFrame.copyOf().apply { this[2] = 0x12 },
            comparisonFrame.copyOf().apply { this[204] = (this[204].toInt() xor 1).toByte() }
        )) {
            assertTrue(runCatching { comparison.toResponse(invalid) }.exceptionOrNull() is IllegalArgumentException)
        }
    }

    @Test
    fun absentServiceProducesNoSyntheticReportsAndRejectsSubmission() = runBlocking {
        // Isolated Compose tests do not initialize the application's communication service.
        assertNull(ServiceManager.getInstance().messageDispatcher)
        val repository = DefaultChassisRepository()
        assertTrue(repository.telemetry.toList().isEmpty())
        assertEquals(ChassisComparisonResult.Failed(ChassisComparisonError.NotReady), repository.requestComparison())
        assertEquals(
            ChassisCommandResult.Rejected(ChassisError.NotReady),
            repository.submitControl(ChassisControl(ChassisControlField.Speed, 0))
        )
        assertEquals(ChassisCommandResult.Rejected(ChassisError.NotReady),
            repository.submitBrakeTimes(ChassisBrakeTimes(12, 34)))
        assertEquals(ChassisBrakeTimes(), repository.brakeTimes)
        for (enabled in listOf(true, false)) {
            assertEquals(ChassisCommandResult.Rejected(ChassisError.NotReady), repository.setEpb(enabled))
            assertEquals(ChassisCommandResult.Rejected(ChassisError.NotReady), repository.setCurrentOffset(enabled))
            assertEquals(ChassisCommandResult.Rejected(ChassisError.NotReady), repository.setEmergencyStop(enabled))
        }
    }

    private fun frame(): ByteArray = byteArrayOf(
        0x5A, 0x3C, 0x16, 0x3E,
        20, 0, 0, 0, 0x10, 0x27, 0, 0,
        0x20, 0x4E, 0, 0, 0x10, 0x27, 0, 0, 0x34, 0x80.toByte(), 0xEF.toByte(), 0xCD.toByte(), 0,
    ).apply { this[24] = CheckSumBit.checkSum(this, 24) }

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
