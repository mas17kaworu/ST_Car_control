package com.longkai.stcarcontrol.st_exp.customView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2018/12/20.
 */

public class MockTestTextView extends AppCompatTextView implements View.OnTouchListener{
    private final String TAG = "MockTestTextView";

    public MockTestTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            Log.i(TAG, "Action down");
        } else if (event.getAction() == MotionEvent.ACTION_UP){
            Log.i(TAG, "Action up");
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            Log.i(TAG, "Action cancel");
        }
        return false;
    }
}
