package com.longkai.stcarcontrol.st_exp.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.Utils.decimalFormat
import java.text.DecimalFormat

class DcdcLayout : RelativeLayout {
    private var image: ImageView? = null
    private var vTextView: TextView? = null
    private var starV = 13.2f


    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        LayoutInflater.from(context).inflate(R.layout.seventh, this)
        image = findViewById(R.id.car_seventh_image)
        vTextView = findViewById<TextView?>(R.id.content01_text_02)?.apply {
            refreshVText()
        }
        image?.let {
            it.setImageResource(R.drawable.carinfo_dc_dc_icon)
        }
    }

    private fun refreshVText() {
        postDelayed({
            vTextView?.apply {
                text = "Voltage : ${decimalFormat.format(starV + Math.random()*0.2) }V"
                refreshVText()
            }

        }, 1000)
    }

}