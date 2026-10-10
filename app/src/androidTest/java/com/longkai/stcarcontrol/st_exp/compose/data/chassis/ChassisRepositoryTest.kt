package com.longkai.stcarcontrol.st_exp.compose.data.chassis

import android.os.Handler
import android.os.Looper
import androidx.test.platform.app.InstrumentationRegistry
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList.CMDChassisReport
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit
import com.longkai.stcarcontrol.st_exp.communication.utils.byteArrayToInt
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList.VCUChassisFragmentMock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ChassisRepositoryTest {
    private lateinit var service: ChassisServiceFixture

    @Before fun setUp() { service = ChassisServiceFixture() }
    @After fun tearDown() { if (::service.isInitialized) service.close() }

    @Test
    fun eachPhysicalControlUsesTheExistingSendApiWithOnlyItsOwnField() = runBlocking {
        val repository = DefaultChassisRepository(service.manager)
        assertFalse(repository.isDemo)
        for (field in ChassisControlField.values()) {
            val range = repository.config.range(field)
            for (value in listOf(range.first, 0, range.last)) {
                assertEquals(ChassisCommandResult.Submitted, repository.submitControl(ChassisControl(field, value)))
                service.awaitIdle()
                val frame = service.writes.last()
                assertEquals(27, frame.size)
                assertEquals(0x3D, frame[3].toInt())
                assertEquals(1 shl field.ordinal, byteArrayToInt(frame, 4))
                val raw = if (field == ChassisControlField.Steering) value * 100 else value
                for (other in ChassisControlField.values()) {
                    assertEquals(if (other == field) raw else 0, byteArrayToInt(frame, 8 + other.ordinal * 4))
                }
                assertEquals(0, frame[24].toInt())
                assertEquals(0, frame[25].toInt())
                if (field == ChassisControlField.Speed && value == -20) {
                    assertArrayEquals(
                        byteArrayOf(0xEC.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte()),
                        frame.copyOfRange(8, 12)
                    )
                }
                assertEquals(CheckSumBit.checkSum(frame.copyOfRange(2, 26), 24), frame[26])
            }
        }
        assertEquals(12, service.writes.size)
        assertEquals(0, service.registeredCommandCount)
    }

    @Test
    fun independentSwitchesSetOnlyTheirOwnValidityBitAndPayloadByte() = runBlocking {
        val repository = DefaultChassisRepository(service.manager)
        for (isEpb in listOf(false, true)) {
            for (enabled in listOf(true, false)) {
                val result = if (isEpb) repository.setEpb(enabled) else repository.setCurrentOffset(enabled)
                assertEquals(ChassisCommandResult.Submitted, result)
                service.awaitIdle()
                val frame = service.writes.last()
                assertEquals(27, frame.size)
                assertEquals(0x5A, frame[0].toInt())
                assertEquals(0x3C, frame[1].toInt())
                assertEquals(0x18, frame[2].toInt())
                assertEquals(0x3D, frame[3].toInt())
                assertEquals(if (isEpb) 0x20 else 0x10, byteArrayToInt(frame, 4))
                for (offset in listOf(8, 12, 16, 20)) assertEquals(0, byteArrayToInt(frame, offset))
                assertEquals(if (!isEpb && enabled) 0x55 else 0, frame[24].toInt())
                assertEquals(if (isEpb && enabled) 0x55 else 0, frame[25].toInt())
                assertEquals(CheckSumBit.checkSum(frame.copyOfRange(2, 26), 24), frame[26])
            }
        }
        assertEquals(4, service.writes.size)
        assertEquals(0, service.registeredCommandCount)
    }

    @Test
    fun emergencyStopUsesItsOwnCommandAndFourByteSwitchValue() = runBlocking {
        val repository = DefaultChassisRepository(service.manager)
        val enabledFrame = byteArrayOf(0x5A, 0x3C, 0x06, 0x3F, 0x55, 0, 0, 0, 0x65)
        val disabledFrame = byteArrayOf(0x5A, 0x3C, 0x06, 0x3F, 0, 0, 0, 0, 0xBA.toByte())
        for (enabled in listOf(true, false, true)) {
            assertEquals(ChassisCommandResult.Submitted, repository.setEmergencyStop(enabled))
            service.awaitIdle()
            assertArrayEquals(if (enabled) enabledFrame else disabledFrame, service.writes.last())
        }
        assertEquals(3, service.writes.size)
        assertEquals(0, service.registeredCommandCount)
    }

    @Test
    fun invalidControlsAreRejectedBeforeCallingTheService() = runBlocking {
        val repository = DefaultChassisRepository(service.manager)
        for (field in ChassisControlField.values()) {
            val range = repository.config.range(field)
            for (value in listOf(range.first - 1, range.last + 1, Int.MIN_VALUE, Int.MAX_VALUE)) {
                assertEquals(
                    ChassisCommandResult.Rejected(ChassisError.InvalidControl),
                    repository.submitControl(ChassisControl(field, value))
                )
            }
        }
        service.awaitIdle()
        assertTrue(service.writes.isEmpty())
    }

    @Test
    fun customRangesAreRespectedWithoutCancellingOrMergingSubmissions() = runBlocking {
        val repository = DefaultChassisRepository(service.manager, ChassisControlConfig(speedKph = 0..10))
        repeat(2) {
            assertEquals(
                ChassisCommandResult.Submitted,
                repository.submitControl(ChassisControl(ChassisControlField.Speed, 10))
            )
        }
        assertEquals(
            ChassisCommandResult.Rejected(ChassisError.InvalidControl),
            repository.submitControl(ChassisControl(ChassisControlField.Speed, 11))
        )
        service.awaitIdle()
        assertEquals(2, service.writes.size)
        assertArrayEquals(service.writes[0], service.writes[1])
    }

    @Test
    fun reportCollectionConvertsUnitsAndUnregistersWithoutSendingCommands() = runBlocking {
        val repository = DefaultChassisRepository(service.manager, clockMillis = { 123L })
        val samples = mutableListOf<ChassisTelemetry>()
        assertEquals(0, service.registeredCommandCount)
        val collection = launch(Dispatchers.Main) { repository.telemetry.collect { samples += it } }
        try {
            service.awaitIdle()
            assertEquals(1, service.registeredCommandCount)
            assertTrue(samples.isEmpty())
            service.receiveReport(CMDChassisReport.Response(20, -54_000, 20_000, 15_000, rampRaw = 12))
            service.receiveReport(CMDChassisReport.Response(21, 1234, 20_001, 0, rampRaw = 0x8000))
            service.receiveReport(CMDChassisReport.Response(-20, 0, 0xFFFF_FFFFL, 0xFFFF_FFFFL, rampRaw = 0xFFFF))
            service.receiveReport(CMDChassisReport.Response(-21, 0, 0, 0, rampRaw = 0))
            service.awaitIdle()
            assertEquals(
                listOf(
                    ChassisTelemetry(123, 20f, -540f, 20_000f, 15_000f, rampPercent = 12),
                    ChassisTelemetry(123, 21f, 12.34f, 20_001f, 0f, rampPercent = 32768),
                    ChassisTelemetry(123, -20f, 0f, 0xFFFF_FFFFL.toFloat(), 0xFFFF_FFFFL.toFloat(), rampPercent = 65535),
                    ChassisTelemetry(123, -21f, 0f, 0f, 0f, rampPercent = 0),
                ),
                samples
            )
        } finally {
            collection.cancelAndJoin()
            service.awaitIdle()
        }
        assertEquals(0, service.registeredCommandCount)
        service.receiveReport(CMDChassisReport.Response(0, 0, 0, 0, rampRaw = 0))
        service.awaitIdle()
        assertEquals(4, samples.size)
        assertTrue(service.writes.isEmpty())
    }

    @Test
    fun mockSpeedCyclesThroughNonnegativeValues() = runBlocking {
        val repository = DefaultChassisRepository(service.manager)
        val samples = mutableListOf<ChassisTelemetry>()
        val collection = launch(Dispatchers.Main) { repository.telemetry.collect { samples += it } }
        val mock = VCUChassisFragmentMock(Handler(Looper.getMainLooper()))
        try {
            service.awaitIdle()
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                repeat(201) { mock.run() }
                mock.stop()
            }
            service.awaitIdle()
            assertEquals(201, samples.size)
            assertEquals(0f, samples.first().speedKph, 0f)
            assertEquals(10f, samples[50].speedKph, 0f)
            assertEquals(20f, samples[100].speedKph, 0f)
            assertEquals(10f, samples[150].speedKph, 0f)
            assertEquals(0f, samples.last().speedKph, 0f)
            assertTrue(samples.all { it.speedKph in 0f..20f })
            assertTrue(samples.all { it.rampPercent in 0..20 })
            assertEquals(20, samples[60].rampPercent)
            assertTrue(service.writes.isEmpty())
        } finally {
            mock.stop()
            collection.cancelAndJoin()
            service.awaitIdle()
        }
    }

    @Test
    fun invalidReportIsLoggedWithoutEndingCollectionOrEmittingSyntheticData() = runBlocking {
        val repository = DefaultChassisRepository(service.manager)
        val samples = mutableListOf<ChassisTelemetry>()
        val collection = launch(Dispatchers.Main) { repository.telemetry.collect { samples += it } }
        try {
            service.awaitIdle()
            val corrupt = ByteArray(23).apply {
                this[0] = 0x5A
                this[1] = 0x3C
                this[2] = 0x14
                this[3] = 0x3E
                this[22] = (CheckSumBit.checkSum(this, 22).toInt() xor 1).toByte()
            }
            service.receiveFrame(corrupt)
            service.awaitIdle()
            assertTrue(samples.isEmpty())
            assertEquals(1, service.registeredCommandCount)
            service.receiveReport(CMDChassisReport.Response(5, 123, 456, 789, rampRaw = 12))
            service.awaitIdle()
            assertEquals(1, samples.size)
            assertEquals(1.23f, samples.single().steeringAngleDegrees, 0f)
        } finally {
            collection.cancelAndJoin()
            service.awaitIdle()
        }
    }
}
