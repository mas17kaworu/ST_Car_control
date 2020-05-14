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
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import com.longkai.stcarcontrol.st_exp.R;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2018/8/25.
 */

public class TorqueDashboard extends View {
    private static final int FULL_ANGLE = 120;

    private Bitmap background;

    private Bitmap scaleBackGround;
    private Bitmap pin;
    private Paint mPaint;
    private TextPaint mTextPaint;
    private float target_value_percent = 80; //0~100
    private float present_value = 0;
    private View view;
    private DecimalFormat df;

    private int leftStartPoint, radius;


    protected   float MIN_INTERVAL = 0.1f;
    protected   float MAX_VALUE = 32.0f; //可变
    protected   float MIN_VALUE = 0f; //可变



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

    protected int resIdScaleBackGround;

    public TorqueDashboard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
      resIdScaleBackGround = R.mipmap.ic_torque_torquirement;
        init(context);
    }


    protected void init(Context context){
        mPaint = new Paint();
        Resources resources = context.getResources();
        background = BitmapFactory.decodeResource(resources, R.mipmap.ic_torque_requirement_back_o);
        pin = BitmapFactory.decodeResource(resources, R.mipmap.ic_torque_requirement_pin);
        scaleBackGround = BitmapFactory.decodeResource(resources, resIdScaleBackGround);
        width = background.getWidth();
        height = background.getHeight();
        matrix = new Matrix();
        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(30);
        mTextPaint.setColor(Color.GREEN);

        radius = width/2;
        leftStartPoint = (int)(radius - radius / 1.41421d);

        df=new DecimalFormat("0.0");
        view = this;
        refreshThread.start();
    }



    public void setPercent(float value){
        if (value > 100) {
            target_value_percent = 100;
        } else if (value < 0){
            target_value_percent = 0;
        } else {
            target_value_percent = value;
        }
    }

    public void setValue(float value){
      if (value > MAX_VALUE) value = MAX_VALUE;
      if (value < MIN_VALUE) value = MIN_VALUE;
      setPercent((value - MIN_VALUE) * 100 / (MAX_VALUE-MIN_VALUE));
    }

    double angle;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(scaleBackGround,0,0,mPaint);
        angle = 135d - present_value / 10d * 9d;
        angle = Math.toRadians(angle);
//        Log.i("LK test", "test " + (radius * (Math.sin(angle) - Math.sin(Math.toRadians(45)))));
        canvas.drawBitmap(pin, (int)(leftStartPoint + ((radius - leftStartPoint) + radius * Math.cos(angle)) - pin.getWidth()/2),
                (int)(height - pin.getHeight() - (radius * (Math.sin(angle) - Math.sin(Math.toRadians(45))))),
                mPaint);
        canvas.drawBitmap(background,0,0,mPaint);
//        canvas.drawText(df.format( (int)((maxValue-minValue) * present_value / 100)) + "v",
//                width/2.f - 30, height/2.f + 60, mTextPaint);
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
