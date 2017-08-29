package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorECVVoltageCodeSet extends CMDDoor{
   public CMDDoorECVVoltageCodeSet(){
       super();
       // TODO: 2017/8/29 赋值
       payload[2] |= ECVVoltageCode;
   }
}
