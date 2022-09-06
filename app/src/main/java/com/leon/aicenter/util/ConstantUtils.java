package com.leon.aicenter.util;

import android.os.Environment;

import com.leon.aicenter.R;

import java.util.HashMap;
import java.util.Map;

public class ConstantUtils {
    public final static String APP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AICenter";
    public final static String FILE_WEATHER = APP_PATH + "/Weather.dat";
    public final static String FILE_WEATHER_PIC = APP_PATH + "/WeatherPic.png";
    public static final String FILE_LOCAL_SETTINGS = APP_PATH + "/Settings.dat";
    public static final String FILE_UPDATE_APK = APP_PATH + "/AICenter.apk";
    public static final String FILE_DEBUG_LOG = APP_PATH + "/debug.log";
    public static final String FILE_SCREEN_SAVER_APK = APP_PATH + "/ScreenSaver.apk";
    public static final String FILE_BACKUP_DB = APP_PATH + "/devices.db";
    public static final String VERSION_URL = "http://szm.etom-link.com/api/Common/Client";
    public static final String SCREEN_SAVER_URL = "http://120.79.155.152:5988/upload/screensaver.apk";
    public static final String OLD_SCREEN_SAVER_URL = "http://120.79.155.152:5988/upload/screensaverold.apk";
    public final static String NUMBER_PATTERN = "^\\d{1,}$";
    public final static int SEND_COMMAND_TIMES = 3;
    public final static int RESEND_LOW_POWER_TIMEOUT = 900;
    public final static int RESEND_HIGH_POWER_TIMEOUT = 700;
    public final static int RESEND_PANEL_TIMEOUT = 2000;
    public final static int SEND_SCENE_TIMES = 3;
    public final static int RESEND_SCENE_TIMEOUT = 1000;
    public final static int DOUBLE_PRESS_TIMEOUT = 500;
    public final static int TYPE_DEVICE = 1;
    public final static int TYPE_SCENE = 2;
    public final static int DRAG_START = 1;
    public final static int DRAG_STOP = 2;
    public final static int CONTINUE = 1;
    public final static int EXIT_SEARCH = 1;
    public final static int COMPLETE = 2;
    public final static String ON = "on";
    public final static String OFF = "off";
    public final static int GATEWAY_TIME = 8000;
    public final static int MAX_TASK_COMMAND = 10;
    public final static int MASK_STATUS_TEMPERATURE = 0x0FC0;
    public final static int MASK_STATUS_FAN = 0x0030;
    public final static int MASK_STATUS_MODE = 0x000C;
    public final static int MASK_STATUS_SWITCH = 0x0003;
    public final static int MASK_STATUS_CLEAR_INDOOR_TEMPERATURE = 0x0FFF;
    public final static int MASK_SET_TEMPERATURE = 0x1000;
    public final static int MASK_SET_FAN = 0x2000;
    public final static int MASK_SET_MODE = 0x4000;
    public final static int TEMPERATURE_SWITCH_ON_OFF = 0xFFFF;
    public final static int[] SCENE_PICTURE = new int[]{R.mipmap.scene_background1,
            R.mipmap.scene_background2,
            R.mipmap.scene_background3,
            R.mipmap.scene_background4,
            R.mipmap.scene_background5,
            R.mipmap.scene_background6,
            R.mipmap.scene_background7,
            R.mipmap.scene_background8,
            R.mipmap.scene_background9,
            R.mipmap.scene_background10,
            R.mipmap.scene_background11,
            R.mipmap.scene_background12,
            R.mipmap.scene_background13,
            R.mipmap.scene_background14
    };

    public final static int[] DEVICE_DEMO_PICTURE = new int[]{
            R.mipmap.demo_1switch,
            R.mipmap.demo_2switches,
            R.mipmap.demo_3switches,
            R.mipmap.demo_4switches,
            R.mipmap.demo_6switches,
            R.mipmap.demo_curtain_3switches,
            R.mipmap.demo_curtain_6switches,
            R.mipmap.demo_panel_screen,
            R.mipmap.demo_panel,
            R.mipmap.demo_temperature
    };

