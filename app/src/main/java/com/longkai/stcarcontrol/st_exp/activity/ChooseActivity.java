package com.longkai.stcarcontrol.st_exp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.longkai.stcarcontrol.st_exp.ConstantData;
import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.STCarApplication;
import com.longkai.stcarcontrol.st_exp.Utils.SharedPreferencesUtil;
import com.longkai.stcarcontrol.st_exp.communication.ConnectionListener;
import com.longkai.stcarcontrol.st_exp.communication.ConnectionType;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDGetVersion;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;

import java.util.Timer;
import java.util.TimerTask;


/**
 *
 * Created by Administrator on 2018/5/12.
 */

public class ChooseActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivBTConnectionState, ivWifiConnectionState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_first);
        STCarApplication.verifyStoragePermissions(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                for (String permission:
                     permissions) {
                    if (permission.equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        STCarApplication.logConfig();
                    }
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initUI();

        String verName = "version";
        try {
            verName = getApplicationContext().getPackageManager().
                    getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ((TextView)findViewById(R.id.tv_choose_activity_version)).setText(verName);
        ServiceManager.getInstance().init(getApplicationContext(), new ServiceManager.InitCompleteListener() {
            @Override
            public void onInitComplete() {
                ServiceManager.getInstance().setConnectionListener(mConnectionListener);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ChooseActivity", "onDestroy: ChooseActivity");
        ServiceManager.getInstance().destroy();
    }

    private void initUI(){
        findViewById(R.id.btn_choose_function).setOnClickListener(this);
        findViewById(R.id.btn_choose_VCU).setOnClickListener(this);
        findViewById(R.id.btn_choose_entertainment).setOnClickListener(this);
        ivBTConnectionState = (ImageView) findViewById(R.id.iv_chooseactivity_lost_bluetooth);
        ivBTConnectionState.setOnClickListener(this);
        ivWifiConnectionState = (ImageView) findViewById(R.id.iv_chooseactivity_lost_wifi);
        ivWifiConnectionState.setOnClickListener(this);

        if (communicationEstablished) {
            ivWifiConnectionState.setVisibility(View.INVISIBLE);
            ivBTConnectionState.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_choose_function:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_choose_VCU:
                Intent intent2 = new Intent(this, VCUActivity.class);
                startActivity(intent2);
                break;
            case R.id.btn_choose_entertainment:
                Intent intent3 = new Intent(this, InfotainmentActivity.class);
                startActivity(intent3);
                break;
            case R.id.iv_chooseactivity_lost_wifi:
                SharedPreferencesUtil.put(this, ConstantData.CONNECTION_TYPE, "WIFI");
                if (!hardwareConnected) {
                    ServiceManager.getInstance().connectToDevice(null, mConnectionListener, ConnectionType.Wifi);
                }
                ServiceManager.getInstance().sendCommandToCar(new CMDGetVersion(), getVersionListener);
                break;
            case R.id.iv_chooseactivity_lost_bluetooth:
                SharedPreferencesUtil.put(this, ConstantData.CONNECTION_TYPE, "BT");
                if (!hardwareConnected) {
                    ServiceManager.getInstance().connectToDevice(null, mConnectionListener, ConnectionType.BT);
                }
                ServiceManager.getInstance().sendCommandToCar(new CMDGetVersion(), getVersionListener);
                break;
        }
    }

    Timer timer;
    ConnectionListener mConnectionListener = new ConnectionListener() {
        @Override
        public void onConnected() {
//            Toast.makeText(getApplicationContext(), "Bt Connected", Toast.LENGTH_SHORT).show();
            hardwareConnected = true;
            if (null == timer){
                timer = new Timer();
            }

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    ServiceManager.getInstance().sendCommandToCar(new CMDGetVersion(), getVersionListener);
                }
            }, 2000);//等两秒发送get version command

        }

        @Override
        public void onDisconnected() {
            Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_LONG).show();
            hardwareConnected = false;
            communicationEstablished = false;
            ivWifiConnectionState.setVisibility(View.VISIBLE);
            ivBTConnectionState.setVisibility(View.VISIBLE);
        }
    };


    public String mVersion;
    private CommandListenerAdapter getVersionListener = new CommandListenerAdapter(){
        @Override
        public void onSuccess(BaseResponse response) {
            super.onSuccess(response);
            //invisible View
            mVersion = ((CMDGetVersion.Response)response).getVersion();
            communicationEstablished = true;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ivBTConnectionState.setVisibility(View.INVISIBLE);
                    ivWifiConnectionState.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),
                            "version:" + mVersion ,Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        public void onTimeout() {
            super.onTimeout();
        }
    };
}
