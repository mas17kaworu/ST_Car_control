package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorYDownOff extends CMDDoor{
   public CMDDoorMirrorYDownOff(){
       super();
       payload[1] &= ~(MirrorYDown);
       refreshDataPayload();
   }
}
