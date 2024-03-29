package com.longkai.stcarcontrol.st_exp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.longkai.stcarcontrol.st_exp.BuildConfig;
import com.longkai.stcarcontrol.st_exp.ConstantData;
import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.Utils.FileUtils;
import com.longkai.stcarcontrol.st_exp.Utils.FileUtils10;
import com.longkai.stcarcontrol.st_exp.Utils.SharedPreferencesUtil;
import com.longkai.stcarcontrol.st_exp.adapter.HorizontalListViewAdapter;
import com.longkai.stcarcontrol.st_exp.communication.ConnectionListener;
import com.longkai.stcarcontrol.st_exp.communication.ConnectionType;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDGetVersion;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;
import com.longkai.stcarcontrol.st_exp.customView.HorizontalListView;
import com.longkai.stcarcontrol.st_exp.fragment.AVASFragment;
import com.longkai.stcarcontrol.st_exp.fragment.BCMDiagnosticFragment;
import com.longkai.stcarcontrol.st_exp.fragment.CarBackCoverFragment;
import com.longkai.stcarcontrol.st_exp.fragment.CarBackLampFragment;
import com.longkai.stcarcontrol.st_exp.fragment.CarBackOLED2Fragment;
import com.longkai.stcarcontrol.st_exp.fragment.CarBackOLEDFragment;
import com.longkai.stcarcontrol.st_exp.fragment.CenterControlFragment;
import com.longkai.stcarcontrol.st_exp.fragment.DoorFragment;
import com.longkai.stcarcontrol.st_exp.fragment.FrontBottomLight;
import com.longkai.stcarcontrol.st_exp.fragment.FrontHeadLamp;
import com.longkai.stcarcontrol.st_exp.fragment.FrontHeadLampTest2;
import com.longkai.stcarcontrol.st_exp.fragment.HighBeamLight;
import com.longkai.stcarcontrol.st_exp.fragment.HomeFragment;
import com.longkai.stcarcontrol.st_exp.fragment.KeyPairFragment;
import com.longkai.stcarcontrol.st_exp.fragment.NFCFragment;
import com.longkai.stcarcontrol.st_exp.fragment.SeatFragment;
import com.longkai.stcarcontrol.st_exp.fragment.SoundFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUUpdateFirmwareFragment;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_BMS_HOME;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_UPDATE_FIRMWARE;
import static com.longkai.stcarcontrol.st_exp.ConstantData.MainFragment.FRAGMENT_TRANSACTION_CAR_BACK_OLED;
import static com.longkai.stcarcontrol.st_exp.ConstantData.MainFragment.FRAGMENT_TRANSACTION_CAR_BACK_OLED2;
import static com.longkai.stcarcontrol.st_exp.ConstantData.MainFragment.FRAGMENT_TRANSACTION_CAR_FRONT_BOTTOM_LIGHT;

public class MainActivity extends BaseActivity implements View.OnClickListener {

  private int mLastflag = 10;

  private HomeFragment mHomeFragment;
  private FrontHeadLamp mFrontLampFragment;
  private SeatFragment mSeatFragment;
  private HighBeamLight mHighBeamLight;
  private FrontBottomLight mFrontBottomLight;
  private DoorFragment mDoorFragment;
  private CenterControlFragment mCenterControlFragment;
  private CarBackLampFragment mCarBackFragment;
  private CarBackCoverFragment mCarBackCoverFragment;
  private BCMDiagnosticFragment mBCMDiagnosticFragment;
  private FrontHeadLampTest2 frontHeadLampTest;
  private NFCFragment nfcFragment;
  private CarBackOLEDFragment carBackOLEDFragment;
  private CarBackOLED2Fragment carBackOLED2Fragment;
  private AVASFragment avasFragment;
  private SoundFragment soundFragment;
  private KeyPairFragment keyPairFragment;

  private VCUUpdateFirmwareFragment updateFirmwareFragment;

  private HorizontalListView hListView;
  private HorizontalListViewAdapter hListViewAdapter;

  private ImageView ivConnectionState, ivWifiConnectionState;
  private ImageView ivDiagram;
  public int mSelectedMode = 0;

  private AtomicBoolean disableSwitchFragment = new AtomicBoolean(false);

  private static final String[] PERMISSIONS = new String[]{
      Manifest.permission.RECORD_AUDIO,
      Manifest.permission.MODIFY_AUDIO_SETTINGS,
      Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE
  };


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
        /*if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    *//*| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION*//*;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }*/
    setContentView(R.layout.activity_main);

