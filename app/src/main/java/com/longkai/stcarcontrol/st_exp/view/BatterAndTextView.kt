package com.longkai.stcarcontrol.st_exp.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.TextView
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.Utils.addViewRefreshRunnable
import com.longkai.stcarcontrol.st_exp.Utils.decimalFormat
import com.longkai.stcarcontrol.st_exp.Utils.removeViewRefreshRunnable


class BatterAndTextView :RelativeLayout {
    private var batteryView: BatteryView? =null
    private var electric: TextView? = null
    private var batteryNumber = 0f
    private var batterChangeRunnable:Runnable = Runnable{randomBattery(batteryChangeEnable);}
    private var batteryChangeEnable = false;
    constructor(context: Context):super(context)

    constructor(context: Context,attributeSet: AttributeSet):super(context,attributeSet)
    init {
        LayoutInflater.from(context).inflate(R.layout.battery_and_text_view,this)
        batteryView = findViewById(R.id.battery)
        electric = findViewById(R.id.electric_text)
    }

    public fun setBatteryAndText(battery: Float ){
        batteryNumber = battery
        refreshBatteryView(battery)
    }

    private fun refreshBatteryView( battery:Float){
        batteryView?.apply {
            setPower(battery)
        }
        electric?.apply {
            text = "Voltage ${decimalFormat.format(battery)}mV"
        }
    }

    public fun randomBattery(change:Boolean){
        batteryChangeEnable = change
        if (change) {
            var battery = batteryNumber + Math.random().toFloat()
            refreshBatteryView(battery)
            addViewRefreshRunnable(batterChangeRunnable)
        } else {
            removeViewRefreshRunnable(batterChangeRunnable)
        }
    }
}