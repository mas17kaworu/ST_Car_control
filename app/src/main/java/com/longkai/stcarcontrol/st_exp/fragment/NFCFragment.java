package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.longkai.stcarcontrol.st_exp.R;

public class NFCFragment extends Fragment {

  private TextView tvKeyInfo, tvFilterInfo;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View mView = inflater.inflate(R.layout.fragment_nfc, container, false);
    tvKeyInfo = (TextView) mView.findViewById(R.id.tv_nfc_key_info);
    tvFilterInfo = (TextView) mView.findViewById(R.id.tv_nfc_filter_info);

    //todo register cmd listener

    return mView;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    //todo unregister Listener

  }
}
