package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorUnfoldOff extends CMDDoor{
   public CMDDoorMirrorUnfoldOff(){
       super();
       payload[1] &= ~(MirrorUnfold);
   }
}
