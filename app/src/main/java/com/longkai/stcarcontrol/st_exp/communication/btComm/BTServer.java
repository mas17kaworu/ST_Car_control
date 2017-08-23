package com.longkai.stcarcontrol.st_exp.communication.btComm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;

import com.longkai.stcarcontrol.st_exp.communication.Command;
import com.longkai.stcarcontrol.st_exp.communication.CommandListener;
import com.longkai.stcarcontrol.st_exp.communication.ConnectionInterface;
import com.longkai.stcarcontrol.st_exp.communication.ConnectionListener;
import com.longkai.stcarcontrol.st_exp.communication.MessageReceivedListener;
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

public class BTServer implements ConnectionInterface{

	private static final String TAG = "BTServer";
	private static final String GamePadName = "DemoCar";//Gamesir-G2u

    private Context mContext;

	private BluetoothServerSocket mBtServerSocket = null;
	private BluetoothSocket mBtClientSocket = null;  //clientsocket
	private BluetoothAdapter mBtAdapter =null;
	private BluetoothDevice mBtDevice = null;

	private BufferedInputStream bis=null;
	private BufferedOutputStream bos=null;

	private Handler detectedHandler=null;

	private ConnectThread CntThread;

	public byte [] sendpackge = new byte[64];

	private DatagramSocket FlyUDPSocket = null;
	private DatagramPacket sendFlyPacket = null; //发数据
	private DatagramPacket rcvFlyPacket = null;  //收数据

	private boolean BtConnect_state = false;

