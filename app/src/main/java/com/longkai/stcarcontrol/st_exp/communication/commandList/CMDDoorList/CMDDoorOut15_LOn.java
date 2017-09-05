package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorOut15_LOn extends CMDDoor{
   public CMDDoorOut15_LOn(){
       super();
       payload[5] |= Out15_L;
       refreshDataPayload();
   }
}
