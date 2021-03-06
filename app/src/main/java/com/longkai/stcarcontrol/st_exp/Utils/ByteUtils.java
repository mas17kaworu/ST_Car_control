package com.longkai.stcarcontrol.st_exp.Utils;

/**
 * Created by Administrator on 2017/10/21.
 */

public class ByteUtils {
    public static String bytes2hex(byte[] bytes)
    {
        final String HEX = "0123456789abcdef";
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        int i = 0;
        for (byte b : bytes)
        {
            i ++;
            // 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt((b >> 4) & 0x0f));
            // 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt(b & 0x0f));

            sb.append(" ");

            if (i>100) {
                sb.append("...");
                break;
            }
        }

        return sb.toString();
    }
}
