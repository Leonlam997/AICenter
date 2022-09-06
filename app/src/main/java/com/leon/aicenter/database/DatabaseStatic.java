package com.leon.aicenter.database;

public class DatabaseStatic {
    public final static String DATABASE_NAME = "Devices.db";
    public final static int DATABASE_VERSION = 2;
    public final static String DEFAULT_GATEWAY = "_Default_Gateway_";

    //房间数据库
    public static class Room {
        public final static String ID = "_id";
        public final static String NAME = "name";//房间名，如客厅，主卧
        public final static String DESCRIPTION = "description";//备注
        public final static int COL_ID = 0;
        public final static int COL_NAME = 1;
        public final static int COL_DESCRIPTION = 2;
    }

    //设备供应商数据库
    public static class Vendor {
        public final static String ID = "_id";
        public final static String NAME = "name";//供应商名称，如小米，华为
        public final static String DESCRIPTION = "description";//备注
        public final static int COL_ID = 0;
        public final static int COL_NAME = 1;
        public final static int COL_DESCRIPTION = 2;
    }

    //设备类型数据库
    public static class Device {
        public final static String ID = "_id";
        public final static String NAME = "name";//设备名称，如开关，门锁
        public final static String TYPE = "type";//设备类型，如01代表一位开关，02代表两位开关
        public final static String BUTTON_AMOUNT = "button_amount";
        public final static String SCENE_AMOUNT = "scene_amount";
        public final static String VENDOR_ID = "vendor_id";
        public final static String DESCRIPTION = "description";//备注
        public final static String KIND = "kind";
        public final static String PICTURE = "picture";
        public final static int COL_ID = 0;
        public final static int COL_NAME = 1;
        public final static int COL_TYPE = 2;
        public final static int COL_BUTTON_AMOUNT = 3;
        public final static int COL_SCENE_AMOUNT = 4;
        public final static int COL_VENDOR_ID = 5;
        public final static int COL_DESCRIPTION = 6;
        public final static int COL_KIND = 7;
        public final static int COL_PICTURE = 8;
    }

    //命令数据库
    public static class Task {
        public final static String ID = "_id";
        public final static String SCENE_ID = "scene_id";
        public final static String DEVICE_ID = "dev_id";
        public final static String ACTION = "action";//命令类型，如01代表开，02代表关
        public final static String INSTRUCTION = "instruction";
        public final static int COL_ID = 0;
        public final static int COL_SCENE_ID = 1;
        public final static int COL_DEVICE_ID = 2;
        public final static int COL_ACTION = 3;
        public final static int COL_INSTRUCTION = 4;
    }

    //拥有设备数据库
    public static class OwnedDevices {
        public final static String ID = "_id";
        public final static String NAME = "name";//设备名称，如客厅吊灯，主卧空调
        public final static String MAC = "mac";
        public final static String CHANNEL = "channel";
        public final static String POWER = "power";
        public final static String PICTURE = "picture";
        public final static String ROOM_ID = "room_id";
        public final static String DEVICE_ID = "dev_id";
        public final static int COL_ID = 0;
        public final static int COL_NAME = 1;
        public final static int COL_MAC = 2;
        public final static int COL_CHANNEL = 3;
        public final static int COL_POWER = 4;
        public final static int COL_PICTURE = 5;
        public final static int COL_DEVICE_ID = 6;
        public final static int COL_ROOM_ID = 7;
    }

    //情景模式数据库
    public static class Scene {
        public final static String ID = "_id";
        public final static String NAME = "name";//模式名称，如回家模式，离家模式
        public final static String MAC = "mac";
        public final static String TYPE = "type";//模式糊弄，如01代表本地情景，02代表语音情景，03代表情景按键
        public final static String KEYWORDS = "keywords";//语音命令，如我回来了，晚安
        public final static String CHANNEL = "channel";
        public final static String POWER = "power";
        public final static String PICTURE = "picture";
        public final static String DEVICE_ID = "dev_id";
        public final static String ROOM_ID = "room_id";
        public final static int COL_ID = 0;
        public final static int COL_NAME = 1;
        public final static int COL_MAC = 2;
        public final static int COL_TYPE = 3;
        public final static int COL_KEYWORDS = 4;
        public final static int COL_CHANNEL = 5;
        public final static int COL_POWER = 6;
        public final static int COL_PICTURE = 7;
        public final static int COL_DEVICE_ID = 8;
        public final static int COL_ROOM_ID = 9;
    }

    public final static String TABLE_VENDOR = "vendor";
    public final static String TABLE_ROOM = "room";
    public final static String TABLE_DEVICE = "device";
    public final static String TABLE_TASK = "task";
    public final static String TABLE_SCENE = "scene";
    public final static String TABLE_OWNED = "owned";

    public final static String CREATE_TABLE_VENDOR = "create table " + TABLE_VENDOR + "(" +
            Vendor.ID + " Integer primary key autoincrement, " +
            Vendor.NAME + " varchar(20) not null, " +
            Vendor.DESCRIPTION + " varchar(80))";

    public final static String CREATE_TABLE_ROOM = "create table " + TABLE_ROOM + "(" +
            Room.ID + " Integer primary key autoincrement, " +
            Room.NAME + " varchar(20) not null, " +
            Room.DESCRIPTION + " varchar(80))";

    public final static String CREATE_TABLE_DEVICE = "create table " + TABLE_DEVICE + "(" +
            Device.ID + " Integer primary key autoincrement, " +
            Device.NAME + " varchar(20), " +
            Device.TYPE + " Integer," +
            Device.BUTTON_AMOUNT + " Integer," +
            Device.SCENE_AMOUNT + " Integer," +
            Device.VENDOR_ID + " Integer," +
            Device.DESCRIPTION + " varchar(80), " +
            Device.KIND + " Integer," +
            Device.PICTURE + " varchar(50))";

    public final static String CREATE_TABLE_TASK = "create table " + TABLE_TASK + "(" +
            Task.ID + " Integer primary key autoincrement, " +
            Task.SCENE_ID + " Integer, " +
            Task.DEVICE_ID + " Integer, " +
            Task.ACTION + " Integer," +
            Task.INSTRUCTION + " varchar(512))";

    public final static String CREATE_TABLE_OWNED = "create table " + TABLE_OWNED + "(" +
            OwnedDevices.ID + " Integer primary key autoincrement, " +
            OwnedDevices.NAME + " varchar(20), " +
            OwnedDevices.MAC + " varchar(20), " +
            OwnedDevices.CHANNEL + " Integer," +
            OwnedDevices.POWER + " Integer," +
            OwnedDevices.PICTURE + " varchar(50), " +
            OwnedDevices.DEVICE_ID + " Integer," +
            OwnedDevices.ROOM_ID + " Integer)";

    public final static String CREATE_TABLE_SCENE = "create table " + TABLE_SCENE + "(" +
            Scene.ID + " Integer primary key autoincrement, " +
            Scene.NAME + " varchar(20), " +
            Scene.MAC + " varchar(20), " +
            Scene.TYPE + " Integer," +
            Scene.KEYWORDS + " varchar(100), " +
            Scene.CHANNEL + " Integer," +
            Scene.POWER + " Integer," +
            Scene.PICTURE + " varchar(50), " +
            Scene.DEVICE_ID + " Integer," +
            Scene.ROOM_ID + " Integer)";
}