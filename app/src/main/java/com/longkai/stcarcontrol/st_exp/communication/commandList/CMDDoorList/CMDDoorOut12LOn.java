package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorOut12LOn extends CMDDoor{
   public CMDDoorOut12LOn(){
       super();
       payload[3] |= Out12L;
   }
}
