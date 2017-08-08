package com.longkai.stcarcontrol.st_exp.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.adapter.HorizontalListViewAdapter;
import com.longkai.stcarcontrol.st_exp.bluetoothComm.commandList.BTCMDGetVersion;
import com.longkai.stcarcontrol.st_exp.bluetoothComm.old.BTManager;
import com.longkai.stcarcontrol.st_exp.bluetoothComm.old.BTServer;
import com.longkai.stcarcontrol.st_exp.customView.HorizontalListView;
import com.longkai.stcarcontrol.st_exp.fragment.DoorFragment;
import com.longkai.stcarcontrol.st_exp.fragment.FrontHeadLamp;
import com.longkai.stcarcontrol.st_exp.fragment.HighBeamLight;
import com.longkai.stcarcontrol.st_exp.fragment.HomeFragment;
import com.longkai.stcarcontrol.st_exp.fragment.SeatFragment;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private int mLastflag = 10;

    private HomeFragment mHomeFragment;
    private FrontHeadLamp mFrontLampFragment;
    private SeatFragment mSeatFragment;
    private HighBeamLight mHighBeamLight;
    private DoorFragment mDoorFragment;


    private HorizontalListView hListView;
    private HorizontalListViewAdapter hListViewAdapter;
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
        initUI();
        setSelect(0);

        startBTConnect();
        mBtServer.sendCommend(new BTCMDGetVersion());
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

        hListView = (HorizontalListView) findViewById(R.id.horizon_listview);
        final int[] ids = {R.drawable.main_activity_bottom_hompage,
                R.drawable.main_activity_bottom_lamp,
                R.drawable.main_activity_bottom_seat,
                R.drawable.main_activity_bottom_door,
                R.drawable.main_activity_bottom_control};

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
                break;
            case 5:
                if (mHighBeamLight == null) {
                    mHighBeamLight = new HighBeamLight();
                }
                transaction.replace(R.id.main_fragment_content, mHighBeamLight);
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


    Animation animation;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rdoBtn_homepage_home:
                setSelect(0);
                /*animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.btn_scale_up);
                v.startAnimation(animation);*/
//                ((RadioButton)v).setBackgroundResource(R.mipmap.ic_navigationbar_homepage_chose);
//                ((RadioButton)v).setButtonDrawable(R.color.transparent);
//                ViewGroup.LayoutParams params = v.getLayoutParams();
//                params.width*=1.5;
//                v.setLayoutParams(params);
                break;
            case R.id.rdoBtn_homepage_control:

                break;
            case R.id.rdoBtn_homepage_door:
                setSelect(3);
                break;
            case R.id.rdoBtn_homepage_front_lamp:
                setSelect(1);
                break;
            case R.id.rdoBtn_homepage_seat:
                setSelect(2);
                break;
        }
    }




}
