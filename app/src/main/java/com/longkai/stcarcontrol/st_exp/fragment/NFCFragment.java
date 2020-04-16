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

  private static class NFCCMDListener extends CommandListenerAdapter<CMDNFCReturn.Response> {
    WeakReference<Fragment> fragment;

    public NFCCMDListener(Fragment fragment){
      this.fragment = new WeakReference<>(fragment);
    }

    @Override public void onSuccess(CMDNFCReturn.Response response) {
      if (fragment.get() != null){
        fragment.get().getActivity().runOnUiThread(new Runnable() {
          @Override public void run() {
            //todo update UI and text


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
