package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2017/8/27.
 */

public class CMDPowerSeat extends BaseCommand{
    /**
     *          b7          b6          b5          b4          b3          b3          b1          b0
     * Code1:
     *
     *
     * Code2:
     *
     *
     * Code3:
     *
     */
    protected static final byte WaistUp = (byte)0x20;
    protected static final byte WaistDown = (byte)0x10;
    protected static final byte WaistOrward = (byte)0x08;
    protected static final byte WaistBackward = (byte)0x04;
    protected static final byte BackrestBackward = (byte)0x02;
    protected static final byte BackrestForward = (byte)0x01;

    protected static final byte SeatFormerUp = (byte)0x20;
    protected static final byte SearFormerDown = (byte)0x10;
    protected static final byte SeatPitchUp = (byte)0x08;
    protected static final byte SeatPitchDown = (byte)0x04;
    protected static final byte SeatFoward = (byte)0x02;
    protected static final byte SeatBackward = (byte)0x01;

    protected static final byte Ventilation = (byte)0x10;
    protected static final byte VentilationLevel = (byte)0x0C;
    protected static final byte HeatCode = (byte)0x03;


    protected static final byte PositionKey3 = (byte)0x08;
    protected static final byte PositionKey2 = (byte)0x04;
    protected static final byte PositionKey1 = (byte)0x02;
    protected static final byte PositionKeySet = (byte)0x01;


    protected static byte[] payload = {0x00,0x00,0x00,0x00};


    public CMDPowerSeat(){
        try{
            data = new byte[6];
            dataLength = 6;
            data[0] = 0x06;
            data[1] = COMMAND_POWER_SEAT;
            data[2] = payload[0];
            data[3] = payload[1];
            data[4] = payload[2];
            data[5] = payload[3];
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    protected void refreshDataPayload(){
        data[2] = payload[0];
        data[3] = payload[1];
        data[4] = payload[2];
        data[5] = payload[3];
    }
    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        return response;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_POWER_SEAT;
    }


    public static class Response extends BaseResponse {
        public Response(byte commandId) {
            super(commandId);
        }
    }
}
