package com.longkai.stcarcontrol.st_exp.customView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import androidx.annotation.Nullable;
import android.text.TextPaint;
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
    private TextPaint mTextPaint;
    private static final int mViewHeight = 400;
    private static final int mViewWidth = 600;


    private static final int BASE_COLOR = 0x55000039;//000039  4B0082
    private static final int BLUE_COLOR = 0x6639008C;
    private static final int PURPLE_COLOR = 0x77BD0094;
    private static final int RED_COLOR = 0x77EE0000;
    private static final int YELLOW_COLOR = 0x99FFA500;
    private static final int WHITE_COLOR = 0xCCFFFFE0;
    private Bitmap colorPool;

    // The points are defined for density=2 screens.
    private static final int BASE_DENSITY = 2;
    private static final int[] mHeatPointPosForDensity2 = {
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
            60,
            40,
            150,
            20,
            20,
            35,
            75,
            66,
            45
            };

    private int radio = 70;

    private RadialGradient radialGradient = null;
    private int[] mHeatPointPos = new int[mHeatPointPosForDensity2.length];

    public CoverHeatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
        colorPool = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_sewentiao);
    }

    public void setTempAndRefresh(float temperature[]){
        //      0   1  2  3  4  5  6  7  8
        //    U[5,  7, 8, 9,10,12,13,17,11] to
        //      17 13 12  9 8  10 7  5  11

        mHeatPointTemp[0] = (int)temperature[7];
        mHeatPointTemp[1] = (int)temperature[6];
        mHeatPointTemp[2] = (int)temperature[5];
        mHeatPointTemp[3] = (int)temperature[3];
        mHeatPointTemp[4] = (int)temperature[2];
        mHeatPointTemp[5] = (int)temperature[4];
        mHeatPointTemp[6] = (int)temperature[1];
        mHeatPointTemp[7] = (int)temperature[0];
        mHeatPointTemp[8] = (int)temperature[8];

        this.postInvalidate();
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
        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(30);
        mTextPaint.setColor(Color.WHITE);
        view = this;
//        test.start();

        // Transform heat point position for different densities
        Float density = getResources().getDisplayMetrics().density;
        for (int i = 0; i < mHeatPointPosForDensity2.length; i++) {
            mHeatPointPos[i] = (int) (mHeatPointPosForDensity2[i] * density / BASE_DENSITY);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mCover,0,0,mPaint);
        int num = 0;
        //u17 暂时隐藏
        /*radialGradient = getRadialGradient(num);
        mPaint.setShader(radialGradient);
        canvas.drawCircle(mHeatPointPos[num*2], mHeatPointPos[num*2+1], radio, mPaint);
        canvas.drawText(mHeatPointTemp[num]+"°C",mHeatPointPos[num*2],  mHeatPointPos[num*2+1], mTextPaint);*/
        num++;

        //u13
        radialGradient = getRadialGradient(num);
        mPaint.setShader(radialGradient);
        canvas.drawCircle(mHeatPointPos[num*2], mHeatPointPos[num*2+1], radio, mPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawText(mHeatPointTemp[num]+"°C",mHeatPointPos[num*2],  mHeatPointPos[num*2+1], mTextPaint);
        num++;

        //u12
        radialGradient = getRadialGradient(num);
        mPaint.setShader(radialGradient);
        canvas.drawCircle(mHeatPointPos[num*2], mHeatPointPos[num*2+1], radio, mPaint);
        canvas.drawText(mHeatPointTemp[num]+"°C",mHeatPointPos[num*2],  mHeatPointPos[num*2+1], mTextPaint);
        num++;

        //u9
        /*clipPath.reset();
        clipPath.addRect(240,0,450,mViewHeight, Path.Direction.CW);
        canvas.save();
        canvas.clipPath(clipPath);*/
        radialGradient = getRadialGradient(num);
        mPaint.setShader(radialGradient);
        canvas.drawCircle(mHeatPointPos[num*2], mHeatPointPos[num*2+1], radio, mPaint);
        canvas.drawText(mHeatPointTemp[num]+"°C",mHeatPointPos[num*2],  mHeatPointPos[num*2+1], mTextPaint);
        num++;

        //u8
        radialGradient = getRadialGradient(num);
        mPaint.setShader(radialGradient);
        canvas.drawCircle(mHeatPointPos[num*2], mHeatPointPos[num*2+1], radio, mPaint);
        canvas.drawText(mHeatPointTemp[num]+"°C",mHeatPointPos[num*2],  mHeatPointPos[num*2+1], mTextPaint);
        num++;

        //u10
        radialGradient = getRadialGradient(num);
        mPaint.setShader(radialGradient);
        canvas.drawCircle(mHeatPointPos[num*2], mHeatPointPos[num*2+1], radio, mPaint);
        canvas.drawText(mHeatPointTemp[num]+"°C",mHeatPointPos[num*2],  mHeatPointPos[num*2+1], mTextPaint);
        num++;

        //u7
        radialGradient = getRadialGradient(num);
        mPaint.setShader(radialGradient);
        canvas.drawCircle(mHeatPointPos[num*2], mHeatPointPos[num*2+1], radio, mPaint);
        canvas.drawText(mHeatPointTemp[num]+"°C",mHeatPointPos[num*2],  mHeatPointPos[num*2+1], mTextPaint);
        num++;

        //u5
        radialGradient = getRadialGradient(num);
        mPaint.setShader(radialGradient);
        canvas.drawCircle(mHeatPointPos[num*2], mHeatPointPos[num*2+1], radio, mPaint);
        canvas.drawText(mHeatPointTemp[num]+"°C",mHeatPointPos[num*2],  mHeatPointPos[num*2+1], mTextPaint);
        num++;

        //u11
        radialGradient = getRadialGradient(num);
        mPaint.setShader(radialGradient);
        canvas.drawCircle(mHeatPointPos[num*2], mHeatPointPos[num*2+1], radio, mPaint);
        canvas.drawText(mHeatPointTemp[num]+"°C",mHeatPointPos[num*2],  mHeatPointPos[num*2+1], mTextPaint);
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

        int height =  colorPool.getHeight();

        if (mHeatPointTemp[num]>120){
            mHeatPointTemp[num] = 119;
        } else if (mHeatPointTemp[num] < -30){
            mHeatPointTemp[num] = -30;
        }
        int y = (int) (mHeatPointTemp[num] + 30 ) * height / (120 + 30); // 在120° 和 -30°之间起作用
        if (y == 0) y=1;
        /*int[] colors = new int[5];
        for (int i=0; i<5 ;i++) {
            colors[i] =
        }*/
        gradient = new RadialGradient(mHeatPointPos[num*2], mHeatPointPos[num*2+1], radio,
                new int[] {position2Color(y), position2Color(y*3/4),
                        position2Color(y*2/4), position2Color(y*1/4), position2Color(1)},
                null,
                Shader.TileMode.CLAMP);

        /*if (mHeatPointTemp[num]>70) {
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
        }*/
        return gradient;
    }

    private int position2Color(int y){
        if (y <= 0){
            y = 1;
        }else if (y >= colorPool.getHeight()){
            y = colorPool.getHeight()-1;
        }
        int targetColor = colorPool.getPixel(colorPool.getWidth()/2, colorPool.getHeight() - y);
        targetColor = targetColor & 0x00ffffff | 0x9F000000;
        return targetColor;
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
