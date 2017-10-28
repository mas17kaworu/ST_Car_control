import sys
import time
import os
import codecs

baseName="CMDLEDHeadLamp"
srcName=baseName+'.csv'

print ("hello")

import csv

csv_reader=csv.reader(open(srcName,encoding='gb18030'))

bitNum=0;

for filename,second in csv_reader:
    if filename=="":
        bitNum=bitNum+1
    if filename!="key" and filename!="":
        file=codecs.open(baseName+filename+'.java',"w","utf-8")
        li=["package com.longkai.stcarcontrol.st_exp.communication.commandList.",baseName,"List;\n"]
        file.writelines(li)
        li=["public class ",baseName,filename," extends ",baseName, "{\n"]
        file.writelines(li)
        li=["   public ",baseName,filename,"() {\n"]
        file.writelines(li)
        li=["      super();\n"]
        file.writelines(li)
        if bitNum==1:
            li=["      payload[",str(bitNum),"] = 0;\n"]
            file.writelines(li)
        li=["   }\n"]
        file.writelines(li)
        li=["   @Override\n\tpublic void turnOn(){\n"]
        file.writelines(li)
        li=["          payload[",str(bitNum),"] |= ",filename,";\n"]
        file.writelines(li)
        li=["          refreshDataPayload();\n"]
        file.writelines(li)
        li=["   }\n"]
        file.writelines(li)
        li=["   @Override\n\tpublic void turnOff(){\n"]
        file.writelines(li)
        li=["          payload[",str(bitNum),"] &= ~(",filename,");\n"]
        file.writelines(li)
        li=["          refreshDataPayload();\n"]
        file.writelines(li)
        li=["   }\n"]
        file.writelines(li)
        li=["}\n"]
        file.writelines(li)
        file.close()
