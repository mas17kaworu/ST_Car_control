package com.longkai.stcarcontrol.st_exp.bluetoothComm;

public class CrcUtils {
    static char[] CRC8T =
            {0, 7, 14, 9, 28, 27, 18, 21, 56, 63, 54, 49, 36, 35, 42, 45, 112, 119, 126, 121, 108, 107,
            98, 101, 72, 79, 70, 65, 84, 83, 90, 93, 224, 231, 238, 233, 252, 251, 242, 245, 216, 223, 214, 209, 196,
            195, 202, 205, 144, 151, 158, 153, 140, 139, 130, 133, 168, 175, 166, 161, 180, 179, 186, 189, 199, 192,
            201, 206, 219, 220, 213, 210, 255, 248, 241, 246, 227, 228, 237, 234, 183, 176, 185, 190, 171, 172, 165,
            162, 143, 136, 129, 134, 147, 148, 157, 154, 39, 32, 41, 46, 59, 60, 53, 50, 31, 24, 17, 22, 3, 4, 13, 10,
            87, 80, 89, 94, 75, 76, 69, 66, 111, 104, 97, 102, 115, 116, 125, 122, 137, 142, 135, 128, 149, 146, 155,
            156, 177, 182, 191, 184, 173, 170, 163, 164, 249, 254, 247, 240, 229, 226, 235, 236, 193, 198, 207, 200,
            221, 218, 211, 212, 105, 110, 103, 96, 117, 114, 123, 124, 81, 86, 95, 88, 77, 74, 67, 68, 25, 30, 23, 16,
            5, 2, 11, 12, 33, 38, 47, 40, 61, 58, 51, 52, 78, 73, 64, 71, 82, 85, 92, 91, 118, 113, 120, 127, 106, 109,
            100, 99, 62, 57, 48, 55, 34, 37, 44, 43, 6, 1, 8, 15, 26, 29, 20, 19, 174, 169, 160, 167, 178, 181, 188,
            187, 150, 145, 152, 159, 138, 141, 132, 131, 222, 217, 208, 215, 194, 197, 204, 203, 230, 225, 232, 239,
            250, 253, 244, 243};

    public static void main(String[] args) {
        //x^8+x^2+x+1 -> 1 0000 0111 -> 0000 0111 -> 0x07
        final byte GX = 0x07;
        byte[] table = new byte[256];
        for (int i8 = 0; i8 < 256; i8++) {
            int nData8 = i8;
            int nAccum8 = 0;
            for (int j32 = 0; j32 < 8; j32++) {
                if (((nData8 ^ nAccum8) & 0x80) != 0)
                    nAccum8 = (nAccum8 << 1) ^ GX;// 多项式
                else
                    nAccum8 <<= 1;
                nData8 <<= 1;
            }
            table[i8] = (byte) nAccum8;
        }
        for (int i = 0; i < 256; i++) {
            System.out.print((table[i] & 0xff));
            if (i % 10 == 9) {
                System.out.println();
            } else {
                System.out.print(",");
            }
        }
    }

    /**
     * crc8  多项式:x8+x2+x+1
     *
     * @param buffer
     * @return
     */
    public static int crc8(byte[] buffer) {
        char crc = 0;
        char i;
        for (byte b : buffer) {
            for (i = 0x80; i != 0; i /= 2) {
                if ((crc & 0x80) != 0) {
                    crc *= 2;
                    crc ^= 0x07;
                } else {
                    crc *= 2;
                }
                if ((b & i) != 0) {
                    crc ^= 0x07;
                }
            }
        }
        byte crci = (byte) crc;
        return crci;
    }

    /**
     * crc8  多项式:x8+x2+x+1
     *
     * @param bs
     * @return
     */
    public static byte crc8Table(byte[] bs) {
        char crc8 = 0;
        for (byte b : bs) {
            crc8 = CRC8T[crc8 ^ b & 0xff];
        }
        return (byte) crc8;
    }

    public static byte crc8Table(byte[] buf, int off, int len) {
        char crc8 = 0;
        for (int i = off; i < off + len; i++) {
            byte b = buf[i];
            crc8 = CRC8T[(crc8 ^ b) & 0xff];
        }
        return (byte) crc8;
    }

}
