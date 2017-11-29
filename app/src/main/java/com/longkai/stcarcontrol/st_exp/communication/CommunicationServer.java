package com.longkai.stcarcontrol.st_exp.communication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.longkai.stcarcontrol.st_exp.ConstantData;
import com.longkai.stcarcontrol.st_exp.Utils.SharedPreferencesUtil;
import com.longkai.stcarcontrol.st_exp.communication.btComm.BTManager;
import com.longkai.stcarcontrol.st_exp.communication.btComm.BTServer;
import com.longkai.stcarcontrol.st_exp.communication.udpComm.UdpServer;

import java.util.LinkedList;
import java.util.List;

/**
 * @author yuneec-lk
 * @version 1.0
 * @since 1.0 on 2017/8/23 0023
 */

public class CommunicationServer extends Service {
    private static CommunicationServer instance;
    private ConnectionInterface mConnection;
    private ConnectionListener mConnectionListener;
    private ProtocolMessageDispatch mMessageHandler;

    private List<ConnectionListener> mConnectionListenerList;

    private int COMMAND_TIME_OUT = 1000;

    private final CommServerBinder mBinder = new CommServerBinder();
    private Handler doBackgroundHandler;

    public CommunicationServer getInstance(){
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initListener();

        HandlerThread handlerThread = new HandlerThread("service-background-liangkui-thread");
        handlerThread.start();
        doBackgroundHandler = new Handler(handlerThread.getLooper());


        String type = (String) SharedPreferencesUtil.get(this, ConstantData.CONNECTION_TYPE, "BT");

        if (type.equalsIgnoreCase("BT")) {// use BTserver
            mConnection = new BTServer(BTManager.getInstance().getBtAdapter(), null, getApplicationContext());
            mConnection.open(null, mConnectionListener);
        } else {// UDP
            mConnection = new UdpServer();
            mConnection.open(null, mConnectionListener);
        }

        mMessageHandler = new ProtocolMessageDispatch(mConnection);
        doBackgroundHandler.postDelayed(commandTimeoutCheck, COMMAND_TIME_OUT);
    }

    @Override
    public void onDestroy() {
        instance = null;
        doBackgroundHandler.removeCallbacksAndMessages(null);
        doBackgroundHandler.getLooper().quit();
        mConnection.close();
        mConnectionListenerList.clear();
        super.onDestroy();
    }

    private void initListener() {
        mConnectionListenerList = new LinkedList<>();

        mConnectionListener = new ConnectionListener() {
            private Handler handler = new Handler();
            DispatchEventToUi dispatchEventToUi = new DispatchEventToUi(mConnectionListenerList);

            @Override
            public void onConnected() {
                if (mConnectionListenerList.size() > 0) {
                    dispatchEventToUi.dispatch(new DispatchRunnable<ConnectionListener>() {
                        @Override
                        public void run(ConnectionListener listener) {
                            try {
                                listener.onConnected();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    handler.postDelayed(new Runnable() {//一直循环直到 list里面有对象
                        @Override
                        public void run() {
                            onConnected();
                        }
                    }, 100);
                }
            }

            @Override
            public void onDisconnected() {
                if (mConnectionListenerList.size() > 0) {
                    dispatchEventToUi.dispatch(new DispatchRunnable<ConnectionListener>() {
                        @Override
                        public void run(ConnectionListener listener) {
                            try {
                                listener.onDisconnected();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    handler.postDelayed(new Runnable() {//一直循环直到 list里面有对象
                        @Override
                        public void run() {
                            onDisconnected();
                        }
                    }, 100);
                }
            }
        };
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    private Runnable commandTimeoutCheck = new Runnable() {
        @Override
        public void run() {
            mMessageHandler.checkTimeoutCommand();
            doBackgroundHandler.postDelayed(commandTimeoutCheck, COMMAND_TIME_OUT);
        }
    };

    private interface DispatchRunnable<T> {
        void run(T t);
    }

    private class DispatchEventToUi<T> {
        private List<T> list;
        private Handler uiHandler;

        public DispatchEventToUi(List<T> list) {
            this.list = list;
            this.uiHandler = new Handler(Looper.getMainLooper());
        }

        public void dispatch(final DispatchRunnable runnable) {
            for (final T listener : list) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        runnable.run(listener);
                    }
                });
            }
        }
    }


    public class CommServerBinder extends Binder{
        public void asyncSendCommand(final Command command, final CommandListener listener) {
            doBackgroundHandler.post(new Runnable() {
                @Override
                public void run() {
                    mMessageHandler.sendCommand(command, listener);
                }
            });
        }

        public void registerConnectionListener(ConnectionListener listener) {
            mConnectionListenerList.add(listener);
        }

        public void unregisterConnectionListener(ConnectionListener listener) {
            mConnectionListenerList.remove(listener);
        }

        public void ConnectToDevice(Bundle bundle, ConnectionListener listener, ConnectionType type){
            if (type.equals(ConnectionType.BT)){
                mConnection = new BTServer(BTManager.getInstance().getBtAdapter(), null, getApplicationContext());
            } else if (type.equals(ConnectionType.Wifi)){
                mConnection = new UdpServer();
            }


            mConnection.open(bundle, listener);
            mConnectionListenerList.clear();
            mConnectionListenerList.add(listener);
        }

    }


}
