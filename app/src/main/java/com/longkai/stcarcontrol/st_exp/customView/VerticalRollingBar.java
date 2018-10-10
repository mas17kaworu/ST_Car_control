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
 * Created by Administrator on 2018/7/9.
 */

public class VerticalRollingBar extends View {

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
    private float value = 30.0f;
    private float tmpfloat;

    protected float MIN_INTERVAL = 0.1f;
    protected float maxValue = 100.0f;
    protected float minValue = -10.0f;

    public VerticalRollingBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VerticalRollingBar);
        int style = array.getInteger(R.styleable.VerticalRollingBar_style, 0);

        if (style == 0) {
            init(context, colorStyle.green);
        } else {
            init(context, colorStyle.blue);
        }
    }

    public synchronized void setMaxValue(float maxValue){
        this.maxValue = maxValue;
    }

    public synchronized void setMinValue(float minValue){
        this.minValue = minValue;
    }

    public synchronized void setValue(float value){
        if (value > maxValue){
            value = maxValue;
        }
        if (value < minValue){
            value = minValue;
        }
        this.value = value;
        this.postInvalidate();
    }

    protected void init(Context context, colorStyle style){
        mPaint = new Paint();
        mRectPaint = new Paint();
        Resources resources = context.getResources();
        switch (style){
            case green:
                mRectPaint.setColor(Color.GREEN);
                pin = BitmapFactory.decodeResource(resources, R.mipmap.ic_verticalbar_green_pin);
                break;
            case blue:
                mRectPaint.setColor(0xff44A6CD);
                pin = BitmapFactory.decodeResource(resources, R.mipmap.ic_verticalbar_red_pin);
                break;
        }
        backBar = BitmapFactory.decodeResource(resources, R.mipmap.ic_verticalbar_back);
        scaleBar = BitmapFactory.decodeResource(resources, R.mipmap.ic_verticalbar_scale_down);
        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(20);
        mTextPaint.setColor(Color.WHITE);
        backgroundWidth = backBar.getWidth();
        backgroundHeight = backBar.getHeight();
        zeroPoint = pin.getHeight()/2;

    }

    //// TODO: 2018/8/4 torque need setPercent function
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this instanceof TorqueVerticalbar){
            canvas.drawBitmap(scaleBar, 0, 0, mPaint);
            canvas.drawBitmap(backBar, scaleWidth + intervalDistance, zeroPoint, mPaint);
            tmpfloat = backgroundHeight - ((value - minValue) * backgroundHeight / (maxValue - minValue));


            canvas.drawRect(scaleWidth + intervalDistance, tmpfloat,
                    scaleWidth + backgroundWidth + intervalDistance,
                    backgroundHeight + zeroPoint,
                    mRectPaint);

        }else {
            canvas.drawBitmap(backBar, 0, zeroPoint, mPaint);
            canvas.drawBitmap(scaleBar, backgroundWidth + intervalDistance, zeroPoint, mPaint);
            tmpfloat = backgroundHeight - ((value - minValue) * backgroundHeight / (maxValue - minValue));

            canvas.drawBitmap(pin,
                    backgroundWidth + intervalDistance,
                    tmpfloat,
                    mPaint);
            canvas.drawRect(0, tmpfloat + pin.getHeight() / 2,
                    backgroundWidth,
                    backgroundHeight + pin.getHeight() / 2,
                    mRectPaint);
            //draw text
            canvas.drawText(String.valueOf(value),
                    backgroundWidth + intervalDistance + pin.getWidth(),
                    tmpfloat + pin.getHeight(),
                    mTextPaint);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this instanceof TorqueVerticalbar) {
            setMeasuredDimension(scaleWidth + intervalDistance + backgroundWidth, scaleHeight);
        } else {
            setMeasuredDimension(backgroundWidth + pin.getWidth() + intervalDistance + 70, backgroundHeight + pin.getHeight());
        }
    }

    protected enum colorStyle {
        green,
        blue
    }
}
