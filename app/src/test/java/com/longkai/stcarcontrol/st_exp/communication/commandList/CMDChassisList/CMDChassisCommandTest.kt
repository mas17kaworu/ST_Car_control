package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList

import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CMDChassisCommandTest {
    @Test
    fun `control matches the complete little endian wire frame`() {
        val command = CMDChassisControl.setSpeed(-123456)
        val expected = bytes(
            0x5A, 0x3C, 0x18, 0x3D,
            0x01, 0x00, 0x00, 0x00,
            0xC0, 0x1D, 0xFE, 0xFF,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0xCC,
        )

        assertEquals(0x3D.toByte(), command.commandId)
        assertArrayEquals(expected, command.toRawData())
    }

    @Test
    fun `zero values and disabled switches still select exactly their own validity bit`() {
        val commands = listOf(
            CMDChassisControl.setSpeed(0),
            CMDChassisControl.setSteering(0),
            CMDChassisControl.setEhbForce(0),
            CMDChassisControl.setEmbForce(0),
            CMDChassisControl.setCurrentOffset(false),
            CMDChassisControl.setEpb(false),
        )
        commands.forEachIndexed { bit, command ->
            val raw = command.toRawData()
            assertSingleField(command, 1 shl bit, 8, ByteArray(0))
            assertArrayEquals(ByteArray(18), raw.copyOfRange(8, 26))
        }
    }

    @Test
    fun `signed controls preserve both int32 limits`() {
        val minimum = bytes(0, 0, 0, 0x80)
        val maximum = bytes(0xFF, 0xFF, 0xFF, 0x7F)
        for ((value, expected) in listOf(Int.MIN_VALUE to minimum, Int.MAX_VALUE to maximum)) {
            assertSingleField(CMDChassisControl.setSpeed(value), 0x01, 8, expected)
            assertSingleField(CMDChassisControl.setSteering(value), 0x02, 12, expected)
        }
    }

    @Test
    fun `unsigned controls preserve uint32 limits`() {
        for ((value, expected) in listOf(0L to bytes(0, 0, 0, 0), 0xFFFF_FFFFL to bytes(255, 255, 255, 255))) {
            assertSingleField(CMDChassisControl.setEhbForce(value), 0x04, 16, expected)
            assertSingleField(CMDChassisControl.setEmbForce(value), 0x08, 20, expected)
        }
    }

    @Test
    fun `numeric factories populate only their own little endian field`() {
        assertSingleField(CMDChassisControl.setSpeed(-123456), 0x01, 8, bytes(0xC0, 0x1D, 0xFE, 0xFF))
        assertSingleField(CMDChassisControl.setSteering(-1234), 0x02, 12, bytes(0x2E, 0xFB, 0xFF, 0xFF))
        assertSingleField(CMDChassisControl.setSpeed(0x12345678), 0x01, 8, bytes(0x78, 0x56, 0x34, 0x12))
        assertSingleField(CMDChassisControl.setSteering(0x12345678), 0x02, 12, bytes(0x78, 0x56, 0x34, 0x12))
        assertSingleField(CMDChassisControl.setEhbForce(0x89AB_CDEF), 0x04, 16, bytes(0xEF, 0xCD, 0xAB, 0x89))
        assertSingleField(CMDChassisControl.setEmbForce(0x89AB_CDEF), 0x08, 20, bytes(0xEF, 0xCD, 0xAB, 0x89))
    }

    @Test
    fun `switches encode independently as 55 or zero`() {
        for (enabled in listOf(false, true)) {
            val expected = bytes(if (enabled) 0x55 else 0)
            assertSingleField(CMDChassisControl.setCurrentOffset(enabled), 0x10, 24, expected)
            assertSingleField(CMDChassisControl.setEpb(enabled), 0x20, 25, expected)
        }
    }

    @Test
    fun `force factories reject values outside uint32`() {
        for (value in listOf(-1L, 0x1_0000_0000L, Long.MIN_VALUE, Long.MAX_VALUE)) {
            assertThrows(IllegalArgumentException::class.java) {
                CMDChassisControl.setEhbForce(value)
            }
            assertThrows(IllegalArgumentException::class.java) {
                CMDChassisControl.setEmbForce(value)
            }
        }
    }

    @Test
    fun `commands and returned frames do not share mutable payloads`() {
        val first = CMDChassisControl.setSpeed(-1)
        val expected = first.toRawData()
        first.toRawData().fill(0)
        CMDChassisControl.setSpeed(100)
        CMDChassisControl.setSteering(200)
        CMDChassisControl.setEhbForce(300)
        CMDChassisControl.setEmbForce(400)
        CMDChassisControl.setCurrentOffset(true)
        CMDChassisControl.setEpb(true)
        assertArrayEquals(expected, first.toRawData())
    }

    @Test
    fun `report parses signed angle and full unsigned values without scaling`() {
        val command = CMDChassisReport()
        val response = command.toResponse(reportFrame())

        assertEquals(0x3E.toByte(), command.commandId)
        assertEquals(0x3E, response.commandId)
        assertEquals(0xFFFF_FFFFL, response.speedRaw)
        assertEquals(-1234, response.steeringRaw)
        assertEquals(0x8000_0000L, response.ehbForceRaw)
        assertEquals(0x1234_5678L, response.embForceRaw)
    }

    @Test
    fun `report accepts zero values and both signed angle limits`() {
        for (angleBytes in listOf(bytes(0, 0, 0, 0), bytes(0, 0, 0, 128), bytes(255, 255, 255, 127))) {
            val frame = reportFrame().apply {
                fill(0, 4, 20)
                angleBytes.copyInto(this, 8)
            }
            val response = CMDChassisReport().toResponse(withChecksum(frame))
            val expectedAngle = when (angleBytes[3].toInt() and 0xFF) {
                0x80 -> Int.MIN_VALUE
                0x7F -> Int.MAX_VALUE
                else -> 0
            }
            assertEquals(expectedAngle, response.steeringRaw)
            assertEquals(0L, response.speedRaw)
            assertEquals(0L, response.ehbForceRaw)
            assertEquals(0L, response.embForceRaw)
        }
    }

    @Test
    fun `report handles reserved command bit and padded bluetooth buffer`() {
        val frame = withChecksum(reportFrame().apply { this[3] = 0xBE.toByte() })
        val padded = ByteArray(128) { 0x55 }.apply { frame.copyInto(this) }
        val response = CMDChassisReport().toResponse(padded)
        assertEquals(0x3E, response.commandId)
        assertEquals(0xFFFF_FFFFL, response.speedRaw)
        assertEquals(-1234, response.steeringRaw)
    }

    @Test
    fun `report rejects null and truncated frames`() {
        val command = CMDChassisReport()
        assertThrows(IllegalArgumentException::class.java) { command.toResponse(null) }
        for (length in 0 until 21) {
            assertThrows(IllegalArgumentException::class.java) {
                command.toResponse(reportFrame().copyOf(length))
            }
        }
    }

    @Test
    fun `report rejects wrong headers lengths and command identifiers`() {
        for ((index, value) in listOf(0 to 0, 1 to 0, 2 to 0x11, 2 to 0x13, 3 to 0x3D, 3 to 0xBD)) {
            val frame = withChecksum(reportFrame().apply { this[index] = value.toByte() })
            assertThrows(IllegalArgumentException::class.java) { CMDChassisReport().toResponse(frame) }
        }
    }

    @Test
    fun `report rejects payload or checksum corruption`() {
        for (index in 4..20) {
            val frame = reportFrame().apply { this[index] = (this[index].toInt() xor 1).toByte() }
            assertThrows(IllegalArgumentException::class.java) { CMDChassisReport().toResponse(frame) }
        }
    }

    @Test
    fun `unsupported queries and acknowledgments fail explicitly`() {
        assertThrows(UnsupportedOperationException::class.java) { CMDChassisReport().toRawData() }
        assertThrows(UnsupportedOperationException::class.java) {
            CMDChassisControl.setSpeed(0).toResponse(reportFrame())
        }
    }

    private fun assertSingleField(command: CMDChassisControl, flag: Int, offset: Int, value: ByteArray) {
        val raw = command.toRawData()
        assertEquals(27, raw.size)
        assertEquals(0x3D.toByte(), command.commandId)
        assertArrayEquals(bytes(0x5A, 0x3C, 0x18, 0x3D, flag, 0, 0, 0), raw.copyOfRange(0, 8))
        val expectedContent = ByteArray(18).apply { value.copyInto(this, offset - 8) }
        assertArrayEquals(expectedContent, raw.copyOfRange(8, 26))
        assertEquals(CheckSumBit.checkSum(raw.copyOfRange(2, 26), 24), raw[26])
    }

    private fun reportFrame() = bytes(
        0x5A, 0x3C, 0x12, 0x3E,
        0xFF, 0xFF, 0xFF, 0xFF,
        0x2E, 0xFB, 0xFF, 0xFF,
        0x00, 0x00, 0x00, 0x80,
        0x78, 0x56, 0x34, 0x12,
        0x59,
    )

    private fun withChecksum(frame: ByteArray): ByteArray = frame.apply {
        this[20] = CheckSumBit.checkSum(this, 20)
    }

    private fun bytes(vararg values: Int): ByteArray = values.map { it.toByte() }.toByteArray()
}
