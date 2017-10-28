package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorXLeftOn extends CMDDoor{
   public CMDDoorMirrorXLeftOn(){
       super();
       payload[1] |= MirrorXLeft;
       refreshDataPayload();
   }
}
