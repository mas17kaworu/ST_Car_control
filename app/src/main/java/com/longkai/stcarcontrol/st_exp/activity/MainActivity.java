package com.longkai.stcarcontrol.st_exp.activity;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import com.longkai.stcarcontrol.st_exp.Utils.FileUtils;
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
import com.longkai.stcarcontrol.st_exp.fragment.FrontHeadLampTest;
import com.longkai.stcarcontrol.st_exp.fragment.FrontHeadLampTest2;
import com.longkai.stcarcontrol.st_exp.fragment.HighBeamLight;
import com.longkai.stcarcontrol.st_exp.fragment.HomeFragment;
import com.longkai.stcarcontrol.st_exp.fragment.SeatFragment;

import java.io.File;
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
    private FrontHeadLampTest2 frontHeadLampTest;


    private HorizontalListView hListView;
    private HorizontalListViewAdapter hListViewAdapter;

    private ImageView ivConnectionState, ivWifiConnectionState;
    private ImageView ivDiagram;
    public int mSelectedMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    *//*| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION*//*;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }*/
        setContentView(R.layout.activity_main);




        ServiceManager.getInstance().init(getApplicationContext(), new ServiceManager.InitCompleteListener() {
            @Override
            public void onInitComplete() {
                ServiceManager.getInstance().setConnectionListener(mConnectionListener);
            }
        });

        ivDiagram = (ImageView) findViewById(R.id.iv_mainactivity_diagram);
        ivDiagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivDiagram.setVisibility(View.INVISIBLE);
            }
        });

        initUI();
        setSelect(0);

        byte UnlockR = (byte) 0x80;
        byte test = (byte) 0xff;
        test &= (~UnlockR);
        Log.d("testLK", UnlockR + "  " + test);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android
                .Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);



        int tmp = 500;
//        float tmp2 = ((float)(5 * tmp) / 1024);
//        Toast.makeText(getApplication()," "+tmp2,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //创建文件夹
                    /*if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        File file = new File(Environment.getExternalStorageDirectory() + "/aa/bb/");
                        if (!file.exists()) {
                            Log.d("jim", "path1 create:" + file.mkdirs());
                        }
                    }*/


                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void initUI(){
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

    private boolean hardwareConnected = false;
    private ConnectionType connectedType;

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
            hardwareConnected = false;
            ivConnectionState.setVisibility(View.VISIBLE);
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
            case 102:
                if (frontHeadLampTest == null){
                    frontHeadLampTest = new FrontHeadLampTest2();
                }
                transaction.replace(R.id.main_fragment_content, frontHeadLampTest);
                break;
            default:
                break;

        }
        transaction.commit();
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */

    // Used to load the 'native-lib' library on application startup.
//    static {
//        System.loadLibrary("native-lib");
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.iv_mainactivity_lost_connect://bt connection
                SharedPreferencesUtil.put(this, ConstantData.CONNECTION_TYPE, "BT");
                if (!hardwareConnected) {
                    ServiceManager.getInstance().connectToDevice(null, mConnectionListener, ConnectionType.BT);
                }
                ServiceManager.getInstance().sendCommandToCar(new CMDGetVersion(), getVersionListener);
                break;
            case R.id.iv_mainacivity_lost_wifi:
                SharedPreferencesUtil.put(this, ConstantData.CONNECTION_TYPE, "WIFI");
                if (!hardwareConnected) {
                    ServiceManager.getInstance().connectToDevice(null, mConnectionListener, ConnectionType.Wifi);
                }
                ServiceManager.getInstance().sendCommandToCar(new CMDGetVersion(), getVersionListener);
                break;
        }
    }

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

    private void releaseFragment(){
        mHomeFragment = null;
        mFrontLampFragment = null;
        mSeatFragment = null;
        mHighBeamLight = null;
        mDoorFragment = null;
        mCenterControlFragment = null;
        mCarBackFragment = null;
        mCarBackCoverFragment = null;
        mBCMDiagnosticFragment = null;
        frontHeadLampTest = null;
        System.gc();
    }


    public void showDiagram(String diagramName){
        FileUtils.copyDiagram2SDCard(this , diagramName);

        String filepath = FileUtils.INTERNAL_PATH + FileUtils.DIAGRAM_PIC + File.separator + diagramName;

        File file = new File(filepath);
        if (file.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(filepath);
            // 将图片显示到ImageView中
            ivDiagram.setVisibility(View.VISIBLE);
            ivDiagram.setImageBitmap(bm);
        }
    }
}
