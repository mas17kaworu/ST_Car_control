package com.longkai.stcarcontrol.st_exp.view

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.Utils.decimalFormat


class OBCLayout : SixLayout {
    private var voltage: TextView? = null
    private var mA: TextView? = null
    private var mA2: TextView? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
    }

    init {
        voltage = findViewById<TextView?>(R.id.content01_text_01)
        mA = findViewById<TextView?>(R.id.content01_text_02)
        mA2 = findViewById<TextView?>(R.id.content02_text_02)
        image?.let {
            it.setImageResource(R.drawable.carinfo_icon_obc)
        }
        refreshTexView()
    }


    private fun refreshTexView() {
        voltage?.let {
            it.text = "Voltage : ${decimalFormat.format(Math.random() + 220)}Vac"
            mA?.text = "Current: ${decimalFormat.format(30 + Math.random() * 2)}A"
            mA2?.text = "Current: ${decimalFormat.format(8 + Math.random() * 0.5f)}A"
            postDelayed({ refreshTexView() }, 1000)
        }
    }

}