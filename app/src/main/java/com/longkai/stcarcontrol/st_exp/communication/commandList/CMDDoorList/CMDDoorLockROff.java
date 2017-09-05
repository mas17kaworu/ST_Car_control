package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorLockROff extends CMDDoor{
   public CMDDoorLockROff(){
       super();
       payload[0] &= ~(LockR);
       refreshDataPayload();
   }
}
