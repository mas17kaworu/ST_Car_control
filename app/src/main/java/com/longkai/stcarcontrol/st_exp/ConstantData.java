package com.longkai.stcarcontrol.st_exp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/29.
 */

public class ConstantData {
    public static Map<Integer,Integer> sDataMap;

    public static final String BluetoothName = "DemoCar";//HC-05  //DemoCar

    public static int[] sSeatFragmentStatus = new int[3];
    public static int mSeatHeatStatus = 0;
    public static int mSeatWindStatus = 0;
    public static int mSeatSetStatus = 0;

    public static int[] sFrontLampFragmentStatus = new int[7];
    public static int sLampDadengStatus = 0;
    public static int sLampJiaodengLeftStatus = 1;
    public static int sLampJiaodengRightStatus = 6;
    public static int sLampJinguangdengStatus = 2;
    public static int sLampRixingdengStatus = 3;
    public static int sLampTurnLeftStatus = 4;
    public static int sLampTurnRightStatus = 5;

    public static int[] sCarBackFragmentStatus = new int[4];
    public static int sCarBackBreakLampStatus = 0;
    public static int sCarBackPositionLampStatus = 1;
    public static int sCarBackTurnLeftLampStatus = 2;
    public static int sCarBackTurnRightLampStatus = 3;

    public static int[] sDoorFragmentStatus = new int[5];
    public static int sDoorAntiGlare = 0;
    public static int sDoorLock = 1;
    public static int sDoorMirrorHeat = 2;
    public static int sDoorMirrorLight = 3;
    public static int sDoorMirrorSelect = 4;

    public static int[] sCenterControlStatus = new int[]{120,120,0,0,0,0,0};
    public static int sCenterControlWindAngle   = 0;
    public static int sCenterControlWindPower   = 1;
    public static int sCenterControlDomeLight   = 2;
    public static int sCenterControlFuelTankLock = 3;
    public static int sCenterControlCentralLock = 4;
    public static int sCenterControlWiper       = 5;//有档位？
    public static int sCenterControlSafeBelt    = 6;

    public static int[] sTrunkStatus = new int[]{0};
    public static int sTrunkStatu   = 0;

    public static final String CONNECTION_TYPE = "connection_type";

    public ConstantData(){
/*        sDataMap = new HashMap<Integer,Integer>();
        sDataMap.put(mSeatHeatStatus, 0);
        sDataMap.put(mSeatWindStatus, 0);
        sDataMap.put(mSeatSetStatus, 0);

        sDataMap.put(sLampDadengStatus, 0);
        sDataMap.put(sLampJiaodengStatus, 0);
        sDataMap.put(sLampJinguangdengStatus, 0);
        sDataMap.put(sLampRixingdengStatus, 0);
        sDataMap.put(sLampTurnLeftStatus, 0);
        sDataMap.put(sLampTurnRightStatus, 0);

        sDataMap.put(sCarBackBreakLampStatus, 0);
        sDataMap.put(sCarBackPositionLampStatus, 0);
        sDataMap.put(sCarBackTurnLeftLampStatus, 0);
        sDataMap.put(sCarBackTurnRightLampStatus, 0);*/

    }
}
