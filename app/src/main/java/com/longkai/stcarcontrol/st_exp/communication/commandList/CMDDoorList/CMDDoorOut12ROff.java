package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorOut12ROff extends CMDDoor{
   public CMDDoorOut12ROff(){
       super();
       payload[4] &= ~(Out12R);
       refreshDataPayload();
   }
}
