package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorGround_L_LightOff extends CMDDoor{
   public CMDDoorMirrorGround_L_LightOff(){
       super();
       payload[3] &= ~(MirrorGround_L_Light);
   }
}
