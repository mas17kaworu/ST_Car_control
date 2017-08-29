package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorOut10ROn extends CMDDoor{
   public CMDDoorOut10ROn(){
       super();
       payload[4] |= Out10R;
   }
}
