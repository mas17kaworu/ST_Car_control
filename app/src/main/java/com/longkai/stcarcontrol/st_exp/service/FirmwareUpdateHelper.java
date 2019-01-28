package com.longkai.stcarcontrol.st_exp.service;

import android.os.Handler;
import android.util.Log;

import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDFOTAList.CMDFOTADATA;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.longkai.stcarcontrol.st_exp.Utils.FileUtils.INTERNAL_PATH;

/**
 * Created by Administrator on 2019/1/13.
 */

public class FirmwareUpdateHelper {
    private static final String TAG = "FirmwareUpdateHelper";

    public static final String FIRMWARE_FOLDER = INTERNAL_PATH + "ST_FIRMWARE" + "/";
    public static final String FIRMWARE_A_PATH = FIRMWARE_FOLDER + "firmwareA.bin";
    public static final String FIRMWARE_TEST_PATH = FIRMWARE_FOLDER + "firmwaretest.bin";
    public static final String FIRMWARE_B_PATH = FIRMWARE_FOLDER + "firmwareB.bin";

    private static final int MAX_BYTES_CAPACITY = 1024;

    /**
     * 一次只能更新一个
     *
     */
    private static List<byte[]> fileBytesList = new ArrayList<>();


    public synchronized static List<byte[]> generateBytesList(File sourceFile){
        if (sourceFile.isFile()) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(sourceFile);
                //reset
                fileBytesList = new ArrayList<>();

                byte[] buffer = new byte[MAX_BYTES_CAPACITY];
                int len = 0;// 每次读取到的数据的长度
                while ((len = fis.read(buffer)) != -1) {// len值为-1时，表示没有数据了
                    Log.i(TAG, "longkai test length = " + len);
                    if (len < MAX_BYTES_CAPACITY){
                        byte[] lastArray = new byte[len];
                        System.arraycopy(buffer,0,lastArray,0,len);
                        fileBytesList.add(fileBytesList.size(), lastArray);
                    } else {
                        fileBytesList.add(fileBytesList.size(), buffer);
                    }
                    buffer = new byte[MAX_BYTES_CAPACITY];
                }
                Log.i(TAG, "longkai fileBytesList length = " + fileBytesList.size());

//                byte2File();
                return fileBytesList;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "firmware 文件不存在");
        }

        return fileBytesList;
    }

    /**
     * @param index  start from 0
     * @return
     */
    public synchronized static byte[] getBytesAtNumber(int index){
        return fileBytesList.get(index);
    }

    public synchronized static List<byte[]> getBytes(){
        return fileBytesList;
    }

    private static void byte2File()
    {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try
        {
            File dir = new File(FIRMWARE_FOLDER);
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(FIRMWARE_TEST_PATH);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            for (byte[] bytes:fileBytesList) {
                bos.write(bytes);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (bos != null)
            {
                try
                {
                    bos.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (fos != null)
            {
                try
                {
                    fos.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
