package com.longkai.stcarcontrol.st_exp.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.longkai.stcarcontrol.st_exp.ConstantData;
import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.Utils.SharedPreferencesUtil;
import com.longkai.stcarcontrol.st_exp.adapter.HorizontalListViewAdapter;
import com.longkai.stcarcontrol.st_exp.communication.ConnectionListener;
import com.longkai.stcarcontrol.st_exp.communication.ConnectionType;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDGetVersion;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;
import com.longkai.stcarcontrol.st_exp.customView.HorizontalListView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018/5/12.
 */

public class VCUActivity extends BaseActivity implements View.OnClickListener{

    private int mLastflag = 10;


    private HorizontalListView hListView;
    private HorizontalListViewAdapter hListViewAdapter;

    private ImageView ivConnectionState, ivWifiConnectionState;
    private ImageView ivDiagram;
    public int mSelectedMode = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vcu);

        ServiceManager.getInstance().init(getApplicationContext(),
                new ServiceManager.InitCompleteListener() {
                    @Override
                    public void onInitComplete() {
                        ServiceManager.getInstance().setConnectionListener(mConnectionListener);
                    }
                });
        initUI();
    }

    private void initUI(){

        ivConnectionState = (ImageView) findViewById(R.id.iv_vcu_lost_connect);
        ivConnectionState.setOnClickListener(this);
        ivWifiConnectionState = (ImageView) findViewById(R.id.iv_vcu_lost_wifi);
        ivWifiConnectionState.setOnClickListener(this);

        hListView = (HorizontalListView) findViewById(R.id.vcu_horizon_listview);
        final int[] ids = {R.drawable.main_activity_bottom_hompage,
                R.drawable.main_activity_bottom_lamp,
                R.drawable.main_activity_bottom_seat,
                R.drawable.main_activity_bottom_door,
                R.drawable.main_activity_bottom_control,
                R.drawable.main_activity_bottom_back_car,
                R.drawable.main_activity_bottom_back_trunk};

        hListViewAdapter = new HorizontalListViewAdapter(getApplicationContext(), ids);
        hListView.setAdapter(hListViewAdapter);
        hListViewAdapter.setSelectIndex(mSelectedMode);
        hListViewAdapter.notifyDataSetChanged();

        hListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.i("MainActivity", "position = " + position);
                mSelectedMode = position;
                hListViewAdapter.setSelectIndex(position);
                hListViewAdapter.notifyDataSetChanged();
                setSelect(position);
            }
        });


    }



    private boolean hardwareConnected = false;

    public String mVersion;

    Timer timer;

    ConnectionListener mConnectionListener = new ConnectionListener() {
        @Override
        public void onConnected() {
//            Toast.makeText(getApplicationContext(), "Bt Connected", Toast.LENGTH_SHORT).show();
            hardwareConnected = true;
            if (null == timer) {
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
            ivConnectionState.setVisibility(View.VISIBLE);
        }
    };


    private CommandListenerAdapter getVersionListener = new CommandListenerAdapter(){
        @Override
        public void onSuccess(BaseResponse response) {
            super.onSuccess(response);
            //invisible View
            mVersion = ((CMDGetVersion.Response)response).getVersion();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ivConnectionState.setVisibility(View.INVISIBLE);
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

    public void setSelect(int i) {
        ivDiagram.setVisibility(View.INVISIBLE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (i > mLastflag) {
            transaction.setCustomAnimations(R.anim.left_slide_in, R.anim.left_slide_out);
        } else if (i < mLastflag) {
            transaction.setCustomAnimations(R.anim.right_slide_in, R.anim.right_slide_out);
        }
        mLastflag = i;
        releaseFragment();
        switch (i) {
            case 0:
                break;
            default:
                break;

        }
        transaction.commit();
    }

    private void releaseFragment(){
        System.gc();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.iv_vcu_lost_connect://bt connection
                SharedPreferencesUtil.put(this, ConstantData.CONNECTION_TYPE, "BT");
                if (!hardwareConnected) {
                    ServiceManager.getInstance().connectToDevice(null, mConnectionListener, ConnectionType.BT);
                }
                ServiceManager.getInstance().sendCommandToCar(new CMDGetVersion(), getVersionListener);
                break;
            case R.id.iv_vcu_lost_wifi:
                SharedPreferencesUtil.put(this, ConstantData.CONNECTION_TYPE, "WIFI");
                if (!hardwareConnected) {
                    ServiceManager.getInstance().connectToDevice(null, mConnectionListener, ConnectionType.Wifi);
                }
                ServiceManager.getInstance().sendCommandToCar(new CMDGetVersion(), getVersionListener);
                break;
        }
    }
}