    ServiceManager.getInstance().setConnectionListener(mConnectionListener);
    //Service will be init in choose activity, no need for here
        /*ServiceManager.getInstance().init(getApplicationContext(), new ServiceManager.InitCompleteListener() {
            @Override
            public void onInitComplete() {
                ServiceManager.getInstance().setConnectionListener(mConnectionListener);
            }
        });*/

    ivDiagram = (ImageView) findViewById(R.id.iv_mainactivity_diagram);
    ivDiagram.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ivDiagram.setVisibility(View.INVISIBLE);
      }
    });

    initUI();
    setSelect(0);

    //byte UnlockR = (byte) 0x80;
    //byte test = (byte) 0xff;
    //test &= (~UnlockR);
    //Log.d("testLK", UnlockR + "  " + test);


    //getActivity().requestPermissions(PERMISSIONS, 1);
    //ActivityCompat.checkSelfPermission(this, PERMISSIONS[1])
    //FileUtils10.INSTANCE.getFilesUnderDownloadST(this);
    //openFileInNewWindow();
    requestAllFilesAccessPermission();
    ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 1);

    int tmp = 500;
    //        float tmp2 = ((float)(5 * tmp) / 1024);
    //        Toast.makeText(getApplication()," "+tmp2,Toast.LENGTH_SHORT).show();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == PICK_TXT_FILE) {
      //data
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    //        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode) {
      case 1:
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          //创建文件夹
                    /*if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        File file = new File(Environment.getExternalStorageDirectory() + "/aa/bb/");
                        if (!file.exists()) {
                            Log.d("jim", "path1 create:" + file.mkdirs());
                        }
                    }*/

        }
        break;
    }
  }
  // Request code for selecting a PDF document.
  private static final int PICK_TXT_FILE = 707;

  private void requestAllFilesAccessPermission() {
    //Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
    //
    //startActivity(
    //    new Intent(
    //        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
    //        uri
    //    )
    //);
  }

  private void openFileInNewWindow() {
    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    intent.setType("text/plain");

    // Optionally, specify a URI for the file that should appear in the
    // system file picker when it loads.
    //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

    startActivityForResult(intent, PICK_TXT_FILE);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  private void initUI() {
    ivConnectionState = (ImageView) findViewById(R.id.iv_mainactivity_lost_connect);
    ivConnectionState.setOnClickListener(this);
    ivWifiConnectionState = (ImageView) findViewById(R.id.iv_mainacivity_lost_wifi);
    ivWifiConnectionState.setOnClickListener(this);

    if (communicationEstablished) {
      ivWifiConnectionState.setVisibility(View.INVISIBLE);
      ivConnectionState.setVisibility(View.INVISIBLE);
    }

    hListView = (HorizontalListView) findViewById(R.id.horizon_listview);
    final int[] ids = {
        R.drawable.main_activity_bottom_hompage,
        R.drawable.main_activity_bottom_lamp,
        R.drawable.main_activity_bottom_seat,
        R.drawable.main_activity_bottom_door,
        R.drawable.main_activity_bottom_control,
        R.drawable.main_acitvity_bottom_nfc,
        R.drawable.main_activity_bottom_back_car,
        R.drawable.main_activity_bottom_back_trunk,
        R.drawable.main_activity_bottom_avas,
        R.drawable.main_activity_bottom_sound,
        R.drawable.main_activity_bottom_key_pair,
        R.drawable.ic_tracking
    };

    hListViewAdapter = new HorizontalListViewAdapter(getApplicationContext(), ids);
    hListView.setAdapter(hListViewAdapter);
    hListViewAdapter.setSelectIndex(mSelectedMode);
    hListViewAdapter.notifyDataSetChanged();

    hListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //                Log.i("MainActivity", "position = " + position);
        if (disableSwitchFragment.get()) {
          return;
        }
        mSelectedMode = position;
        hListViewAdapter.setSelectIndex(position);
        hListViewAdapter.notifyDataSetChanged();
        setSelect(position);
      }
    });
  }

  public String mVersion;

  Timer timer;

  private ConnectionType connectedType;

  ConnectionListener mConnectionListener = new ConnectionListener() {
    @Override
    public void onConnected() {
      //            Toast.makeText(getApplicationContext(), "Bt Connected", Toast.LENGTH_SHORT).show();
      hardwareConnected = true;
      if (null == timer) {
        timer = new Timer();
      }

      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          ServiceManager.getInstance().sendCommandToCar(new CMDGetVersion(), getVersionListener);
        }
      }, 2000);//等两秒发送get version command


            /*new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 2000);
*/

    }

    @Override
    public void onDisconnected() {
      Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_LONG).show();
      hardwareConnected = false;
      communicationEstablished = false;
      ivConnectionState.setVisibility(View.VISIBLE);
      ivWifiConnectionState.setVisibility(View.VISIBLE);
    }
  };

  public void setSelect(int i) {
    if (disableSwitchFragment.get()) {
      return;
    }
    ivDiagram.setVisibility(View.INVISIBLE);
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    if (i > mLastflag) {
      transaction.setCustomAnimations(R.anim.left_slide_in, R.anim.left_slide_out);
    } else if (i < mLastflag) {
      transaction.setCustomAnimations(R.anim.right_slide_in, R.anim.right_slide_out);
    }
    mLastflag = i;
    releaseFragment();
    switch (i) {
      case FRAGMENT_TRANSACTION_BMS_HOME:
        if (mHomeFragment == null) {
          mHomeFragment = new HomeFragment();
          //                    transaction.add(R.id.main_fragment_content, mHomeFragment);
        }
        transaction.replace(R.id.main_fragment_content, mHomeFragment);
        break;
      case 1:
        if (mFrontLampFragment == null) {
          mFrontLampFragment = new FrontHeadLamp();
          //                    transaction.add(R.id.main_fragment_content, mFrontLampFragment);
        }
        transaction.replace(R.id.main_fragment_content, mFrontLampFragment);

        break;
      case 2:
        if (mSeatFragment == null) {
          mSeatFragment = new SeatFragment();
          //                    transaction.add(R.id.main_fragment_content, mSeatFragment);
        }
        transaction.replace(R.id.main_fragment_content, mSeatFragment);
        break;
      case 3:
        if (mDoorFragment == null) {
          mDoorFragment = new DoorFragment();
          //                    transaction.add(R.id.main_fragment_content, mSeatFragment);
        }
        transaction.replace(R.id.main_fragment_content, mDoorFragment);
        break;
      case 4:
        if (mCenterControlFragment == null) {
          mCenterControlFragment = new CenterControlFragment();
          //                    transaction.add(R.id.main_fragment_content, mSeatFragment);
        }
        transaction.replace(R.id.main_fragment_content, mCenterControlFragment);

        break;
      case ConstantData.MainFragment.FRAGMENT_TRANSACTION_CAR_BACK:
        if (mCarBackFragment == null) {
          mCarBackFragment = new CarBackLampFragment();
        }
        transaction.replace(R.id.main_fragment_content, mCarBackFragment);
        break;
      case ConstantData.MainFragment.FRAGMENT_TRANSACTION_CAR_BACK_COVER:
        if (mCarBackCoverFragment == null) {
          mCarBackCoverFragment = new CarBackCoverFragment();
        }
        transaction.replace(R.id.main_fragment_content, mCarBackCoverFragment);
        break;
      case ConstantData.MainFragment.FRAGMENT_TRANSACTION_NFC:
        if (nfcFragment == null) {
          nfcFragment = new NFCFragment();
        }
        transaction.replace(R.id.main_fragment_content, nfcFragment);
        break;
      case ConstantData.MainFragment.FRAGMENT_TRANSACTION_AVAS:
        if (avasFragment == null) {
          avasFragment = new AVASFragment();
        }
        transaction.replace(R.id.main_fragment_content, avasFragment);
        break;
      case ConstantData.MainFragment.FRAGMENT_TRANSACTION_SOUND:
        if (soundFragment == null) {
          soundFragment = new SoundFragment();
        }
        transaction.replace(R.id.main_fragment_content, soundFragment);
        break;
      case ConstantData.MainFragment.FRAGMENT_TRANSACTION_KEY_PAIR:
        if (keyPairFragment == null) {
          keyPairFragment = new KeyPairFragment();
        }
        transaction.replace(R.id.main_fragment_content, keyPairFragment);
        break;
      case 100:
        if (mHighBeamLight == null) {
          mHighBeamLight = new HighBeamLight();
        }
        transaction.replace(R.id.main_fragment_content, mHighBeamLight);
        break;
      case 101:
        if (mBCMDiagnosticFragment == null) {
          mBCMDiagnosticFragment = new BCMDiagnosticFragment();
        }
        transaction.replace(R.id.main_fragment_content, mBCMDiagnosticFragment);
        break;
      case 102:
        if (frontHeadLampTest == null) {
          frontHeadLampTest = new FrontHeadLampTest2();
        }
        transaction.replace(R.id.main_fragment_content, frontHeadLampTest);
        break;
      case FRAGMENT_TRANSACTION_UPDATE_FIRMWARE:
        if (updateFirmwareFragment == null) {
          updateFirmwareFragment = new VCUUpdateFirmwareFragment();
        }
        transaction.replace(R.id.main_fragment_content, updateFirmwareFragment);
        break;
      case FRAGMENT_TRANSACTION_CAR_BACK_OLED:
        if (carBackOLEDFragment == null) {
          carBackOLEDFragment = new CarBackOLEDFragment();
        }
        transaction.replace(R.id.main_fragment_content, carBackOLEDFragment);
        break;
      case FRAGMENT_TRANSACTION_CAR_BACK_OLED2:
        if (carBackOLED2Fragment == null) {
          carBackOLED2Fragment = new CarBackOLED2Fragment();
        }
        transaction.replace(R.id.main_fragment_content, carBackOLED2Fragment);
        break;
      case FRAGMENT_TRANSACTION_CAR_FRONT_BOTTOM_LIGHT:
        if (mFrontBottomLight == null) {
          mFrontBottomLight = new FrontBottomLight();
        }
        transaction.replace(R.id.main_fragment_content, mFrontBottomLight);
        break;
      default:
        break;
    }
    transaction.commit();
  }

  /**
   * A native method that is implemented by the 'native-lib' native library,
   * which is packaged with this application.
   */

  // Used to load the 'native-lib' library on application startup.
  //    static {
  //        System.loadLibrary("native-lib");
  //    }
  @Override
  public void onClick(View v) {
    switch (v.getId()) {

      case R.id.iv_mainactivity_lost_connect://bt connection
        SharedPreferencesUtil.put(this, ConstantData.CONNECTION_TYPE, "BT");
        if (!hardwareConnected) {
          ServiceManager.getInstance()
              .connectToDevice(null, mConnectionListener, ConnectionType.BT);
        }
        ServiceManager.getInstance().sendCommandToCar(new CMDGetVersion(), getVersionListener);
        break;
      case R.id.iv_mainacivity_lost_wifi:
        SharedPreferencesUtil.put(this, ConstantData.CONNECTION_TYPE, "WIFI");
        if (!hardwareConnected) {
          ServiceManager.getInstance()
              .connectToDevice(null, mConnectionListener, ConnectionType.Wifi);
        }
        ServiceManager.getInstance().sendCommandToCar(new CMDGetVersion(), getVersionListener);
        break;
    }
  }

  private CommandListenerAdapter getVersionListener = new CommandListenerAdapter() {
    @Override
    public void onSuccess(BaseResponse response) {
      super.onSuccess(response);
      //invisible View
      mVersion = ((CMDGetVersion.Response) response).getVersion();
      communicationEstablished = true;
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          ivConnectionState.setVisibility(View.INVISIBLE);
          ivWifiConnectionState.setVisibility(View.INVISIBLE);
          Toast.makeText(getApplicationContext(),
              "version:" + mVersion, Toast.LENGTH_SHORT).show();
        }
      });
    }

    @Override
    public void onTimeout() {
      super.onTimeout();
    }
  };

  private void releaseFragment() {
    mHomeFragment = null;
    mFrontLampFragment = null;
    mSeatFragment = null;
    mHighBeamLight = null;
    mDoorFragment = null;
    mCenterControlFragment = null;
    mCarBackFragment = null;
    mCarBackCoverFragment = null;
    mBCMDiagnosticFragment = null;
    frontHeadLampTest = null;
    nfcFragment = null;
    avasFragment = null;
    soundFragment = null;
    keyPairFragment = null;
    System.gc();
  }

  public void showDiagram(String diagramName) {
    FileUtils.copyDiagram2SDCard(this, diagramName);

    String filepath =
        FileUtils.INTERNAL_PATH + FileUtils.DIAGRAM_PIC + File.separator + diagramName;

    File file = new File(filepath);
    if (file.exists()) {
      Bitmap bm = BitmapFactory.decodeFile(filepath);
      // 将图片显示到ImageView中
      ivDiagram.setVisibility(View.VISIBLE);
      ivDiagram.setImageBitmap(bm);
    }
  }

  public void enableSwitchFragment() {
    disableSwitchFragment.set(false);
  }

  public void disableSwitchFragment() {
    disableSwitchFragment.set(true);
  }
}
