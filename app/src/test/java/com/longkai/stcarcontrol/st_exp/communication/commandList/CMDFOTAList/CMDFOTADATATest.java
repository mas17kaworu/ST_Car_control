package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDFOTAList;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Administrator on 2019/2/1.
 */
public class CMDFOTADATATest {
    @Test
    public void testConstructor_notLast(){
        byte[] payload = new byte[0x7ab];
        CMDFOTADATA cmd = new CMDFOTADATA(0x255, 0x7ab,0, payload);
        byte[] raw = cmd.toRawData();
        for (int i = 0; i < 10; i++){
            System.out.print(raw[i] + " ");
        }
        Assert.assertEquals(0x02, (raw[4] & 0xff));
        Assert.assertEquals(0x55, (raw[5] & 0xff));
    }

    @Test
    public void testConstructor_LastPackage(){
        byte[] payload = new byte[0x7ab];
        CMDFOTADATA cmd = new CMDFOTADATA(0x255, 0x7ab, 1, payload);
        byte[] raw = cmd.toRawData();
        for (int i = 0; i < 10; i++){
            System.out.print(raw[i] + " ");
        }
        Assert.assertEquals(0x87, (raw[4] & 0xff));
        Assert.assertEquals(0xab, (raw[5] & 0xff));
    }

}