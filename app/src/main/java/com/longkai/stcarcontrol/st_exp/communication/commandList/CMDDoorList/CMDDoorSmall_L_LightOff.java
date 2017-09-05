package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorSmall_L_LightOff extends CMDDoor{
   public CMDDoorSmall_L_LightOff(){
       super();
       payload[3] &= ~(Small_L_Light);
       refreshDataPayload();
   }
}
