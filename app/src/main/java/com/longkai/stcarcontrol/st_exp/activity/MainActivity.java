package com.longkai.stcarcontrol.st_exp.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.fragment.FrontHeadLamp;
import com.longkai.stcarcontrol.st_exp.fragment.HomeFragment;

public class MainActivity extends BaseActivity {

    private Button btnFront;
    private Button btnControl;
    private Button btnSeat;
    private Button btnGate;
    private Button btnBack;

    private int mLastflag = 10;

    private HomeFragment mHomeFragment;
    private FrontHeadLamp mFrontLampFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initUI();
    }

    private void initUI(){
        btnFront = (Button) findViewById(R.id.main_btn_front);
        btnControl = (Button) findViewById(R.id.main_btn_control_panel);
        btnSeat = (Button) findViewById(R.id.main_btn_seat);
        btnGate = (Button) findViewById(R.id.main_btn_gate);
        btnBack = (Button) findViewById(R.id.main_btn_back);
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
        //隐藏fragment
        hideFragment(transaction);

        switch (i) {
            case 0:
                if (mHomeFragment == null) {
                    mHomeFragment = new HomeFragment();
                    transaction.add(R.id.main_fragment_content, mHomeFragment);
                } else {
                    transaction.show(mHomeFragment);
                }
                break;
            case 1:
                if (mFrontLampFragment == null) {
                    mFrontLampFragment = new FrontHeadLamp();
                    transaction.add(R.id.main_fragment_content, mFrontLampFragment);
                } else {
                    transaction.show(mFrontLampFragment);
                }
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;

        }
        transaction.commit();
    }


    /**
     * 隐藏fragment
     *
     * @param transaction fragment事务
     */
    private void hideFragment(FragmentTransaction transaction) {
        /*if (mHomeFragment != null) {
            transaction.hide(mHomeFragment);
        }
        if (mGalleryFragment != null) {
            transaction.hide(mGalleryFragment);
        }
        if (mPersenCenter != null) {
            transaction.hide(mPersenCenter);
        }*/
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
