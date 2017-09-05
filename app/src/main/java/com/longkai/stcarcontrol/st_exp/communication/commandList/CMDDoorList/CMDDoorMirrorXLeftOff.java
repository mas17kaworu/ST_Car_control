package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorXLeftOff extends CMDDoor{
   public CMDDoorMirrorXLeftOff(){
       super();
       payload[1] &= ~(MirrorXLeft);
       refreshDataPayload();
   }
}
