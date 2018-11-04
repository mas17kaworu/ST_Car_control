package com.longkai.stcarcontrol.st_exp;

import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUGUI7List.CMDVCUGUI7DCDC;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUGUI7List.CMDVCUGUI7OBCOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUGUI7List.CMDVCUGUI7OBCOn;

import org.junit.Test;

/**
 * Created by Administrator on 2018/11/1.
 */

public class CMDVCUGUI7Test {
    @Test
    public void testStaticPayload(){
        CMDVCUGUI7OBCOn cmdvcu7OBCOn = new CMDVCUGUI7OBCOn();
        CMDVCUGUI7OBCOff cmdvcu7OBCOff = new CMDVCUGUI7OBCOff();
        if (cmdvcu7OBCOn.getValue() == 0) {
            cmdvcu7OBCOff.off();
            cmdvcu7OBCOn.on();
        } else {
            cmdvcu7OBCOn.off();
            cmdvcu7OBCOff.on();
        }
        printArray(cmdvcu7OBCOff.getPayload());

        CMDVCUGUI7DCDC cmdvcugui7DCDC = new CMDVCUGUI7DCDC();
        printArray(cmdvcugui7DCDC.getPayload());
        if (cmdvcugui7DCDC.getValue() == 0) {
            cmdvcugui7DCDC.on();
        } else {
            cmdvcugui7DCDC.off();
        }
        printArray(cmdvcugui7DCDC.getPayload());
    }

    private void printArray(byte[] array){
        for (byte t:array) {
            System.out.print(" " + t);
        }
        System.out.println();
    }
}
