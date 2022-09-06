package com.leon.aicenter.serial;

public class CRC8 {
    public static byte calcCRC(byte[] data) {
        int crc = 0x79;
        int crc_b;
        for (byte datum : data) {
            for (int j = 0; j < 8; j++) {
                crc_b = datum ^ crc;
                if ((crc_b & 1) != 0) {
                    crc ^= 0X18;
                    crc >>= 1;
                    crc |= 0x80;
                } else
                    crc >>= 1;
                datum >>= 1;
            }
        }
        return (byte) crc;
    }
}
