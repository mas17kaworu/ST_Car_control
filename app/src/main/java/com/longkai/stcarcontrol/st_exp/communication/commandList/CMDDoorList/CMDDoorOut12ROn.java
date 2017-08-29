package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorOut12ROn extends CMDDoor{
   public CMDDoorOut12ROn(){
       super();
       payload[4] |= Out12R;
   }
}
