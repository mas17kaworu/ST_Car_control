package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorHeatOn extends CMDDoor{
   public CMDDoorMirrorHeatOn(){
       super();
       payload[2] |= MirrorHeat;
   }
}
