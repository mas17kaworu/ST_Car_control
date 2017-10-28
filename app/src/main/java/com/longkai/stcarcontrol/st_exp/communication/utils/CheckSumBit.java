package com.longkai.stcarcontrol.st_exp.communication.utils;

/**
 * Created by Administrator on 2017/8/14.
 */

public class CheckSumBit {
    public static byte checkSum(byte[] raw, int length){
        int sum = 0;
        int i;
        for (i=0;i<length;i++){
            sum = sum + (raw[i] & 0xff);
            if (sum>255){
                sum = sum - 255;
            }
        }
        sum = 255 - sum;
        return (byte) (sum&0xff);
    }
}
