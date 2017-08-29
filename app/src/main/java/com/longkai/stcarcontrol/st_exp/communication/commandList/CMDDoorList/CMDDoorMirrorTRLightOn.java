package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorTRLightOn extends CMDDoor{
   public CMDDoorMirrorTRLightOn(){
       super();
       payload[4] |= MirrorTRLight;
   }
}
