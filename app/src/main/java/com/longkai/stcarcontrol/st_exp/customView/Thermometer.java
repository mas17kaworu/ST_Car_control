package com.longkai.stcarcontrol.st_exp.customView;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.longkai.stcarcontrol.st_exp.R;

/**
 * Created by Administrator on 2018/8/26.
 */

public class Thermometer extends View {
    protected Bitmap backBar;
    protected Bitmap pin;
    protected Bitmap scaleBar;
    protected Paint mPaint;
    protected Paint mRectPaint;

    protected TextPaint mTextPaint;
    protected int backgroundWidth, backgroundHeight;
    protected int scaleWidth, scaleHeight;
    protected int zeroPoint;
    protected int intervalDistance = 5;
    int left, right, top, bottom;

    private float value = 30.0f;
    private float percentValue = 0;
    private int pillarHeight;

    private static final float MIN_INTERVAL = 0.1f;
    private static final float MAX_VALUE = 250.0f;
    private static final float MIN_VALUE = -40.0f;

    public Thermometer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public synchronized void setValue(float value){
        this.value = value;
        setPercentValue((value - MIN_VALUE) * 100 / (MAX_VALUE - MIN_VALUE));
    }

    public synchronized void setPercentValue(float percentValue){
        this.percentValue = percentValue;
        pillarHeight = (int) ((bottom - top) * percentValue / 100);
        this.postInvalidate();
    }
    protected void init(Context context){
        mPaint = new Paint();
        mRectPaint = new Paint();
        mRectPaint.setColor(Color.RED);

        Resources resources = context.getResources();
        backBar = BitmapFactory.decodeResource(resources, R.mipmap.ic_mcu_thermometer_back);
        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(20);
        mTextPaint.setColor(Color.WHITE);
        backgroundWidth = backBar.getWidth();
        backgroundHeight = backBar.getHeight();

        left = backgroundWidth - 20;
        top = 10;
        bottom = backgroundHeight - 25;
        right = backgroundWidth - 8;

        //for test
        setPercentValue(50);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
            canvas.drawBitmap(backBar, 0, 0, mPaint);


            canvas.drawRect(
                    left,
                    bottom - pillarHeight,
                    backgroundWidth - 8,
                    backgroundHeight - 25,
                    mRectPaint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(backgroundWidth, backgroundHeight);
    }
}