	public BTServer() {

	}
	public BTServer(BluetoothAdapter mBtAdapter,Handler detectedHandler, Context context){
		this.mBtAdapter = mBtAdapter;
		this.detectedHandler = detectedHandler;
        mContext = context;

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction("android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED");
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceive, filter);
	}

	private BroadcastReceiver mReceive = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
            Log.i(TAG, "Receive action" + action);
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)){
				if (detectedHandler != null) {
					detectedHandler.sendEmptyMessage(1000);
				}
				mConnectionListener.onConnected();
            }else if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
				if (detectedHandler != null) {
					detectedHandler.sendEmptyMessage(1001);
				}
				mConnectionListener.onDisconnected();
            }else if( BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)){//ST CAR 的蓝牙连接不走这里
//				Toast.makeText(getApplicationContext(), "lk", Toast.LENGTH_SHORT).show();
				if(intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, -1)
						== BluetoothAdapter.STATE_DISCONNECTED){
//					detectedHandler.sendEmptyMessage(1001);
				}
                if(intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, -1)
                        == BluetoothAdapter.STATE_CONNECTED){
//                    detectedHandler.sendEmptyMessage(1000);
                }
			} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                Log.i(TAG, "blueState = " + blueState);
                switch(blueState){
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.i(TAG,"onReceive---------STATE_TURNING_ON");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.i(TAG,"onReceive---------STATE_ON");
                        //自动重连
                        connectToDevice();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.e(TAG,"onReceive---------STATE_TURNING_OFF");
//                        BleUtil.toReset(mContext);
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Log.e(TAG,"onReceive---------STATE_OFF");
                        resetBT();
                        break;
                }
            }
		}
	};

	public void connectToDevice() {
		Log.d(TAG, "connectToDevice.");
		try {
			if(null == mBtDevice) {
				Set<BluetoothDevice> BondedDevieces = mBtAdapter.getBondedDevices();
				if (BondedDevieces.size()>0) {
					int tmp = GamePadName.length();
					for (BluetoothDevice bdevice:BondedDevieces)
					{
						Log.d(TAG, "Name = " + bdevice.getName());
						if (bdevice.getName().length()>=tmp){
							if (bdevice.getName().substring(0, tmp).equals(GamePadName))
								mBtDevice = bdevice;
						}

					}
				}
				//mBtDevice = mBtAdapter.getRemoteDevice(BluetoothMsg.lastblueToothAddress);
			}
			if(mBtDevice.getBondState() == BluetoothDevice.BOND_NONE) {
				Log.d(TAG, "开始配对");
				Method creMethod = BluetoothDevice.class.getMethod("createBond");
				creMethod.invoke(mBtDevice);
			}

			if(null == mBtClientSocket) {
				Log.d(TAG, "1");
				CntThread = new ConnectThread(mBtDevice);
				CntThread.start();
			}

		}
		catch(Exception ex) {
			Log.d(TAG, "无法配对!");
		}
	}

	private void resetBT(){
        Log.d(TAG, "resetBT.");
        BtConnect_state = false;
        try {
            if (mBtClientSocket != null) {
                mBtClientSocket.close();
                mBtClientSocket = null;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mBtDevice=null;

    }

	public void disconnectBT() {
		Log.d(TAG, "disconnectBT.");
        resetBT();
		mContext.unregisterReceiver(mReceive);
	}

	private ConnectionListener mConnectionListener;

    @Override
    public boolean open(Bundle parameter, ConnectionListener listener) {
		mConnectionListener = listener;
        connectToDevice();
        return true;
    }

    @Override
    public void close() {
        resetBT();
        mContext.unregisterReceiver(mReceive);
    }

    @Override
    public int writeDataBlock(byte[] data) {
        if (sendbyteArray(data)){
            return data.length;
        }else {
            return -1;
        }
    }

    private MessageReceivedListener MessageReceivedListener;
    @Override
    public void setReceiveListener(final MessageReceivedListener listener) {
        MessageReceivedListener = listener;

    }

    private class ConnectThread extends Thread {
		private  BluetoothSocket btSocket;
		private  BluetoothDevice btDevice;
		private InputStream inputStream;

		public ConnectThread(BluetoothDevice btDevice) {
			this.btDevice  = btDevice;
		}
		public void run() {

			Method m1;
			try {
				m1= btDevice.getClass().getMethod( "createRfcommSocket", new Class[] { int.class });
				BluetoothSocket s = (BluetoothSocket) m1.invoke(btDevice, 1);
				s.connect();
				btSocket = s;
			} catch (Exception ex2) {
				Log.d(TAG, "error2");

				//dui hua kuang
				btSocket = null;
			}
			if (btSocket != null) {
				Log.d(TAG, "连接成功！");
				mBtClientSocket = btSocket;
				BtConnect_state = true;

				try {
					inputStream = mBtClientSocket.getInputStream();
					bos = new BufferedOutputStream(mBtClientSocket.getOutputStream());
				} catch (IOException e) {
					e.printStackTrace();
				}
				sendmsg("first message");
			}
			//receive ConstantData from bluetooth
			if (mBtClientSocket != null) {
				Log.d(TAG, "test5");
				byte[] Gbuffer = new byte[128];
				//初始化接收的包
				while(BtConnect_state){
					try {
						int count = inputStream.read(Gbuffer);
                        if (count >=4){
                            MessageReceivedListener.onReceive(Gbuffer, 0, count);
                        }
						Log.d(TAG, "count = " + count + "Gamepad ConstantData : ");
					} catch (IOException e){
//						e.printStackTrace();
					}
						/*for (int i=0; i<20;i++)
							//Log.d(TAG, CommonUtils.getUnsignedByte(Gbuffer[i]) + " " );
							System.out.print(CommonUtils.getUnsignedByte(Gbuffer[i]) + " ");
						System.out.println(" ");*/
						/*sendpackge = mYP.packegeYP(Gbuffer);
						Log.d(TAG, "mCP = "+mCP);
						mCP.Package(Gbuffer);*/

				}
			}
		}
	}

	public synchronized boolean sendmsg(String msg){
		boolean result=false;
		if(null==mBtClientSocket || bos==null)
			return false;
		try {
			bos.write(msg.getBytes());
			bos.flush();
			result=true;
			Log.d(TAG, "BT send:" + msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	private synchronized boolean sendbyteArray(byte[] msg){
		boolean result=false;
		if(null==mBtClientSocket || bos==null) {
            return false;
        }
		try {
			bos.write(msg);
			bos.flush();
			result=true;
			Log.d(TAG, "BT send:" + msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
        return result;
	}

	public void closeBTServer(){
		try{
			if(bis != null)
				bis.close();
			if(bos != null)
				bos.close();
			if(mBtServerSocket != null)
				mBtServerSocket.close();
			if(mBtClientSocket != null)
				mBtClientSocket.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
