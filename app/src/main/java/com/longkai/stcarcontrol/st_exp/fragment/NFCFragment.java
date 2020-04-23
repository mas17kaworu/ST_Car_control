package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDNFCList.CMDNFCReturn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;
import java.lang.ref.WeakReference;

public class NFCFragment extends Fragment {

  private TextView tvKeyInfo, tvFilterInfo;

  private CMDNFCReturn command = new CMDNFCReturn();

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View mView = inflater.inflate(R.layout.fragment_nfc, container, false);
    tvKeyInfo = (TextView) mView.findViewById(R.id.tv_nfc_key_info);
    tvFilterInfo = (TextView) mView.findViewById(R.id.tv_nfc_filter_info);

    ServiceManager.getInstance().registerRegularlyCommand(command, new NFCCMDListener(this));
    return mView;
  }

  public void setTvKeyInfo(String text) {
    tvKeyInfo.setText(text);
  }

  public void setTvFilterInfo(String text) {
    tvFilterInfo.setText(text);
  }

  private static class NFCCMDListener extends CommandListenerAdapter<CMDNFCReturn.Response> {
    WeakReference<NFCFragment> fragment;

    public NFCCMDListener(NFCFragment fragment) {
      this.fragment = new WeakReference<>(fragment);
    }

    @Override public void onSuccess(final CMDNFCReturn.Response response) {
      if (fragment.get() != null) {
        fragment.get().getActivity().runOnUiThread(new Runnable() {
          @Override public void run() {
            //todo update UI and text
            switch (response.key_info) {
              case 0:
                fragment.get()
                    .setTvKeyInfo(
                        fragment.get().getContext().getString(R.string.nfc_key_not_available));
                break;
              case 1:
                fragment.get()
                    .setTvKeyInfo(fragment.get().getContext().getString(R.string.nfc_valid_key));
                break;
              case 2:
                fragment.get()
                    .setTvKeyInfo(fragment.get().getContext().getString(R.string.nfc_invalid_key));
                break;
            }
            switch (response.filter_info) {
              case 0:
                fragment.get()
                    .setTvFilterInfo(
                        fragment.get().getContext().getString(R.string.filter_not_available));
                break;
              case 1:
                fragment.get()
                    .setTvFilterInfo(fragment.get().getContext().getString(R.string.valid_filter));
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
