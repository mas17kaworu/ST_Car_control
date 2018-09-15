package com.longkai.stcarcontrol.st_exp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.longkai.stcarcontrol.st_exp.R;

/**
 *
 * Created by Administrator on 2018/5/12.
 */

public class ChooseActivity extends BaseActivity implements View.OnClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_first);

    }

    @Override
    protected void onStart() {
        super.onStart();
        initUI();
    }

    private void initUI(){
        findViewById(R.id.btn_choose_function).setOnClickListener(this);
        findViewById(R.id.btn_choose_VCU).setOnClickListener(this);
        findViewById(R.id.btn_choose_entertainment).setOnClickListener(this);
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
        }
    }
}
