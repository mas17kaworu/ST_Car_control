package com.longkai.stcarcontrol.st_exp.customView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * 一个自定义点击区域的控件
 * 原理是透明的地方点击返回false
 *
 * Created by Administrator on 2017/7/9.
 */

public class MenuViewItem extends FrameLayout {

    private int width = -1;

    private int height = -1;

    private Bitmap bitmap;

    public MenuViewItem(Context context) {

        super( context);

    }



    public MenuViewItem(Context context, AttributeSet attrs, int defStyle) {

        super( context, attrs, defStyle);

    }



    public MenuViewItem(Context context, AttributeSet attrs) {

        super( context, attrs);

    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP){
            if (mOnNewTouchEventListener!= null) {
                mOnNewTouchEventListener.onUp();
            }
        }

        if(action != MotionEvent.ACTION_DOWN) {

            return super.onTouchEvent( event);

        }

        int x = (int)event.getX();

        int y = (int)event.getY();

        if(width == -1 || height == -1) {

            Drawable drawable = ((StateListDrawable)getBackground()).getCurrent();

            bitmap = ((BitmapDrawable)drawable).getBitmap();

            width = getWidth();

            height = getHeight();

        }

        if(null == bitmap || x < 0 || y < 0 || x >= width || y >= height) {

            return false;

        }

        int pixel = bitmap.getPixel( x, y);

        if(Color.TRANSPARENT == pixel) {

            return false;

        }

        if (mOnNewTouchEventListener!=null) {
            mOnNewTouchEventListener.onDown();
        }
        return super.onTouchEvent( event);

    }

    public void setOnNewTouchEventListener(OnNewTouchEventListener listener){
        mOnNewTouchEventListener = listener;
    }

    OnNewTouchEventListener mOnNewTouchEventListener;

    public interface OnNewTouchEventListener{
        void onDown();
        void onUp();
    }

}
