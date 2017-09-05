package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorGround_R_LightOn extends CMDDoor{
   public CMDDoorMirrorGround_R_LightOn(){
       super();
       payload[4] |= MirrorGround_R_Light;
       refreshDataPayload();
   }
}
