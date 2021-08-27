package com.longkai.stcarcontrol.st_exp.customView.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.Utils.BitmapUtil;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDNFCList.CMDNFCReturn;

public class NFCDialog extends Dialog {

  Context context;

  private TextView tvKeyInfo, tvFilterInfo, tvDoorInfo;

  private ImageView ivKey, ivFilter, ivDoor;

  private static Integer keyCache, filterCache, doorCache;

  private CMDNFCReturn.Response response;

  public NFCDialog(@NonNull Context context, CMDNFCReturn.Response response) {
    super(context);
    this.context = context;
    this.response = response;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    View mView = View.inflate(context, R.layout.dialog_nfc, null);
    setContentView(mView);

    tvKeyInfo = (TextView) mView.findViewById(R.id.tv_nfc_dialog_key_info);
    tvFilterInfo = (TextView) mView.findViewById(R.id.tv_nfc_dialog_filter_info);
    tvDoorInfo = (TextView) mView.findViewById(R.id.tv_nfc_dialog_door_info);

    ivKey = (ImageView) mView.findViewById(R.id.iv_nfc_dialog_key_status);
    ivFilter = (ImageView) mView.findViewById(R.id.iv_nfc_dialog_filter_status);
    ivDoor = (ImageView) mView.findViewById(R.id.iv_nfc_dialog_door_status);

    Window win = getWindow();
    WindowManager.LayoutParams lp = win.getAttributes();
    lp.height = BitmapUtil.dp2px(context, 270);
    lp.width = BitmapUtil.dp2px(context, 500);
    win.setAttributes(lp);
  }

  @Override protected void onStart() {
    super.onStart();

    if (!response.key_info.equals(keyCache)) {
      keyCache = response.key_info;
      switch (response.key_info) {
        case 0:
          tvKeyInfo.setText(R.string.nfc_key_not_available);
          ivKey.setImageResource(R.mipmap.ic_nfc_key_not_available);
          break;
        case 1:
          tvKeyInfo.setText(R.string.nfc_valid_key);
          ivKey.setImageResource(R.mipmap.ic_nfc_key_available);
          break;
        case 2:
          tvKeyInfo.setText(R.string.nfc_invalid_key);
          ivKey.setImageResource(R.mipmap.ic_nfc_key_invalid);
          break;
      }
    } else {
      tvKeyInfo.setVisibility(View.GONE);
      ivKey.setVisibility(View.GONE);
    }
    //filter
    if (!response.filter_info.equals(filterCache)) {
      filterCache = response.filter_info;
      switch (response.filter_info) {
        case 0:
          tvFilterInfo.setText(R.string.filter_not_available);
          ivFilter.setImageResource(R.mipmap.ic_nfc_filter_invalid);
          break;
        case 1:
          tvFilterInfo.setText(R.string.valid_filter);
          ivFilter.setImageResource(R.mipmap.ic_nfc_filter_available);
          break;
      }
    } else {
      tvFilterInfo.setVisibility(View.GONE);
      ivFilter.setVisibility(View.GONE);
    }
    //door
    if (!response.door_info.equals(doorCache)) {
      doorCache = response.door_info;
      switch (response.door_info) {
        case 1:
          tvDoorInfo.setVisibility(View.VISIBLE);
          ivDoor.setVisibility(View.VISIBLE);
          tvDoorInfo.setText(R.string.nfc_lock_door);
          ivDoor.setImageResource(R.mipmap.ic_nfc_door_lock);
          break;
        case 2:
          tvDoorInfo.setVisibility(View.VISIBLE);
          ivDoor.setVisibility(View.VISIBLE);
          tvDoorInfo.setText(R.string.nfc_unlock_door);
          ivDoor.setImageResource(R.mipmap.ic_nfc_door_unlock);
          break;
        default:
          //tvDoorInfo.setVisibility(View.GONE);
          //ivDoor.setVisibility(View.GONE);
          break;
      }
    } else {
      tvDoorInfo.setVisibility(View.GONE);
      ivDoor.setVisibility(View.GONE);
    }
  }
}
