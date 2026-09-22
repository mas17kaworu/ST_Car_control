package com.longkai.stcarcontrol.st_exp.activity;

import android.Manifest;
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

import java.util.ArrayList;
import java.util.List;

/**
 * 用于与底层蓝牙通信
 *
 * Created by Administrator on 2017/7/9.
 */

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    protected static final int REQUEST_STARTUP_PERMISSIONS = 1;

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

        if (savedInstanceState == null) {
            requestStartupPermissions();
        }

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

    @NonNull
    protected List<String> getStartupPermissions() {
        List<String> permissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        return permissions;
    }

    private void requestStartupPermissions() {
        List<String> missingPermissions = new ArrayList<>();
        for (String permission : getStartupPermissions()) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (missingPermissions.isEmpty()) {
            onStartupPermissionsGranted();
        } else {
            ActivityCompat.requestPermissions(this, missingPermissions.toArray(new String[0]),
                    REQUEST_STARTUP_PERMISSIONS);
        }
    }

    protected void onStartupPermissionsGranted() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_STARTUP_PERMISSIONS) {
            return;
        }
        if (grantResults.length == 0 || permissions.length != grantResults.length) {
            Log.w(TAG, "Startup permission request cancelled or incomplete");
            return;
        }

        boolean allGranted = true;
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                Log.w(TAG, "Permission denied: " + permissions[i]);
            }
        }
        if (allGranted) {
            onStartupPermissionsGranted();
        } else {
            Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
