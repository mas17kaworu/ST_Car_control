package com.longkai.stcarcontrol.st_exp.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.Utils.dp2px
import com.longkai.stcarcontrol.st_exp.Utils.sp2px
object IndicatorColor{
    var COLOR_GRAY = 0
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
    private var circleColor: Int = Color.GRAY
    private var textColor: Int = Color.WHITE
    private var roundBean: RoundBean? = null
    private var padding = 9.dp2px(context)
    private var textWidth = 0f
    private var text:String ="Ceshi"
    private var textHeight = 0f
    private var textList:List<String> = ArrayList<String>()

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
                getColor(R.styleable.Indicator_color,Color.GRAY)?.let {
                    circleColor = it
                }
            }
            a?.recycle()
        }
        initData(RoundBean(tips = text, circleColor =  circleColor))
    }
    init {
        paint.textSize = 11.sp2px(context).toFloat()
        var font = paint.fontMetrics
        textHeight = font.bottom - font.top
    }
    private fun initData(roundBean: RoundBean?) {
        this.roundBean = roundBean
        roundBean?.let {
            textList = it.tips .split("\n")
            this.circleColor = it.circleColor
            this.textColor = it.textColor
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
//            postDelayed( {  hideCircle() },800)
        }

    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas?.let {
            if (roundBean != null) {
                var dp = 1.dp2px(context)
                paint.color = textColor!!
                paint.style = Paint.Style.FILL
                var y = 0f
                for (string in textList) {
                    y += textHeight
                    var width = paint.measureText(string)
                    it.drawText(string, (textWidth -width)/2, y, paint)
                }
                paint.color = roundBean!!.circleColor
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
                IndicatorColor.COLOR_GRAY -> {
                    it.circleColor =  Color.GRAY
                }
            }
            invalidate()
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        roundBean?.let {
            textWidth = 0f
            for (string in textList) {
                var width = paint.measureText(string)
                if (width > textWidth) {
                    textWidth = width;
                }
            }
            setMeasuredDimension((textWidth + padding + circleRadius * 2).toInt(),
                (textHeight*0.5f + textList.size *textHeight).toInt())

        }
    }
}