package com.longkai.stcarcontrol.st_exp.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.longkai.stcarcontrol.st_exp.ConstantData;
import com.longkai.stcarcontrol.st_exp.Enum.TboxStateEnum;
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
import com.longkai.stcarcontrol.st_exp.fragment.VCUBMSFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUChargeFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUGYHLSDFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUTorqueFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUVCUCFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUHomeFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUMCUFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUTboxFragment;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadPoolExecutor;

import static android.support.v4.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
import static android.support.v4.widget.DrawerLayout.LOCK_MODE_UNLOCKED;

/**
 * Created by Administrator on 2018/5/12.
 */

public class VCUActivity extends BaseActivity implements View.OnClickListener{

    private int mLastflag = 10;

    private VCUHomeFragment mVCUHomeFragment;
    private VCUVCUCFragment mVCUVCUCFragment;
    private VCUGYHLSDFragment mVCUGYHLSDFragment;
    private VCUBMSFragment mVCUBMSFragment;
    private VCUMCUFragment vcumcuFragment;
    private VCUTboxFragment vcuTboxFragment;
    private VCUChargeFragment vcuChargeFragment;
    private VCUTorqueFragment vcuTorqueFragment;

    private HorizontalListView hListView;
    private HorizontalListViewAdapter hListViewAdapter;

    private ListView lvDrawerVCU;
    private DrawerLayout drawerLayoutVCU;

