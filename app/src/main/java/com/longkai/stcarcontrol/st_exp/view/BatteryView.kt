package com.longkai.stcarcontrol.st_exp.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.longkai.stcarcontrol.st_exp.Utils.dp2px
import java.util.Random


/**
 * 电池电量绘制
 */
class BatteryView : View {
    private var mPower = 60f;//电量
    private var mBatteryStroke = 2f;

    private var mBatteryHeight = 16.dp2px(context).toFloat(); // 电池的高度
    private var mBatteryWidth = 8.dp2px(context).toFloat(); // 电池的宽度
    private var mCapHeight = 2.dp2px(context).toFloat();
    private var mCapWidth = 3.dp2px(context).toFloat()

    private var mPaint: Paint? = null
    private var mBatteryRect: RectF? = null
    private var mCapRect: RectF? = null
    private var mPowerRect: RectF? = null

    private var powerPadding = 1.dp2px(context).toFloat()

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    init {

        mPaint = Paint()?.apply {
            color = Color.GRAY;
            isAntiAlias = true;
            strokeWidth = mBatteryStroke;
        }
        initBattery()
        setPower((Math.random() * 100f).toFloat() + 20)
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.let {
            drawBatteryPower(canvas)
        }
    }

    private fun drawBatteryPower(canvas: Canvas) {
        mPaint?.let {
            mPaint?.color = Color.parseColor("#878888")
            canvas.save()
            it.style = Paint.Style.FILL;
            it.strokeWidth = mBatteryStroke;
            //绘制电池嘴
            canvas.drawRoundRect(mCapRect!!, 5.dp2px(context).toFloat(), 2f, it);
            //绘制电池身体
            canvas.drawRoundRect(
                mBatteryRect!!,
                5.dp2px(context).toFloat(),
                5.dp2px(context).toFloat(),
                it
            );// 需要考虑 画笔的宽度 注意他下面不用考虑


            it.strokeWidth = 1f;
            mPaint?.color = Color.GREEN
            canvas.drawRect(mPowerRect!!, it);
            canvas.restore();
        }

    }

    private fun initBattery() {
        mCapRect = RectF(
            17.dp2px(context).toFloat(),
            0f,
            17.dp2px(context).toFloat() + mCapWidth,
            mCapHeight + mBatteryStroke
        );
        mBatteryRect = RectF(0f, mCapHeight, mBatteryWidth, mCapHeight + mBatteryHeight);
        var batterTop = mBatteryHeight * (100 - mPower) / 100
        mPowerRect = RectF(
            0f + powerPadding,
            mCapHeight + batterTop,
            mBatteryWidth - powerPadding,
            mBatteryHeight - powerPadding + mCapHeight
        )
        Log.i("xhs", "power $mPower")
    }


    public fun setPower(power: Float) {
        mPower = power
        if (mPower > 100) {
            mPower = 100f;
        }
        initBattery()
        invalidate()
    }

}