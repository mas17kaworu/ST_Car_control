package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorTRLightOff extends CMDDoor{
   public CMDDoorMirrorTRLightOff(){
       super();
       payload[4] &= ~(MirrorTRLight);
       refreshDataPayload();
   }
}
