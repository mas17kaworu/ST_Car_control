package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

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
    protected static final byte DRLLight = (byte)0x80;
    protected static final byte Corner = (byte)0x40;
    protected static final byte TurnLeft = (byte)0x20;
    protected static final byte TurnRight = (byte)0x10;
    protected static final byte Position = (byte)0x08;
    protected static final byte HBAll = (byte)0x04;
    protected static final byte LowBeam2 = (byte)0x02;
    protected static final byte LowBeam1 = (byte)0x01;

    protected static final byte AutoCon = (byte)0x40;
    protected static final byte EnergySave = (byte)0x20;
    protected static final byte Parking = (byte)0x10;
    protected static final byte Curve = (byte)0x08;
    protected static final byte Country = (byte)0x04;
    protected static final byte Highway = (byte)0x02;
    protected static final byte Urban = (byte)0x01;

    protected static final byte HBLED8 = (byte)0x80;
    protected static final byte HBLED7 = (byte)0x40;
    protected static final byte HBLED6 = (byte)0x20;
    protected static final byte HBLED5 = (byte)0x10;
    protected static final byte HBLED4 = (byte)0x08;
    protected static final byte HBLED3 = (byte)0x04;
    protected static final byte HBLED2 = (byte)0x02;
    protected static final byte HBLED1 = (byte)0x01;


    protected static byte[] payload = {0x00,0x00,0x00};

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

    protected void refreshDataPayload(){
        data[2] = payload[0];
        data[3] = payload[1];
        data[4] = payload[2];
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        return response;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_LED_HEADLAMP;
    }


    public static class Response extends BaseResponse {
        public Response(byte commandId) {
            super(commandId);
        }
    }
}
