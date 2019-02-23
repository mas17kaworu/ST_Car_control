package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDECallList;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2019/2/22.
 */
public class CMDECallTest {

    @Test
    public void CMDTestConstructor(){
        CMDECall cmdeCall = new CMDECall("15201968078", CMDECall.Type.call);
        byte[] raw = cmdeCall.toRawData();
        for (int i = 0; i < 12; i++){
            System.out.print(raw[i] + " ");
        }
    }
}