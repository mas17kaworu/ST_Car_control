package com.longkai.stcarcontrol.st_exp.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.longkai.stcarcontrol.st_exp.communication.ConnectionListener;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.btComm.BTManager;
import com.longkai.stcarcontrol.st_exp.communication.btComm.BTServer;

/**
 * 用于与底层蓝牙通信
 *
 * Created by Administrator on 2017/7/9.
 */

public class BaseActivity extends AppCompatActivity {
    public BTServer mBtServer;
    protected boolean hardwareConnected = false;
    protected boolean communicationEstablished = false;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
