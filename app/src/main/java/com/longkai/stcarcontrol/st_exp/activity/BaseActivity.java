package com.longkai.stcarcontrol.st_exp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.longkai.stcarcontrol.st_exp.communication.btComm.BTServer;

import org.apache.log4j.Level;

import java.io.File;

import de.mindpipe.android.logging.log4j.LogConfigurator;

import static com.longkai.stcarcontrol.st_exp.Utils.FileUtils.INTERNAL_PATH;

/**
 * 用于与底层蓝牙通信
 *
 * Created by Administrator on 2017/7/9.
 */

public class BaseActivity extends AppCompatActivity {
    public BTServer mBtServer;
    protected static boolean hardwareConnected = false;
    protected static boolean communicationEstablished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        实现全沉浸效果，只能在android5.0以上有效
         */
        //getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= 21) {
            View mDecorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    /*| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION*/;
            mDecorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        verifyStoragePermissions(this);

        /*mBtServer = new BTServer(BTManager.getInstance().getBtAdapter(),
                mBTDetectedHandler,
                getApplicationContext());*/
    }




    /********************************************************************************/
    Handler mBTDetectedHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1000:
                    Toast.makeText(getApplicationContext(), "Bt Connected", Toast.LENGTH_SHORT).show();
                    break;
                case 1001:
                    Toast.makeText(getApplicationContext(), "Bt Disconnected", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "Unknow Bt event", Toast.LENGTH_SHORT).show();
            }
        }
    };

    protected void startBTConnect() {
        Log.d("BT LK", "startSendThread");
        //btServer = new BTServer(BTManager.getInstance().getBtAdapter(), detectedHandler, mWifiAdmin);
        if (null != mBtServer) {
            mBtServer.connectToDevice();
        }
        else {
            Log.d("BT LK", "con't start fc thread.");
        }
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
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    private void verifyStoragePermissions(Activity activity) {

        try {
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, 1);
            } else {
//                Toast.makeText(this, "permitted", Toast.LENGTH_SHORT).show();
//                logConfig();
//                FileUtils.createSDDir(INTERNAL_PATH + "testlk");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (permissions.length > 0) {
                    switch (permissions[0]) {
                        case Manifest.permission.WRITE_EXTERNAL_STORAGE://权限1
                            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(this, "permitted", Toast.LENGTH_SHORT).show();
//                            logConfig();
                            } else {
                                Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                }
                break;
            default:
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
