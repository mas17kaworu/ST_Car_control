package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorGround_R_LightOff extends CMDDoor{
   public CMDDoorMirrorGround_R_LightOff(){
       super();
       payload[4] &= ~(MirrorGround_R_Light);
       refreshDataPayload();
   }
}
