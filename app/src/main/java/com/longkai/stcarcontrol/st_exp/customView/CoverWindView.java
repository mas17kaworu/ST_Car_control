package com.longkai.stcarcontrol.st_exp.customView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.longkai.stcarcontrol.st_exp.R;

/**
 * Created by Administrator on 2017/9/9.
 */

public class CoverWindView extends View {
    private Bitmap mArrowBitmap, dstbmp;

    private int mViewWidth,mViewHeight;

    private float mPower;
    private int mAngle;
    Matrix matrix = new Matrix();

    private Paint mPaint;
    public CoverWindView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    private void init(Context context){
        Resources resources = context.getResources();
        /*mCover = BitmapFactory.decodeResource(resources, R.mipmap.ic_center_control_backpic);
        mViewWidth = mCover.getWidth();
        mViewHeight = mCover.getHeight();
        Log.i("CoverWindView","mViewWidth = " + mViewWidth);
        Log.i("CoverWindView","mViewHeight = " + mViewHeight);*/
        mArrowBitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_wind_direction);
        mPaint = new Paint();
        mAngle = 0;
        mPower = 0.75f;

        matrix.postScale(mPower, 1f);
        dstbmp = Bitmap.createBitmap(mArrowBitmap, 0, 0, mArrowBitmap.getWidth(), mArrowBitmap.getHeight(),
                matrix, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.rotate(mAngle , 260, 350);
        canvas.drawBitmap(dstbmp,220,350,mPaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(mAngle , 690, 350);
        canvas.drawBitmap(dstbmp,650,350,mPaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(mAngle , 980, 350);
        canvas.drawBitmap(dstbmp,940,350,mPaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(mAngle , 1410, 350);
        canvas.drawBitmap(dstbmp,1370,350,mPaint);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(1700, 900);//测出来的
    }

    public void setAngle(int angle){
        mAngle = angle;
        postInvalidate();
    }

    /**
     *
     *
     * @param scale 0.5-1
     */
    public void setPower(float scale){
        mPower = scale;
        matrix.reset();
        matrix.postScale(mPower, 1f);
        dstbmp = Bitmap.createBitmap(mArrowBitmap, 0, 0, mArrowBitmap.getWidth(), mArrowBitmap.getHeight(),
                matrix, true);
        postInvalidate();
    }
}
