package com.longkai.stcarcontrol.st_exp.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.longkai.stcarcontrol.st_exp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/4.
 */

//// TODO: 2018/8/4 通信问题: VCU 和 Infotainment 可能需要公用一个连接 目前想法是VCU activity ondestroy 不去disconnect
public class InfotainmentActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivSoundA, ivSoundB, ivSoundC, ivSoundD;
    private List<ImageView> soundBtnList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_infotainment);

        ivSoundA = (ImageView) findViewById(R.id.iv_infot_engine_s_a);
        ivSoundA.setOnClickListener(this);
        ivSoundB = (ImageView) findViewById(R.id.iv_infot_engine_s_b);
        ivSoundB.setOnClickListener(this);
        ivSoundC = (ImageView) findViewById(R.id.iv_infot_engine_s_c);
        ivSoundC.setOnClickListener(this);
        ivSoundD = (ImageView) findViewById(R.id.iv_infot_engine_s_d);
        ivSoundD.setOnClickListener(this);
        soundBtnList.add(ivSoundA);
        soundBtnList.add(ivSoundB);
        soundBtnList.add(ivSoundC);
        soundBtnList.add(ivSoundD);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_infot_engine_s_a:
                clickSoundBtn((ImageView)v);
                break;
            case R.id.iv_infot_engine_s_b:
                clickSoundBtn((ImageView)v);
                break;
            case R.id.iv_infot_engine_s_c:
                clickSoundBtn((ImageView)v);
                break;
            case R.id.iv_infot_engine_s_d:
                clickSoundBtn((ImageView)v);
                break;
        }
    }

    private void clickSoundBtn(ImageView targetIV){
        for (ImageView  iv: soundBtnList) {
            if (iv == targetIV) {
                iv.setSelected(true);
            } else {
                iv.setSelected(false);
            }
        }
    }
}
