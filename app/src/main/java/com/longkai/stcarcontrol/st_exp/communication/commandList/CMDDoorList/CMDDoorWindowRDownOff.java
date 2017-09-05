package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorWindowRDownOff extends CMDDoor{
   public CMDDoorWindowRDownOff(){
       super();
       payload[0] &= ~(WindowRDown);
       refreshDataPayload();
   }
}