    public enum ActionType {
        on, off, toggle, scene, pause, none
    }

    public enum SceneType {
        local, switcher, panel, deviceInPanel
    }

    public enum Status {
        off, on, offline, onPause, offPause
    }

    public enum DeviceKind {
        none, switcher, curtain, panel, temperature
    }

    public enum FanLevel {
        low, mid, high, auto
    }

    public enum TemperatureMode {
        cool, fan, hot
    }

    public static final String PLAY_MODE_RANDOM = "random";
    public static final String PLAY_MODE_SINGLE = "single";
    public static final String PLAY_MODE_LIST = "list";
    public static final Map<String, Integer> playModeImage = new HashMap<String, Integer>() {
        {
            put(PLAY_MODE_RANDOM, R.mipmap.mode_random);
            put(PLAY_MODE_SINGLE, R.mipmap.mode_single);
            put(PLAY_MODE_LIST, R.mipmap.mode_loop);
        }
    };
    public static final Map<SceneType, Integer> maxCommand = new HashMap<SceneType, Integer>() {
        {
            put(SceneType.switcher, 7);
            put(SceneType.panel, 28);
        }
    };

    public static final String[][] PANEL_SCENE_NAME = new String[][]{
            {"回家模式", "离家模式", "睡眠模式", "起床模式", "就餐模式", "观影模式"},
            {"我回来了", "我出门了", "我睡觉了", "我起床了", "我吃饭了", "看电影了"},
    };

    public static final int[] PANEL_SCENE_CHANNEL = new int[]{
            0x0B, 0x0A, 0x0D, 0x0C, 0x0E, 0x0F
    };

    public static final String[] PANEL_SWITCH_DEVICES = new String[]{
            "所有灯", "客厅灯", "吸顶灯", "落地灯", "背景灯", "氛围灯", "踢脚灯", "吊灯", "槽灯", "壁灯", "筒灯", "射灯", "廊灯", "灯带", "夜灯",
            "台灯", "阅读灯", "卧室灯", "床头灯", "衣柜灯", "镜前灯", "餐厅灯", "厨房灯", "麻将灯", "卫浴灯", "排风扇", "换气扇", "阳台灯"
    };

    public static final int[][] PANEL_SWITCH_CONTROL = new int[][]{
            {0x10, 0x28, 0x2A, 0x2C, 0x2E, 0x30, 0x32, 0x34, 0x36, 0x38, 0x3A, 0x3C, 0x3E, 0x40, 0x42,
                    0x44, 0x46, 0x48, 0x4A, 0x4C, 0x4E, 0x50, 0x52, 0x54, 0x56, 0x58, 0x5A, 0x5C},
            {0x11, 0x29, 0x2B, 0x2D, 0x2F, 0x31, 0x33, 0x35, 0x37, 0x39, 0x3B, 0x3D, 0x3F, 0x41, 0x43,
                    0x45, 0x47, 0x49, 0x4B, 0x4D, 0x4F, 0x51, 0x53, 0x55, 0x57, 0x59, 0x5B, 0x5D}
    };

    public static final String[] PANEL_CURTAIN_DEVICES = new String[]{
            "所有窗帘", "所有纱帘", "窗帘", "左窗帘", "右窗帘", "纱帘", "左纱帘", "右纱帘", "幕布", "晾衣架"
    };

    public static final int[][] PANEL_CURTAIN_CONTROL = new int[][]{
            {0x12, 0x15, 0x70, 0x73, 0x76, 0x79, 0x7C, 0x7F, 0x82, 0x85},
            {0x13, 0x16, 0x71, 0x74, 0x77, 0x7A, 0x7D, 0x80, 0x83, 0x86},
            {0x14, 0x17, 0x72, 0x75, 0x78, 0x7B, 0x7E, 0x81, 0x84, 0x87}
    };
}
