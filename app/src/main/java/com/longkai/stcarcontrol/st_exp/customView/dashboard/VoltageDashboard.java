package com.longkai.stcarcontrol.st_exp.customView.dashboard;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.longkai.stcarcontrol.st_exp.R;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2018/7/8.
 */

public class VoltageDashboard extends View{
    private static final int FULL_ANGLE = 120;

    private Bitmap background;
    private Bitmap pin;
    private Paint mPaint;
    private TextPaint mTextPaint;
    private float target_value_percent = 0; //0~100
    private float target_value = 0;
    private float present_value_percent = 0;
    private View view;
    private DecimalFormat df;

    private static final float MIN_INTERVAL = 0.1f;
    private float MAX_VALUE = 80.0f;
    private float MIN_VALUE = 0f;

    private Thread refreshThread = new Thread(){
        @Override
        public void run() {
            super.run();
            while (true) {
                if (Math.abs(present_value_percent - target_value_percent)> MIN_INTERVAL) {
                    present_value_percent += caculateInterval(target_value_percent, present_value_percent);
                }
                view.postInvalidate();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    Matrix matrix;

    int width;
    int height;

    private String unit = "";

    public VoltageDashboard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VoltageDashboard);
        int resID = array.getInteger(R.styleable.VoltageDashboard_backpicture, 0);
        unit = resID==0? "V" : "A";
        resID = resID==0? R.mipmap.ic_vcu_dashboard_voltage_back : R.mipmap.ic_vcu_dashboard_current_back;

        init(context, resID);
    }

    public void setMaxValue(float maxValue){
        MAX_VALUE = maxValue;
    }

    public void setMinValue(float minValue){
        MIN_VALUE = minValue;
    }

    private void init(Context context, int backgroundResId){
        mPaint = new Paint();
        Resources resources = context.getResources();
        background = BitmapFactory.decodeResource(resources, backgroundResId);
        pin = BitmapFactory.decodeResource(resources, R.mipmap.ic_dashboard_voltage_pin);
        width = background.getWidth();
        height = background.getHeight();
        matrix = new Matrix();
        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(30);
        mTextPaint.setColor(Color.GREEN);

        df=new DecimalFormat("0.0");
        view = this;
        refreshThread.start();
    }

    public void setValue(float value){
        if (value > MAX_VALUE){
            target_value = MAX_VALUE;
        } else if (value < MIN_VALUE){
            target_value = MIN_VALUE;
        } else {
            target_value = value;
        }
        target_value_percent = (int)(target_value * 100 / (MAX_VALUE-MIN_VALUE));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(background,0,0,mPaint);
        matrix.setRotate(present_value_percent * FULL_ANGLE / 100, pin.getWidth()/2+4, pin.getHeight()/2 + 4);
        canvas.drawBitmap(pin, matrix, mPaint);
        /*canvas.drawText(df.format( (int)((MAX_VALUE-MIN_VALUE) * present_value_percent / 100)) + "v",
                width/2.f - 30, height/2.f + 60, mTextPaint);*/

        canvas.drawText(df.format( target_value) + unit,
                width/2.f - 30, height/2.f + 60, mTextPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        refreshThread = null;
    }

    private float caculateInterval(float target, float present){
        float a = (target - present) * 7 / 100;
        return Math.abs(a) > MIN_INTERVAL? a : (MIN_INTERVAL * (target>present ? 1 : -1));
    }
}
