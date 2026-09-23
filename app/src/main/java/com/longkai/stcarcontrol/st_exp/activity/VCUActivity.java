package com.longkai.stcarcontrol.st_exp.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.fragment.app.Fragment;
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
import com.longkai.stcarcontrol.st_exp.fragment.VCUChassisFragment;
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
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_CHASSIS;
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

    private static final String STATE_SELECTED_PAGE = "vcu.selectedPage";
    private int mLastflag = -1;

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
    private VCUChassisFragment vcuChassisFragment;

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
        Fragment restoredFragment = getSupportFragmentManager()
                .findFragmentById(R.id.vcu_main_fragment_content);
        if (restoredFragment != null) {
            mLastflag = restoreFragmentReference(restoredFragment);
            updateSelectedTab(mLastflag);
            updateDrawerForPage(mLastflag);
            revealSelectedTab();
        } else {
            setSelect(savedInstanceState == null ? FRAGMENT_TRANSACTION_HOME
                    : savedInstanceState.getInt(STATE_SELECTED_PAGE, FRAGMENT_TRANSACTION_HOME));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_PAGE, mLastflag);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // DrawerLayout restores its own lock/open state after onCreate.
        updateDrawerForPage(mLastflag);
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
        canChangeWifiConnectVisible = !communicationEstablished;
        changeWifiConnectVisible(true);
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
                R.drawable.vcu_activity_bottom_chassis,
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
                setSelect(VCUTabNavigation.pageIdAt(position));
            }
        });

        drawerLayoutVCU = (DrawerLayout) findViewById(R.id.drawerLayout_vcu);
