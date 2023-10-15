package com.longkai.stcarcontrol.st_exp.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.RelativeLayout
import android.widget.Switch
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU.CMDResponse
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU.CMDSEND
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter

class VcuCarInfoLayout : RelativeLayout {
    private var motor: Switch? = null
    private var TAG = "VcuCarInfoLayout"
    private var crash: IndicatorView? = null
    private var dc_c: IndicatorView? = null
    private var ac_charge: IndicatorView? = null

    constructor(context: Context) : super(context) {
        initLayout()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initLayout()
    }


    private fun initLayout() {
        LayoutInflater.from(context).inflate(R.layout.vcu_car_layout, this)
        motor = findViewById<Switch?>(R.id.vcu_car_switch_1)?.apply {
            setOnCheckedChangeListener(object : OnCheckedChangeListener {
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    var comment = CMDSEND()
                    if (isChecked) {
                        comment.turnOn()
                    } else {
                        comment.turnOff()
                    }
                    sendMsg(comment)
                }

            })
        }


        post {
            crash = (parent as View)?.findViewById(R.id.carinfo_crash)
            dc_c = (parent as View)?.findViewById(R.id.carinfo_dc_c)
            ac_charge = (parent as View)?.findViewById(R.id.carinfo_ac_charge)
        }
    }


    private fun sendMsg(cmdsend: CMDSEND) {
        ServiceManager.getInstance()
            .sendCommandToCar(cmdsend, object : CommandListenerAdapter<CMDResponse>() {
                override fun onSuccess(response: CMDResponse?) {
                    super.onSuccess(response)
                    response?.let {
                        it.data?.takeIf { it.size > 3 }?.apply {
                            try {
                                var res = this[3]
                                var binary = Integer.toBinaryString(res.toInt())
                                var len = binary.length
                                if (len > 0) {
                                    crash?.changeCircleColor(Character.getNumericValue(binary[len - 1]))
                                }
                                if (len > 1) {
                                    dc_c?.changeCircleColor(Character.getNumericValue(binary[len - 2]))
                                }
                                if (len > 2) {
                                    ac_charge?.changeCircleColor(Character.getNumericValue(binary[len - 3]))
                                }
                            } catch (e: Exception) {

                            }
                        }
                    }
                }

                override fun onError(errorCode: Int) {
                    super.onError(errorCode)
                    Log.d(TAG, "onErr")
                }

                override fun onTimeout() {
                    super.onTimeout()
                    Log.d(TAG, "timeout")
                }

            })
    }

}