package com.longkai.stcarcontrol.st_exp.customView.dashboard;

import android.content.Context;
import android.content.res.Resources;
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
 * Created by Administrator on 2018/6/2.
 */

public class DashboardView extends View {
    private Bitmap background;
    private Bitmap pin;
    private Paint mPaint;
    private TextPaint mTextPaint;
    private float target_value_percent = 0; //0~100
    private float present_value = 0;
    private View view;
    private DecimalFormat df;

    private static final float MIN_INTERVAL = 0.1f;
    private static final float MAX_VALUE = 9000;
    private static final float MIN_VALUE = 0;

    private Thread refreshThread = new Thread(){
        @Override
        public void run() {
            super.run();
            while (true) {
                if (Math.abs(present_value - target_value_percent)> MIN_INTERVAL) {
                    present_value += caculateInterval(target_value_percent, present_value);
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

    public DashboardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        mPaint = new Paint();
        Resources resources = context.getResources();
        background = BitmapFactory.decodeResource(resources, R.mipmap.ic_dashboard_enginspeed_back);
        pin = BitmapFactory.decodeResource(resources, R.mipmap.ic_dashboard_speed_pin);
        width = background.getWidth();
        height = background.getHeight();
        matrix = new Matrix();
        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(30);
        mTextPaint.setColor(Color.GREEN);

        df=new DecimalFormat("0000");
        view = this;
        refreshThread.start();
    }

    public void setValue(float value){
        target_value_percent = (value - MIN_VALUE) / (MAX_VALUE - MIN_VALUE);
    }

    public void setPercent(float percent){
        target_value_percent = percent;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(background,0,0,mPaint);
        matrix.setRotate(present_value * 240 / 100, width/2, height/2);
        canvas.drawBitmap(pin, matrix, mPaint);
        canvas.drawText(df.format( (int)((MAX_VALUE-MIN_VALUE) * present_value / 100)) + "rpm",
                width/2.f - 40, height/2.f + 100, mTextPaint);
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
