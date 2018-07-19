package com.longkai.stcarcontrol.st_exp.fragment;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.longkai.stcarcontrol.st_exp.Enum.TboxStateEnum;
import com.longkai.stcarcontrol.st_exp.Interface.TboxStateChange;
import com.longkai.stcarcontrol.st_exp.R;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2018/5/20.
 */

public class VCUTboxFragment extends Fragment implements View.OnClickListener, TboxStateChange {
    private View mView;
    private TextView tvZhengche, tvDianji;

    private GifImageView gif_view_send;
    private TboxStateChange tboxStateChange;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_vcu_tbox, container, false);
        tvZhengche = (TextView) mView.findViewById(R.id.tv_vcu_tbox_zhengche);
        tvDianji = (TextView) mView.findViewById(R.id.tv_vcu_tbox_dianji);
        gif_view_send = (GifImageView) mView.findViewById(R.id.gifv_tbox);

        chooseTV(tvZhengche);
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        tboxStateChange = this;
    }

    @Override
    public void onClick(View v) {

    }

    private void chooseTV(TextView tv){
        tv.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG);
        tv.getPaint().setFakeBoldText(true);
        tv.setTextColor(0xff5cacee);
        tv.postInvalidate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gif_view_send.setImageDrawable(null);
    }

    @Override
    public void changeTo(TboxStateEnum state) {
        switch (state){
            case DateTime:
            case DataCollect:
                break;
            case DataStore:
                loadGifToMainView(R.mipmap.gif_tbox_data_store);
                break;
            case DataTransport:
                loadGifToMainView(R.mipmap.gif_tbox_data_transport);
                break;
            case DataResend:
                loadGifToMainView(R.mipmap.gif_tbox_resend);
                break;
            case Register:

                break;
            case Individual:
                loadGifToMainView(R.mipmap.gif_tbox_individual);
                break;
            case RemoteControl:
                loadGifToMainView(R.mipmap.gif_tbox_data_remote);
                break;
            case MailAndPhone:
                loadGifToMainView(R.mipmap.gif_tbox_mail_phone);
                break;
        }
    }

    private void loadGifToMainView(int resID){
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), resID);
            gif_view_send.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
