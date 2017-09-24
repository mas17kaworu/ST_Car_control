package com.longkai.stcarcontrol.st_exp.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
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
import com.longkai.stcarcontrol.st_exp.fragment.BCMDiagnosticFragment;
import com.longkai.stcarcontrol.st_exp.fragment.CarBackCoverFragment;
import com.longkai.stcarcontrol.st_exp.fragment.CarBackLampFragment;
import com.longkai.stcarcontrol.st_exp.fragment.CenterControlFragment;
import com.longkai.stcarcontrol.st_exp.fragment.DoorFragment;
import com.longkai.stcarcontrol.st_exp.fragment.FrontHeadLamp;
import com.longkai.stcarcontrol.st_exp.fragment.HighBeamLight;
import com.longkai.stcarcontrol.st_exp.fragment.HomeFragment;
import com.longkai.stcarcontrol.st_exp.fragment.SeatFragment;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private int mLastflag = 10;

    private HomeFragment mHomeFragment;
    private FrontHeadLamp mFrontLampFragment;
    private SeatFragment mSeatFragment;
    private HighBeamLight mHighBeamLight;
    private DoorFragment mDoorFragment;
    private CenterControlFragment mCenterControlFragment;
    private CarBackLampFragment mCarBackFragment;
    private CarBackCoverFragment mCarBackCoverFragment;
    private BCMDiagnosticFragment mBCMDiagnosticFragment;


    private HorizontalListView hListView;
    private HorizontalListViewAdapter hListViewAdapter;

    private ImageView ivConnectionState, ivWifiConnectionState;
    public int mSelectedMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    /*| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION*/;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_main);




        ServiceManager.getInstance().init(getApplicationContext(), new ServiceManager.InitCompleteListener() {
            @Override
            public void onInitComplete() {
                ServiceManager.getInstance().setConnectionListener(mConnectionListener);

            }
        });

        initUI();
        setSelect(0);

        byte UnlockR	= (byte) 0x80;
        byte test = (byte) 0xff;
        test &= (~UnlockR);
        Log.i("testLK", UnlockR + "  " + test);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void initUI(){
        findViewById(R.id.rdoBtn_homepage_home).setOnClickListener(this);
        findViewById(R.id.rdoBtn_homepage_control).setOnClickListener(this);
        findViewById(R.id.rdoBtn_homepage_door).setOnClickListener(this);
        findViewById(R.id.rdoBtn_homepage_front_lamp).setOnClickListener(this);
        findViewById(R.id.rdoBtn_homepage_seat).setOnClickListener(this);

        ivConnectionState = (ImageView) findViewById(R.id.iv_mainactivity_lost_connect);
        ivConnectionState.setOnClickListener(this);
        ivWifiConnectionState = (ImageView) findViewById(R.id.iv_mainacivity_lost_wifi);
        ivWifiConnectionState.setOnClickListener(this);

        hListView = (HorizontalListView) findViewById(R.id.horizon_listview);
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

    public String mVersion;

    Timer timer;

    ConnectionListener mConnectionListener = new ConnectionListener() {
        @Override
        public void onConnected() {
//            Toast.makeText(getApplicationContext(), "Bt Connected", Toast.LENGTH_SHORT).show();
            if (null == timer){
                timer = new Timer();
            }
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    ServiceManager.getInstance().sendCommandToCar(new CMDGetVersion(),new CommandListenerAdapter(){
                        @Override
                        public void onSuccess(BaseResponse response) {
                            super.onSuccess(response);
                            //invisible View
                            ivConnectionState.setVisibility(View.INVISIBLE);
                            ivWifiConnectionState.setVisibility(View.INVISIBLE);



                            mVersion = ((CMDGetVersion.Response)response).getVersion();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            mVersion ,Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                        @Override
                        public void onTimeout() {
                            super.onTimeout();
                        }
                    });
                }
            }, 1800);


            /*new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 2000);
*/

        }

        @Override
        public void onDisconnected() {
            Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_LONG).show();
            ivConnectionState.setVisibility(View.VISIBLE);
        }
    };

    public void setSelect(int i) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (i > mLastflag) {
            transaction.setCustomAnimations(R.anim.left_slide_in, R.anim.left_slide_out);
        } else if (i < mLastflag) {
            transaction.setCustomAnimations(R.anim.right_slide_in, R.anim.right_slide_out);
        }
        mLastflag = i;
        switch (i) {
            case 0:
                if (mHomeFragment == null) {
                    mHomeFragment = new HomeFragment();
//                    transaction.add(R.id.main_fragment_content, mHomeFragment);
                }
                transaction.replace(R.id.main_fragment_content, mHomeFragment);
                break;
            case 1:
                if (mFrontLampFragment == null) {
                    mFrontLampFragment = new FrontHeadLamp();
//                    transaction.add(R.id.main_fragment_content, mFrontLampFragment);
                }
                transaction.replace(R.id.main_fragment_content, mFrontLampFragment);

                break;
            case 2:
                if (mSeatFragment == null) {
                    mSeatFragment = new SeatFragment();
//                    transaction.add(R.id.main_fragment_content, mSeatFragment);
                }
                transaction.replace(R.id.main_fragment_content, mSeatFragment);
                break;
            case 3:
                if (mDoorFragment == null) {
                    mDoorFragment = new DoorFragment();
//                    transaction.add(R.id.main_fragment_content, mSeatFragment);
                }
                transaction.replace(R.id.main_fragment_content, mDoorFragment);
                break;
            case 4:
                if (mCenterControlFragment == null) {
                    mCenterControlFragment = new CenterControlFragment();
//                    transaction.add(R.id.main_fragment_content, mSeatFragment);
                }
                transaction.replace(R.id.main_fragment_content, mCenterControlFragment);

                break;
            case 5:
                if (mCarBackFragment == null) {
                    mCarBackFragment = new CarBackLampFragment();
                }
                transaction.replace(R.id.main_fragment_content, mCarBackFragment);
                break;
            case 6:
                if (mCarBackCoverFragment == null){
                    mCarBackCoverFragment = new CarBackCoverFragment();
                }
                transaction.replace(R.id.main_fragment_content, mCarBackCoverFragment);
                break;
            case 100:
                if (mHighBeamLight == null) {
                    mHighBeamLight = new HighBeamLight();
                }
                transaction.replace(R.id.main_fragment_content, mHighBeamLight);
                break;
            case 101:
                if (mBCMDiagnosticFragment == null){
                    mBCMDiagnosticFragment = new BCMDiagnosticFragment();
                }
                transaction.replace(R.id.main_fragment_content, mBCMDiagnosticFragment);
                break;
            default:
                break;

        }
        transaction.commit();
        // TODO: 2017/8/29 release fragment
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            /*case R.id.rdoBtn_homepage_home:
                setSelect(0);
                *//*animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.btn_scale_up);
                v.startAnimation(animation);*//*
//                ((RadioButton)v).setBackgroundResource(R.mipmap.ic_navigationbar_homepage_chose);
//                ((RadioButton)v).setButtonDrawable(R.color.transparent);
//                ViewGroup.LayoutParams params = v.getLayoutParams();
//                params.width*=1.5;
//                v.setLayoutParams(params);
                break;
            case R.id.rdoBtn_homepage_control:
                setSelect(4);
                break;
            case R.id.rdoBtn_homepage_door:
                setSelect(3);
                break;
            case R.id.rdoBtn_homepage_front_lamp:
                setSelect(1);
                break;
            case R.id.rdoBtn_homepage_seat:
                setSelect(2);
                break;*/
            case R.id.iv_mainactivity_lost_connect://bt connection
                SharedPreferencesUtil.put(this, ConstantData.CONNECTION_TYPE, "BT");
                ServiceManager.getInstance().connectToDevice(null, mConnectionListener, ConnectionType.BT);
                break;
            case R.id.iv_mainacivity_lost_wifi:
                SharedPreferencesUtil.put(this, ConstantData.CONNECTION_TYPE, "WIFI");
                ServiceManager.getInstance().connectToDevice(null, mConnectionListener, ConnectionType.Wifi);
                break;
        }
    }




}