//        drawerLayoutVCU.setScrimColor(Color.TRANSPARENT);//去除阴影
        initDrawerLayout();
    }

    private void updateDrawer(VCUState state){
        vcuState = state;
        switch (vcuState){
            case CHASSIS:
                drawerLayoutVCU.closeDrawers();
                findViewById(R.id.rl_drawer_bms).setVisibility(View.INVISIBLE);
                findViewById(R.id.rl_drawer_vcu).setVisibility(View.INVISIBLE);
                findViewById(R.id.rl_drawer_tbox).setVisibility(View.INVISIBLE);
                drawerLayoutVCU.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED);
                break;
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
        changeWifiConnectVisible(true);
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
            hardwareConnected = false;
            communicationEstablished = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    canChangeWifiConnectVisible = true;
                    changeWifiConnectVisible(true);
                    Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_LONG).show();
                }
            });
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
                    canChangeWifiConnectVisible = false;
                    changeWifiConnectVisible(false);
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
        if (i == 200) {
            i = FRAGMENT_TRANSACTION_GYHLSD;
        }
        if (VCUTabNavigation.tabPositionForPage(i) < 0) {
            Log.e("VCUActivity", "Unknown VCU page: " + i);
            return;
        }
        ivDiagram.setVisibility(View.INVISIBLE);
        if (i == mLastflag) {
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        int direction = VCUTabNavigation.animationDirection(mLastflag, i);
        if (direction > 0) {
            transaction.setCustomAnimations(R.anim.left_slide_in, R.anim.left_slide_out);
        } else if (direction < 0) {
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
                break;
            case FRAGMENT_TRANSACTION_VCUVCU:
                if (mVCUVCUCFragment == null){
                    mVCUVCUCFragment = new VCUVCUCFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, mVCUVCUCFragment);
                break;
            case FRAGMENT_TRANSACTION_TMP:
                if (mCarInfoFragment == null){
                    mCarInfoFragment = new CarInfoFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, mCarInfoFragment);
                break;
            case FRAGMENT_TRANSACTION_BMS:
                if (mVCUBMSFragment == null){
                    mVCUBMSFragment = new VCUBMSFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, mVCUBMSFragment);
                break;
            case FRAGMENT_TRANSACTION_MCU:
                if (vcumcuFragment == null){
                    vcumcuFragment = new VCUMCUFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, vcumcuFragment);
                break;
            case FRAGMENT_TRANSACTION_TBOX:
                if (vcuTboxFragment == null){
                    vcuTboxFragment = new VCUTboxFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, vcuTboxFragment);
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
            case FRAGMENT_TRANSACTION_CHASSIS:
                if (vcuChassisFragment == null) {
                    vcuChassisFragment = new VCUChassisFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, vcuChassisFragment);
                break;
            case FRAGMENT_TRANSACTION_UPDATE_FIRMWARE:
                if (vcuUpdateFirmwareFragment == null){
                    vcuUpdateFirmwareFragment = new VCUUpdateFirmwareFragment();
                }
                transaction.replace(R.id.vcu_main_fragment_content, vcuUpdateFirmwareFragment);
                break;
            default:
                break;

        }
        updateSelectedTab(i);
        updateDrawerForPage(i);
        transaction.commit();
    }

    private void updateSelectedTab(int pageId) {
        mSelectedMode = VCUTabNavigation.tabPositionForPage(pageId);
        hListViewAdapter.setSelectIndex(mSelectedMode);
        hListViewAdapter.notifyDataSetChanged();
    }

    private void updateDrawerForPage(int pageId) {
        switch (pageId) {
            case FRAGMENT_TRANSACTION_CHASSIS:
                updateDrawer(VCUState.CHASSIS);
                break;
            case FRAGMENT_TRANSACTION_HOME:
            case FRAGMENT_TRANSACTION_OBC_DEMO:
                updateDrawer(VCUState.HomeScreen);
                break;
            case FRAGMENT_TRANSACTION_TMP:
                updateDrawer(VCUState.CARINFO);
                break;
            case FRAGMENT_TRANSACTION_VCUVCU:
            case FRAGMENT_TRANSACTION_GYHLSD:
            case FRAGMENT_TRANSACTION_CHARGE:
            case FRAGMENT_TRANSACTION_TORQUE:
            case FRAGMENT_TRANSACTION_OBC:
                updateDrawer(VCUState.VCU);
                break;
            case FRAGMENT_TRANSACTION_BMS:
            case FRAGMENT_TRANSACTION_MONITOR:
                updateDrawer(VCUState.BMS);
                break;
            case FRAGMENT_TRANSACTION_MCU:
                updateDrawer(VCUState.MCU);
                break;
            case FRAGMENT_TRANSACTION_TBOX:
                updateDrawer(VCUState.TBox);
                break;
            case FRAGMENT_TRANSACTION_UPDATE_FIRMWARE:
                updateDrawer(VCUState.UPDATE);
                break;
            default:
                break;
        }
    }

    private int restoreFragmentReference(Fragment fragment) {
        if (fragment instanceof VCUHomeFragment) {
            mVCUHomeFragment = (VCUHomeFragment) fragment;
            return FRAGMENT_TRANSACTION_HOME;
        } else if (fragment instanceof CarInfoFragment) {
            mCarInfoFragment = (CarInfoFragment) fragment;
            return FRAGMENT_TRANSACTION_TMP;
        } else if (fragment instanceof VCUVCUCFragment) {
            mVCUVCUCFragment = (VCUVCUCFragment) fragment;
            return FRAGMENT_TRANSACTION_VCUVCU;
        } else if (fragment instanceof VCUOBCDemoFragment) {
            vcuobcDemoFragment = (VCUOBCDemoFragment) fragment;
            return FRAGMENT_TRANSACTION_OBC_DEMO;
        } else if (fragment instanceof VCUChassisFragment) {
            vcuChassisFragment = (VCUChassisFragment) fragment;
            return FRAGMENT_TRANSACTION_CHASSIS;
        } else if (fragment instanceof VCUBMSFragment) {
            mVCUBMSFragment = (VCUBMSFragment) fragment;
            return FRAGMENT_TRANSACTION_BMS;
        } else if (fragment instanceof VCUMCUFragment) {
            vcumcuFragment = (VCUMCUFragment) fragment;
            return FRAGMENT_TRANSACTION_MCU;
        } else if (fragment instanceof VCUTboxFragment) {
            vcuTboxFragment = (VCUTboxFragment) fragment;
            return FRAGMENT_TRANSACTION_TBOX;
        } else if (fragment instanceof VCUGYHLSDFragment) {
            mVCUGYHLSDFragment = (VCUGYHLSDFragment) fragment;
            return FRAGMENT_TRANSACTION_GYHLSD;
        } else if (fragment instanceof VCUChargeFragment) {
            vcuChargeFragment = (VCUChargeFragment) fragment;
            return FRAGMENT_TRANSACTION_CHARGE;
        } else if (fragment instanceof VCUTorqueFragment) {
            vcuTorqueFragment = (VCUTorqueFragment) fragment;
            return FRAGMENT_TRANSACTION_TORQUE;
        } else if (fragment instanceof VCUBMSMonitorFragment) {
            vcubmsMonitorFragment = (VCUBMSMonitorFragment) fragment;
            return FRAGMENT_TRANSACTION_MONITOR;
        } else if (fragment instanceof VCUOBCFragment) {
            vcuobcFragment = (VCUOBCFragment) fragment;
            return FRAGMENT_TRANSACTION_OBC;
        } else if (fragment instanceof VCUUpdateFirmwareFragment) {
            vcuUpdateFirmwareFragment = (VCUUpdateFirmwareFragment) fragment;
            return FRAGMENT_TRANSACTION_UPDATE_FIRMWARE;
        }
        throw new IllegalStateException("Unknown restored VCU page: " + fragment.getClass().getName());
    }

    private void revealSelectedTab() {
        hListView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                View firstTab = hListView.getChildAt(0);
                if (firstTab == null) {
                    return;
                }
                hListView.removeOnLayoutChangeListener(this);
                int tabWidth = firstTab.getWidth() + firstTab.getPaddingRight();
                int offset = mSelectedMode * tabWidth
                        - (hListView.getWidth() - firstTab.getWidth()) / 2;
                hListView.scrollTo(Math.max(0, offset));
            }
        });
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
        UPDATE,
        CHASSIS
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
        int visibility = visible && canChangeWifiConnectVisible
                && vcuState != VCUState.CHASSIS && vcuState != VCUState.CARINFO
                ? View.VISIBLE : View.INVISIBLE;
        ivConnectionState.setVisibility(visibility);
        ivWifiConnectionState.setVisibility(visibility);
    }
}
