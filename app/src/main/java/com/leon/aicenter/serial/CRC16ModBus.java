package com.leon.aicenter.serial;

public class CRC16ModBus {
    public static byte[] calcCRC(byte[] data) {
        int crcValue = 0xFFFF;
        for (byte datum : data) {
            crcValue ^= Byte.toUnsignedInt(datum) & 0xFFFF;
            for (int j = 0; j < 8; j++) {
                if ((crcValue & 0x0001) != 0) {
                    crcValue >>= 1;
                    crcValue ^= 0xA001;
                } else {
                    crcValue >>= 1;
                }
            }
        }
        byte[] crcArr = new byte[2];
        crcArr[0] = (byte) (crcValue & 0xFF);
        crcArr[1] = (byte) (crcValue >> 8);
        return crcArr;
    }
}
