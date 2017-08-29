package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorBlind_L_LightOn extends CMDDoor{
   public CMDDoorMirrorBlind_L_LightOn(){
       super();
       payload[3] |= MirrorBlind_L_Light;
   }
}
