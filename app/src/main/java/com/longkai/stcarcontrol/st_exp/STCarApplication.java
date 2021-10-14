package com.longkai.stcarcontrol.st_exp;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.longkai.stcarcontrol.st_exp.Utils.CrashHandler;

import org.apache.log4j.Level;

import java.io.File;

import de.mindpipe.android.logging.log4j.LogConfigurator;

import static com.longkai.stcarcontrol.st_exp.Utils.FileUtils.INTERNAL_PATH;

/**
 * Created by Administrator on 2017/12/10.
 */

public class STCarApplication extends Application {

    public static final boolean inUIDebugMode = true;

    @Override
    public void onCreate() {
        super.onCreate();

        CrashHandler.getsInstance().init(this);

//        Log.i("longkai", "Karl test isExternalStorageWritable = " + FileUtils.isExternalStorageWritable());
//        Log.i("longkai", "Karl test INTERNAL_PATH = " + INTERNAL_PATH);
//        logConfig();
//        FileUtils.createSDDir(INTERNAL_PATH + "testlk");
    }



    public static void logConfig(){
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


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static String[] permissions =new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static void verifyStoragePermissions(Activity activity) {
        try {
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, permissions, REQUEST_EXTERNAL_STORAGE);
            } else {
                logConfig();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
