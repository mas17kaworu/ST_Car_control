import sys
import time
import os
import codecs

baseName="CMDVCUControl"
srcName="CMDVCUControl.csv"

print ("hello")

import csv

csv_reader=csv.reader(open(srcName,encoding='gb18030'))

bitNum=0;

for filename,second in csv_reader:
    if filename=="":
        bitNum=bitNum+1
    if filename!="key" and filename!="":
        file=codecs.open(baseName+filename+'On.java',"w","utf-8")
        li=["package com.longkai.stcarcontrol.st_exp.communication.commandList.",baseName,"List;\n"]
        file.writelines(li)
        li=["public class ",baseName,filename,"On extends ",baseName, "{\n"]
        file.writelines(li)
        li=["   public ",baseName,filename,"On(){\n"]
        file.writelines(li)
        li=["       super();\n"]
        file.writelines(li)
        li=["       payload[",str(bitNum),"] |= ",filename,";\n"]
        file.writelines(li)
        li=["       refreshDataPayload();\n"]
        file.writelines(li)
        li=["   }\n"]
        file.writelines(li)
        li=["}\n"]
        file.writelines(li)
        file.close()

        file=codecs.open(baseName+filename+'Off.java',"w","utf-8")
        li=["package com.longkai.stcarcontrol.st_exp.communication.commandList.",baseName,"List;\n"]
        file.writelines(li)
        li=["public class ",baseName,filename,"Off extends ",baseName, "{\n"]
        file.writelines(li)
        li=["   public ",baseName,filename,"Off(){\n"]
        file.writelines(li)
        li=["       super();\n"]
        file.writelines(li)
        li=["       payload[",str(bitNum),"] &= ~(",filename,");\n"]
        file.writelines(li)
        li=["       refreshDataPayload();\n"]
        file.writelines(li)
        li=["   }\n"]
        file.writelines(li)
        li=["}\n"]
        file.writelines(li)
        file.close()
