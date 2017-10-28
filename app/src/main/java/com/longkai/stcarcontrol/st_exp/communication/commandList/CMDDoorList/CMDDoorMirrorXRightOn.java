package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorXRightOn extends CMDDoor{
   public CMDDoorMirrorXRightOn(){
       super();
       payload[1] |= MirrorXRight;
       refreshDataPayload();
   }
}
