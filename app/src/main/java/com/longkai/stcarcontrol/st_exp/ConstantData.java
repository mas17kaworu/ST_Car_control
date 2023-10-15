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

    public static int[] sDoorFragmentStatus = new int[13];
    public static int sDoorAntiGlare = 0;
    public static int sDoorLeftLock = 1;
    public static int sDoorRightLock = 2;
    public static int sDoorMirrorHeat = 3;
    public static int sDoorMirrorLeftLight = 4;
    public static int sDoorMirrorRightLight = 5;
    public static int sDoorMirrorSelect = 6; //默认0 左边；1代表右边
    public static int sDoorFadeZoneLeftLamp = 7;
    public static int sDoorFadeZoneRightLamp = 8;
    public static int sDoorGroundLeftLamp = 9;
    public static int sDoorGroundRightLamp = 10;
    public static int sDoorFootLeftLamp = 11;
    public static int sDoorFootRightLamp = 12;


    public static int[] sCenterControlStatus = new int[]{120,120,0,0,0,0,0};
    public static int sCenterControlWindAngle   = 0;
    public static int sCenterControlWindPower   = 1;
    public static int sCenterControlDomeLight   = 2;
    public static int sCenterControlFuelTankLock = 3;
    public static int sCenterControlCentralLock = 4;
    public static int sCenterControlWiper       = 5;//0关 1低 2高
    public static int sCenterControlSafeBelt    = 6;

    public static int[] sBackOLEDStatus = new int[9];
    public static int sBackOLEDReverse = 0;
    public static int sBackOLEDBreak = 1;
    public static int sBackOLEDPosition = 2;
    public static int sBackOLEDTurnLeft = 3;
    public static int sBackOLEDTurnRight = 4;
    public static int sBackOLEDAuto1 = 5;
    public static int sBackOLEDAuto2 = 6;
    public static int sBackOLEDAuto3 = 7;
    public static int sBackOLEDStopOLED = 8;

    public static boolean sVCUJDQ1State,sVCUJDQ2State,sVCUJDQ3State;

    public static int[] sTrunkStatus = new int[]{0};
    public static int sTrunkStatu   = 0;

    public static final String CONNECTION_TYPE = "connection_type";

    public static final String BCM_DIAGRAM = "ic_Diagram_BCM.png";
    public static final String DOOR_DIAGRAM = "ic_Diagram_Door.png";
    public static final String HL_DIAGRAM = "ic_Diagram_HL.png";
    public static final String HVAC_DIAGRAM = "ic_Diagram_HVAC.png";
    public static final String PLCM_DIAGRAM = "ic_Diagram_PLCM.png";
    public static final String POWER_SEAT_DIAGRAM = "ic_Diagram_Power_seat.png";

    //BMS

    public static final int FRAGMENT_TRANSACTION_BMS_HOME = 0;

    //VCU
    //VCU
    public static final int FRAGMENT_TRANSACTION_HOME = 0;
    public static final int FRAGMENT_TRANSACTION_TMP = FRAGMENT_TRANSACTION_HOME + 1;
    public static final int FRAGMENT_TRANSACTION_VCUVCU = FRAGMENT_TRANSACTION_TMP + 1;
    public static final int FRAGMENT_TRANSACTION_OBC_DEMO = FRAGMENT_TRANSACTION_VCUVCU + 1;
    public static final int FRAGMENT_TRANSACTION_BMS = FRAGMENT_TRANSACTION_OBC_DEMO + 1;
    public static final int FRAGMENT_TRANSACTION_MCU = FRAGMENT_TRANSACTION_BMS + 1;
    public static final int FRAGMENT_TRANSACTION_TBOX = FRAGMENT_TRANSACTION_MCU + 1;

    public static final int FRAGMENT_TRANSACTION_GYHLSD = FRAGMENT_TRANSACTION_TBOX + 1;
    public static final int FRAGMENT_TRANSACTION_CHARGE = FRAGMENT_TRANSACTION_GYHLSD + 1;
    public static final int FRAGMENT_TRANSACTION_TORQUE = FRAGMENT_TRANSACTION_CHARGE + 1;
    public static final int FRAGMENT_TRANSACTION_MONITOR = FRAGMENT_TRANSACTION_TORQUE + 1;
    public static final int FRAGMENT_TRANSACTION_OBC = FRAGMENT_TRANSACTION_MONITOR + 1;


    public static final int FRAGMENT_TRANSACTION_UPDATE_FIRMWARE = 104;

    public static class MainFragment{
      public static final int FRAGMENT_TRANSACTION_NFC = 5;
      public static final int FRAGMENT_TRANSACTION_CAR_BACK = 6;
      public static final int FRAGMENT_TRANSACTION_CAR_BACK_COVER = 7;
      public static final int FRAGMENT_TRANSACTION_AVAS = 8;
      public static final int FRAGMENT_TRANSACTION_SOUND = 9;
      public static final int FRAGMENT_TRANSACTION_KEY_PAIR = 10;

      public static final int FRAGMENT_TRANSACTION_CAR_BACK_OLED = 105;
      public static final int FRAGMENT_TRANSACTION_CAR_BACK_OLED2 = 106;
      public static final int FRAGMENT_TRANSACTION_CAR_FRONT_BOTTOM_LIGHT = 107;
    }

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
