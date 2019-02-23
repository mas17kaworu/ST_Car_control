package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDInfoteinmentList;

import org.junit.Assert;
import org.junit.Test;

public class CMDNewInfoteinmentTest {
    private CMDNewInfoteinmentEngineVoice engineVoice;
    private CMDNewInfoteinmentVolumeIncrease volumeIncrease;
    private CMDNewInfoteinmentVolumeDecrease volumeDecrease;

    @Test
    public void constructorTest() {
        engineVoice = new CMDNewInfoteinmentEngineVoice();
        engineVoice.changeVoiceTo(3);
        byte[] raw = engineVoice.toRawData();
        for (int i = 0; i < 12; i++){
            System.out.print(raw[i] + " ");
        }
        System.out.println();
        Assert.assertEquals(raw[4], 2);
        Assert.assertEquals(raw[5], 3);

        volumeIncrease = new CMDNewInfoteinmentVolumeIncrease();
        raw = volumeIncrease.toRawData();
        for (int i = 0; i < 12; i++){
            System.out.print(raw[i] + " ");
        }
        System.out.println();
        Assert.assertEquals(raw[4], 1);
        Assert.assertEquals(raw[6], 1);

        volumeDecrease = new CMDNewInfoteinmentVolumeDecrease();
        raw = volumeDecrease.toRawData();
        for (int i = 0; i < 12; i++){
            System.out.print(raw[i] + " ");
        }
        System.out.println();
        Assert.assertEquals(raw[4], 1);
        Assert.assertEquals(raw[6], 2);
    }

}