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
import com.longkai.stcarcontrol.st_exp.Utils.addViewRefreshRunnable
import com.longkai.stcarcontrol.st_exp.Utils.removeViewRefreshRunnable
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU.CMDMotorPower
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU.CMDResponse
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU.CMDSTATUS
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter
import com.longkai.stcarcontrol.st_exp.view.IndicatorColor.COLOR_GRAY
import com.longkai.stcarcontrol.st_exp.view.IndicatorColor.COLOR_GREEN

object CMDMsgSend {
    private var TAG = "CMDMsgSend"
    private var retryDelayTime = 3000L

    /**
     * 失败重试三次
     */
    @JvmStatic
    fun sendMsg(cmdsend: CMDMotorPower, retryCounts: Int, handler: Handler) {
        Log.d(TAG, "sendMsg")
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
    private var acc: Switch? = null
    private var generator: Switch? = null
    private final var TAG = "VcuTruckInfoLayout"
    private var pedal: IndicatorView? = null
    private var brake: IndicatorView? = null
    private var torque: TextView? = null
    private var speed: TextView? = null
    private val maxSpeed = 50
    private val minSpeed = 0
    private var currentSpeed = 0;
    private var isAddSpeed = false
    private var speedRunnable: Runnable = Runnable {
        changeSpeed()
    }

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

    private fun changeSpeed() {
        removeCallbacks(speedRunnable)
        if (isAddSpeed) {
            if (currentSpeed < maxSpeed) {
                speed?.text = "Speed: $currentSpeed RPM"
                currentSpeed += 10;
                postDelayed(speedRunnable, 1000)
            } else {
                currentSpeed = maxSpeed
                speed?.text = "Speed: $maxSpeed RPM"
            }
        } else {
            if (currentSpeed > minSpeed) {
                speed?.text = "Speed: $currentSpeed RPM"
                postDelayed(speedRunnable, 1000)
                currentSpeed -= 10
            } else {
                currentSpeed = minSpeed
                speed?.text = "Speed: $minSpeed RPM"
                torque?.text = "Torque: 0 NM"
            }
        }
    }

    private fun initLayout() {
        LayoutInflater.from(context).inflate(R.layout.vcu_truck_layout, this)
        mPower = findViewById(R.id.vcu_switch)
        statusTextView = findViewById(R.id.vcu_status)
        torque = findViewById(R.id.vcu_torque)
        speed = findViewById(R.id.vcu_speed)
        mPower?.let {
            it.setOnCheckedChangeListener(object : OnCheckedChangeListener {
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    CMDSTATUS.sPower = isChecked
                    var comment = CMDMotorPower(isChecked, CMDSTATUS.sMotor)
                    CMDMsgSend.sendMsg(comment, 3, handler)
                    if (isChecked) {
                        statusTextView?.let {
                            addViewRefreshRunnable(statusChangeRunnable)
                        }
                    } else {
                        statusTextView?.apply {
                            removeViewRefreshRunnable(statusChangeRunnable)
                            this.typeface = Typeface.DEFAULT
                            setTextColor(Color.WHITE)
                        }
                    }
                }
            })
        }

        acc = findViewById<Switch>(R.id.vcu_acc)?.apply {
            setOnCheckedChangeListener { buttonView, isChecked ->
                run {
                    TractionStatus.ACC = isChecked
                    if (isChecked) {
                        generator?.isChecked = false
                        pedal?.changeCircleColor(COLOR_GREEN)
                        brake?.changeCircleColor(COLOR_GRAY)
                        torque?.text = "Torque: 10 NM"
                        isAddSpeed = true
                        changeSpeed()
                    } else {
                        if(isAddSpeed){
                            this@VcuTruckInfoLayout.removeCallbacks(speedRunnable)
                        }
                    }
                    CMDSTATUS.sMotor = isChecked
                    var comment = CMDMotorPower(CMDSTATUS.sPower, isChecked)
                    CMDMsgSend.sendMsg(comment, 3, handler)
                }
            }
        }
        generator = findViewById<Switch>(R.id.vcu_generator)?.apply {
            setOnCheckedChangeListener { buttonView, isChecked ->
                run {
                    if (isChecked) {
                        acc?.isChecked = false
                        brake?.changeCircleColor(COLOR_GREEN)
                        pedal?.changeCircleColor(COLOR_GRAY)
                        torque?.text = "Torque: -10 NM"
                        isAddSpeed = false
                        changeSpeed()
                    } else {
                        if(!isAddSpeed){
                            this@VcuTruckInfoLayout.removeCallbacks(speedRunnable)
                        }
                    }
                }
            }
        }

        pedal = findViewById(R.id.vcu_pedal)
        brake = findViewById(R.id.vcu_brake)
    }


}