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
 * Created by Administrator on 2018/8/26.
 */

public class MCUVoltageDashboard extends View {
    private static final int FULL_ANGLE = 300;

    private Bitmap background;
    private Bitmap pin;
    private Bitmap icon;
    private Paint mPaint;
    private TextPaint mTextPaint;
    private float target_value_percent = 0; //0~100
    private float present_value_percent = 0;
    private View view;
    private DecimalFormat df;

    private static final float MIN_INTERVAL = 0.1f;
    private static final float MAX_VALUE = 6000f;
    private static final float MIN_VALUE = 0f;

    private Thread refreshThread = new Thread(){
        @Override
        public void run() {
            super.run();
            while (true) {
                if (Math.abs(present_value_percent - target_value_percent)> MIN_INTERVAL) {
                    present_value_percent += caculateInterval(target_value_percent, present_value_percent);
                } else {
                    present_value_percent = target_value_percent;
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

    public MCUVoltageDashboard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        mPaint = new Paint();
        Resources resources = context.getResources();
        background = BitmapFactory.decodeResource(resources, R.mipmap.ic_mcu_voltage_dashboard_back);
        pin = BitmapFactory.decodeResource(resources, R.mipmap.ic_mcu_voltage_dashboard_pin);
        icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_mcu_voltage_dashboard_icon);
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
        target_value_percent = (value - MIN_VALUE) / (MAX_VALUE - MIN_VALUE) * 100;
    }

    public void setPercentage(float value) {
        target_value_percent = value;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(background,0,0,mPaint);
        matrix.postRotate((present_value_percent * FULL_ANGLE / 100) - 150, pin.getWidth()/2, pin.getHeight());
        matrix.postTranslate((width - pin.getWidth()) / 2, height/2-pin.getHeight());

        canvas.drawBitmap(pin, matrix, mPaint);
        matrix.reset();
        canvas.drawBitmap(icon, (width - icon.getWidth()) / 2, (height - icon.getHeight()) / 2,mPaint );
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
