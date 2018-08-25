package com.longkai.stcarcontrol.st_exp.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.longkai.stcarcontrol.st_exp.Enum.TboxStateEnum;
import com.longkai.stcarcontrol.st_exp.Interface.StateChange;
import com.longkai.stcarcontrol.st_exp.R;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2018/5/20.
 */

public class VCUTboxFragment extends Fragment implements View.OnClickListener, StateChange<TboxStateEnum> {
    private View mView;
    private TextView tvZhengche, tvDianji, tvJizhi, tvGuzhang;

    private GifImageView gif_view_send;
    private StateChange tboxStateChange;

    private HashSet<TextView> sheetTextViews = new HashSet<>();
    private View dataSheetLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_vcu_tbox, container, false);
        tvZhengche = (TextView) mView.findViewById(R.id.tv_vcu_tbox_zhengche);
        tvZhengche.setOnClickListener(this);
        tvDianji = (TextView) mView.findViewById(R.id.tv_vcu_tbox_dianji);
        tvDianji.setOnClickListener(this);
        tvJizhi = (TextView) mView.findViewById(R.id.tv_vcu_tbox_jizhi);
        tvJizhi.setOnClickListener(this);
        tvGuzhang = (TextView) mView.findViewById(R.id.tv_vcu_tbox_guzhangliebiao);
        tvGuzhang.setOnClickListener(this);
        gif_view_send = (GifImageView) mView.findViewById(R.id.gifv_tbox);
        dataSheetLayout = mView.findViewById(R.id.layout_tbox_sheet);
        sheetTextViews.addAll(Arrays.asList(tvDianji, tvZhengche, tvJizhi, tvGuzhang));
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
        switch (v.getId()){
            case R.id.tv_vcu_tbox_zhengche:
            case R.id.tv_vcu_tbox_dianji:
            case R.id.tv_vcu_tbox_jizhi:
            case R.id.tv_vcu_tbox_guzhangliebiao:
                chooseTV((TextView) v);
                break;
        }
    }

    private void chooseTV(TextView tv){
        for (TextView textView : sheetTextViews){
            if (textView.hashCode() == tv.hashCode()){
                textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                textView.getPaint().setFakeBoldText(true);
                textView.setTextColor(0xff5cacee);
                textView.postInvalidate();
            } else {
                textView.getPaint().setFlags(0);
                textView.getPaint().setFakeBoldText(false);
                textView.setTextColor(0xffffffff);
                textView.postInvalidate();
            }
        }

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
                dataSheetLayout.setVisibility(View.VISIBLE);
                releaseGifView();
                break;
            case DataStore:
                loadGifToMainView(R.mipmap.gif_tbox_data_store);
                dataSheetLayout.setVisibility(View.INVISIBLE);
                break;
            case DataTransport:
                loadGifToMainView(R.mipmap.gif_tbox_data_transport);
                dataSheetLayout.setVisibility(View.INVISIBLE);
                break;
            case DataResend:
                loadGifToMainView(R.mipmap.gif_tbox_resend);
                dataSheetLayout.setVisibility(View.INVISIBLE);
                break;
            case Register:

                break;
            case Individual:
                loadGifToMainView(R.mipmap.gif_tbox_individual);
                dataSheetLayout.setVisibility(View.INVISIBLE);
                break;
            case RemoteControl:
                loadGifToMainView(R.mipmap.gif_tbox_data_remote);
                dataSheetLayout.setVisibility(View.INVISIBLE);
                break;
            case MailAndPhone:
                loadGifToMainView(R.mipmap.gif_tbox_mail_phone);
                dataSheetLayout.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public StateChange getController(){
        return tboxStateChange;
    }

    private void loadGifToMainView(int resID){
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), resID);
            gif_view_send.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void releaseGifView(){
        try {
            gif_view_send.setImageDrawable(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
