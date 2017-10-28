package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorBlind_R_LightOff extends CMDDoor{
   public CMDDoorMirrorBlind_R_LightOff(){
       super();
       payload[4] &= ~(MirrorBlind_R_Light);
       refreshDataPayload();
   }
}
