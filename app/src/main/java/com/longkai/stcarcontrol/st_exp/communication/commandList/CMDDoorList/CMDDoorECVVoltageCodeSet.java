package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorECVVoltageCodeSet extends CMDDoor{
   public CMDDoorECVVoltageCodeSet(int value){
       super();
       //先清零
       payload[2] &= (~ECVVoltageCode);
       payload[2] |= (ECVVoltageCode & value);
   }
}
