package com.longkai.stcarcontrol.st_exp.customView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.longkai.stcarcontrol.st_exp.R;

/**
 * Created by Administrator on 2018/7/31.
 */

public class TorqueVerticalbar extends VerticalRollingBar {


    public TorqueVerticalbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context, colorStyle style){
        mPaint = new Paint();
        mRectPaint = new Paint();
        Resources resources = context.getResources();
        backBar = BitmapFactory.decodeResource(resources, R.mipmap.ic_torque_bar_back);
        scaleBar = BitmapFactory.decodeResource(resources, R.mipmap.ic_torque_bar_scale);
        mTextPaint = new TextPaint();
        mRectPaint.setColor(0xff44A6CD);
        mTextPaint.setTextSize(20);
        mTextPaint.setColor(Color.WHITE);
        backgroundWidth = backBar.getWidth();
        backgroundHeight = backBar.getHeight();
        scaleWidth = scaleBar.getWidth();
        scaleHeight = scaleBar.getHeight();
        zeroPoint = (scaleHeight - backgroundHeight)/2;
    }
}
