package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorOutHS_ROff extends CMDDoor{
   public CMDDoorOutHS_ROff(){
       super();
       payload[5] &= ~(OutHS_R);
   }
}
