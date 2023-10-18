package com.longkai.stcarcontrol.st_exp.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import com.longkai.stcarcontrol.st_exp.Utils.dp2px
import com.longkai.stcarcontrol.st_exp.Utils.sp2px

class FirstTextsLayout : View {
    private val paint = Paint();
    private val start2X = 108.dp2px(context).toFloat()
    private val gapLine = 5.dp2px(context)
    private val gapTextView = 3.dp2px(context)
    private val lineHeight = 1.dp2px(context)

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)


     init {

    }
    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.let {
            var drawLineY = 0f;
            paint.textSize = 14.sp2px(context).toFloat()
            paint.style = Paint.Style.FILL
            paint.strokeWidth = 3.dp2px(context).toFloat()
            paint.typeface = Typeface.DEFAULT_BOLD
            var fontMetricsBig = paint.fontMetrics
            var bigTextHeight = fontMetricsBig.bottom - fontMetricsBig.top
            var halfTextHeight = bigTextHeight / 2
            drawText(it, 0f, bigTextHeight, "Type")
            drawText(it, start2X, bigTextHeight, "ST product")
            drawLineY = bigTextHeight + gapLine+ lineHeight;
            drawLine(canvas, 0f, drawLineY)


            paint.strokeWidth = 1f
            paint.textSize = 9.sp2px(context).toFloat()
            paint.typeface = Typeface.DEFAULT
            var fontMetrics = paint.fontMetrics
            var textHeight = fontMetrics.bottom - fontMetrics.top
//            drawLineY += lineHeight
            drawSmallText(canvas, 0f, drawLineY + gapTextView + textHeight, "MCU")
            drawSmallText(canvas, start2X, drawLineY + gapTextView + textHeight, "SR6P6")
            drawLineY += gapLine + textHeight + gapTextView +lineHeight
            drawLine(canvas, 0f, drawLineY)


//            drawLineY += lineHeight
            drawSmallText(canvas, 0f, drawLineY + gapTextView + textHeight, "PMIC")
            drawSmallText(canvas, start2X, drawLineY + gapTextView + textHeight, "SPSB100")
            drawLineY += gapLine + textHeight + gapTextView +lineHeight
            drawLine(canvas, 0f, drawLineY)


//            drawLineY += lineHeight
            drawSmallText(canvas, 0f, drawLineY + gapTextView + textHeight, "Gate driver")
            drawSmallText(canvas, start2X, drawLineY + gapTextView + textHeight, "L9502E,STGAP2SICSAC")
            drawLineY += gapLine + textHeight + gapTextView +lineHeight
            drawLine(canvas, 0f, drawLineY)

//            drawLineY += lineHeight
            drawSmallText(canvas, 0f, drawLineY + gapTextView + textHeight, "Drivers")
            drawSmallText(canvas, start2X, drawLineY + gapTextView + textHeight, "L9965A,L9965T,L9965C,L9965P")
            drawLineY += gapLine + textHeight + gapTextView +lineHeight
            drawLine(canvas, 0f, drawLineY)


//            drawLineY += lineHeight
            drawSmallText(canvas, 0f, drawLineY + gapTextView + textHeight, "BMS")
            drawSmallText(canvas, start2X, drawLineY + gapTextView + textHeight, "L9301.L9305.L9960T.VNQ7050")
            drawLineY += gapLine + textHeight + gapTextView +lineHeight
            drawLine(canvas, 0f, drawLineY)


//            drawLineY += lineHeight
            drawSmallText(canvas, 0f, drawLineY + gapTextView + textHeight, "SiC Module")
            drawSmallText(canvas, start2X, drawLineY + gapTextView + textHeight, "ADP480120W3")
            drawLineY += gapLine + textHeight + gapTextView +lineHeight
            drawLine(canvas, 0f, drawLineY)

            paint.textSize = 9.sp2px(context).toFloat()
            textHeight = paint.fontMetrics.bottom -  paint.fontMetrics.top
//            drawLineY += lineHeight
            drawSmallText(canvas, 0f, drawLineY + gapTextView + textHeight, "SiC Discretes, OA")
            drawSmallText(canvas, start2X, drawLineY + gapTextView + textHeight, "SCT018HU65,SCT055HU65,SCTO70HU120,OA TSB582")
            drawLineY += gapLine + textHeight + gapTextView +lineHeight
            drawLine(canvas, 0f, drawLineY)
        }
    }

    private fun drawLine(canvas: Canvas, startX: Float, startY: Float) {
        paint.color = Color.parseColor("#252627")
        paint.strokeWidth = 1.dp2px(context).toFloat()
        canvas.drawLine(startX, startY, width.toFloat(), startY, paint)
    }

    private fun drawText(canvas: Canvas, startX: Float, startY: Float, text: String) {
        paint.color = Color.parseColor("#ffffff")
        canvas.drawText(text, startX, startY, paint)
    }

    private fun drawSmallText(canvas: Canvas, startX: Float, startY: Float, text: String) {
        paint.color = Color.parseColor("#d9ffffff")
        canvas.drawText(text, startX, startY, paint)
    }


}