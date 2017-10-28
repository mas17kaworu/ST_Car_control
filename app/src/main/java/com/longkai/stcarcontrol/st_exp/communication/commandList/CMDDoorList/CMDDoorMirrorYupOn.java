package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorYupOn extends CMDDoor{
   public CMDDoorMirrorYupOn(){
       super();
       payload[1] |= MirrorYup;
       refreshDataPayload();
   }
}
