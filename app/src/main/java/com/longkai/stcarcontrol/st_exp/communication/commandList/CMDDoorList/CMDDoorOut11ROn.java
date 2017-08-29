package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorOut11ROn extends CMDDoor{
   public CMDDoorOut11ROn(){
       super();
       payload[4] |= Out11R;
   }
}