    private ImageView ivConnectionState, ivWifiConnectionState;
    private ImageView ivDiagram;//框图
    public int mSelectedMode = 0;
    public VCUState vcuState;

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
        setSelect(0);
        vcuState = VCUState.HomeScreen;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ServiceManager.getInstance().destroy();
    }

    private void initUI(){

        ivConnectionState = (ImageView) findViewById(R.id.iv_vcu_lost_connect);
        ivConnectionState.setOnClickListener(this);
        ivWifiConnectionState = (ImageView) findViewById(R.id.iv_vcu_lost_wifi);
        ivWifiConnectionState.setOnClickListener(this);
        ivDiagram = (ImageView) findViewById(R.id.iv_vcu_activity_diagram);
        ivDiagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivDiagram.setVisibility(View.INVISIBLE);
            }
        });

        hListView = (HorizontalListView) findViewById(R.id.vcu_horizon_listview);
        final int[] ids = {R.drawable.vcu_activity_bottom_home,
                R.drawable.vcu_activity_bottom_vcu,
//                R.drawable.vcu_activity_bottom_gyhlsd,
                R.drawable.vcu_activity_bottom_bms,
                R.drawable.vcu_activity_bottom_mcu,
                R.drawable.vcu_activity_bottom_tbox,
//                R.drawable.vcu_activity_bottom_gyhlxd,
//                R.drawable.vcu_activity_bottom_charge
        };

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

        drawerLayoutVCU = (DrawerLayout) findViewById(R.id.drawerLayout_vcu);
        drawerLayoutVCU.setScrimColor(Color.TRANSPARENT);//去除阴影
        initDrawerLayout();
    }

    private void updateDrawer(VCUState state){
        vcuState = state;
        switch (vcuState){
            case MCU:
            case HomeScreen:
                drawerLayoutVCU.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED);
                break;
            case BMS:
                findViewById(R.id.rl_drawer_bms).setVisibility(View.VISIBLE);
                findViewById(R.id.rl_drawer_vcu).setVisibility(View.INVISIBLE);
                findViewById(R.id.rl_drawer_tbox).setVisibility(View.INVISIBLE);
                drawerLayoutVCU.setDrawerLockMode(LOCK_MODE_UNLOCKED);
                break;
            case VCU:
                findViewById(R.id.rl_drawer_vcu).setVisibility(View.VISIBLE);
                findViewById(R.id.rl_drawer_tbox).setVisibility(View.INVISIBLE);
                findViewById(R.id.rl_drawer_bms).setVisibility(View.INVISIBLE);
                drawerLayoutVCU.setDrawerLockMode(LOCK_MODE_UNLOCKED);
                break;
            case TBox:
                findViewById(R.id.rl_drawer_vcu).setVisibility(View.INVISIBLE);
                findViewById(R.id.rl_drawer_tbox).setVisibility(View.VISIBLE);
                findViewById(R.id.rl_drawer_bms).setVisibility(View.INVISIBLE);
                drawerLayoutVCU.setDrawerLockMode(LOCK_MODE_UNLOCKED);
                break;
        }
        /*final List<String> list = new ArrayList<String>();
        list.add("网易");
        list.add("腾讯");
        list.add("新浪");
        list.add("搜狐");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        lvDrawerVCU.setAdapter(adapter);
        lvDrawerVCU.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDrawerLayout();
            }
        });*/


    }

    private void initDrawerLayout(){

        //vcu Drawer
        findViewById(R.id.btn_vcu_gysd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send command
                setSelect(1);
                showDrawerLayout();
                //only for test
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mVCUVCUCFragment.getController().shangDianState2();
                    }
                }, 500);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mVCUVCUCFragment.getController().shangDianState3();
                    }
                }, 2000);

            }
        });

        findViewById(R.id.btn_vcu_gyxd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelect(1);
                showDrawerLayout();

                //only for test
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mVCUVCUCFragment.getController().xiaDianState2();
                    }
                }, 500);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mVCUVCUCFragment.getController().xiaDianState3();
                    }
                }, 2000);
            }
        });

        findViewById(R.id.btn_vcu_niuju_jisuan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelect(8);
                showDrawerLayout();
            }
        });

        findViewById(R.id.btn_vcu_charging).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelect(7);
                showDrawerLayout();
            }
        });


        findViewById(R.id.btn_tbox_date_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vcuTboxFragment.getController().changeTo(TboxStateEnum.DateTime);
                showDrawerLayout();
            }
        });

        findViewById(R.id.btn_tbox_data_collect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vcuTboxFragment.getController().changeTo(TboxStateEnum.DataCollect);
                showDrawerLayout();
            }
        });

        findViewById(R.id.btn_tbox_data_store).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vcuTboxFragment.getController().changeTo(TboxStateEnum.DataStore);
                showDrawerLayout();
            }
        });

        findViewById(R.id.btn_tbox_data_transpot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vcuTboxFragment.getController().changeTo(TboxStateEnum.DataTransport);
                showDrawerLayout();
            }
        });

        findViewById(R.id.btn_tbox_data_resend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vcuTboxFragment.getController().changeTo(TboxStateEnum.DataResend);
                showDrawerLayout();
            }
        });

        findViewById(R.id.btn_tbox_individual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vcuTboxFragment.getController().changeTo(TboxStateEnum.Individual);
                showDrawerLayout();
            }
        });

        findViewById(R.id.btn_tbox_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vcuTboxFragment.getController().changeTo(TboxStateEnum.Register);
                showDrawerLayout();
            }
        });

        findViewById(R.id.btn_tbox_remote_control).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vcuTboxFragment.getController().changeTo(TboxStateEnum.RemoteControl);
                showDrawerLayout();
            }
        });

        findViewById(R.id.btn_tbox_mail_and_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vcuTboxFragment.getController().changeTo(TboxStateEnum.MailAndPhone);
                showDrawerLayout();
            }
        });
    }

    private void showDrawerLayout() {
        if (!drawerLayoutVCU.isDrawerOpen(Gravity.START)) {
            drawerLayoutVCU.openDrawer(Gravity.START);
        } else {
            drawerLayoutVCU.closeDrawer(Gravity.START);
        }
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
                if (mVCUHomeFragment == null) {
                    mVCUHomeFragment = new VCUHomeFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, mVCUHomeFragment);
                updateDrawer(VCUState.HomeScreen);
                break;
            case 1:
                if (mVCUVCUCFragment == null){
                    mVCUVCUCFragment = new VCUVCUCFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, mVCUVCUCFragment);
                updateDrawer(VCUState.VCU);
                break;
            case 200:
                if (mVCUGYHLSDFragment == null){
                    mVCUGYHLSDFragment = new VCUGYHLSDFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, mVCUGYHLSDFragment);
                break;
            case 2:
                if (mVCUBMSFragment == null){
                    mVCUBMSFragment = new VCUBMSFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, mVCUBMSFragment);
                updateDrawer(VCUState.BMS);
                break;
            case 3:
                if (vcumcuFragment == null){
                    vcumcuFragment = new VCUMCUFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, vcumcuFragment);
                updateDrawer(VCUState.MCU);
                break;
            case 4:
                if (vcuTboxFragment == null){
                    vcuTboxFragment = new VCUTboxFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, vcuTboxFragment);
                updateDrawer(VCUState.TBox);
                break;
            case 6:
                if (mVCUGYHLSDFragment == null){
                    mVCUGYHLSDFragment = new VCUGYHLSDFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, mVCUGYHLSDFragment);
                break;
            case 7:
                if (vcuChargeFragment == null){
                    vcuChargeFragment = new VCUChargeFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, vcuChargeFragment);
                break;
            case 8:
                if (vcuTorqueFragment == null){
                    vcuTorqueFragment = new VCUTorqueFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, vcuTorqueFragment);
                break;
            default:
                break;

        }
        transaction.commit();
    }

    private void releaseFragment(){
        mVCUHomeFragment = null;
        mVCUVCUCFragment = null;
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

    public enum VCUState{
        HomeScreen,
        VCU,
        BMS,
        MCU,
        TBox
    }
}
