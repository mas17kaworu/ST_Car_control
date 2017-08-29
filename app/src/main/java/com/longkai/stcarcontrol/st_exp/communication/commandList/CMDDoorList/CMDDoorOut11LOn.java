package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorOut11LOn extends CMDDoor{
   public CMDDoorOut11LOn(){
       super();
       payload[3] |= Out11L;
   }
}
