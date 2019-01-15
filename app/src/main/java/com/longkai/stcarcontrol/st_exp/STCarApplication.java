package com.longkai.stcarcontrol.st_exp;

import android.app.Application;
import android.util.Log;

import com.longkai.stcarcontrol.st_exp.Utils.CrashHandler;

import org.apache.log4j.Level;

import de.mindpipe.android.logging.log4j.LogConfigurator;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2017/12/10.
 */

public class STCarApplication extends Application {

    public static final boolean inUIDebugMode = false;

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getsInstance().init(this);
    }

    private void logConfig(){
        final LogConfigurator logConfigurator = new LogConfigurator();
        logConfigurator.setFileName("");
        logConfigurator.setRootLevel(Level.ALL);
        logConfigurator.setLevel("org.apache", Level.ALL);
        logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
        logConfigurator.setMaxFileSize(1024 * 1024 * 5);
        logConfigurator.setImmediateFlush(true);
        logConfigurator.configure();

    }
}
