package com.longkai.stcarcontrol.st_exp.communication.udpComm;

import android.os.Bundle;
import android.util.Log;

import com.longkai.stcarcontrol.st_exp.communication.ConnectionInterface;
import com.longkai.stcarcontrol.st_exp.communication.ConnectionListener;
import com.longkai.stcarcontrol.st_exp.communication.MessageReceivedListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;

/**
 * Created by Administrator on 2017/8/30.
 */

public class UdpServer implements ConnectionInterface {
    private static final String TAG = "BaseUdpServer";
//    private String mServerIp = "127.0.0.1";//我自己的IP
    private int mServerPort = 45130;//我的UDP端口号
    private String mTargetIp = "192.168.31.11";//对方的IP
    private int mTargetPort = 1388;//对方的UDP端口号

    private byte[] mRecvBuffer = new byte[1024];
    private byte[] mSendBuffer = new byte[1024];
    private DatagramSocket mSocket = null;
    private RecevThread mRecevThread = null;

    DatagramPacket recvPacket = null;
    DatagramPacket sendPacket = null;

    MessageReceivedListener mMessageReceivedListener;
    private ConnectionListener mConnectionListener;

    public UdpServer(){
        recvPacket = new DatagramPacket(mRecvBuffer, mRecvBuffer.length);
        try {
            sendPacket = new DatagramPacket(mSendBuffer, mSendBuffer.length, new InetSocketAddress(mTargetIp, mTargetPort));
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    public UdpServer(int port) {
        mServerPort = port;

        recvPacket = new DatagramPacket(mRecvBuffer, mRecvBuffer.length);
        try {
            sendPacket = new DatagramPacket(mSendBuffer, mSendBuffer.length, new InetSocketAddress(mTargetIp, mTargetPort));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void startUdpServer() {
        if (mRecevThread != null && mRecevThread.isAlive()) {
            return;
        }
        stopUdpServer();

        mRecevThread = new RecevThread();
        mRecevThread.start();
    }

    /**
     * RecevThread
     *
     * @author zhou.zheng
     * @version 1.0 2017/4/18
     * @since 1.0
     */
    private class RecevThread extends Thread {
        public boolean isRunning = true;

        @Override
        public void run() {
            super.run();
            try {
//                InetSocketAddress socketAddress = new InetSocketAddress(mServerIp, mServerPort);
                mSocket = new DatagramSocket(mServerPort);//mSocket = new DatagramSocket(socketAddress);
                mSocket.setReuseAddress(true);
                mSocket.setSoTimeout(200);
                //Log.d(TAG, "LocalUdpServer Started!");
            } catch (SocketException e) {
                e.printStackTrace();

                Log.e(TAG, "BaseUdpServer Start Error");
                //return;
            }

            mConnectionListener.onConnected();

            while (isRunning) {
                try {
                    if (mSocket != null) {
                        mSocket.receive(recvPacket);

                        sendPacket.setSocketAddress(recvPacket.getSocketAddress());
                        //AOA Send
                        byte[] temp = Arrays.copyOf(recvPacket.getData(), recvPacket.getLength());
                        //Log.d(TAG, "LocalUdpServer Recv:" + ByteUtils.byteArrayToHexString(temp));
                        mMessageReceivedListener.onReceive(temp,0,temp.length);
                        Log.i(TAG,"Got UDP package length = "+ temp.length);
                    }
                } catch (IOException e) {
//                    e.printStackTrace();
                }
            }
        }
    }

    public void send(byte[] data, int length) {
        if (mSocket == null) {
            //Log.d(TAG, "LocalUdpServer is not started");
            return;
        }
        sendPacket.setData(data);
        sendPacket.setLength(length);
        try {
            mSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopUdpServer() {
        if (mRecevThread != null) {
            mRecevThread.isRunning = false;
            mRecevThread = null;
        }
        if (mSocket != null) {
            //test
//            mConnectionListener.onDisconnected();
            mSocket.disconnect();
            mSocket.close();
            mSocket = null;
        }
    }

    @Override
    public boolean open(Bundle parameter, ConnectionListener listener) {
        mConnectionListener = listener;
        startUdpServer();
        return false;
    }

    @Override
    public void close() {
        stopUdpServer();
        mConnectionListener.onDisconnected();
    }

    @Override
    public int writeDataBlock(byte[] data) {
        send(data, data.length);
        return data.length;
    }

    @Override
    public void setReceiveListener(MessageReceivedListener listener) {
        mMessageReceivedListener = listener;
    }
}
