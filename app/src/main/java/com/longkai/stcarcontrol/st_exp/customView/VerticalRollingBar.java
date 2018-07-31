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

    private Bitmap backBar;
    private Bitmap pin;
    private Bitmap scaleBar;
    private Paint mPaint;
    private Paint mRectPaint;

    private TextPaint mTextPaint;
    int backgroundWidth, backgroundheight;
    int zeroPoint;
    int intervalDistance = 5;
    private float value = 30.0f;
    private float tmpfloat;

    private static final float MIN_INTERVAL = 0.1f;
    private static final float MAX_VALUE = 100.0f;
    private static final float MIN_VALUE = -10.0f;

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

    public synchronized void setValue(float value){
        this.value = value;
        this.invalidate();
    }

    private void init(Context context, colorStyle style){
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
        backgroundheight = backBar.getHeight();
        zeroPoint = pin.getHeight()/2;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(backBar, 0, zeroPoint, mPaint);
        canvas.drawBitmap(scaleBar, backgroundWidth + intervalDistance, zeroPoint, mPaint);
        tmpfloat = backgroundheight - ((value - MIN_VALUE) * backgroundheight / (MAX_VALUE - MIN_VALUE));

        canvas.drawBitmap(pin,
                backgroundWidth + intervalDistance,
                tmpfloat,
                mPaint);
        canvas.drawRect(0, tmpfloat + pin.getHeight() /2 ,
                backgroundWidth,
                backgroundheight + pin.getHeight() /2,
                mRectPaint);
        //draw text
        canvas.drawText(String.valueOf(value),
                backgroundWidth + intervalDistance + pin.getWidth(),
                tmpfloat + pin.getHeight(),
                mTextPaint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(backgroundWidth + pin.getWidth() + intervalDistance + 70, backgroundheight + pin.getHeight() );
    }

    private enum colorStyle {
        green,
        blue
    }
}