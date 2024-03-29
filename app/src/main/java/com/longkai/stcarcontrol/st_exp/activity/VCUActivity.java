package com.longkai.stcarcontrol.st_exp.activity;

import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.longkai.stcarcontrol.st_exp.ConstantData;
import com.longkai.stcarcontrol.st_exp.Enum.BMSMonitorEnum;
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
import com.longkai.stcarcontrol.st_exp.fragment.CarInfoFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUBMSFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUBMSMonitorFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUChargeFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUGYHLSDFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUOBCDemoFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUOBCFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUTorqueFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUUpdateFirmwareFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUVCUCFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUHomeFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUMCUFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUTboxFragment;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import static androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
import static androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_BMS;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_CHARGE;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_GYHLSD;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_HOME;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_MCU;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_MONITOR;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_OBC;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_OBC_DEMO;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_TBOX;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_TMP;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_TORQUE;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_UPDATE_FIRMWARE;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_VCUVCU;

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
    private VCUBMSMonitorFragment vcubmsMonitorFragment;
    private VCUOBCFragment vcuobcFragment;
    private VCUUpdateFirmwareFragment vcuUpdateFirmwareFragment;
    private VCUOBCDemoFragment vcuobcDemoFragment;
    private CarInfoFragment mCarInfoFragment;

    private HorizontalListView hListView;
    private HorizontalListViewAdapter hListViewAdapter;

    private ListView lvDrawerVCU;
    private DrawerLayout drawerLayoutVCU;

    private ImageView ivConnectionState, ivWifiConnectionState;
    private ImageView ivDiagram;//框图
    public int mSelectedMode = 0;
    public VCUState vcuState;

    private boolean canChangeWifiConnectVisible = false;

    private AtomicBoolean disableSwitchFragment = new AtomicBoolean(false);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vcu);


        ServiceManager.getInstance().setConnectionListener(mConnectionListener);
        //Service will be init in choose activity, no need for here
        /*ServiceManager.getInstance().init(getApplicationContext(), new ServiceManager.InitCompleteListener() {
            @Override
            public void onInitComplete() {
                ServiceManager.getInstance().setConnectionListener(mConnectionListener);
            }
        });*/

        initUI();
        setSelect(FRAGMENT_TRANSACTION_HOME);
        vcuState = VCUState.HomeScreen;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        ServiceManager.getInstance().destroy();
    }

    private void initUI(){

        ivConnectionState = (ImageView) findViewById(R.id.iv_vcu_lost_connect);
        ivConnectionState.setOnClickListener(this);
        ivWifiConnectionState = (ImageView) findViewById(R.id.iv_vcu_lost_wifi);
        ivWifiConnectionState.setOnClickListener(this);
        canChangeWifiConnectVisible = true;
        if (communicationEstablished) {
            ivWifiConnectionState.setVisibility(View.INVISIBLE);
            ivConnectionState.setVisibility(View.INVISIBLE);
            canChangeWifiConnectVisible = false;
        }
        ivDiagram = (ImageView) findViewById(R.id.iv_vcu_activity_diagram);
        ivDiagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivDiagram.setVisibility(View.INVISIBLE);
            }
        });

        hListView = (HorizontalListView) findViewById(R.id.vcu_horizon_listview);
        final int[] ids = {
                R.drawable.vcu_activity_bottom_home,
                R.drawable.vcu_activity_bottom_car,
                R.drawable.vcu_activity_bottom_vcu,
                R.drawable.vcu_activity_bottom_obc,
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
                if (disableSwitchFragment.get()){
                    return;
                }
                mSelectedMode = position;
                hListViewAdapter.setSelectIndex(position);
                hListViewAdapter.notifyDataSetChanged();
                setSelect(position);
            }
        });

        drawerLayoutVCU = (DrawerLayout) findViewById(R.id.drawerLayout_vcu);
