package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorYDownOn extends CMDDoor{
   public CMDDoorMirrorYDownOn(){
       super();
       payload[1] |= MirrorYDown;
       refreshDataPayload();
   }
}
