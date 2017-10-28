package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorFoldOff extends CMDDoor{
   public CMDDoorMirrorFoldOff(){
       super();
       payload[1] &= ~(MirrorFold);
       refreshDataPayload();
   }
}
