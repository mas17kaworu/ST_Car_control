package com.longkai.stcarcontrol.st_exp.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.Utils.dp2px
import com.longkai.stcarcontrol.st_exp.Utils.sp2px

class DrawView : View {
    private var drawText = "FOC"
    private var style = 0
    private var paint = Paint()
    private var minWidth = 35.dp2px(context)
    private var height = 20.dp2px(context)
    private var currentWidth = minWidth.toFloat()
    private var padding = 5.dp2px(context)
    private var cor = 3.dp2px(context)
    private var path = Path()
    private var textHeight = 0
    private var bgColor = Color.parseColor("#293b92")

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet){
        initView(attributeSet)
    }

    public fun changeBgColor(color:Int){
        bgColor = color
        invalidate()
    }

    private fun initView(attributeSet: AttributeSet?){
        attributeSet?.let {
            var a = context?.obtainStyledAttributes(it, R.styleable.DrawView)?.apply {
                getString(R.styleable.DrawView_drawDesc)?.apply {
                    drawText = this
                }
                style = getInteger(R.styleable.DrawView_drawStyle, 0)
                bgColor = getColor(R.styleable.DrawView_drawBgColor,bgColor)
            }
            a?.recycle()
        }
        var width = paint.measureText(drawText) + 2 * padding
        when (style) {
            0 -> {
                width += cor
            }

            1 -> {
                width += cor * 2
            }
        }
        currentWidth = if (width < minWidth) {
            minWidth.toFloat()
        } else {
            width
        }
    }
    init {
        paint.textSize = 10.sp2px(context).toFloat()
        paint.isAntiAlias = true
        var fo = paint.fontMetrics
        textHeight = (fo.bottom - fo.top).toInt()

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(currentWidth.toInt(), height)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas?.let {
            when (style) {
                0 -> {
                    drawStart(it)
                }

                1 -> {
                    drawEnd(it)
                }

            }

        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    }

    private fun drawStart(canvas: Canvas) {
        paint.color = bgColor
        path.moveTo(0f, 0f)
        path.lineTo(currentWidth - cor, 0f)
        path.lineTo(currentWidth, (height / 2).toFloat())
        path.lineTo(currentWidth - cor, height.toFloat())
        path.lineTo(0f, height.toFloat())
        canvas.drawPath(path, paint)
        paint.color = Color.parseColor("#ffffff")
        canvas.drawText(drawText,padding.toFloat(),((height + textHeight)/2-1.dp2px(context)).toFloat(),paint)
    }

    private fun drawEnd(canvas: Canvas) {
        paint.color = bgColor
        path.moveTo(0f, 0f)
        path.lineTo(currentWidth - cor, 0f)
        path.lineTo(currentWidth, (height / 2).toFloat())
        path.lineTo(currentWidth - cor, height.toFloat())
        path.lineTo(0f, height.toFloat())
        path.lineTo(cor.toFloat(), (height/2).toFloat())
        canvas.drawPath(path, paint)
        paint.color = Color.parseColor("#ffffff")
        canvas.drawText(drawText,padding.toFloat(),((height + textHeight)/2-1.dp2px(context)).toFloat(),paint)
    }


}