package com.longkai.stcarcontrol.st_exp.customView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

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

    byte[] mHeatPointPos = new byte[18];
    byte[] mHeatPointTmp = new byte[9];
    private RadialGradient radialGradient = null;

    public CoverHeatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init(){
        /*for (int i =0; i<200/4; i=i+4) {
            bytes[i] = 0x55;
            bytes[i+1] = 0x55;
            bytes[i+2] = 0x00;
            bytes[i+3] = 0x55;
        }
        String d = new String(bytes);
        int length = bytes.length;*/
//        Bitmap bitMap = BitmapFactory.decodeByteArray(bytes, 0, length);
//        mCover = BitmapFactory.decodeByteArray()

        mCover = Bitmap.createBitmap( mViewWidth, mViewHeight, Bitmap.Config.ARGB_8888);
        radialGradient = new RadialGradient(200, 200, 100, RED_COLOR, BASE_COLOR,
                Shader.TileMode.CLAMP);
        mPaint = new Paint();
        view = this;
        test.start();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mCover,0,0,mPaint);



        radialGradient = new RadialGradient(200, 200, 100,
                new int[] {YELLOW_COLOR, RED_COLOR, BASE_COLOR},
                null,
                Shader.TileMode.CLAMP);
        mPaint.setShader(radialGradient);
        canvas.drawCircle(200, 200, 100, mPaint);


        clipPath.reset();
        clipPath.addRect(240,0,450,mViewHeight, Path.Direction.CW);
        canvas.save();
        canvas.clipPath(clipPath);
        radialGradient = new RadialGradient(300, 200, 100,
                new int[] {WHITE_COLOR, YELLOW_COLOR, RED_COLOR, BASE_COLOR},
                null,
                Shader.TileMode.CLAMP);
        mPaint.setShader(radialGradient);
        canvas.drawCircle(300, 200, 100, mPaint);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mCover.getWidth(), mCover.getHeight());
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
