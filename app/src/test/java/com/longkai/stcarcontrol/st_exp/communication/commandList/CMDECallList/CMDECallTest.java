package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDECallList;

import junit.framework.Assert;

import org.junit.Test;

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
        Assert.assertEquals(raw[4], 15);
        Assert.assertEquals(raw[5], 20);
        Assert.assertEquals(raw[6], 19);
        Assert.assertEquals(raw[7], 68);
        Assert.assertEquals(raw[8], 7);
        Assert.assertEquals(raw[9], 8);
    }
}