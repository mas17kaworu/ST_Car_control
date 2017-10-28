package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorSelectOff extends CMDDoor{
   public CMDDoorMirrorSelectOff(){
       super();
       payload[1] &= ~(MirrorSelect);
       refreshDataPayload();
   }
}
