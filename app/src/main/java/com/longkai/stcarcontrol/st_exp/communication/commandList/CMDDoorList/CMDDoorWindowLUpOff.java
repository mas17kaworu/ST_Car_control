package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorWindowLUpOff extends CMDDoor{
   public CMDDoorWindowLUpOff(){
       super();
       payload[0] &= ~(WindowLUp);
       refreshDataPayload();
   }
}
