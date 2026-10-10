package com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList

import android.os.Handler
import android.util.Log
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList.CMDChassisReport
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentBase

class VCUChassisFragmentMock(handler: Handler) : MockFragmentBase(handler) {
    private var tick = 0L

    @Synchronized
    override fun run() {
        if (isStopped) return
        handler.removeCallbacks(this)
        if (dispatcher == null) {
            Log.w("VCUChassisFragmentMock", "Cannot start chassis mock: service is not initialized")
            stop()
            return
        }
        val frame = CMDChassisReport.Response(
            speedRaw = triangle(100, 20),
            steeringRaw = (triangle(80, 6_400) - 3_200).toInt(),
            ehbForceRaw = triangle(60, 20_000),
            embForceRaw = triangle(90, 20_000)
        ).mockResponse()
        dispatcher.onReceive(frame, 0, frame.size)
        tick++
        handler.postDelayed(this, 100)
    }

    @Synchronized
    fun stop() {
        isStopped = true
        handler.removeCallbacks(this)
    }

    private fun triangle(halfPeriodTicks: Long, maximum: Long): Long {
        val phase = tick % (halfPeriodTicks * 2)
        return minOf(phase, halfPeriodTicks * 2 - phase) * maximum / halfPeriodTicks
    }
}
