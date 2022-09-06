package com.leon.aicenter.serial;

import java.util.Arrays;

public class SerialCommand {
    private static byte sn = 1;
    public static final byte CMD_PREFIX = (byte) 0x55;
    public static final byte CMD_SUFFIX = (byte) 0xFF;
    public static final byte DATA_PREFIX = (byte) 0x02;
    public static final byte DATA_SUFFIX = (byte) 0xFF;
    public static final byte CODE_GW_SEARCH = (byte) 0x01;
    public static final byte CODE_GW_REQUIRE = (byte) 0x02;
    public static final byte CODE_GW_FINISHED = (byte) 0x16;
    public static final byte CODE_SND_REGISTER = (byte) 0x01;
    public static final byte CODE_SND_UNREGISTER = (byte) 0x02;
    public static final byte CODE_SND_CONTROL = (byte) 0x03;
    public static final byte CODE_SND_STATUS = (byte) 0x04;
    public static final byte CODE_SND_REQUIRE = (byte) 0x05;
    public static final byte CODE_SND_SCENE_SET = (byte) 0x06;
    public static final byte CODE_SND_SCENE_GET = (byte) 0x07;
    public static final byte CODE_SND_SCENE_DEL = (byte) 0x08;
    public static final byte CODE_SND_SCENE_EXEC = (byte) 0x09;
    public static final byte CODE_SND_DEVICE_CONFIG = (byte) 0x0A;
    public static final byte CODE_SND_TOGGLE = (byte) 0x0F;
    public static final byte CODE_SND_TEMPERATURE_CONTROL = (byte) 0x18;
    public static final byte CODE_SND_GW_MAC_SET = (byte) 0x7E;
    public static final byte CODE_SND_GW_MAC_GET = (byte) 0x7F;
    public static final byte CODE_RCV_REGISTER = (byte) 0x81;
    public static final byte CODE_RCV_UNREGISTER = (byte) 0x82;
    public static final byte CODE_RCV_CONTROL = (byte) 0x83;
    public static final byte CODE_RCV_STATUS = (byte) 0x84;
    public static final byte CODE_RCV_REQUIRE = (byte) 0x85;
    public static final byte CODE_RCV_SCENE_SET = (byte) 0x86;
    public static final byte CODE_RCV_SCENE_GET = (byte) 0x87;
    public static final byte CODE_RCV_SCENE_DEL = (byte) 0x88;
    public static final byte CODE_RCV_DEVICE_CONFIG = (byte) 0x8A;
    public static final byte CODE_RCV_TEMPERATURE_CONTROL = (byte) 0x98;
    public static final byte CODE_RCV_GW_MAC_SET = (byte) 0xFE;
    public static final byte CODE_RCV_GW_MAC_GET = (byte) 0xFF;
    public static final byte CODE_FLAG_TO_DEVICE = (byte) 0xBB;
    public static final byte CODE_FLAG_TO_LOW_DEVICE = (byte) 0xEE;
    public static final byte CODE_FLAG_FROM_DEVICE = (byte) 0xAA;
    public static final byte CODE_FLAG_REG_FROM_DEVICE = (byte) 0xBC;
    public static final byte POWER_LOW = (byte) 0x40;
    public static final byte POWER_NORMAL = (byte) 0xC0;
    public static final int CODE_TEMPERATURE_SET = 1;
    public static final int CODE_TEMPERATURE_SWITCH = 2;
    public static final int CODE_TEMPERATURE_MODE = 3;
    public static final int CODE_TEMPERATURE_FAN = 5;
    public static final byte CODE_TEMPERATURE_READ = 0x03;
    public static final byte CODE_TEMPERATURE_WRITE = 0x06;
    // 0     1   2 3~8 9~10  11   12    13~14   15
    //帧头 总长度 SN MAC 类型 功耗 数据帧头 数据长度 命令码 数据内容 数据CRC 数据帧尾 CRC 帧尾
    public static final int RETURN_COMMAND_CODE_AT = 15;

    public static boolean checkData(byte[] bytes) {
        return bytes.length >= 4 && bytes[0] == CMD_PREFIX && bytes[bytes.length - 1] == CMD_SUFFIX
                && bytes[1] == bytes.length - 4
                && bytes[bytes.length - 2] == CRC8.calcCRC(Arrays.copyOfRange(bytes, 0, bytes.length - 2));
    }

    public static byte[] makeCmdData(byte code, byte[] mac, byte[] type, byte[] data) {
        int len = data.length + 1;
        byte[] dataPackage = new byte[]{DATA_PREFIX, (byte) (len & 0xFF), (byte) ((len >> 8) & 0xFF), code, (byte) 0x00, DATA_SUFFIX};
        if (data.length != 0) {
            byte[] tmp = new byte[dataPackage.length + data.length];
            System.arraycopy(dataPackage, 0, tmp, 0, dataPackage.length - 2);
            System.arraycopy(data, 0, tmp, dataPackage.length - 2, data.length);
            System.arraycopy(dataPackage, dataPackage.length - 2, tmp, dataPackage.length - 2 + data.length, 2);
            dataPackage = tmp;
        }
        dataPackage[dataPackage.length - 2] = CRC8.calcCRC(Arrays.copyOfRange(dataPackage, 0, dataPackage.length - 2));
        byte[] cmd = new byte[5 + mac.length + type.length + dataPackage.length];
        cmd[0] = CMD_PREFIX;
        cmd[1] = (byte) (cmd.length - 4);
        cmd[2] = sn++;
        System.arraycopy(mac, 0, cmd, 3, mac.length);
        System.arraycopy(type, 0, cmd, 3 + mac.length, type.length);
        System.arraycopy(dataPackage, 0, cmd, 3 + mac.length + type.length, dataPackage.length);
        cmd[cmd.length - 2] = CRC8.calcCRC(Arrays.copyOfRange(cmd, 0, cmd.length - 2));
        cmd[cmd.length - 1] = CMD_SUFFIX;
        return cmd;
    }

    public static byte[] makeTemperatureCmd(byte address, boolean read, int code, int data) {
        byte[] cmd = new byte[8];
        cmd[0] = address;
        cmd[1] = (byte) (read ? CODE_TEMPERATURE_READ : CODE_TEMPERATURE_WRITE);
        cmd[2] = (byte) ((code >> 8) & 0x0FF);
        cmd[3] = (byte) (code & 0x0FF);
        cmd[4] = (byte) ((data >> 8) & 0x0FF);
        cmd[5] = (byte) (data & 0x0FF);
        byte[] crc = CRC16ModBus.calcCRC(Arrays.copyOfRange(cmd, 0, 6));
        cmd[6] = crc[0];
        cmd[7] = crc[1];
        return cmd;
    }
}
