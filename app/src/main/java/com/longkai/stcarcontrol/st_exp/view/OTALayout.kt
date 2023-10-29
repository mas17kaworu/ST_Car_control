package com.longkai.stcarcontrol.st_exp.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import android.widget.TextView
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.Utils.dp2px

class OTALayout : LinearLayout, OnClickListener {
    private var progress: OTAProgress? = null
    private var otaButton: TextView? = null
    private var otaVersion:TextView? = null
    private var retryCounts = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attriSet: AttributeSet) : super(context, attriSet)

    init {
        this.orientation = HORIZONTAL
        LayoutInflater.from(context).inflate(R.layout.ota_layout, this)
        progress = findViewById(R.id.ota_prgress)
        otaButton = findViewById<TextView?>(R.id.ota_button)?.apply {
            setOnClickListener(this@OTALayout)
        }
        otaVersion = findViewById(R.id.ota_version)

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ota_button -> {
                otaButton?.takeIf { it.isEnabled }?.let {
                    it.isEnabled = false
                    it.isSelected = true
                    otaVersion?.visibility = GONE
                    progress?.visibility = VISIBLE
                    upgrade()
                }
            }
        }
    }

    private fun upgrade() {
        if (++retryCounts <= 5) {
            progress?.updatePercent(retryCounts/5.0f)
            postDelayed({upgrade()},1000)
        } else {
            progress?.visibility = View.GONE
            otaVersion?.apply {
                visibility = VISIBLE
                text = "FW Ver:1.1 "
            }
            otaButton?.let {
                it.isEnabled =true
                it.isSelected = false
            }

        }
    }

}

class OTAProgress : View {
    val paint = Paint()
    var percent = 0.1f
    private val defaultColor = Color.parseColor("#212222")
    private val progressColor = Color.parseColor("#FF008D06")
    public fun updatePercent(percent: Float) {
        this.percent = percent
        invalidate()
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attriSet: AttributeSet) : super(context, attriSet)

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.let {
            drawDeFaultProgressPaint(it)
            initProgressPaint(it)
        }
    }

    /**
     * 初始化进度条默认色
     */
    private fun drawDeFaultProgressPaint(canvas: Canvas) {
        paint.color = defaultColor
        paint.strokeWidth = height.toFloat()
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.FILL
        var startX = height
        var endX = width - height
        var y = (height / 2).toFloat()
        canvas.drawLine(startX.toFloat(), y, endX.toFloat(), y, paint)
    }

    /**
     * 初始化进度条颜色
     */
    private fun initProgressPaint(canvas: Canvas) {
        paint.color = progressColor
        paint.strokeWidth = height.toFloat()
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.FILL
        var startX = height
        var endX = (width - height) * percent
        var y = (height / 2).toFloat()
        canvas.drawLine(startX.toFloat(), y, endX.toFloat(), y, paint)
    }

}