package com.longkai.stcarcontrol.st_exp;

import android.app.Application;
import android.util.Log;

import com.longkai.stcarcontrol.st_exp.Utils.CrashHandler;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2017/12/10.
 */

public class STCarApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        CrashHandler.getsInstance().init(this);
    }
}
