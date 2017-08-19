package com.longkai.stcarcontrol.st_exp.communication.commandList;

/**
 *
 *
 * Created by Administrator on 2017/8/15.
 */

public class CMDLEDHeadLamp extends BaseCommand {

    /**
     *          b7          b6          b5          b4          b3          b3          b1          b0
     * Code1:   DRL light   Corner      Turn Left   Turn Right  Position    HB all      Low Beam2   Low Beam1
     *
     *
     * Code2:               Auto con    Energy save Parking     Curve       Country     Highway     Urban
     *
     *
     * Code3:   HB LED8     HB LED7     HB LED6     HB LED5     HB LED4     HB LED3     HB LED2     HB LED1
     *
     */
    private static final byte DRLLight = (byte)0x80;
    private static final byte Corner = (byte)0x40;
    private static final byte TurnLeft = (byte)0x20;
    private static final byte TurnRight = (byte)0x10;
    private static final byte Position = (byte)0x08;
    private static final byte HBAll = (byte)0x04;
    private static final byte LowBeam2 = (byte)0x02;
    private static final byte LowBeam1 = (byte)0x01;

    private static final byte AutoCon = (byte)0x40;
    private static final byte EnergySave = (byte)0x20;
    private static final byte Parking = (byte)0x10;
    private static final byte Curve = (byte)0x08;
    private static final byte Country = (byte)0x04;
    private static final byte Hightway = (byte)0x02;
    private static final byte Urban = (byte)0x01;

    private static final byte HBLED8 = (byte)0x08;
    private static final byte HBLED7 = (byte)0x07;
    private static final byte HBLED6 = (byte)0x06;
    private static final byte HBLED5 = (byte)0x05;
    private static final byte HBLED4 = (byte)0x04;
    private static final byte HBLED3 = (byte)0x03;
    private static final byte HBLED2 = (byte)0x02;
    private static final byte HBLED1 = (byte)0x01;


    public static byte[] payload = {0x00,0x00,0x00};

    public CMDLEDHeadLamp(){
        try{
            data = new byte[5];
            dataLength = 5;
            data[0] = 0x05;
            data[1] = COMMAND_LED_HEADLAMP;
            data[2] = payload[0];
            data[3] = payload[1];
            data[4] = payload[2];
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void DRLLightOn(){
        payload[0]|=DRLLight;
    }

    public static void DRLLightOff(){
        payload[0]&= (~DRLLight);
    }

    public static void CornerOn(){
        payload[0]|= Corner;
    }

    public static void CornerOff(){
        payload[0]&= (~Corner);
    }


    public static void TurnLeftOn(){
        payload[0]|= TurnLeft;
    }

    public static void TurnLeftOff(){
        payload[0]&= (~TurnLeft);
    }


    public static void TurnRightOn(){
        payload[0]|= TurnRight;
    }

    public static void TurnRightOff(){
        payload[0]&= (~TurnRight);
    }

    public static void PositionOn(){
        payload[0]|= Position;
    }

    public static void PositionOff(){
        payload[0]&= (~Position);
    }





    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        return null;
    }

    @Override
    public byte getCommandId() {
        return 0;
    }
}
