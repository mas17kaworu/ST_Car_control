package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOBCDemoList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDFOTAList.CMDFOTADATA;
import org.junit.Assert;
import org.junit.Test;

public class CMDOBCReturnTest {

  @Test
  public void testConstructor_notLast() throws Exception {
    byte[] responseBytes = {
        0x5a, 0x3c, 0x12, 0x26,
        0x03,
        0x25,
        0x03,
        (byte)0xA4,
        0x0c,
        (byte)0xC6,
        0x0a,
        0x6D,
        0x02,
        0x3E
    };
    CMDOBCReturn cmd = new CMDOBCReturn();
    CMDOBCReturn.Response response = (CMDOBCReturn.Response) cmd.toResponse(responseBytes);
    System.out.println(((responseBytes[6] & 0xff) << 8 | (responseBytes[7] & 0xff)) + "");

    System.out.println(
        response.PFCState + " " +
            response.LLCState + " " +
            response.Vac + " " +
            response.Vbus + " " +
            response.Vbat + " " +
            response.Ibat + " "

    );

    //Assert.assertEquals(0x02, (raw[4] & 0xff));
    //Assert.assertEquals(0x55, (raw[5] & 0xff));
  }
}