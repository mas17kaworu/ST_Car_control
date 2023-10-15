package com.longkai.stcarcontrol.st_exp.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View

import com.longkai.stcarcontrol.st_exp.Utils.dp2px
import com.longkai.stcarcontrol.st_exp.Utils.sp2px
import kotlin.math.ceil

public data class AppProgressViewBean(val text:String,var des:String = "Test",val percent:Float = 0f);
class AppProgressView: View{
    private var beans:List<AppProgressViewBean>? = null
    private var  verticalCount =  1;
    private var  horizontalCount =  2;
    private final val TAG = "AppProgressView"
    constructor(context: Context,viewBean: List<AppProgressViewBean>):super(context){
        this.beans = viewBean
        this.beans?.let {
            verticalCount = ceil((it.size/2).toDouble()).toInt()
        }
    }
    constructor(context: Context,attrs: AttributeSet?):super(context,attrs){

    }

    private val defaultColor = Color.parseColor("#212222")
    private val progressColor =  Color.parseColor("#2e4ce9")
    private val textColor = Color.parseColor("#c1c1c2")
    private val dividerColor = Color.parseColor("#393a3b")
    private val titleColor = Color.parseColor("#ffffff")
    private val paddingleftRight = 23.dp2px(context)

    private var halfWidth =0

    val paint = Paint();
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        halfWidth = width/2
        drawTitle(canvas)
        drawDivider(canvas)
        beans?.let {
            for(indexVertical in 0 until verticalCount){
                for(indexHorizontal in 0 until horizontalCount){
                    var index  =indexVertical * horizontalCount +indexHorizontal
                    Log.i(TAG,"index:$index indexVertical$indexVertical  indexHorizontal$indexHorizontal")
                    if(index < it.size){
                        canvas?.let {
                            drawTextPaint(beans?.get(index)!!,it,indexVertical,indexHorizontal)
                            drawDeFaultProgressPaint(beans?.get(index)!!,it,indexVertical,indexHorizontal)
                            initProgressPaint(beans?.get(index)!!,it,indexVertical,indexHorizontal)
                        }
                    }
                }
            }
        }

    }


    public fun initData(viewBean: List<AppProgressViewBean>){
        this.beans= viewBean
        this.beans?.let {
            verticalCount = ceil((it.size/2).toDouble()).toInt()
        }
    }

    private fun drawTitle(canvas: Canvas?){
        paint.color =  titleColor
        paint.strokeCap = Paint.Cap.BUTT
        paint.strokeWidth = 1.dp2px(context).toFloat()
        paint.textSize = 24.sp2px(context).toFloat()
        paint.style = Paint.Style.FILL
        var metrics = paint.fontMetrics
        canvas?.drawText("MEAT1",paddingleftRight.toFloat(),16.dp2px(context)+(metrics.bottom -metrics.top)/2,paint)

    }
    private fun drawDivider(canvas: Canvas?){
        paint.color =  dividerColor
        paint.strokeCap = Paint.Cap.BUTT
        paint.strokeWidth = 1.dp2px(context).toFloat()
        paint.style = Paint.Style.FILL
        canvas?.drawLine(paddingleftRight.toFloat(),52.dp2px(context).toFloat(),width.toFloat()- paddingleftRight,52.dp2px(context).toFloat(),paint)

    }
    private fun drawTextPaint(bean:AppProgressViewBean,canvas: Canvas,indexVertical:Int,indexHorizontal:Int){
        paint.color =  textColor
        paint.strokeCap = Paint.Cap.BUTT
        paint.style = Paint.Style.FILL
        paint.textSize = 16.sp2px(context).toFloat()
        var metrics = paint.fontMetrics
        var textLen = metrics.bottom -metrics.top;
        var startY = 71.dp2px(context) +textLen/2 + (20.dp2px(context) +textLen)*indexVertical
        var startX = halfWidth * indexHorizontal + paddingleftRight
        canvas.drawText(bean.text + " " +bean.percent + "%",startX.toFloat(),startY,paint)
    }

    /**
     * 初始化进度条默认色
     */
    private fun drawDeFaultProgressPaint(bean:AppProgressViewBean,canvas: Canvas,indexVertical:Int,indexHorizontal:Int){
        paint.color =  defaultColor
        paint.strokeWidth = 6.dp2px(context).toFloat()
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.FILL
        var startX = halfWidth * indexHorizontal + paddingleftRight
        var endX = startX + halfWidth - paddingleftRight - paddingleftRight/2
        var y = 92.dp2px(context) + 40.dp2px(context) * indexVertical
        canvas.drawLine(startX.toFloat(),y.toFloat(),endX.toFloat(),y.toFloat(),paint)
    }

    /**
     * 初始化进度条颜色
     */
    private fun initProgressPaint(bean:AppProgressViewBean,canvas: Canvas,indexVertical:Int,indexHorizontal:Int){
        paint.strokeWidth = 6.dp2px(context).toFloat()
        paint.color = progressColor
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.FILL
        var startX = halfWidth * indexHorizontal + paddingleftRight
        var endX = startX +(halfWidth - 16.dp2px(context)) * bean.percent / 100
        var y =92.dp2px(context)  + 40.dp2px(context) *indexVertical
        canvas.drawLine(startX.toFloat(), y.toFloat(), endX, y.toFloat(), paint)
    }

}
