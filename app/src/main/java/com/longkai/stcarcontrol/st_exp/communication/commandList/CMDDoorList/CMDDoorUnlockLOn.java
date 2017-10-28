package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorUnlockLOn extends CMDDoor{
   public CMDDoorUnlockLOn(){
       super();
       payload[0] |= UnlockL;
       refreshDataPayload();
   }
}
