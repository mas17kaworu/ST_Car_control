package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorBlind_L_LightOff extends CMDDoor{
   public CMDDoorMirrorBlind_L_LightOff(){
       super();
       payload[3] &= ~(MirrorBlind_L_Light);
   }
}
