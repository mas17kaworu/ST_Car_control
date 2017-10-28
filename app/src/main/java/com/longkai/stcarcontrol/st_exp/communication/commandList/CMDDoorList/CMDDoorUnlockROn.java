package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorUnlockROn extends CMDDoor{
   public CMDDoorUnlockROn(){
       super();
       payload[0] |= UnlockR;
       refreshDataPayload();
   }
}
