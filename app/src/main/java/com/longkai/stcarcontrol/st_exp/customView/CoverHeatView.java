package com.longkai.stcarcontrol.st_exp.customView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.longkai.stcarcontrol.st_exp.R;

/**
 * Created by Administrator on 2017/9/7.
 */

public class CoverHeatView extends View{

    View view;
    private Bitmap mCover;
    private Paint mPaint;
    private static final int mViewHeight = 400;
    private static final int mViewWidth = 600;



    private static final int BASE_COLOR = 0x554B0082;
    private static final int RED_COLOR = 0x77EE0000;
    private static final int YELLOW_COLOR = 0x99FFA500;
    private static final int WHITE_COLOR = 0xCCFFFFE0;

    Path clipPath = new Path();

    private static final int[] mHeatPointPos = {
            130,200,
            440,223,
            655,223,
            170,310,
            285,325,
            505,330,
            655,330,
            560,400,
            560,560,
            };

    private int[] mHeatPointTemp = {
            50,
            40,
            80,
            20,
            20,
            35,
            75,
            36,
            45
            };

    private int radio = 70;

    private RadialGradient radialGradient = null;

    public CoverHeatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context){
        /*for (int i =0; i<200/4; i=i+4) {
            bytes[i] = 0x55;
            bytes[i+1] = 0x55;
            bytes[i+2] = 0x00;
            bytes[i+3] = 0x55;
        }
        String d = new String(bytes);
        int length = bytes.length;*/
        Resources resources = context.getResources();
        Bitmap bitMap = BitmapFactory.decodeResource(resources, R.mipmap.ic_dianluban);
//        mCover = BitmapFactory.decodeByteArray()

        mCover = Bitmap.createBitmap(bitMap.getWidth(), bitMap.getHeight(), Bitmap.Config.ARGB_8888);
        radialGradient = new RadialGradient(200, 200, 100, RED_COLOR, BASE_COLOR,
                Shader.TileMode.CLAMP);
        mPaint = new Paint();
        view = this;
//        test.start();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mCover,0,0,mPaint);
        int num = 0;
        //u17
        radialGradient = getRadialGradient(num);
        mPaint.setShader(radialGradient);
        canvas.drawCircle(mHeatPointPos[num*2], mHeatPointPos[num*2+1], radio, mPaint);
        num++;

        //u13
        radialGradient = getRadialGradient(num);
        mPaint.setShader(radialGradient);
        canvas.drawCircle(mHeatPointPos[num*2], mHeatPointPos[num*2+1], radio, mPaint);
        num++;

        //u12
        radialGradient = getRadialGradient(num);
        mPaint.setShader(radialGradient);
        canvas.drawCircle(mHeatPointPos[num*2], mHeatPointPos[num*2+1], radio, mPaint);
        num++;

        //u9
        /*clipPath.reset();
        clipPath.addRect(240,0,450,mViewHeight, Path.Direction.CW);
        canvas.save();
        canvas.clipPath(clipPath);*/
        radialGradient = getRadialGradient(num);
        mPaint.setShader(radialGradient);
        canvas.drawCircle(mHeatPointPos[num*2], mHeatPointPos[num*2+1], radio, mPaint);
        num++;

        //u8
        radialGradient = getRadialGradient(num);
        mPaint.setShader(radialGradient);
        canvas.drawCircle(mHeatPointPos[num*2], mHeatPointPos[num*2+1], radio, mPaint);
        num++;

        //u10
        radialGradient = getRadialGradient(num);
        mPaint.setShader(radialGradient);
        canvas.drawCircle(mHeatPointPos[num*2], mHeatPointPos[num*2+1], radio, mPaint);
        num++;

        //u7
        radialGradient = getRadialGradient(num);
        mPaint.setShader(radialGradient);
        canvas.drawCircle(mHeatPointPos[num*2], mHeatPointPos[num*2+1], radio, mPaint);
        num++;

        //u5
        radialGradient = getRadialGradient(num);
        mPaint.setShader(radialGradient);
        canvas.drawCircle(mHeatPointPos[num*2], mHeatPointPos[num*2+1], radio, mPaint);
        num++;

        //u11
        radialGradient = getRadialGradient(num);
        mPaint.setShader(radialGradient);
        canvas.drawCircle(mHeatPointPos[num*2], mHeatPointPos[num*2+1], radio, mPaint);
        num++;
        /*clipPath.reset();
        clipPath.addRect(240,0,450,mViewHeight, Path.Direction.CW);
        canvas.save();
        canvas.clipPath(clipPath);
        radialGradient = getRadialGradient(num);
        mPaint.setShader(radialGradient);
        canvas.drawCircle(300, 200, radio, mPaint);
        canvas.restore();*/
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mCover.getWidth(), mCover.getHeight());
    }

    private RadialGradient getRadialGradient(int num){
        RadialGradient gradient;
        if (mHeatPointTemp[num]>70) {
            gradient = new RadialGradient(mHeatPointPos[num*2], mHeatPointPos[num*2+1], radio,
                    new int[] {WHITE_COLOR, YELLOW_COLOR, RED_COLOR, BASE_COLOR},
                    null,
                    Shader.TileMode.CLAMP);
        } else if (mHeatPointTemp[num] > 50){
            gradient = new RadialGradient(mHeatPointPos[num*2], mHeatPointPos[num*2+1], radio,
                    new int[]{YELLOW_COLOR, RED_COLOR, BASE_COLOR},
                    null,
                    Shader.TileMode.CLAMP);
        } else {
            gradient = new RadialGradient(mHeatPointPos[num*2], mHeatPointPos[num*2+1], radio,
                    new int[]{RED_COLOR, BASE_COLOR},
                    null,
                    Shader.TileMode.CLAMP);
        }
        return gradient;
    }

    Thread test = new Thread(){
        int color = 0x55550055;
        @Override
        public void run() {
            super.run();
            while (true) {
                for (int i = 0; i < mViewWidth; i++) {
                    for (int j = 0; j < mViewHeight; j++) {
                        mCover.setPixel(i, j, color);
                    }
                }
                color += 20;

                view.postInvalidate();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };




}
