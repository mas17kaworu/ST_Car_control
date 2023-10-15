package com.longkai.stcarcontrol.st_exp.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.longkai.stcarcontrol.st_exp.R

class BatteryLayout:RelativeLayout {
    constructor(context: Context):super(context)
    constructor(context: Context,attributeSet: AttributeSet):super(context,attributeSet)

    init {
        LayoutInflater.from(context).inflate(R.layout.fragment_car_battery_layout,this)
    }
}