package com.longkai.stcarcontrol.st_exp.view

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU.CMDMotorPower
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU.CMDResponse
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU.CMDSTATUS
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter

object CMDMsgSend {
    private var TAG = "CMDMsgSend"
    private var retryDelayTime = 3000L

    /**
     * 失败重试三次
     */
    @JvmStatic
    fun sendMsg(cmdsend: CMDMotorPower, retryCounts: Int, handler: Handler) {
        if (retryCounts > 0) {
            ServiceManager.getInstance()
                .sendCommandToCar(cmdsend, object : CommandListenerAdapter<CMDResponse>() {
                    override fun onSuccess(response: CMDResponse?) {
                        super.onSuccess(response)
                    }

                    override fun onError(errorCode: Int) {
                        super.onError(errorCode)
                        Log.d(TAG, "onErr")
                        if (retryCounts - 1 > 0) {
                            handler?.postDelayed(
                                { sendMsg(cmdsend, retryCounts - 1, handler) },
                                retryDelayTime
                            )
                            Log.d(TAG, "sendMsg retryCounts${retryCounts}")
                        }
                    }

                    override fun onTimeout() {
                        super.onTimeout()
                        Log.d(TAG, "timeout")
                        if (retryCounts - 1 > 0) {
                            handler?.postDelayed(
                                { sendMsg(cmdsend, retryCounts - 1, handler) },
                                retryDelayTime
                            )
                            Log.d(TAG, "sendMsg retryCounts${retryCounts}")
                        }
                    }

                })

        }

    }
}

class VcuTruckInfoLayout : RelativeLayout {
    private var mPower: Switch? = null
    private final var TAG = "VcuTruckInfoLayout"

    private var statusTextView: TextView? = null
    private var statusChangeRunnable: Runnable = Runnable {
        statusTextView?.apply {
            this.typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#ff00ff0a"))
        }
    }

    constructor(context: Context) : super(context) {
        initLayout()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initLayout()
    }


    private fun initLayout() {
        LayoutInflater.from(context).inflate(R.layout.vcu_truck_layout, this)
        mPower = findViewById(R.id.vcu_switch)
        statusTextView = findViewById(R.id.vcu_status)
        mPower?.let {
            it.setOnCheckedChangeListener(object : OnCheckedChangeListener {
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    CMDSTATUS.sPower = isChecked
                    var comment = CMDMotorPower(isChecked, CMDSTATUS.sMotor)
                    CMDMsgSend.sendMsg(comment, 3, handler)
                    if (isChecked) {
                        statusTextView?.let {
                            it.removeCallbacks(statusChangeRunnable)
                            it.postDelayed(statusChangeRunnable, 1000)
                        }
                    } else {
                        statusTextView?.apply {
                            removeCallbacks(statusChangeRunnable)
                            this.typeface = Typeface.DEFAULT
                            setTextColor(Color.WHITE)
                        }
                    }
                }
            })
        }
    }


}