//        drawerLayoutVCU.setScrimColor(Color.TRANSPARENT);//去除阴影
        initDrawerLayout();
    }

    private void updateDrawer(VCUState state){
        vcuState = state;
        switch (vcuState){
            case MCU:
            case UPDATE:
            case HomeScreen:
            case CARINFO:
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
    }

    private void initDrawerLayout(){

        //vcu Drawer
        findViewById(R.id.btn_vcu_gysd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send command
                setSelect(FRAGMENT_TRANSACTION_VCUVCU);
                showDrawerLayout();
                //only for test
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mVCUVCUCFragment != null) {
                            mVCUVCUCFragment.getController().shangDianState1();
                        }
                    }
                });
            }
        });

        findViewById(R.id.btn_vcu_gyxd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelect(FRAGMENT_TRANSACTION_VCUVCU);
                showDrawerLayout();


                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mVCUVCUCFragment !=null){
                            mVCUVCUCFragment.getController().xiaDianState1();
                        }
                    }
                });

                /*handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mVCUVCUCFragment !=null){
                            mVCUVCUCFragment.getController().xiaDianState2();
                        }
                    }
                }, 1000);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mVCUVCUCFragment !=null){
                            mVCUVCUCFragment.getController().xiaDianState3();
                        }
                    }
                }, 2000);*/
            }
        });

        findViewById(R.id.btn_vcu_niuju_jisuan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelect(FRAGMENT_TRANSACTION_TORQUE);//8
                showDrawerLayout();
            }
        });

        findViewById(R.id.btn_vcu_charging).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelect(FRAGMENT_TRANSACTION_CHARGE);
                showDrawerLayout();
            }
        });

        findViewById(R.id.btn_vcu_obc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelect(FRAGMENT_TRANSACTION_OBC);
                showDrawerLayout();
            }
        });

        //bms
        findViewById(R.id.btn_drawer_bms_battery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelect(FRAGMENT_TRANSACTION_BMS);
                showDrawerLayout();
            }
        });

        findViewById(R.id.btn_drawer_bms_connection_detector).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelect(FRAGMENT_TRANSACTION_MONITOR);
                vcubmsMonitorFragment.getController().changeTo(BMSMonitorEnum.Connection);
                showDrawerLayout();
            }
        });

        findViewById(R.id.btn_drawer_bms_insulation_monitor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelect(FRAGMENT_TRANSACTION_MONITOR);
                vcubmsMonitorFragment.getController().changeTo(BMSMonitorEnum.Insulation);
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

        findViewById(R.id.btn_tbox_update_firmware).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelect(FRAGMENT_TRANSACTION_UPDATE_FIRMWARE);
//                vcuTboxFragment.getController().changeTo(TboxStateEnum.UpdateFirmware);
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
            communicationEstablished = false;
            ivConnectionState.setVisibility(View.VISIBLE);
            ivWifiConnectionState.setVisibility(View.VISIBLE);
            canChangeWifiConnectVisible = true;
        }
    };


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
                    ivConnectionState.setVisibility(View.INVISIBLE);
                    ivWifiConnectionState.setVisibility(View.INVISIBLE);
                    canChangeWifiConnectVisible = false;
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
        if (disableSwitchFragment.get()){
            return;
        }
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
            case FRAGMENT_TRANSACTION_HOME:
                if (mVCUHomeFragment == null) {
                    mVCUHomeFragment = new VCUHomeFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, mVCUHomeFragment);
                updateDrawer(VCUState.HomeScreen);
                break;
            case FRAGMENT_TRANSACTION_VCUVCU:
                if (mVCUVCUCFragment == null){
                    mVCUVCUCFragment = new VCUVCUCFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, mVCUVCUCFragment);
                updateDrawer(VCUState.VCU);
                break;
            case FRAGMENT_TRANSACTION_TMP:
                if (mCarInfoFragment == null){
                    mCarInfoFragment = new CarInfoFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, mCarInfoFragment);
                updateDrawer(VCUState.CARINFO);
                break;
            case 200:
                if (mVCUGYHLSDFragment == null){
                    mVCUGYHLSDFragment = new VCUGYHLSDFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, mVCUGYHLSDFragment);
                break;
            case FRAGMENT_TRANSACTION_BMS:
                if (mVCUBMSFragment == null){
                    mVCUBMSFragment = new VCUBMSFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, mVCUBMSFragment);
                updateDrawer(VCUState.BMS);
                break;
            case FRAGMENT_TRANSACTION_MCU:
                if (vcumcuFragment == null){
                    vcumcuFragment = new VCUMCUFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, vcumcuFragment);
                updateDrawer(VCUState.MCU);
                break;
            case FRAGMENT_TRANSACTION_TBOX:
                if (vcuTboxFragment == null){
                    vcuTboxFragment = new VCUTboxFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, vcuTboxFragment);
                updateDrawer(VCUState.TBox);
                break;
            case FRAGMENT_TRANSACTION_GYHLSD:
                if (mVCUGYHLSDFragment == null){
                    mVCUGYHLSDFragment = new VCUGYHLSDFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, mVCUGYHLSDFragment);
                break;
            case FRAGMENT_TRANSACTION_CHARGE:
                if (vcuChargeFragment == null){
                    vcuChargeFragment = new VCUChargeFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, vcuChargeFragment);
                break;
            case FRAGMENT_TRANSACTION_TORQUE:
                if (vcuTorqueFragment == null){
                    vcuTorqueFragment = new VCUTorqueFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, vcuTorqueFragment);
                break;
            case FRAGMENT_TRANSACTION_MONITOR:
                if (vcubmsMonitorFragment == null){
                    vcubmsMonitorFragment = new VCUBMSMonitorFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, vcubmsMonitorFragment);
                break;
            case FRAGMENT_TRANSACTION_OBC:
                if (vcuobcFragment == null){
                    vcuobcFragment = new VCUOBCFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, vcuobcFragment);
                break;
            case FRAGMENT_TRANSACTION_OBC_DEMO:
                if (vcuobcDemoFragment == null){
                  vcuobcDemoFragment = new VCUOBCDemoFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, vcuobcDemoFragment);
                break;
            case FRAGMENT_TRANSACTION_UPDATE_FIRMWARE:
                if (vcuUpdateFirmwareFragment == null){
                    vcuUpdateFirmwareFragment = new VCUUpdateFirmwareFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, vcuUpdateFirmwareFragment);
                updateDrawer(VCUState.UPDATE);
                break;
            default:
                break;

        }
        transaction.commit();
    }

    private void releaseFragment(){
        mVCUHomeFragment = null;
        mVCUVCUCFragment = null;
        //vcubmsMonitorFragment = null;
        //vcuChargeFragment = null;
      vcuobcDemoFragment = null;
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
        CARINFO,
        VCU,
        BMS,
        MCU,
        TBox,
        UPDATE
    }

    public void showDiagram(){
        //homepage for now
        ivDiagram.setImageResource(R.mipmap.ic_vcu_diagram_homepage);
        ivDiagram.setVisibility(View.VISIBLE);
        ivDiagram.postInvalidate();
    }

    public void enableSwitchFragment(){
        disableSwitchFragment.set(false);
    }

    public void disableSwitchFragment(){
        disableSwitchFragment.set(true);
    }

    public void changeWifiConnectVisible(boolean visible) {
        if (canChangeWifiConnectVisible) {
            if (visible) {
                ivConnectionState.setVisibility(View.VISIBLE);
                ivWifiConnectionState.setVisibility(View.VISIBLE);
            } else {
                ivConnectionState.setVisibility(View.INVISIBLE);
                ivWifiConnectionState.setVisibility(View.INVISIBLE);
            }
        }
    }
}
