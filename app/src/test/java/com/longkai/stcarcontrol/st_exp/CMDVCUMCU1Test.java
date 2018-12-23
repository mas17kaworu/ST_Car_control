package com.longkai.stcarcontrol.st_exp;

import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUMCU1List.CMDVCUMCU1;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2018/12/22.
 */
public class CMDVCUMCU1Test {
    CMDVCUMCU1 cmd;

    @Test
    public void toResponse() throws Exception {
        cmd = new CMDVCUMCU1();
        CMDVCUMCU1.Response response = new CMDVCUMCU1.Response(cmd.getCommandId());
        response.Current_of_MCU = 1023;
        byte[] mockByte = response.mockResponse();
        for (byte i:
             mockByte) {
            System.out.println(i);
        }
    }

}