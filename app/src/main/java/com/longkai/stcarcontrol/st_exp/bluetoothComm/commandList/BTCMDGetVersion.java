package com.longkai.stcarcontrol.st_exp.bluetoothComm.commandList;

/**
 * Created by Administrator on 2017/8/5.
 */

public class BTCMDGetVersion extends BaseBtCommand{
    public BTCMDGetVersion(){
        try{
            data = new byte[2];
            dataLength = 2;
            data[0] = 0x02;
            data[1] = COMMAND_GET_FIRMWARE;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected byte getCommandID(){
        return COMMAND_GET_FIRMWARE;
    }
}
