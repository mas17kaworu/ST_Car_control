package com.longkai.stcarcontrol.st_exp.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDNFCList.CMDNFCReturn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;
import com.longkai.stcarcontrol.st_exp.customView.dashboard.DashboardView;
import com.longkai.stcarcontrol.st_exp.customView.dialog.NFCDialog;
import com.longkai.stcarcontrol.st_exp.mockMessage.MockMessageServiceImpl;
import java.lang.ref.WeakReference;

public class NFCFragment extends Fragment {

  public TextView tvKeyInfo, tvFilterInfo, tvDoorInfo;

  public ImageView ivKey, ivFilter, ivDoor;

  private CMDNFCReturn command = new CMDNFCReturn();

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View mView = inflater.inflate(R.layout.fragment_nfc, container, false);
    tvKeyInfo = (TextView) mView.findViewById(R.id.tv_nfc_key_info);
    tvFilterInfo = (TextView) mView.findViewById(R.id.tv_nfc_filter_info);
    tvDoorInfo = (TextView) mView.findViewById(R.id.tv_nfc_door_info);

    ivKey = (ImageView) mView.findViewById(R.id.iv_nfc_key_status);
    ivFilter = (ImageView) mView.findViewById(R.id.iv_nfc_filter_status);
    ivDoor = (ImageView) mView.findViewById(R.id.iv_nfc_door_status);

    ServiceManager.getInstance().registerRegularlyCommand(command, new NFCCMDListener(this));

    //test
    MockMessageServiceImpl.getService().StartService(NFCFragment.class.toString());

    return mView;
  }

  @Override public void onStart() {
    super.onStart();
  }

  public void setTvKeyInfo(String text) {
    tvKeyInfo.setText(text);
  }

  public void setIvKey(int resID) {
    ivKey.setImageResource(resID);
  }

  public void setTvFilterInfo(String text) {
    tvFilterInfo.setText(text);
  }

  public void setIvFilter(int resID) {
    ivFilter.setImageResource(resID);
  }

  public void setTvDoorInfo(String text) {
    tvDoorInfo.setText(text);
  }

  public void setIvDoor(int resID) {
    ivDoor.setImageResource(resID);
  }

  NFCDialog nfcDialog;

  public void showDialog(final CMDNFCReturn.Response response){
    nfcDialog = new NFCDialog(this.getContext(), response);
    nfcDialog.show();
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override public void run() {
        nfcDialog.dismiss();
      }
    }, 3000);
  }

  private static class NFCCMDListener extends CommandListenerAdapter<CMDNFCReturn.Response> {
    WeakReference<NFCFragment> fragment;

    public NFCCMDListener(NFCFragment fragment) {
      this.fragment = new WeakReference<>(fragment);
    }

    private CMDNFCReturn.Response lastResponse;

    @Override public void onSuccess(final CMDNFCReturn.Response response) {
      if (fragment.get() != null) {
        fragment.get().getActivity().runOnUiThread(new Runnable() {
          @Override public void run() {
            //compare with
            if (lastResponse == null || !response.equals(lastResponse)){
              fragment.get().showDialog(response);
            }
            lastResponse = response;

            switch (response.key_info) {
              case 0:
                fragment.get()
                    .setTvKeyInfo(
                        fragment.get().getContext().getString(R.string.nfc_key_not_available));
                fragment.get()
                    .setIvKey(R.mipmap.ic_nfc_key_not_available);
                break;
              case 1:
                fragment.get()
                    .setTvKeyInfo(fragment.get().getContext().getString(R.string.nfc_valid_key));
                fragment.get()
                    .setIvKey(R.mipmap.ic_nfc_key_available);
                break;
              case 2:
                fragment.get()
                    .setTvKeyInfo(fragment.get().getContext().getString(R.string.nfc_invalid_key));
                fragment.get()
                    .setIvKey(R.mipmap.ic_nfc_key_invalid);
                break;
            }
            //filter
            switch (response.filter_info) {
              case 0:
                fragment.get()
                    .setTvFilterInfo(
                        fragment.get().getContext().getString(R.string.filter_not_available));
                fragment.get()
                    .setIvFilter(R.mipmap.ic_nfc_filter_invalid);
                break;
              case 1:
                fragment.get()
                    .setTvFilterInfo(fragment.get().getContext().getString(R.string.valid_filter));
                fragment.get()
                    .setIvFilter(R.mipmap.ic_nfc_filter_available);
                break;
            }
            //door
            switch (response.door_info) {
              case 1:
                fragment.get()
                    .tvDoorInfo.setVisibility(View.VISIBLE);
                fragment.get()
                    .ivDoor.setVisibility(View.VISIBLE);
                fragment.get()
                    .setTvDoorInfo(
                        fragment.get().getContext().getString(R.string.nfc_lock_door));
                fragment.get()
                    .setIvDoor(R.mipmap.ic_nfc_door_lock);
                break;
              case 2:
                fragment.get()
                    .tvDoorInfo.setVisibility(View.VISIBLE);
                fragment.get()
                    .ivDoor.setVisibility(View.VISIBLE);
                fragment.get()
                    .setTvDoorInfo(fragment.get().getContext()
                        .getString(R.string.nfc_unlock_door));
                fragment.get()
                    .setIvDoor(R.mipmap.ic_nfc_door_unlock);
                break;
              default:
                /*fragment.get()
                    .tvDoorInfo.setVisibility(View.INVISIBLE);
                fragment.get()
                    .ivDoor.setVisibility(View.INVISIBLE);*/
                break;
            }

          }
        });
      }
    }
  }

  @Override public void onDestroy() {
    ServiceManager.getInstance().unregisterRegularlyCommand(command);
    super.onDestroy();
  }


}
