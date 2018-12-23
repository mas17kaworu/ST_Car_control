package com.longkai.stcarcontrol.st_exp.communication;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Created by Administrator on 2017/7/17.
 */

public class ServiceManager {
    private CommunicationServer.CommServerBinder binder = null;
    private static ServiceManager instance;
    private Context context;
    private Intent serviceIntent;

    protected boolean mIsBinded = false;

    private InitCompleteListener mInitCompleteListener;

    private ServiceManager() {
        instance = this;
    }

    public static ServiceManager getInstance() {
        if (instance == null) {
            synchronized (ServiceManager.class) {
                if (instance == null) {
                    instance = new ServiceManager();
                }
            }
        }
        return instance;
    }

    //service connected listener
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            binder = (CommunicationServer.CommServerBinder) service;
            mIsBinded = true;
            mInitCompleteListener.onInitComplete();
        }

        public void onServiceDisconnected(ComponentName className) {
            mIsBinded = false;
        }
    };

    public interface InitCompleteListener {
        void onInitComplete();
    }

    public void init(Context context, InitCompleteListener initCompleteListener) {
        if (this.context != null) {
            return;
        }
        this.context = context;
        this.mInitCompleteListener = initCompleteListener;
        serviceIntent = new Intent();
        serviceIntent.setClass(context,
                com.longkai.stcarcontrol.st_exp.communication.CommunicationServer.class);
        bindCommService();
    }

    public void destroy(){
        unbindCommService();
        context.stopService(serviceIntent);
        binder = null;
        context = null;
    }

    private void bindCommService() {
        mIsBinded = false;
        context.bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindCommService() {
        try {
            if (mIsBinded) {
                context.unbindService(mConnection);
                mIsBinded = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void sendCommandToCar(Command command, CommandListener listener) {
        if (binder != null) {
            binder.asyncSendCommand(command, listener);
        }
    }

    public void setConnectionListener(ConnectionListener connectionListener) {
        if (connectionListener != null) {
            binder.registerConnectionListener(connectionListener);
        }
    }

    public void connectToDevice(Bundle bundle, ConnectionListener listener, ConnectionType type){
        if (null != binder) {
            binder.ConnectToDevice(bundle, listener, type);
        }
    }

    public ProtocolMessageDispatch getMessageDispatcher(){
        if (null != binder) {
            return binder.getMessageHandler();
        } else {
            return null;
        }
    }
}
