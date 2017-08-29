package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorUnfoldOn extends CMDDoor{
   public CMDDoorMirrorUnfoldOn(){
       super();
       payload[1] |= MirrorUnfold;
   }
}
