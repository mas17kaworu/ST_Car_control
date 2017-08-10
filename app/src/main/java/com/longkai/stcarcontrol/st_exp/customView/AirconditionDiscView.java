package com.longkai.stcarcontrol.st_exp.customView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

    private int width = -1;
    private int height = -1;

    private Bitmap discBitmap;

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

        /*this.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int)event.getX();
                int y = (int)event.getY();
                Log.i(TAG, "x = " + x + " y = " + y);
                return false;
            }
        });*/
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();


        if(action != MotionEvent.ACTION_DOWN && action!=MotionEvent.ACTION_MOVE) {
            return super.onTouchEvent(event);
        }

        //
        int x = (int)event.getX();
        int y = (int)event.getY();
        Log.i(TAG, "x = " + x + " y = " + y);
        if(width == -1 || height == -1) {
            width = getWidth();
            height = getHeight();
        }

        if(x < 0 || y < 0 || x >= width || y >= height) {
            return false;
        }


        /*int pixel = discBitmap.getPixel(x, y);
        if(Color.TRANSPARENT != pixel) {


        }*/


        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(discBitmap,0,0,mPaint);
    }

}
