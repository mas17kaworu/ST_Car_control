package com.longkai.stcarcontrol.st_exp.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.Utils.dp2px
import com.longkai.stcarcontrol.st_exp.Utils.sp2px
object IndicatorColor{
    var COLOR_RED = 0
    var COLOR_GREEN = 1

}
data class RoundBean(
    var circleColor: Int = Color.RED,
    var textColor: Int = Color.parseColor("#ffffff"),
    var tips: String
)

class IndicatorView : View {
    private var circleRadius = 14f
    private var paint = Paint()
    private var circleColor: Int = Color.RED
    private var textColor: Int = Color.WHITE
    private var roundBean: RoundBean? = null
    private var padding = 9.dp2px(context)
    private var textWidth = 0f
    private var text:String ="Ceshi"
    private var textHeight = 0f

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet){
        initView(attributeSet)
    }

    private fun initView(attributeSet: AttributeSet?) {
        attributeSet?.let { it ->
            var a= context?.obtainStyledAttributes(it,R.styleable.Indicator)?.apply {
                getString(R.styleable.Indicator_tipText)?.let { 
                    text = it
                }
                getColor(R.styleable.Indicator_color,Color.RED)?.let {
                    circleColor = it
                }
            }
            a?.recycle()
        }
        initData(RoundBean(tips = text, circleColor =  circleColor))
    }
    init {
        paint.textSize = 14.sp2px(context).toFloat()
        var font = paint.fontMetrics
         textHeight = font.bottom - font.top
    }
    private fun initData(roundBean: RoundBean?) {
        roundBean?.let {
            this.circleColor = it.circleColor
            this.textColor = it.textColor
            this.roundBean = it
            textWidth = paint.measureText(it.tips)
            showCircle()
        }
    }

    private fun hideCircle(){
        circleColor = Color.TRANSPARENT
        invalidate()
        postDelayed({showCircle()},200)
    }

    private fun showCircle(){
        roundBean?.let {
            circleColor = it.circleColor
            invalidate()
            postDelayed( {  hideCircle() },800)
        }

    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.let {
            if (roundBean != null) {
                var dp = 1.dp2px(context)
                paint.color = textColor!!
                paint.style = Paint.Style.FILL
                var y = textHeight + dp
                it.drawText(roundBean!!.tips, 0f, y, paint)
                paint.color = circleColor
                it.drawCircle(textWidth + padding+circleRadius/2, (y+circleRadius)/2+dp, circleRadius-dp/2, paint)
            }

        }
    }

    public fun changeCircleColor(color:Int){
        roundBean?.let {
            when(color){
                IndicatorColor.COLOR_GREEN  -> {
                    it.circleColor =  Color.GREEN
                }
                IndicatorColor.COLOR_RED -> {
                    it.circleColor =  Color.RED
                }
            }
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        roundBean?.let {
            textWidth = paint.measureText(it.tips)
            setMeasuredDimension((textWidth + padding + circleRadius * 2).toInt(),
                textHeight.toInt()+2.dp2px(context))

        }
    }
}