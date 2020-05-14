package com.longkai.stcarcontrol.st_exp.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOBCDemoList.CMDOBCReturn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUGUI7List.CMDVCUGUI7;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;
import com.longkai.stcarcontrol.st_exp.customView.dashboard.OBCDemoDashboard;
import com.longkai.stcarcontrol.st_exp.mockMessage.MockMessageServiceImpl;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2018/10/29.
 */

public class VCUOBCDemoFragment extends Fragment implements View.OnClickListener {
  private View mView;

  private ImageView ivSwitch;
  private TextView tvTimeCounting;
  private GifImageView gifVCharging;
  private AtomicBoolean charging = new AtomicBoolean(false);

  private OBCDemoCMDListener commandListener;

  private static long ZERO = -28800000L;
  private long time = ZERO;

  public OBCDemoDashboard dashboardVbat, dashboardVac, dashboardVbus, dashboardIbat;
  public TextView tvPFCState, tvLLCState;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mView = inflater.inflate(R.layout.fragment_vcu_obc_demo, container, false);

    ivSwitch = (ImageView) mView.findViewById(R.id.iv_obc_demo_switch);
    ivSwitch.setOnClickListener(this);

    tvTimeCounting = (TextView) mView.findViewById(R.id.tv_obc_demo_time);

    gifVCharging = (GifImageView) mView.findViewById(R.id.gifv_vcu_obc_demo_charging);

    tvPFCState = (TextView) mView.findViewById(R.id.tv_obc_demo_pfc);
    tvLLCState = (TextView) mView.findViewById(R.id.tv_obc_demo_llc);

    dashboardVbat = (OBCDemoDashboard) mView.findViewById(R.id.dashboard_obc_demo_vbat);
    dashboardVac = (OBCDemoDashboard) mView.findViewById(R.id.dashboard_obc_demo_vbac);
    dashboardVbus = (OBCDemoDashboard) mView.findViewById(R.id.dashboard_obc_demo_vbus);
    dashboardIbat = (OBCDemoDashboard) mView.findViewById(R.id.dashboard_obc_demo_Ibat);

    //todo register obc demo return

    //handler.postDelayed(runnable, 1000);// 打开定时器，500ms后执行runnable
    commandListener = new OBCDemoCMDListener(this);

    dashboardIbat.setValue(0);
    dashboardVac.setValue(0);
    dashboardVbus.setValue(0);
    dashboardVbat.setValue(0);

    ServiceManager.getInstance().registerRegularlyCommand(cmdobcReturn, commandListener);

    //test
    MockMessageServiceImpl.getService().StartService(VCUOBCDemoFragment.class.toString());

    return mView;
  }
  SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");

  Handler handler = new Handler();
  Runnable runnable = new Runnable() {
    @Override
    public void run() {
      if (charging.get()) {
        time += 1000;
        String date = df.format(time);
        tvTimeCounting.setText(date);
        handler.postDelayed(runnable, 1000);
      }
    }
  };

  private CMDOBCReturn cmdobcReturn = new CMDOBCReturn();
  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.iv_obc_demo_switch:
        if (charging.get()){ //关闭
          charging.set(false);
          ivSwitch.setImageResource(R.mipmap.ic_obc_demo_switch_on);
          gifVCharging.setVisibility(View.INVISIBLE);
          CMDVCUGUI7.instance.OBCDemoOff();
          ServiceManager.getInstance().sendCommandToCar(CMDVCUGUI7.instance,new CommandListenerAdapter());


        } else { //开启
          time =  ZERO;
          String date = df.format(time);
          tvTimeCounting.setText(date);
          charging.set(true);
          handler.removeCallbacks(runnable);
          handler.postDelayed(runnable, 1000);
          ivSwitch.setImageResource(R.mipmap.ic_obc_demo_switch_off);
          showChargingGif();
          CMDVCUGUI7.instance.OBCDemoOn();
          ServiceManager.getInstance().sendCommandToCar(CMDVCUGUI7.instance,new CommandListenerAdapter());

        }
        break;
    }
  }

  @Override
  public void onDestroy() {
    ServiceManager.getInstance().unregisterRegularlyCommand(cmdobcReturn);
    super.onDestroy();
    handler.removeCallbacks(runnable);
  }

  private void showChargingGif(){
    try {
      GifDrawable gifDrawable = new GifDrawable(getResources(), R.mipmap.gif_obc_demo_charging);
      gifVCharging.setVisibility(View.VISIBLE);
      gifVCharging.setImageDrawable(gifDrawable);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }



  private static class OBCDemoCMDListener extends CommandListenerAdapter<CMDOBCReturn.Response>{

    private WeakReference<VCUOBCDemoFragment> reference;
    OBCDemoCMDListener(VCUOBCDemoFragment fragment){
      reference = new WeakReference<>(fragment);
    }

    @Override public void onSuccess(final CMDOBCReturn.Response response) {
      if (reference.get() != null) {
        final VCUOBCDemoFragment fragment = reference.get();
        fragment.getActivity().runOnUiThread(new Runnable() {
          @Override public void run() {
            fragment.dashboardVbat.setValue(response.Vbat);
            fragment.dashboardVbus.setValue(response.Vbus);
            fragment.dashboardVac.setValue(response.Vac);
            fragment.dashboardIbat.setValue(response.Ibat);

            Resources res = fragment.getActivity().getResources();
            String[] pfcStates=res.getStringArray(R.array.pfc_state);
            switch (response.PFCState){
              case 0:
              fragment.tvPFCState.setText(pfcStates[0]);
              break;
              case 1:
                fragment.tvPFCState.setText(pfcStates[1]);
                break;
              case 2:
                fragment.tvPFCState.setText(pfcStates[2]);
                break;
              case 3:
                fragment.tvPFCState.setText(pfcStates[3]);
                break;
              case 4:
                fragment.tvPFCState.setText(pfcStates[4]);
                break;
              case 5:
                fragment.tvPFCState.setText(pfcStates[5]);
                break;
              case 6:
                fragment.tvPFCState.setText(pfcStates[6]);
                break;
              case 7:
                fragment.tvPFCState.setText(pfcStates[7]);
                break;
            }

            String[] llcStates=res.getStringArray(R.array.llc_state);
            switch (response.LLCState){
              case 0x00:
                fragment.tvLLCState.setText(llcStates[0]);
                break;
              case 0x05:
                fragment.tvLLCState.setText(llcStates[1]);
                break;
              case 0x0a:
                fragment.tvLLCState.setText(llcStates[2]);
                break;
              case 0x15:
                fragment.tvLLCState.setText(llcStates[3]);
                break;
              case 0x1A:
                fragment.tvLLCState.setText(llcStates[4]);
                break;
              case 0x25:
                fragment.tvLLCState.setText(llcStates[5]);
                break;
              case 0x35:
                fragment.tvLLCState.setText(llcStates[6]);
                break;
              case 0x3A:
                fragment.tvLLCState.setText(llcStates[7]);
                break;
              case 0xEE:
                fragment.tvLLCState.setText(llcStates[8]);
                break;

            }
          }
        });
      }
    }
  }


}
