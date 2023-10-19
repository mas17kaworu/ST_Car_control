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
    private var km:TextView? = null
    private var km2:TextView? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
    }

    init {
        voltage = findViewById<TextView?>(R.id.content01_text_01)
        mA = findViewById<TextView?>(R.id.content01_text_02)
        mA2 = findViewById<TextView?>(R.id.content02_text_02)
        km = findViewById<TextView?>(R.id.content01_text_03)
        km2 = findViewById<TextView?>(R.id.content02_text_03)
        image?.let {
            it.setImageResource(R.drawable.carinfo_icon_obc)
        }
        refreshTexView()
    }


    private fun refreshTexView() {
        voltage?.let {
            var tmp1 = Math.random() * 2 + 219;
            var tmp2 = 28 + Math.random() * 4
            var tmp3 = 7.5 + Math.random();
            it.text = "Voltage : ${decimalFormat.format(tmp1)}Vac"
            mA?.text = "Current: ${decimalFormat.format(tmp2)}A"
            mA2?.text = "Current: ${decimalFormat.format(tmp3)}A"
            km?.text = "Power: ${decimalFormat.format(tmp1 * tmp2)}KW"
            km2?.text = "Power: ${decimalFormat.format(802 * tmp3)}KW"
            postDelayed({ refreshTexView() }, 1000)
        }
    }

}