package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorOut10LOn extends CMDDoor{
   public CMDDoorOut10LOn(){
       super();
       payload[3] |= Out10L;
   }
}
