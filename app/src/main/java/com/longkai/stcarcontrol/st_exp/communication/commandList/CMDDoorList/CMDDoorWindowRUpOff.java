package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorWindowRUpOff extends CMDDoor{
   public CMDDoorWindowRUpOff(){
       super();
       payload[0] &= ~(WindowRUp);
   }
}
