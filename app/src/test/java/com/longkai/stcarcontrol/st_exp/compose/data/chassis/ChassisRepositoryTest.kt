package com.longkai.stcarcontrol.st_exp.compose.data.chassis

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChassisRepositoryTest {
    @Test
    fun `valid positive negative and zero steps reach the adapter`() = runTest {
        val source = RecordingSource()
        val repository = DefaultChassisRepository(source)

        for (step in listOf(-5, 0, 5)) {
            val target = ChassisControlState(speedStep = step, steeringStep = step)
            assertEquals(ChassisCommandResult.PreviewOnly, repository.submitControls(target))
        }

        assertEquals(listOf(-5, 0, 5), source.targets.map { it.speedStep })
        assertEquals(listOf(-5, 0, 5), source.targets.map { it.steeringStep })
        assertTrue(repository.isDemo)
    }

    @Test
    fun `invalid step ranges and normalized levels never reach the adapter`() = runTest {
        val source = RecordingSource()
        val repository = DefaultChassisRepository(source)
        val invalid = listOf(
            ChassisControlState(speedStep = -6),
            ChassisControlState(speedStep = 6),
            ChassisControlState(steeringStep = -6),
            ChassisControlState(steeringStep = 6),
            ChassisControlState(ehbLevel = -0.1f),
            ChassisControlState(ehbLevel = 1.1f),
            ChassisControlState(embLevel = -0.1f),
            ChassisControlState(embLevel = 1.1f),
            ChassisControlState(ehbLevel = Float.NaN),
            ChassisControlState(ehbLevel = Float.POSITIVE_INFINITY),
            ChassisControlState(ehbLevel = Float.NEGATIVE_INFINITY),
            ChassisControlState(embLevel = Float.NaN),
            ChassisControlState(embLevel = Float.POSITIVE_INFINITY),
            ChassisControlState(embLevel = Float.NEGATIVE_INFINITY),
        )

        invalid.forEach {
            assertEquals(
                ChassisCommandResult.Rejected(ChassisError.InvalidControl),
                repository.submitControls(it),
            )
        }
        assertTrue(source.targets.isEmpty())
    }

    @Test
    fun `normalized brake levels accept both endpoints`() = runTest {
        val source = RecordingSource()
        val repository = DefaultChassisRepository(source)

        for (level in listOf(0f, 1f)) {
            repository.submitControls(ChassisControlState(ehbLevel = level, embLevel = level))
        }

        assertEquals(listOf(0f, 1f), source.targets.map { it.ehbLevel })
        assertEquals(listOf(0f, 1f), source.targets.map { it.embLevel })
    }

    @Test
    fun `configurable demo ranges and adapter results are preserved`() = runTest {
        val config = ChassisControlConfig(-2, 3, -1, 2)
        val source = RecordingSource(config, isDemo = false)
        val repository = DefaultChassisRepository(source)
        source.result = ChassisCommandResult.Rejected(ChassisError.NotReady)

        assertEquals(config, repository.config)
        assertFalse(repository.isDemo)
        assertEquals(
            ChassisCommandResult.Rejected(ChassisError.NotReady),
            repository.submitControls(ChassisControlState(speedStep = 3, steeringStep = -1)),
        )
        assertEquals(
            ChassisCommandResult.Rejected(ChassisError.InvalidControl),
            repository.submitControls(ChassisControlState(speedStep = 4)),
        )
        assertEquals(1, source.targets.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `configuration must contain neutral zero`() {
        ChassisControlConfig(speedMinStep = 1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `bipolar configuration cannot collapse to a single value`() {
        ChassisControlConfig(speedMinStep = 0, speedMaxStep = 0)
    }

    private class RecordingSource(
        override val config: ChassisControlConfig = ChassisControlConfig(),
        override val isDemo: Boolean = true,
    ) : ChassisDeviceDataSource {
        override val telemetry: Flow<ChassisTelemetry> = emptyFlow()
        val targets = mutableListOf<ChassisControlState>()
        var result: ChassisCommandResult = ChassisCommandResult.PreviewOnly

        override suspend fun submitControls(controls: ChassisControlState): ChassisCommandResult {
            targets += controls
            return result
        }
    }
}
