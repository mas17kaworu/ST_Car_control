package com.longkai.stcarcontrol.st_exp.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.RelativeLayout
import android.widget.Switch
import com.longkai.stcarcontrol.st_exp.R

class BmsLayout:RelativeLayout {
    private var bmsSwitch: Switch?=null
    private var gridView:AppsGridView? =null
    private var battery = 60f
    private var listView:ArrayList<BatterAndTextView> = ArrayList<BatterAndTextView>();
    constructor(context: Context):super(context)
    constructor(context: Context,attributeSet: AttributeSet):super(context,attributeSet)
    init {
        LayoutInflater.from(context).inflate(R.layout.bms_layout,this)
        gridView = findViewById(R.id.batter_gird_view)
        bmsSwitch = findViewById<Switch?>(R.id.vcu_car_switch_2)?.apply {
            setOnCheckedChangeListener(object:OnCheckedChangeListener{
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    TractionStatus.DFA = isChecked
                    batteryChange(isChecked)
                    (this@BmsLayout.parent as View)?.invalidate()
                }
            })
        }
        initGridView()
    }

    private fun batteryChange(check: Boolean) {
        for (batteryView in listView) {
            batteryView.randomBattery(check)
        }
    }

    private fun initGridView(){
        for(index in 0 until 18){
            var batteryView = BatterAndTextView(context)
            batteryView.setBatteryAndText(battery + Math.random().toFloat())
            listView.add(batteryView)
            gridView?.addView(batteryView)
        }
    }
}