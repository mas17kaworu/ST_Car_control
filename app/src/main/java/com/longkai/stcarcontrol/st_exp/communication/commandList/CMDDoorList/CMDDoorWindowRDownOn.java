package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorWindowRDownOn extends CMDDoor{
   public CMDDoorWindowRDownOn(){
       super();
       payload[0] |= WindowRDown;
       refreshDataPayload();
   }
}
