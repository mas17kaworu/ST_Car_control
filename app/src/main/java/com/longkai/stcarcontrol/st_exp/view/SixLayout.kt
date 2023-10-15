package com.longkai.stcarcontrol.st_exp.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.longkai.stcarcontrol.st_exp.R

open class SixLayout : RelativeLayout {
    private var showIndicator = true
    private var indicatorView: IndicatorView? = null
    private var title: String = ""
    protected var image: ImageView? = null

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        initView(attributeSet)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.fragment_car_six_layout, this)
        image = findViewById(R.id.car_six_image)
    }

    private fun initView(attributeSet: AttributeSet?) {
        attributeSet?.let { it ->
            var a = context?.obtainStyledAttributes(it, R.styleable.SixLayout)?.apply {
                showIndicator = getBoolean(R.styleable.SixLayout_showIndicator, true)
                getString(R.styleable.SixLayout_title)?.apply {
                    title = this
                }
            }
            a?.recycle()
        }
        indicatorView = findViewById<IndicatorView?>(R.id.carinfo_ac_charge)?.apply {
            visibility = if (showIndicator) VISIBLE else GONE
        }
        findViewById<TextView>(R.id.car_six_title)?.let {
            if (!TextUtils.isEmpty(title)) {
                it.text = title
            }
        }

    }


}