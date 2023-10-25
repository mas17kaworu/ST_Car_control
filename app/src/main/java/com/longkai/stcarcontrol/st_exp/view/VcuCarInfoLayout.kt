package com.longkai.stcarcontrol.st_exp.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.RelativeLayout
import android.widget.Switch
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU.CMDMotorPower
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU.CMDSTATUS

class VcuCarInfoLayout : RelativeLayout {
    private var motor: Switch? = null
    private var TAG = "VcuCarInfoLayout"
    private var optimize:Switch? = null
    private var rdcTextView1:DrawView? = null
    private var rdcTextView2:DrawView? = null

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
                    CMDSTATUS.sMotor = isChecked
                    var comment = CMDMotorPower(CMDSTATUS.sPower,isChecked)
                    CMDMsgSend.sendMsg(comment,3, handler)
                }

            })
        }

        rdcTextView1 = findViewById(R.id.vcu_car_rdc1)
        rdcTextView2 = findViewById(R.id.vcu_car_rdc2)
        optimize = findViewById<Switch?>(R.id.vcu_car_switch_optimize)?.apply {
            setOnCheckedChangeListener(object : OnCheckedChangeListener {
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    rdcTextView2?.changeBgColor(if(isChecked) Color.parseColor("#293b92") else Color.parseColor("#373839"))
                    rdcTextView1?.changeBgColor(if(isChecked) Color.parseColor("#373839") else Color.parseColor("#293b92"))
                    TractionStatus.resolver = isChecked
                    (this@VcuCarInfoLayout.parent as View)?.invalidate()
                }
            })
        }

    }


}