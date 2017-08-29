package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorBlind_R_LightOn extends CMDDoor{
   public CMDDoorMirrorBlind_R_LightOn(){
       super();
       payload[4] |= MirrorBlind_R_Light;
   }
}
