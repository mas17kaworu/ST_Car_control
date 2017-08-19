package com.longkai.stcarcontrol.st_exp.communication.btComm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
//import android.util.Log;

public class BTManager {

    private static String TAG = "BTManager";

    //private List<BTItem> mListDeviceBT=null; 
    private BluetoothAdapter mBtAdapter =null;
    private static BTManager manager=null;

    private BTManager(){
        //mListDeviceBT=new ArrayList<BTItem>();  
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    public static BTManager getInstance(){
        if(null==manager)
            manager =new BTManager();
        return manager;
    }

    private StatusBlueTooth blueStatusListener=null;

    public void setBlueListner(StatusBlueTooth blueLis){
        this.blueStatusListener = blueLis;
    }

    public BluetoothAdapter getBtAdapter(){
        return this.mBtAdapter;
    }

    //打开蓝牙设备

    public void openBluetooth(Activity activity) {
        if(null == mBtAdapter){   ////Device does not support Bluetooth
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setTitle("No bluetooth devices");
            dialog.setMessage("Your equipment does not support bluetooth, please change device");

            dialog.setNegativeButton("cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "No bluetooth on this device.");
                        }
                    });
            dialog.show();
            return;
        }
        // If BT is not on, request that it be enabled.
        if (!mBtAdapter.isEnabled()) {
            /*Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableIntent, 3);*/
            Log.d(TAG,"BTAdapter enable.");
            mBtAdapter.enable();
        }
    }

    public void closeBluetooth(){
        if(mBtAdapter.isEnabled())
            mBtAdapter.disable();
    }
    //Return true if the local Bluetooth adapter is currently in the device discovery process
    //即本地蓝牙正处于搜索状态
    public boolean isDiscovering(){
        return mBtAdapter.isDiscovering();
    }

    public void scanDevice(){
        //mListDeviceBT.clear();  未使用
        if(!mBtAdapter.isDiscovering())
            mBtAdapter.startDiscovery();
    }

    public void cancelScanDevice(){
        if(mBtAdapter.isDiscovering())
            mBtAdapter.cancelDiscovery();
    }

    public void registerBluetoothReceiver(Context mcontext){
        // Register for broadcasts when         start bluetooth search
        IntentFilter startSearchFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        mcontext.registerReceiver(mBlueToothReceiver, startSearchFilter);
        // Register for broadcasts when        a device is discovered
        IntentFilter discoveryFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mcontext.registerReceiver(mBlueToothReceiver, discoveryFilter);
        // Register for broadcasts when         discovery has finished
        IntentFilter foundFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mcontext.registerReceiver(mBlueToothReceiver, foundFilter);
    }

    public void unregisterBluetooth(Context mcontext){
        cancelScanDevice();
        mcontext.unregisterReceiver(mBlueToothReceiver);
    }

    //所有可匹配的蓝牙设备列表
    public List<BTItem> getPairBluetoothItem(){
        List<BTItem> mBTitemList = null;
        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        Iterator<BluetoothDevice> it = pairedDevices.iterator();
        while(it.hasNext()){
            if(mBTitemList == null)
                mBTitemList = new ArrayList<BTItem>();

            BluetoothDevice device=it.next();
            BTItem item = new BTItem();
            item.setBuletoothName(device.getName());
            item.setBluetoothAddress(device.getAddress());
            item.setBluetoothType(BluetoothDevice.BOND_BONDED);
            mBTitemList.add(item);
        }
        return mBTitemList;
    }


    private final BroadcastReceiver mBlueToothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //开始搜索
            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                if(blueStatusListener != null)
                    blueStatusListener.BTDeviceSearchStatus(StatusBlueTooth.SEARCH_START);
            }
            //搜索到一个
            else if (BluetoothDevice.ACTION_FOUND.equals(action)){
                // When discovery finds a device
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    BTItem item=new BTItem();
                    item.setBuletoothName(device.getName());
                    item.setBluetoothAddress(device.getAddress());
                    item.setBluetoothType(device.getBondState());
                    // 搜寻到蓝牙设备通知前台更新
                    if(blueStatusListener != null)
                        blueStatusListener.BTSearchFindItem(item);
                    //mListDeviceBT.add(item);
                }
            }
            //搜索完成
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                // When discovery is finished, change the Activity title
                if(blueStatusListener != null)
                    blueStatusListener.BTDeviceSearchStatus(StatusBlueTooth.SEARCH_END);
            }
        }
    };
}