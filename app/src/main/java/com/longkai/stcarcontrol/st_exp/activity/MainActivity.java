package com.longkai.stcarcontrol.st_exp.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.bluetoothComm.old.BTManager;
import com.longkai.stcarcontrol.st_exp.bluetoothComm.old.BTServer;
import com.longkai.stcarcontrol.st_exp.fragment.FrontHeadLamp;
import com.longkai.stcarcontrol.st_exp.fragment.HomeFragment;
import com.longkai.stcarcontrol.st_exp.fragment.SeatFragment;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private int mLastflag = 10;

    private HomeFragment mHomeFragment;
    private FrontHeadLamp mFrontLampFragment;
    private SeatFragment mSeatFragment;

    private BTServer mBtServer;

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
        initUI();
        setSelect(0);
        mBtServer = new BTServer(BTManager.getInstance().getBtAdapter(),
                mBTDetectedHandler,
                getApplicationContext());
        startBTConnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mBtServer){
            mBtServer.disconnectBT();
        }
    }

    private void initUI(){
        findViewById(R.id.rdoBtn_homepage_home).setOnClickListener(this);
        findViewById(R.id.rdoBtn_homepage_control).setOnClickListener(this);
        findViewById(R.id.rdoBtn_homepage_door).setOnClickListener(this);
        findViewById(R.id.rdoBtn_homepage_front_lamp).setOnClickListener(this);
        findViewById(R.id.rdoBtn_homepage_seat).setOnClickListener(this);

    }



    private void setSelect(int i) {
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
                break;
            case 4:
                break;

        }
        transaction.commit();
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
            case R.id.rdoBtn_homepage_home:
                setSelect(0);
                break;
            case R.id.rdoBtn_homepage_control:

                break;
            case R.id.rdoBtn_homepage_door:
                break;
            case R.id.rdoBtn_homepage_front_lamp:
                setSelect(1);
                break;
            case R.id.rdoBtn_homepage_seat:
                setSelect(2);
                break;
        }
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

    public void startBTConnect() {
        Log.d("BT LK", "startSendThread");
        //btServer = new BTServer(BTManager.getInstance().getBtAdapter(), detectedHandler, mWifiAdmin);
        if (null != mBtServer) {
            mBtServer.connectToDevice();
        }
        else {
            Log.d("BT LK", "con't start fc thread.");
        }
    }

}
