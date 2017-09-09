package com.longkai.stcarcontrol.st_exp.customView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.longkai.stcarcontrol.st_exp.R;

/**
 * Created by Administrator on 2017/8/10.
 */

public class AirconditionDiscView extends View {
    private static String TAG = "Aircondition";
    private Paint mPaint;

    private int viewWidth = -1;
    private int viewHeight = -1;

    private int mOriginCordX = 0;
    private int mOriginCordY = 0;

    private int progressOld = 0;
    private int progressPresent = 0;
    private int progressReal = 0;
    private int progressLast = 0;

    private Bitmap discBitmap, scaleBgBitmap, scaleCoverBitmap;

    public AirconditionDiscView(Context context) {
        super(context);
        init(context);
    }

    public AirconditionDiscView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AirconditionDiscView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context){
        mPaint = new Paint();
        Resources resources = context.getResources();
        discBitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_air_disc);
        scaleBgBitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_air_scale_back);
        scaleCoverBitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_air_scale_cover);
    }

    /**
     * get Progress
     *
     * @return 本应用的范围0~240
     */
    public int getProgress(){
        return progressReal;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP){
            progressOld = progressReal;
            progressPresent = 0;
        }

        if(action != MotionEvent.ACTION_DOWN && action!=MotionEvent.ACTION_MOVE) {
            return super.onTouchEvent(event);
        }

        //
        int x = (int)event.getX();
        int y = (int)event.getY();

        if(viewWidth == -1 || viewHeight == -1) {
            viewWidth = getWidth();
            viewHeight = getHeight();
        }

        if(x < 0 || y < 0 || x >= viewWidth || y >= viewHeight) {
            return false;
        }

        Log.i(TAG, "x= " + x + " y= " + y);
        if (action == MotionEvent.ACTION_DOWN) {
            int pixel = discBitmap.getPixel(x, y);
            if (Color.TRANSPARENT != pixel) {
                mOriginCordX = x;
                mOriginCordY = y;
            }
        }

        if (action == MotionEvent.ACTION_MOVE){
            double tmp = getAngle(viewWidth/2, viewHeight/2, x, y, mOriginCordX, mOriginCordY);
            progressPresent = (int)tmp;

            int x1 = mOriginCordX - viewWidth/2;
            int y1 = mOriginCordY - viewHeight/2;
            int x2 = x - viewWidth/2;
            int y2 = y - viewHeight/2;

//            Log.i(TAG,"* = " + (x1*y2 - x2*y1));

            if ((x1*y2 - x2*y1) < 0 ){
                progressPresent = -progressPresent;
            }


            /*if ((progressPresent + progressOld) > 240 && (progressPresent + progressOld) < 360) {
                progressPresent = 240 - progressOld;
            }

            if ((progressPresent + progressOld) < 0 && (progressPresent + progressOld) > -120) {
                progressPresent = -progressOld;
            }*/
            progressReal = progressReal % 360;
            progressReal = progressOld + progressPresent;
            if (progressReal < 0){
                progressReal += 360;
            }

            if (progressReal > 240) {
                if (progressLast > 120){
                    progressReal = 240;
                } else {
                    progressReal = 0;
                }
            } else {
                progressLast = progressReal;
            }
            Log.i(TAG,"Angle = " + progressPresent);
            invalidate();
        }

        return super.onTouchEvent(event);
    }
    Path clipPath = new Path();
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = discBitmap.getWidth();
        int h = discBitmap.getHeight();
        canvas.save();
        canvas.rotate(-120 + progressReal , w /2, h /2); // -120度是左边0点  范围是-120 到 +120
        canvas.drawBitmap(discBitmap,0,0,mPaint);
        canvas.restore();
        canvas.drawBitmap(scaleBgBitmap,0,0,mPaint);

        clipPath.reset();
        clipPath.moveTo(w / 2,h / 2);
        clipPath.lineTo(0,h);
        clipPath.addArc(0,0,w,h, -120 -90 , progressReal);
        clipPath.lineTo(w / 2,h / 2);
        canvas.save();
        canvas.clipPath(clipPath);
        canvas.drawBitmap(scaleCoverBitmap, 0, 0, mPaint);
        canvas.restore();
        clipPath.close();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(discBitmap.getWidth(), discBitmap.getHeight());
    }


    private double getAngle(int centerX, int centerY, int aX, int aY, int bX, int bY){
        float dx1, dx2, dy1, dy2;
        double angle;
        dx1 = aX - centerX;
        dy1 = aY - centerY;

        dx2 = bX - centerX;
        dy2 = bY - centerY;
        float c = (float)Math.sqrt(dx1 * dx1 + dy1 * dy1) * (float)Math.sqrt(dx2 * dx2 + dy2 * dy2);
        if (c == 0) return -1;
        angle = Math.acos((dx1 * dx2 + dy1 * dy2) / c);
        angle = Math.toDegrees(angle);
        return angle;
    }
}
