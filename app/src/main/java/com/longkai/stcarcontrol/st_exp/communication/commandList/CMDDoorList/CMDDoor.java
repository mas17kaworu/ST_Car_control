package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2017/8/27.
 */

public class CMDDoor extends BaseCommand{

    protected static final byte UnlockR	= (byte) 0x80;
    protected static final byte LockR	= 0x40;
    protected static final byte UnlockL	= 0x20;
    protected static final byte LockL	= 0x10;
    protected static final byte WindowRUp = 0x08;
    protected static final byte WindowRDown = 0x04;
    protected static final byte WindowLUp = 0x02;
    protected static final byte WindowLDown = 0x01;

    protected static final byte MirrorSelect = 0x40;
    protected static final byte MirrorYup	= 0x20;
    protected static final byte MirrorYDown = 0x10;
    protected static final byte MirrorXLeft = 0x08;
    protected static final byte MirrorXRight= 0x04;
    protected static final byte MirrorUnfold= 0x02;
    protected static final byte MirrorFold	= 0x01;

    protected static final byte MirrorHeat	= 0x40;
    protected static final byte ECVVoltageCode = 0x3f;

    protected static final byte Small_L_Light	= (byte) 0x80;
    protected static final byte MirrorTLLight	= 0x40;
    protected static final byte Out12L			= 0x20;
    protected static final byte Out11L			= 0x10;
    protected static final byte Out10L			= 0x08;
    protected static final byte MirrorBlind_L_Light	= 0x04;
    protected static final byte DoorFoot_L_Light	= 0x02;
    protected static final byte MirrorGround_L_Light = 0x01;

    protected static final byte Small_R_Light	=(byte) 0x80;
    protected static final byte MirrorTRLight	= 0x40;
    protected static final byte Out12R			= 0x20;
    protected static final byte Out11R			= 0x10;
    protected static final byte Out10R			= 0x08;
    protected static final byte MirrorBlind_R_Light	= 0x04;
    protected static final byte DoorFoot_R_Light	= 0x02;
    protected static final byte MirrorGround_R_Light = 0x01;

    protected static final byte OutHS_R	= 0x08;
    protected static final byte Out15_R	= 0x04;
    protected static final byte OutHS_L	= 0x02;
    protected static final byte Out15_L	= 0x01;

    protected static byte[] payload = {0x00,0x00,0x00,0x00,0x00,0x00};

    /**
     *  带dia位的
     *
     */
    public CMDDoor(){
        try{
            data = new byte[8];
            dataLength = 8;
            data[0] = (byte) dataLength;
            data[1] = (byte) 0x80 & COMMAND_DOOR;
            data[2] = payload[0];
            data[3] = payload[1];
            data[4] = payload[2];
            data[5] = payload[3];
            data[6] = payload[4];
            data[7] = payload[5];
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        response.setStatus(data[3]);
        return response;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_DOOR;
    }


    public static class Response extends BaseResponse {

        private int RMotorStatus;
        private int RAntiPinch;
        private int LMotorStatus;
        private int LAntiPinch;

        public Response(byte commandId) {
            super(commandId);
        }

        private void setStatus(byte value){
            RMotorStatus = (value & 0x08) > 0 ? 1 : 0;
            RAntiPinch = (value & 0x04) > 0 ? 1 : 0;
            LMotorStatus = (value & 0x02) > 0 ? 1 : 0;
            LAntiPinch = (value & 0x01) > 0 ? 1 : 0;
        }
        public int getRMotorStatus(){
            return RMotorStatus;
        }

        private int getRAntiPinch() {
            return RAntiPinch;
        }

        public int getLMotorStatus() {
            return LMotorStatus;
        }

        public int getLAntiPinch() {
            return LAntiPinch;
        }
    }
}
