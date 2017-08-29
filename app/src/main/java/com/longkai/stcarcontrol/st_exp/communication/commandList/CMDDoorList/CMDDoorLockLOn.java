package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorLockLOn extends CMDDoor{
   public CMDDoorLockLOn(){
       super();
       payload[0] |= LockL;
   }
}
