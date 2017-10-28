package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorXRightOff extends CMDDoor{
   public CMDDoorMirrorXRightOff(){
       super();
       payload[1] &= ~(MirrorXRight);
       refreshDataPayload();
   }
}
