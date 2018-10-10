package com.longkai.stcarcontrol.st_exp;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        byte t = (byte) (0b101<<5 | 0x16);
        assertEquals(t, (byte)0b10110110);
        byte[] data = {0x01,0x01};
        int tmp = (int)(((data[1] & 0xff)<<8 |(data[0] & 0xff) - 1) * 1.0f);
        assertEquals(tmp, 0x0100);
    }

    @Test
    public void final_test() {
        byte[] data = {(byte)0x88,(byte)0xa5};
        int Pedal_Status = (data[0] & 0xff)<<8 | (data[1] & 0xff);
        System.out.println("longkai Pedal_Status = " + (float)Pedal_Status);
    }


}