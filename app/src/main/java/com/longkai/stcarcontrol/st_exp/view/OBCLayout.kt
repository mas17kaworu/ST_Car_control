package com.longkai.stcarcontrol.st_exp.view

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.Utils.addViewRefreshRunnable
import com.longkai.stcarcontrol.st_exp.Utils.decimalFormat
import com.longkai.stcarcontrol.st_exp.fragment.ac_Charge_Value


class OBCLayout : SixLayout {
    private var voltage: TextView? = null
    private var voltage2: TextView? = null
    private var mA: TextView? = null
    private var mA2: TextView? = null
    private var km: TextView? = null
    private var km2: TextView? = null
    private var voltage2Number = 790
    private var hzText: TextView? = null
    private var runnable: Runnable = Runnable { refreshTexView() }

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
    }

    init {
        voltage = findViewById<TextView?>(R.id.content01_text_01)
        voltage2 = findViewById<TextView?>(R.id.content02_text_01)
        mA = findViewById<TextView?>(R.id.content01_text_02)
        mA2 = findViewById<TextView?>(R.id.content02_text_02)
        km = findViewById<TextView?>(R.id.content01_text_03)
        km2 = findViewById<TextView?>(R.id.content02_text_03)
        hzText = findViewById<TextView?>(R.id.content01_text_04)
        image?.let {
            it.setImageResource(R.drawable.carinfo_icon_obc)
        }
        refreshTexView()
    }


    private fun refreshTexView() {

        voltage?.let {
            if (ac_Charge_Value) {
                var tmp2 = 28 + Math.random() * 4
                var tmp1 = Math.random() * 2 + 219
                var tmp3 = 7 + Math.random()//减1 后即能确保左侧power 大于右侧200多
                it.text = "Voltage : ${decimalFormat.format(tmp1)}Vac"
                mA?.text = "Current : ${decimalFormat.format(tmp2)}A"
                mA2?.text = "Current : ${decimalFormat.format(tmp3)}A"
                km?.text = "Power : ${decimalFormat.format(tmp1 * tmp2)}W"
                km2?.text = "Power : ${decimalFormat.format(voltage2Number * tmp3)}W"
                hzText?.text = "Frequency : 50Hz"
            } else {
                it.text = "Voltage : --- Vac"
                mA?.text = "Current : --- A"
                mA2?.text = "Current : --- A"
                km?.text = "Power : --- W"
                km2?.text = "Power : --- W"
                hzText?.text = "Frequency : --- Hz"
            }

            if (ac_Charge_Value) {
                voltage2?.let {
                    if (voltage2Number > 820) {
                        return
                    }
                    it.text = "Voltage : ${decimalFormat.format(voltage2Number++)}V"

                }
            } else {
                voltage2?.let { it.text = "Voltage : ${decimalFormat.format(voltage2Number)}V"}
            }

            addViewRefreshRunnable(runnable)
        }
    }


}