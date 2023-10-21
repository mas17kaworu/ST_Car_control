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
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU.CMDDFA
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU.CMDDFAResponse
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter

class BmsLayout : RelativeLayout {
    private var TAG = "BmsLayout"
    private var bmsSwitch: Switch? = null
    private var gridView: AppsGridView? = null
    private var battery = 60f
    private var listView: ArrayList<BatterAndTextView> = ArrayList<BatterAndTextView>();
    private var ac_charge: IndicatorView? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        LayoutInflater.from(context).inflate(R.layout.bms_layout, this)
        gridView = findViewById(R.id.batter_gird_view)
        bmsSwitch = findViewById<Switch?>(R.id.vcu_car_switch_2)?.apply {
            setOnCheckedChangeListener(object : OnCheckedChangeListener {
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    TractionStatus.DFA = isChecked
                    batteryChange(isChecked)
                    sendMsg(CMDDFA())
                    (this@BmsLayout.parent as View)?.invalidate()
                }
            })
        }
        initGridView()
        post {
            ac_charge = (parent as View)?.findViewById(R.id.carinfo_ac_charge)
        }
    }

    private fun sendMsg(sendMsg: CMDDFA) {
        ServiceManager.getInstance()
            .sendCommandToCar(sendMsg, object : CommandListenerAdapter<CMDDFAResponse>() {
                override fun onSuccess(response: CMDDFAResponse?) {
                    super.onSuccess(response)
                    response?.let {
                        refreshView(response)
                    }
                }

                override fun onError(errorCode: Int) {
                    super.onError(errorCode)
                }

                override fun onTimeout() {
                    super.onTimeout()
                    Log.d(TAG, "timeout")
                }

            })
    }

    private fun batteryChange(check: Boolean) {
        for (batteryView in listView) {
            batteryView.randomBattery(check)
        }
    }

    private fun initGridView() {
        for (index in 0 until 18) {
            var batteryView = BatterAndTextView(context)
            batteryView.setBatteryAndText(battery + Math.random().toFloat())
            listView.add(batteryView)
            gridView?.addView(batteryView)
        }
    }

    private fun refreshView(response: CMDDFAResponse) {
        findViewById<IndicatorView>(R.id.carinfo_crash)?.let {
            it.changeCircleColor(if (response.crash) IndicatorColor.COLOR_GREEN else IndicatorColor.COLOR_RED)
        }
        findViewById<IndicatorView>(R.id.carinfo_dc_c)?.let {
            it.changeCircleColor(if (response.dc_c) IndicatorColor.COLOR_GREEN else IndicatorColor.COLOR_RED)
        }
        ac_charge?.changeCircleColor(if (response.ac_c) IndicatorColor.COLOR_GREEN else IndicatorColor.COLOR_RED)
    }
}