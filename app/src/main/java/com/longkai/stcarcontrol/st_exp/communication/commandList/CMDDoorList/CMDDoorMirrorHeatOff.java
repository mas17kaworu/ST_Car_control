package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorHeatOff extends CMDDoor{
   public CMDDoorMirrorHeatOff(){
       super();
       payload[2] &= ~(MirrorHeat);
       refreshDataPayload();
   }
}
