package com.longkai.stcarcontrol.st_exp.compose.data.chassis.fake

import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisCommandResult
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisControlState
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisError
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisTelemetry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FakeChassisDeviceDataSourceTest {
    @Test
    fun `simulation is cold and stops sampling when its collector stops`() = runTest {
        var clockReads = 0
        val source = FakeChassisDeviceDataSource(clockMillis = {
            clockReads++
            testScheduler.currentTime
        })
        val samples = mutableListOf<ChassisTelemetry>()

        advanceTimeBy(1_000)
        assertEquals(0, clockReads)
        val collection = launch { source.telemetry.collect { samples += it } }
        runCurrent()
        advanceTimeBy(250)
        runCurrent()
        assertEquals(listOf(1_000L, 1_100L, 1_200L), samples.map { it.timestampMillis })
        collection.cancelAndJoin()
        val readsAtStop = clockReads
        val samplesAtStop = samples.size
        advanceTimeBy(10_000)

        assertEquals(readsAtStop, clockReads)
        assertEquals(samplesAtStop, samples.size)
    }

    @Test
    fun `commands are preview only and feedback approaches rather than acknowledges target`() =
        runTest {
            val source = FakeChassisDeviceDataSource(
                clockMillis = { testScheduler.currentTime }
            )
            val result = source.submitControls(
                ChassisControlState(
                    speedStep = -2,
                    steeringStep = 3,
                    ehbLevel = 0.5f,
                    embLevel = 0.5f,
                    currentOffsetEnabled = true,
                )
            )
            val samples = source.telemetry.take(11).toList()

            assertTrue(source.isDemo)
            assertEquals(ChassisCommandResult.PreviewOnly, result)
            assertEquals(0f, samples.first().speedKph, 0f)
            assertEquals(0f, samples.first().ehbPressureMpa, 0f)
            assertEquals(1_000L, samples.last().timestampMillis)
            assertTrue(samples.last().speedKph in -16f..-1f)
            assertTrue(samples.last().steeringAngleDegrees in 1f..18f)
            assertTrue(samples.last().ehbPressureMpa in 0.1f..4f)
            assertTrue(samples.last().embValue in 1f..50f)
            assertTrue(samples.all { it.ehbPressureMpa >= 0f && it.embValue >= 0f })
        }

    @Test
    fun `invalid direct preview request cannot change simulated target`() = runTest {
        val source = FakeChassisDeviceDataSource(clockMillis = { testScheduler.currentTime })
        assertEquals(
            ChassisCommandResult.Rejected(ChassisError.InvalidControl),
            source.submitControls(ChassisControlState(embLevel = Float.NaN)),
        )

        val samples = source.telemetry.take(3).toList()
        assertTrue(samples.all { it.embValue == 0f && it.speedKph == 0f })
    }

    @Test
    fun `timestamp comes from clock instead of sample number`() = runTest {
        var time = 40_000L
        val source = FakeChassisDeviceDataSource(
            clockMillis = { time },
            waitForNextSample = { time += 750L },
        )
        val samples = source.telemetry.take(3).toList()

        assertEquals(listOf(40_000L, 40_750L, 41_500L), samples.map { it.timestampMillis })
    }

    @Test
    fun `full preview targets remain inside displayed chart scales`() = runTest {
        val source = FakeChassisDeviceDataSource(clockMillis = { testScheduler.currentTime })
        source.submitControls(ChassisControlState(steeringStep = 5, ehbLevel = 1f, embLevel = 1f))
        val samples = source.telemetry.take(150).toList()
        assertTrue(samples.all { it.steeringAngleDegrees in -30f..30f })
        assertTrue(samples.all { it.ehbPressureMpa in 0f..12f })
        assertTrue(samples.all { it.embValue in 0f..30f })
    }
}
