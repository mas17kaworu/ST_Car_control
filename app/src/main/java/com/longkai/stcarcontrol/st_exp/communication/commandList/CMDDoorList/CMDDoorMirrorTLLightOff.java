package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorTLLightOff extends CMDDoor{
   public CMDDoorMirrorTLLightOff(){
       super();
       payload[3] &= ~(MirrorTLLight);
   }
}
