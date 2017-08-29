package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorTLLightOn extends CMDDoor{
   public CMDDoorMirrorTLLightOn(){
       super();
       payload[3] |= MirrorTLLight;
   }
}
