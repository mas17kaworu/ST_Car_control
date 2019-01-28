package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.activity.VCUActivity;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDFOTAList.CMDFOTADATA;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDFOTAList.CMDFOTADIAG;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;
import com.longkai.stcarcontrol.st_exp.mockMessage.MockMessageServiceImpl;
import com.longkai.stcarcontrol.st_exp.service.FirmwareUpdateHelper;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_TBOX;

/**
 * Created by Administrator on 2019/1/10.
 */

public class VCUUpdateFirmwareFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "VCUUpdateFirmware";

    private View mView;

    private ImageView ivUpdateAIcon, ivUpdateBIcon;
    private ImageView ivUpdateAControl, ivUpdateBControl;
    private ImageView ivBackBtn;

    private TextView tvStatusLevel1, tvStatusLevel2;

    private GifImageView gifLoadingView;

    private AtomicBoolean AinUpdating = new AtomicBoolean(false);
    private AtomicBoolean BinUpdating = new AtomicBoolean(false);

    private static final int TIMEOUT = 10000;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_vcu_update_firmware, container, false);

        ivUpdateAIcon = (ImageView) mView.findViewById(R.id.iv_vcu_update_a_icon);
        ivUpdateAIcon.setOnClickListener(this);
        ivUpdateBIcon = (ImageView) mView.findViewById(R.id.iv_vcu_update_b_icon);
        ivUpdateBIcon.setOnClickListener(this);

        ivUpdateAControl = (ImageView) mView.findViewById(R.id.iv_vcu_update_a_control_btn);
        ivUpdateAControl.setOnClickListener(this);
        ivUpdateBControl = (ImageView) mView.findViewById(R.id.iv_vcu_update_b_control_btn);
        ivUpdateBControl.setOnClickListener(this);


        ivBackBtn = (ImageView) mView.findViewById(R.id.iv_back_to_tbox);
        ivBackBtn.setOnClickListener(this);

        tvStatusLevel1 = (TextView) mView.findViewById(R.id.tv_vcu_update_status_level_1);
        tvStatusLevel2 = (TextView) mView.findViewById(R.id.tv_vcu_update_status_level_2);

        gifLoadingView = (GifImageView) mView.findViewById(R.id.gifv_vcu_upgrade_bar);

        //test
        MockMessageServiceImpl.getService().StartService(VCUUpdateFirmwareFragment.class.toString());

        return mView;
    }

    CommandListenerAdapter<CMDFOTADIAG.Response> diagHandler = new CommandListenerAdapter<CMDFOTADIAG.Response>(TIMEOUT){
        @Override
        public void onSuccess(CMDFOTADIAG.Response response) {
            super.onSuccess(response);
            final CMDFOTADIAG.Response rs = response;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (rs.DIAG_CMD_RESPONSE){
                        case 0x01://允许更新A区
                            ivUpdateAControl.setImageResource(R.mipmap.ic_vcu_update_cancel);
                            AinUpdating.set(true);
                            inUpgrading();
                            ivUpdateBIcon.setImageResource(R.mipmap.ic_vcu_update_b_forbidden);
                            tvStatusLevel1.setText(R.string.updating_A);
                            tvStatusLevel2.setVisibility(View.VISIBLE);
                            tvStatusLevel2.setText(R.string.uploading);
                            showUploadingProgress();
                            //start send data A
                            sendFirmwareData(UpdateSection.A);
                            break;
                        case 0x02://允许更新B区
                            ivUpdateBControl.setImageResource(R.mipmap.ic_vcu_update_cancel);
                            BinUpdating.set(true);
                            inUpgrading();
                            ivUpdateAIcon.setImageResource(R.mipmap.ic_vcu_update_a_forbidden);
                            tvStatusLevel1.setText(R.string.updating_B);
                            tvStatusLevel2.setVisibility(View.VISIBLE);
                            tvStatusLevel2.setText(R.string.uploading);
                            showUploadingProgress();
                            //start send data b
                            sendFirmwareData(UpdateSection.B);
                            break;
                        case 0x00://当前软件在该区运行
                            tvStatusLevel1.setText(R.string.running_on_selected_section);
                            tvStatusLevel2.setText("");
                            break;
                        case 0x03://拒绝
                            tvStatusLevel1.setText(R.string.update_refuse);
                            tvStatusLevel2.setText("");
                            break;
                    }
                }
            });


        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_vcu_update_a_icon:
                if (BinUpdating.get() || AinUpdating.get()){
                    return;
                }

                ivUpdateAIcon.setImageResource(R.mipmap.ic_vcu_update_a_selected);
                ivUpdateBIcon.setImageResource(R.mipmap.ic_vcu_update_b_normal);
                ivUpdateAControl.setVisibility(View.VISIBLE);
                ivUpdateAControl.setImageResource(R.mipmap.ic_vcu_update_start_update);
                ivUpdateBControl.setVisibility(View.INVISIBLE);
                break;
            case R.id.iv_vcu_update_b_icon:
                if (AinUpdating.get() || BinUpdating.get()){
                    return;
                }

                ivUpdateBIcon.setImageResource(R.mipmap.ic_vcu_update_b_selected);
                ivUpdateAIcon.setImageResource(R.mipmap.ic_vcu_update_a_normal);
                ivUpdateBControl.setVisibility(View.VISIBLE);
                ivUpdateBControl.setImageResource(R.mipmap.ic_vcu_update_start_update);
                ivUpdateAControl.setVisibility(View.INVISIBLE);
                break;

            case R.id.iv_vcu_update_a_control_btn:
                if (AinUpdating.get()) {
                    ServiceManager.getInstance().sendCommandToCar(new CMDFOTADIAG(0x03), new CommandListenerAdapter());
                    resetStatus();
                } else {
                    ServiceManager.getInstance().sendCommandToCar(new CMDFOTADIAG(0x01), diagHandler);
                }
                break;
            case R.id.iv_vcu_update_b_control_btn:
                if (BinUpdating.get()){
                    ServiceManager.getInstance().sendCommandToCar(new CMDFOTADIAG(0x03), new CommandListenerAdapter());
                    resetStatus();
                } else {
                    ServiceManager.getInstance().sendCommandToCar(new CMDFOTADIAG(0x02), diagHandler);
                }
                break;
            case R.id.iv_back_to_tbox:
                ((VCUActivity)getActivity()).setSelect(FRAGMENT_TRANSACTION_TBOX);
                break;
        }
    }

    private void sendFirmwareData(UpdateSection section){
        File file = null;
        if (section == UpdateSection.A){
            file = new File(FirmwareUpdateHelper.FIRMWARE_A_PATH);
        } else if (section == UpdateSection.B) {
            file = new File(FirmwareUpdateHelper.FIRMWARE_B_PATH);
        }
        FirmwareUpdateHelper.generateBytesList(file);
        uploaderImpl = new Uploader();
        mainThread.post(uploaderImpl);
    }

    enum UpdateSection{
        A,
        B
    }

    private void resetStatus(){
        AinUpdating.set(false);
        BinUpdating.set(false);

        ivUpdateAIcon.setImageResource(R.mipmap.ic_vcu_update_a_normal);
        ivUpdateBIcon.setImageResource(R.mipmap.ic_vcu_update_b_normal);
        ivUpdateAControl.setVisibility(View.INVISIBLE);
        ivUpdateBControl.setVisibility(View.INVISIBLE);
        tvStatusLevel1.setText(R.string.update_idle);
        tvStatusLevel2.setText("");

        gifLoadingView.setVisibility(View.INVISIBLE);
        ivBackBtn.setClickable(true);
        ((VCUActivity)getActivity()).enableSwitchFragment();
    }

    private void inUpgrading(){
        ivBackBtn.setClickable(false);
        ((VCUActivity)getActivity()).disableSwitchFragment();
    }

    private void showUploadingProgress(){
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.mipmap.gif_vcu_update_uploading);
            gifLoadingView.setVisibility(View.VISIBLE);
            gifLoadingView.setImageDrawable(gifDrawable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler mainThread = new Handler();
    AtomicInteger packagerIndex = new AtomicInteger(0);
    Uploader uploaderImpl;

    class Uploader implements Runnable {
        public Uploader(){
            packagerIndex.set(0);
        }

        @Override
        public void run() {
            if (FirmwareUpdateHelper.getBytes().size()>0 && (AinUpdating.get() || BinUpdating.get())) {
                Log.i(TAG, "Prepare sending data bytes index = " + packagerIndex.get());
                byte[] bytes = FirmwareUpdateHelper.getBytesAtNumber(packagerIndex.get());
                int isLast = 0;
                if (packagerIndex.get() >= FirmwareUpdateHelper.getBytes().size() - 1) {
                    isLast = 1;
                }

                ServiceManager.getInstance().sendCommandToCar(
                        new CMDFOTADATA(packagerIndex.get(), bytes.length, isLast, bytes),
                        new CommandListenerAdapter<CMDFOTADATA.Response>(TIMEOUT) {
                            @Override
                            public void onSuccess(CMDFOTADATA.Response response) {
                                super.onSuccess(response);
                                final CMDFOTADATA.Response rp = response;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.i(TAG, "DATA response PKG_Aborted:" + rp.PKG_Aborted);
                                        Log.i(TAG, "DATA response PKG_Received:" + rp.PKG_Received);
                                        Log.i(TAG, "DATA response isLastPackage:" + rp.isLastPackage);

                                        if (rp.PKG_Aborted == 1) { //abort
                                            resetStatus();
                                            tvStatusLevel1.setText(R.string.upload_failed);
                                            return;
                                        }

                                        if (rp.PKG_Received == 1) { //package trans success
                                            if (rp.isLastPackage){//全部发送完成 开始接收诊断信号
                                                tvStatusLevel2.setText(R.string.upload_finish);
                                                //Register listener
                                                registerDIAGListener();

                                            }else {
                                                packagerIndex.set(packagerIndex.get() + 1);
                                                mainThread.post(uploaderImpl);
                                            }
                                        } else if (rp.PKG_Received == 0) { //packager trans failed
                                            packagerIndex.set(rp.PKG_NUM);
                                            mainThread.post(uploaderImpl);
                                        }

                                    }
                                });


                            }

                            @Override
                            public void onTimeout() {
                                super.onTimeout();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        resetStatus();
                                        tvStatusLevel1.setText(R.string.upload_failed);
                                    }
                                });


                            }
                        });
            }
        }
    }

    private CommandListenerAdapter<CMDFOTADIAG.Response> DIAGFeedBack = new CommandListenerAdapter<CMDFOTADIAG.Response>(TIMEOUT){
        @Override
        public void onSuccess(final CMDFOTADIAG.Response response) {
            super.onSuccess(response);
            final CMDFOTADIAG.Response rs = response;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (rs.DIAG_CMD_RESPONSE == CMDFOTADIAG.Response.FLASHING) {
                        tvStatusLevel2.setText(R.string.update_flashing_firmware);
                        registerDIAGListener();
                    } else if (rs.DIAG_CMD_RESPONSE == CMDFOTADIAG.Response.FLASH_ABORT) {
                        resetStatus();
                        tvStatusLevel1.setText(R.string.update_flash_failed);
                    } else if (rs.DIAG_CMD_RESPONSE == CMDFOTADIAG.Response.FLASH_FINISHED) {
                        resetStatus();
                        tvStatusLevel1.setText(R.string.update_flashing_finished);
                    }
                }
            });
        }

        @Override
        public void onTimeout() {
            super.onTimeout();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    resetStatus();
                    tvStatusLevel1.setText(R.string.update_flash_failed);
                }
            });
        }
    };

    private void registerDIAGListener() {
        ServiceManager.getInstance().registerCommandOnce(
                new CMDFOTADIAG(0x00),
                DIAGFeedBack
        );
    }
}
