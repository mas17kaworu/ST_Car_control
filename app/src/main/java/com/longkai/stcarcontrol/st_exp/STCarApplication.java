package com.longkai.stcarcontrol.st_exp;

import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.longkai.stcarcontrol.st_exp.Utils.CrashHandler;
import com.longkai.stcarcontrol.st_exp.Utils.FileUtils;

import org.apache.log4j.Level;

import java.io.File;

import de.mindpipe.android.logging.log4j.LogConfigurator;

import static android.content.ContentValues.TAG;
import static com.longkai.stcarcontrol.st_exp.Utils.FileUtils.INTERNAL_PATH;

/**
 * Created by Administrator on 2017/12/10.
 */

public class STCarApplication extends Application {

    public static final boolean inUIDebugMode = false;

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getsInstance().init(this);
//        Log.i("longkai", "Karl test isExternalStorageWritable = " + FileUtils.isExternalStorageWritable());
//        Log.i("longkai", "Karl test INTERNAL_PATH = " + INTERNAL_PATH);
        logConfig();
//        FileUtils.createSDDir(INTERNAL_PATH + "testlk");
    }



    private void logConfig(){
        final LogConfigurator logConfigurator = new LogConfigurator();
        logConfigurator.setFileName(INTERNAL_PATH
                + "ST_DEMO_CAR" + File.separator + "logs"
                + File.separator + "communication.txt");
        logConfigurator.setRootLevel(Level.ALL);
        logConfigurator.setLevel("org.apache", Level.ALL);
        logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
        logConfigurator.setMaxFileSize(1024 * 1024 * 5);
        logConfigurator.setImmediateFlush(true);
        logConfigurator.configure();

    }
}
