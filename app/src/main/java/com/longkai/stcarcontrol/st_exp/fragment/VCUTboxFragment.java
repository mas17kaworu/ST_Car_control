package com.longkai.stcarcontrol.st_exp.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.longkai.stcarcontrol.st_exp.Enum.TboxStateEnum;
import com.longkai.stcarcontrol.st_exp.Interface.StateChange;
import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.activity.VCUActivity;
import com.longkai.stcarcontrol.st_exp.communication.Command;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDECallList.CMDECall;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_UPDATE_FIRMWARE;

/**
 * Created by Administrator on 2018/5/20.
 */

public class VCUTboxFragment extends Fragment implements View.OnClickListener, StateChange<TboxStateEnum> {
    private View mView;
    private TextView tvZhengche, tvDianji, tvJizhi, tvGuzhang, tvDateTime;

    private RelativeLayout rlPhone, rlRemoteBreak;

    private GifImageView gif_view_send;
    private StateChange tboxStateChange;

    private HashSet<TextView> sheetTextViews = new HashSet<>();
    private View dataSheetLayout;

    private ScheduledExecutorService threadPool = Executors.newSingleThreadScheduledExecutor();
    SimpleDateFormat simpleDateFormat;

    private EditText phoneNumberEditor;
    private String phoneNumber="";

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

        tvDateTime = (TextView) mView.findViewById(R.id.tv_tbox_date_time);

        simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss");// HH:mm:ss



        rlPhone = (RelativeLayout) mView.findViewById(R.id.rl_tbox_phone_message);
        rlRemoteBreak = (RelativeLayout) mView.findViewById(R.id.rl_tbox_remote_control_break);

        gif_view_send = (GifImageView) mView.findViewById(R.id.gifv_tbox);
        dataSheetLayout = mView.findViewById(R.id.layout_tbox_sheet);
        sheetTextViews.addAll(Arrays.asList(tvDianji, tvZhengche, tvJizhi, tvGuzhang));

        mView.findViewById(R.id.iv_tbox_phone_setting).setOnClickListener(this);
        mView.findViewById(R.id.iv_tbox_phone_call).setOnClickListener(this);

        phoneNumberEditor = ((EditText)mView.findViewById(R.id.et_tbox_phone_number));

        if (threadPool.isShutdown()){
            threadPool = Executors.newSingleThreadScheduledExecutor();
        }
        threadPool.scheduleAtFixedRate(updateTimeThread, 0, 1000, TimeUnit.MILLISECONDS);
        chooseTV(tvZhengche);
        changeTo(TboxStateEnum.DateTime);
        return mView;
    }

    Date date = new Date(System.currentTimeMillis());
    Thread updateTimeThread = new Thread(new Runnable() {
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    date = new Date(System.currentTimeMillis());
                    tvDateTime.setText(simpleDateFormat.format(date));
                }
            });

        }
    });


    @Override
    public void onResume() {
        super.onResume();
        tboxStateChange = this;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        threadPool.shutdown();
    }

    @Override
    public void onPause() {
        super.onPause();

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
            case R.id.iv_tbox_phone_setting:
                if (checkPhoneNumberFormat()) {
                    Command cmd = new CMDECall(phoneNumberEditor.getText().toString(), CMDECall.Type.set);
                    ServiceManager.getInstance().sendCommandToCar(cmd, new CommandListenerAdapter());
                }
                break;
            case R.id.iv_tbox_phone_call:
                if (checkPhoneNumberFormat()) {
                    Command cmd = new CMDECall(phoneNumberEditor.getText().toString(), CMDECall.Type.call);
                    ServiceManager.getInstance().sendCommandToCar(cmd, new CommandListenerAdapter());
                }
                break;
        }
    }

    private boolean checkPhoneNumberFormat(){
        String numberStr = phoneNumberEditor.getText().toString();
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        if (numberStr.length() == 11 && pattern.matcher(numberStr).matches()){
            return true;
        } else {
            Toast.makeText(getActivity(), R.string.warning_phone_number_format_error, Toast.LENGTH_LONG).show();
            return false;
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
        //clean
        cleanWindow();
        switch (state){
            case DateTime:
            case DataCollect:
                dataSheetLayout.setVisibility(View.VISIBLE);
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
                loadGifToMainView(R.mipmap.gif_tbox_data_transport);
                break;
            case Individual:
                loadGifToMainView(R.mipmap.gif_tbox_individual);
                break;
            case RemoteControl:
                loadGifToMainView(R.mipmap.gif_tbox_data_remote);
                rlRemoteBreak.setVisibility(View.VISIBLE);
                break;
            case MailAndPhone:
                loadGifToMainView(R.mipmap.gif_tbox_mail_phone);
                rlPhone.setVisibility(View.VISIBLE);
                break;
            case UpdateFirmware:

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

    private void cleanWindow(){
        rlPhone.setVisibility(View.INVISIBLE);
        dataSheetLayout.setVisibility(View.INVISIBLE);
        rlRemoteBreak.setVisibility(View.INVISIBLE);
        releaseGifView();
    }
}
