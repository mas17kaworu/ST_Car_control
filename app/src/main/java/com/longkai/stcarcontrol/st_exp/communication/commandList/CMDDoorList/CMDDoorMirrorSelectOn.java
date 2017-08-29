package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorSelectOn extends CMDDoor{
   public CMDDoorMirrorSelectOn(){
       super();
       payload[1] |= MirrorSelect;
   }
}
