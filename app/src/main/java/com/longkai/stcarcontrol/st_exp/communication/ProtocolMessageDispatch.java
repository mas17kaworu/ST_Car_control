package com.longkai.stcarcontrol.st_exp.communication;

import android.util.Log;
import android.util.SparseArray;

import com.longkai.stcarcontrol.st_exp.Utils.LoggerTemp;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

import static com.longkai.stcarcontrol.st_exp.Utils.ByteUtils.bytes2hex;

/**
 * Created by lk on 3/8/2016.
 */
public class ProtocolMessageDispatch implements MessageReceivedListener{

    private static final Logger logger = LoggerFactory.getLogger(ProtocolMessageDispatch.class);
    private SparseArray<Command> mSentCommandList;
    private SparseArray<CommandListener> mCommandListenerList;
    private ConcurrentLinkedQueue<Integer> mRegularCommandIdList;
    private final Object listLock = new Object();

    private ConnectionInterface mConnection;

    public ProtocolMessageDispatch(ConnectionInterface connection) {
        mConnection = connection;
        mConnection.setReceiveListener(this);

        mSentCommandList = new SparseArray();
        mCommandListenerList = new SparseArray();
        mRegularCommandIdList = new ConcurrentLinkedQueue<>();
    }

    /**
     * Send drone command.
     *
     * @param command  The drone command.
     * @param listener Report command execute result.
     */
    public void sendCommand(Command command, CommandListener listener) {
        registerCommandOnce(command, listener);
        byte[] sendData = command.toRawData();
        logger.info(">>>> ouput >>>> " + bytes2hex(sendData));
        int writeNum = mConnection.writeDataBlock(sendData);
        if (writeNum > 0) {
            //send success
        }
    }

    public void registerCommandOnce(Command command, CommandListener listener) {
        if (listener != null) {
            listener.setSendTimeStamp(System.currentTimeMillis());
            synchronized (listLock) {
                mSentCommandList.put(command.getCommandId(), command);
                mCommandListenerList.put(command.getCommandId(), listener);
                Log.i("Command","Send package commandID = " + command.getCommandId());
            }
        }
    }

    public void registerRegularCommand(Command command, CommandListener listener) {
        registerCommandOnce(command, listener);
      listener.setTimeout(Integer.MAX_VALUE);
        mRegularCommandIdList.add(command.getCommandId() & 0xff);

    }

    public void unregisterRegularCommand(Command command) {
        int commandId = command.getCommandId() & 0xff;
        mRegularCommandIdList.remove(commandId);
        synchronized (listLock){
            mSentCommandList.remove(commandId);
            mCommandListenerList.remove(commandId);
        }
    }

    /**
     * Check all sent commands timeout, if waiting time over timeout, the
     * command will be responsed timeout error, and removed form command check
     * list.
     */
    public void checkTimeoutCommand() {
        synchronized (listLock) {
            for (int i = 0; i < mCommandListenerList.size(); i++) {
                int commandId = mCommandListenerList.keyAt(i);

                if (isCMDInRegularList(commandId)){
                    return;
                }

                CommandListener listener = mCommandListenerList.get(commandId);
                if (listener != null && (System.currentTimeMillis() - listener.getSendTimestamp() > listener.getTimeout())) {

                    listener.onTimeout();

                    mCommandListenerList.remove(commandId);
                    mSentCommandList.remove(commandId);
                }
            }
        }
    }


    /**
     * Called when received a completely message.
     *
     * @param data   The data buffer of read from protocol layer.
     * @param offset The offset of data.
     * @param length The length of received.
     */
    @Override
    public void onReceive(byte[] data, int offset, int length) {
        LoggerTemp.getLogger().writeToLogger1("received udp data");
        if (data[0] == BaseCommand.COMMAND_HEAD0 && data[1] == BaseCommand.COMMAND_HEAD1){
            byte[] raw = new byte[257];
            System.arraycopy(data, 0, raw, 0, length);//接收的时候checksum包含头
//            if (data[length-1] == CheckSumBit.checkSum(raw, length-1) ){//检查完毕
            if (true ){//检查完毕
//                LoggerTemp.getLogger().writeToLogger2("raw data check finished");
                logger.info("<<<< input <<<< " + bytes2hex(data));
                int commandId = raw[3] & 0x7f;
                Log.i("Command","Got package commandId = " + commandId);
                Command command;
                CommandListener listener;
                synchronized (listLock) {
                    command = mSentCommandList.get(commandId);
                    listener = mCommandListenerList.get(commandId);

                    if (!isCMDInRegularList(commandId)) {
                        mSentCommandList.remove(commandId);
                        mCommandListenerList.remove(commandId);
                    }
                }
                Log.i("Command","Got package command = " + command);
                if (command == null || listener == null) {
                    return;
                }

                if ((System.currentTimeMillis() - listener.getSendTimestamp()) > listener.getTimeout()) {
                    listener.onTimeout();
                } else {
                    BaseResponse response = null;
                    try {
                        response = command.toResponse(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                        listener.onTimeout();
                        return;
                    }
                    if (response != null) {
                        LoggerTemp.getLogger().writeToLogger3("onSuccess commandID = " + commandId);
                        listener.onSuccess(response);
                    }
                }
            }
        }
    }

    public SparseArray<Command> getSentCommandList(){
        return mSentCommandList;
    }

    /**
     * check if commandID is in regularCMDList
     */
    private boolean isCMDInRegularList(int commandId){
        for (Integer j:
                mRegularCommandIdList) {
            if (j == commandId){
                return true;
            }
        }
        return false;
    }

}