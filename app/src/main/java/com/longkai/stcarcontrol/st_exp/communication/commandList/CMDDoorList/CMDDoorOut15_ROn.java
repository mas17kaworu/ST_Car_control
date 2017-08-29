package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorOut15_ROn extends CMDDoor{
   public CMDDoorOut15_ROn(){
       super();
       payload[5] |= Out15_R;
   }
}
