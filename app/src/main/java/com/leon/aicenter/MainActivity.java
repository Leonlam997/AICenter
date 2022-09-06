package com.leon.aicenter;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;
import com.leon.aicenter.adapter.DevicePagerAdapter;
import com.leon.aicenter.adapter.HomeDeviceAdapter;
import com.leon.aicenter.adapter.PanelEntryAdapter;
import com.leon.aicenter.adapter.RoomAdapter;
import com.leon.aicenter.adapter.RoomPagerAdapter;
import com.leon.aicenter.adapter.ScenePagerAdapter;
import com.leon.aicenter.adapter.SceneTaskAdapter;
import com.leon.aicenter.adapter.SelectImageAdapter;
import com.leon.aicenter.bean.APIMsgBean;
import com.leon.aicenter.bean.APIMusicBean;
import com.leon.aicenter.bean.APIMusicProgressBean;
import com.leon.aicenter.bean.DeviceBean;
import com.leon.aicenter.bean.DevicePropsBean;
import com.leon.aicenter.bean.HomeDeviceBean;
import com.leon.aicenter.bean.PanelEntryBean;
import com.leon.aicenter.bean.RoomBean;
import com.leon.aicenter.bean.SceneBean;
import com.leon.aicenter.bean.SelectImageBean;
import com.leon.aicenter.bean.SettingsBean;
import com.leon.aicenter.bean.TTSBean;
import com.leon.aicenter.bean.UpdateVersionBean;
import com.leon.aicenter.bean.VoiceInputBean;
import com.leon.aicenter.bean.WakeupWordBean;
import com.leon.aicenter.bean.WeatherBean;
import com.leon.aicenter.component.AddDeviceDialog;
import com.leon.aicenter.component.InputTextDialog;
import com.leon.aicenter.component.SelectRoomDialog;
import com.leon.aicenter.component.TemperatureCtrlDialog;
import com.leon.aicenter.component.WarningDialog;
import com.leon.aicenter.database.BackupTask;
import com.leon.aicenter.database.DatabaseStatic;
import com.leon.aicenter.database.MyHelper;
import com.leon.aicenter.databinding.ActivityMainBinding;
import com.leon.aicenter.fragment.DevicesFragment;
import com.leon.aicenter.fragment.ScenesFragment;
import com.leon.aicenter.serial.SerialCommand;
import com.leon.aicenter.util.BufferedWriterUtil;
import com.leon.aicenter.util.ConstantUtils;
import com.leon.aicenter.util.NRSign;
import com.leon.aicenter.util.Sha256;
import com.leon.aicenter.util.TextToNumber;
import com.sznaner.nrmessage.NRMessage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MY_LOG";
    private static final int HANDLER_SEND_CMD = 1;
    private static final int HANDLER_RECEIVE_CMD = 2;
    public static final int HANDLER_SWITCH_PRESS = 3;
    public static final int HANDLER_SCENE_PRESS = 4;
    public static final int HANDLER_ADD_DEVICES = 5;
    public static final int HANDLER_MODIFY_SCENE = 6;
    public static final int HANDLER_ADD_FINISHED = 7;
    public static final int HANDLER_ITEM_SELECTED = 8;
    public static final int HANDLER_DELETE_SCENE = 9;
    public static final int HANDLER_DELETE_DEVICE = 10;
    public static final int HANDLER_RENAME_DEVICE = 11;
    private static final int HANDLER_CHECK_VERSION = 12;
    private static final int HANDLER_UPDATE = 13;
    private static final int HANDLER_UPDATE_PROGRESS = 14;
    private static final int HANDLER_UPDATE_FAIL = 15;
    private static final int HANDLER_MUSIC_ELAPSE = 16;
    private static final int HANDLER_MUSIC_SONG = 17;
    private static final int HANDLER_TOAST = 18;
    private static final int HANDLER_RESEND_SWITCH_PRESS = 19;
    private static final int HANDLER_RESEND_SCENE_PRESS = 20;
    private static final int HANDLER_SWITCH_DOUBLE_PRESS_TIMEOUT = 21;
    private static final int HANDLER_CHANGE_HINTS = 22;
    //    private static final int HANDLER_STOP_VOICE_DIALOG = 23;
    public static final int HANDLER_DRAG = 24;
    private static final int HANDLER_FRESH_TAB = 25;
    private static final String path_485 = "/dev/extern485_device";
    private static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";
    public static boolean working = false;
    public static boolean modifying = false;
    public static boolean selectMode = false;
    public static boolean singleSelect = false;
    private ActivityMainBinding binding;
    private MyHelper myHelper;
    private SQLiteDatabase db = null;
    private Toast mToast;
    private String gwMac = "";
    private String lastSpeech = "";
    private DeviceBean currentDevice = new DeviceBean();
    private SceneBean currentScene;
    private BDLocation lastLocation;
    private SerialPortManager mSerialPortManager;
    private UpdateVersionBean versionBean;
    //    private boolean waitForVoiceDialog = false;
    private boolean isPlaying = false;
    private boolean deviceChanged;
    private boolean sceneChanged;
    private boolean modifyingScene;
    private boolean modifyingRoom;
    private boolean waitForDoublePress;
    private int devIndex = 0;
    private int resendCount = 0;
    private int channelIndex;
    private int changeTemperatureStatus;
    private byte[] receiveData;
    private byte[] lastData;
    private CommandType cmdType = CommandType.idle;
    private Module currentModule;
    private List<RoomBean> roomBeans;
    private List<RoomBean> tempRoomBeans;
    private List<DevicesFragment> devicesFragments;
    private List<DevicesFragment> selectDeviceFragments;
    private List<ScenesFragment> scenesFragments;
    private List<ScenesFragment> selectSceneFragments;
    private List<List<DeviceBean>> deviceLists;
    private List<List<DeviceBean>> selectDevices;
    private List<List<SceneBean>> selectScenes;
    private List<List<SceneBean>> sceneLists;
    private List<List<Integer>> deviceOrder;
    private List<List<Integer>> sceneOrder;
    private List<DeviceBean> allDevices;
    private List<DeviceBean> discoveredDevices;
    private List<List<DevicePropsBean>> devicePropsBeans;
    private List<SceneBean> allScenes;
    private List<SceneBean> modifyScenes;
    private List<SceneBean.TaskBean> taskBeans;
    private List<SceneBean.TaskBean> currentTask;
    private List<HomeDeviceBean> homeDeviceBeans;
    private List<DeviceBean> homeDevices;
    private List<SceneBean> homeScenes;
    private List<Boolean> changeLists;
    private List<Integer> roomOrder;
    private final List<Message> messageList = new ArrayList<>();
    private DevicePagerAdapter devicePagerAdapter;
    private DevicePagerAdapter selectDeviceAdapter;
    private ScenePagerAdapter scenePagerAdapter;
    private ScenePagerAdapter selectSceneAdapter;
    private RoomAdapter roomAdapter;
    private HomeDeviceAdapter homeDeviceAdapter;
    private SceneTaskAdapter taskAdapter;
    private DevicesFragment devicesInRoom;
    private ScenesFragment scenesInRoom;
    private AddDeviceDialog addDeviceDialog;
    private APIMusicBean musicBean;
    private APIMusicProgressBean progressBean;
    private TabLayout currentTabTitle;
    private Button currentButtonConfirm;
    private WarningDialog updateDialog;
    private SettingsBean settings;
    private TemperatureCtrlDialog ctrlDialog;
    private VolumeReceiver volumeReceiver;
    private RssiBroadcast rssiBroadcast;

    public enum CommandType {
        none, require, search, register, complete, waiting, idle, allStatus, device, scene, exitSearch, task, setTask, getTask, delete, panelTask, test
    }

    private enum Module {
        home, device, scene, room
    }

    private enum FrameType {
        list, select, modify, background, detail, panelSwitch, panelCurtain
    }

    public void myToast(String msg) {
        Log.d(TAG, msg);
        if (msg != null && !msg.isEmpty()) {
            Message message = otherHandler.obtainMessage(HANDLER_TOAST);
            message.obj = msg;
            otherHandler.sendMessage(message);
        }
    }

    public void myToast(@StringRes int msg) {
        Message message = otherHandler.obtainMessage(HANDLER_TOAST);
        message.obj = getString(msg);
        otherHandler.sendMessage(message);
    }

    private Handler otherHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case HANDLER_CHANGE_HINTS:
                    int[] hints = new int[]{
                            R.string.text_speech_hints,
                            R.string.text_speech_device_hints,
                            R.string.text_speech_music_hints
                    };
                    binding.layoutHome.layoutWidget.tvHints.setText(hints[(int) (Math.random() * hints.length)]);
                    otherHandler.sendEmptyMessageDelayed(HANDLER_CHANGE_HINTS, 15000);
                    break;
                case HANDLER_CHECK_VERSION:
                    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo info = connMgr.getActiveNetworkInfo();
                    if (info != null && info.isAvailable()) {
                        new GetVersion().execute();
                    }
                    otherHandler.sendEmptyMessageDelayed(HANDLER_CHECK_VERSION, 1000 * 60 * 120);
                    break;
                case HANDLER_UPDATE_FAIL:
                case HANDLER_UPDATE:
                    if (versionBean.getUrl() != null && !versionBean.getUrl().isEmpty() && (updateDialog == null || !updateDialog.isShowing())) {
                        updateDialog = new WarningDialog(MainActivity.this);
                        updateDialog.setOnPositiveClick(v -> {
                            updateDialog.dismiss();
                            new DownloadApkTask().execute(versionBean.getUrl(), ConstantUtils.FILE_UPDATE_APK);
                            updateDialog = new WarningDialog(MainActivity.this);
                            updateDialog.setMessage(R.string.text_downloading);
                            updateDialog.setOnPositiveClick(v1 -> updateDialog.dismiss());
                            updateDialog.show();
                            updateDialog.setProgress(0);
                        });
                        updateDialog.setMessage(msg.what == HANDLER_UPDATE_FAIL ? R.string.text_download_fail : R.string.text_found_new_version);
                        updateDialog.show();
                    }
                    break;
                case HANDLER_UPDATE_PROGRESS:
                    if (updateDialog != null && updateDialog.isShowing())
                        updateDialog.setProgress(msg.arg1);
                    break;
                case HANDLER_MUSIC_SONG:
                    isPlaying = musicBean.getState().equals("play");
                    binding.layoutHome.layoutWidget.btnPause.setImageResource(isPlaying ? R.drawable.button_pause_style : R.drawable.button_play_style);
                    if (!musicBean.getName().equals(binding.layoutHome.layoutWidget.tvSongTitle.getText().toString()))
                        binding.layoutHome.layoutWidget.tvSongTitle.setText(musicBean.getName());
                    if (!musicBean.getSinger().equals(binding.layoutHome.layoutWidget.tvSinger.getText().toString()))
                        binding.layoutHome.layoutWidget.tvSinger.setText(musicBean.getSinger());
                    if (null == musicBean.getCover() || musicBean.getCover().isEmpty())
                        binding.layoutHome.layoutWidget.ivAlbum.setImageResource(R.mipmap.album);
                    Integer mode = ConstantUtils.playModeImage.get(musicBean.getMode());
                    if (null != mode)
                        binding.layoutHome.layoutWidget.btnMode.setImageResource(mode);
                    break;
                case HANDLER_MUSIC_ELAPSE:
                    binding.layoutHome.layoutWidget.pbSong.setProgress(progressBean.getProgress());
                    binding.layoutHome.layoutWidget.pbSong.setMax(progressBean.getMaxProgress());
                    binding.layoutHome.layoutWidget.tvTotalTime.setText(String.format(Locale.CHINA, "%d:%02d", progressBean.getMaxProgress() / 60000, (progressBean.getMaxProgress() % 60000) / 1000));
                    binding.layoutHome.layoutWidget.tvElapse.setText(String.format(Locale.CHINA, "%d:%02d", progressBean.getProgress() / 60000, (progressBean.getProgress() % 60000) / 1000));
                    break;
                case HANDLER_TOAST:
                    if (mToast != null)
                        mToast.cancel();
                    mToast = Toast.makeText(MainActivity.this, (String) msg.obj, Toast.LENGTH_SHORT);
                    LinearLayout layout = (LinearLayout) mToast.getView();
                    TextView tv = (TextView) layout.getChildAt(0);
                    layout.setBackgroundResource(R.drawable.toast_background);
                    tv.setTextSize(22);
                    tv.setTextColor(getColor(R.color.white));
                    mToast.setGravity(Gravity.CENTER, 0, 80);
                    mToast.show();
                    break;
//                case HANDLER_STOP_VOICE_DIALOG:
//                    waitForVoiceDialog = false;
//                    NRMessage.post(APIMsgBean.com_sznaner_voice_set, JSON.toJSONString(new APIMsgBean(APIMsgBean.stopDialog)));
//                    break;
            }
            return true;
        }
    });

    private Handler commHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Log.d(TAG, "Handler cmdType=" + cmdType + ",what=" + msg.what + ",working=" + working);
            switch (msg.what) {
                case HANDLER_SEND_CMD: {
                    byte[] mac = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xFF,
                            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
                    byte[] type = new byte[]{(byte) 0x04, (byte) 0x02, (byte) 0x80};
                    commHandler.removeMessages(HANDLER_SEND_CMD);
                    receiveData = null;
                    switch (cmdType) {
                        case test: {
//                            type[0] = (byte) 3;
//                            type[1] = (byte) 0x11;
//                            type[2] = (byte) 0xc0;
//                            sendBytes(SerialCommand.makeCmdData(SerialCommand.CODE_SND_DEVICE_CONFIG,
//                                    new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xd2, (byte) 0xd1, (byte) 0xaa, (byte) 0xf0}, type, new byte[0]));
                            db = myHelper.getWritableDatabase();
                            db.beginTransaction();
                            ContentValues values = new ContentValues();
                            for (int i = 0; i < 10; i++) {
                                values.put(DatabaseStatic.OwnedDevices.MAC, "00000000000" + i);
                                values.put(DatabaseStatic.OwnedDevices.NAME, i);
                                values.put(DatabaseStatic.OwnedDevices.DEVICE_ID, 21);
                                deviceOrder.get(0).add((int) db.insert(DatabaseStatic.TABLE_OWNED, null, values));
                            }
                            db.setTransactionSuccessful();
                            db.endTransaction();
                            db.close();
                            initDevicePager();
                        }
                        break;
                        case require:
                            sendBytes(SerialCommand.makeCmdData(SerialCommand.CODE_GW_REQUIRE, mac, type, new byte[0]));
                            commHandler.sendEmptyMessageDelayed(HANDLER_SEND_CMD, 1000);
                            break;
                        case search:
                            if (addDeviceDialog == null) {
                                currentDevice = new DeviceBean();
                                addDeviceDialog = new AddDeviceDialog(MainActivity.this);
                                addDeviceDialog.setCancelable(false);
                                addDeviceDialog.setCanceledOnTouchOutside(false);
                                addDeviceDialog.setMainHandler(commHandler);
                                addDeviceDialog.show();
                                addDeviceDialog.setOnDismissListener(dialog -> {
                                    if (cmdType == CommandType.waiting)
                                        sendBytes(SerialCommand.makeCmdData(SerialCommand.CODE_GW_FINISHED, mac, type, new byte[0]));
                                    cmdType = CommandType.idle;
                                    Log.d(TAG, "idle 1");
                                    working = false;
                                    addDeviceDialog = null;
                                });
                                devIndex = 0;
                                deviceChanged = false;
                                sceneChanged = false;
                                discoveredDevices = new ArrayList<>();
                                devicePropsBeans = new ArrayList<>();
                            }
                            sendBytes(SerialCommand.makeCmdData(SerialCommand.CODE_GW_SEARCH, mac, type, new byte[0]));
                            commHandler.sendEmptyMessageDelayed(HANDLER_SEND_CMD, ConstantUtils.GATEWAY_TIME);
                            break;
                        case exitSearch:
                        case complete:
                            sendBytes(SerialCommand.makeCmdData(SerialCommand.CODE_GW_FINISHED, mac, type, new byte[0]));
                            commHandler.sendEmptyMessageDelayed(HANDLER_SEND_CMD, ConstantUtils.GATEWAY_TIME);
                            break;
                        case register:
                            if (resendCount++ >= ConstantUtils.SEND_COMMAND_TIMES) {
                                resendCount = 0;
                                myToast(currentDevice.getName() + "注册失败！");
                                devIndex++;
                            }
                            if (devIndex < discoveredDevices.size()) {
                                currentDevice = discoveredDevices.get(devIndex);
                                if (gwMac.length() >= 8 && currentDevice.getMac().length() >= 12) {
                                    byte[] data = new byte[4];
                                    for (int i = 0; i < 4; i++)
                                        data[i] = (byte) Integer.parseInt(gwMac.substring(gwMac.length() - 8 + i * 2, gwMac.length() - 6 + i * 2), 16);
                                    for (int i = 0; i < 6; i++)
                                        mac[i] = (byte) Integer.parseInt(currentDevice.getMac().substring(currentDevice.getMac().length() - 12 + i * 2, currentDevice.getMac().length() - 10 + i * 2), 16);
                                    type[0] = (byte) ((currentDevice.getDevType() >> 8) & 0xFF);
                                    type[1] = (byte) (currentDevice.getDevType() & 0xFF);
                                    type[2] = currentDevice.getPower();
                                    sendBytes(SerialCommand.makeCmdData(SerialCommand.CODE_SND_GW_MAC_SET, mac, type, data));
                                    commHandler.sendEmptyMessageDelayed(HANDLER_SEND_CMD, currentDevice.getKind() == ConstantUtils.DeviceKind.temperature || currentDevice.getKind() == ConstantUtils.DeviceKind.panel ? ConstantUtils.RESEND_PANEL_TIMEOUT :
                                            currentDevice.getPower() == SerialCommand.POWER_LOW ? ConstantUtils.RESEND_LOW_POWER_TIMEOUT : ConstantUtils.RESEND_HIGH_POWER_TIMEOUT);
                                }
                            } else {
                                resendCount = 0;
                                devIndex = 0;
                                nextDeviceStatus();
                            }
                            break;
                        case getTask:
                        case setTask:
                        case panelTask:
                        case delete:
                            if (resendCount++ >= ConstantUtils.SEND_COMMAND_TIMES) {
                                deviceOffline(currentScene.getMac());
                                if (cmdType == CommandType.setTask)
                                    myToast(R.string.text_set_task_fail);
                                cmdType = CommandType.idle;
                                working = false;
                                checkMessageList();
                            } else {
                                working = true;
                                for (int i = 0; i < 6; i++)
                                    mac[i] = (byte) Integer.parseInt(currentScene.getMac().substring(currentScene.getMac().length() - 12 + i * 2, currentScene.getMac().length() - 10 + i * 2), 16);
                                type[0] = (byte) ((currentScene.getDevType() >> 8) & 0xFF);
                                type[1] = (byte) (currentScene.getDevType() & 0xFF);
                                type[2] = currentScene.getPower();
                                byte[] data;
                                byte action;
                                switch (cmdType) {
                                    case setTask:
                                    case panelTask:
                                        action = SerialCommand.CODE_SND_SCENE_SET;
                                        if (resendCount > 1) {
                                            data = lastData;
                                            break;
                                        } else {
                                            byte[] body = makeTaskData();
                                            if (body.length != 0) {
                                                byte[] head;
                                                switch (currentScene.getType()) {
                                                    case deviceInPanel:
                                                        head = new byte[]{(byte) currentScene.getAllChannel()[channelIndex], 0, (byte) (body.length / 7)};
                                                        break;
                                                    case panel:
                                                        head = new byte[]{(byte) currentScene.getChannel(), 0, (byte) (body.length / 7)};
                                                        break;
                                                    default:
                                                        head = new byte[]{(byte) (currentScene.getChannel() - 1), 0, (byte) (body.length / 7)};
                                                }
                                                data = new byte[body.length + head.length];
                                                System.arraycopy(head, 0, data, 0, head.length);
                                                System.arraycopy(body, 0, data, head.length, body.length);
                                                lastData = data;
                                                break;
                                            } else if (currentScene.getTaskBeans().size() > 0) {
                                                lastData = null;
                                                working = false;
                                                cmdType = CommandType.idle;
                                                Log.d(TAG, "idle 2");
                                                return true;
                                            }
                                            cmdType = CommandType.delete;
                                        }
                                    case delete:
                                        data = new byte[]{(byte) (currentScene.getChannel() - 1)};
                                        action = SerialCommand.CODE_SND_SCENE_DEL;
                                        break;
                                    default:
                                        if (currentScene.getType() == ConstantUtils.SceneType.switcher)
                                            data = new byte[]{(byte) (currentScene.getChannel() - 1), 0, ConstantUtils.MAX_TASK_COMMAND};
                                        else
                                            data = new byte[]{(byte) currentScene.getChannel(), 0, ConstantUtils.MAX_TASK_COMMAND};
                                        action = SerialCommand.CODE_SND_SCENE_GET;
                                        break;
                                }
                                sendBytes(SerialCommand.makeCmdData(action, mac, type, data));
                                commHandler.sendEmptyMessageDelayed(HANDLER_SEND_CMD, currentScene.getKind() == ConstantUtils.DeviceKind.panel ? ConstantUtils.RESEND_PANEL_TIMEOUT :
                                        currentScene.getPower() == SerialCommand.POWER_LOW ? ConstantUtils.RESEND_LOW_POWER_TIMEOUT : ConstantUtils.RESEND_HIGH_POWER_TIMEOUT);
                            }
                            break;
                        case allStatus:
                            working = true;
                            if (resendCount++ >= ConstantUtils.SEND_COMMAND_TIMES) {
                                commHandler.removeMessages(HANDLER_SEND_CMD);
                                deviceOffline(currentDevice.getMac());
                                nextDeviceStatus();
                                break;
                            }
                            if (currentDevice.getMac().length() >= 12) {
                                byte action = currentDevice.getKind() == ConstantUtils.DeviceKind.temperature ? SerialCommand.CODE_SND_TEMPERATURE_CONTROL : currentDevice.getKind() == ConstantUtils.DeviceKind.panel ? SerialCommand.CODE_SND_REQUIRE : SerialCommand.CODE_SND_STATUS;
                                for (int i = 0; i < 6; i++)
                                    mac[i] = (byte) Integer.parseInt(currentDevice.getMac().substring(currentDevice.getMac().length() - 12 + i * 2, currentDevice.getMac().length() - 10 + i * 2), 16);
                                type[0] = (byte) ((currentDevice.getDevType() >> 8) & 0xFF);
                                type[1] = (byte) (currentDevice.getDevType() & 0xFF);
                                type[2] = currentDevice.getPower();
                                sendBytes(SerialCommand.makeCmdData(action, mac, type,
                                        currentDevice.getKind() == ConstantUtils.DeviceKind.temperature ? SerialCommand.makeTemperatureCmd(countTemperatureDevice(), true, 0, 6) : new byte[0]));
                                commHandler.sendEmptyMessageDelayed(HANDLER_SEND_CMD, currentDevice.getKind() == ConstantUtils.DeviceKind.temperature || currentDevice.getKind() == ConstantUtils.DeviceKind.panel ? ConstantUtils.RESEND_PANEL_TIMEOUT :
                                        currentDevice.getPower() == SerialCommand.POWER_LOW ? ConstantUtils.RESEND_LOW_POWER_TIMEOUT : ConstantUtils.RESEND_HIGH_POWER_TIMEOUT);
                            }
                            break;
                    }
                }
                break;
                case HANDLER_SWITCH_DOUBLE_PRESS_TIMEOUT: {
                    commHandler.removeMessages(HANDLER_SWITCH_DOUBLE_PRESS_TIMEOUT);
                    waitForDoublePress = false;
                    resendCount = 0;
                    Message m = commHandler.obtainMessage(HANDLER_RESEND_SWITCH_PRESS);
                    m.obj = msg.obj;
                    m.arg1 = msg.arg1;
                    m.arg2 = msg.arg2;
                    commHandler.sendMessage(m);
                }
                break;
                case HANDLER_SWITCH_PRESS:
                    if (working) {
                        Message m = commHandler.obtainMessage(HANDLER_SWITCH_PRESS);
                        m.obj = new DeviceBean((DeviceBean) msg.obj);
                        m.arg1 = msg.arg1;
                        m.arg2 = msg.arg2;
                        messageList.add(m);
                        break;
                    } else if (((DeviceBean) msg.obj).getKind() == ConstantUtils.DeviceKind.curtain) {
                        if (!waitForDoublePress) {
                            waitForDoublePress = true;
                            Message m = commHandler.obtainMessage(HANDLER_SWITCH_DOUBLE_PRESS_TIMEOUT);
                            m.obj = msg.obj;
                            m.arg1 = msg.arg1;
                            m.arg2 = msg.arg2;
                            commHandler.sendMessageDelayed(m, ConstantUtils.DOUBLE_PRESS_TIMEOUT);
                            break;
                        }
                    } else
                        waitForDoublePress = false;
                    commHandler.removeMessages(HANDLER_SWITCH_DOUBLE_PRESS_TIMEOUT);
                    resendCount = 0;
                case HANDLER_RESEND_SWITCH_PRESS:
                    receiveData = null;
                    if (msg.obj == null) {
                        checkMessageList();
                        break;
                    }
                    currentDevice = (DeviceBean) msg.obj;
                    working = true;
                    if (currentDevice.getKind() == ConstantUtils.DeviceKind.panel) {
                        changeDeviceFrame(FrameType.panelSwitch);
                        working = false;
                        cmdType = CommandType.idle;
                        Log.d(TAG, "idle 3");
                    } else if (resendCount++ >= ConstantUtils.SEND_COMMAND_TIMES) {
                        deviceOffline(currentDevice.getMac());
                        if (cmdType == CommandType.task)
                            nextTask();
                        else {
                            working = false;
                            cmdType = CommandType.idle;
                            Log.d(TAG, "idle 4");
                            checkMessageList();
                        }
                    } else if (null != currentDevice && currentDevice.getMac().length() >= 12) {
                        cmdType = CommandType.values()[msg.arg1];
                        byte[] mac = new byte[6];
                        byte[] type = new byte[3];
                        for (int i = 0; i < 6; i++)
                            mac[i] = (byte) Integer.parseInt(currentDevice.getMac().substring(currentDevice.getMac().length() - 12 + i * 2, currentDevice.getMac().length() - 10 + i * 2), 16);
                        type[0] = (byte) ((currentDevice.getDevType() >> 8) & 0xFF);
                        type[1] = (byte) (currentDevice.getDevType() & 0xFF);
                        type[2] = currentDevice.getPower();
                        byte[] data = new byte[2];
                        if (cmdType == CommandType.task) {
                            if (currentDevice.getKind() == ConstantUtils.DeviceKind.temperature) {
                                changeTemperatureStatus = msg.arg2;
                                data = nextTemperatureCommand();
                                if (data == null) {
                                    nextTask();
                                    return true;
                                }
                            } else {
                                data[0] = (byte) ((msg.arg2 >> 8) & 0xFF);
                                data[1] = (byte) (msg.arg2 & 0xFF);
                            }
                        } else {
                            switch (currentDevice.getKind()) {
                                case temperature:
                                    if (msg.arg2 == ConstantUtils.TEMPERATURE_SWITCH_ON_OFF)
                                        changeTemperatureStatus = (currentDevice.getStatus() & ~ConstantUtils.MASK_STATUS_SWITCH) + ((currentDevice.getStatus() & ConstantUtils.MASK_STATUS_SWITCH) == 1 ? 0 : 1);
                                    else
                                        changeTemperatureStatus = msg.arg2;
                                    if ((changeTemperatureStatus & ConstantUtils.MASK_STATUS_CLEAR_INDOOR_TEMPERATURE) == (currentDevice.getStatus() & ConstantUtils.MASK_STATUS_CLEAR_INDOOR_TEMPERATURE)) {
                                        working = false;
                                        cmdType = CommandType.idle;
                                        Log.d(TAG, "idle 5");
                                        break;
                                    }
                                    data = nextTemperatureCommand();
                                    break;
                                case curtain:
                                    int channel = 2;
                                    if (waitForDoublePress) {
                                        Log.d(TAG, "Curtain:" + currentDevice.getStatusEnum());
                                        switch (currentDevice.getStatusEnum()) {
                                            case offPause:
                                                channel = 3;
                                                break;
                                            case onPause:
                                                channel = 1;
                                                break;
                                        }
                                    } else {
                                        switch (currentDevice.getStatusEnum()) {
                                            case on:
                                            case offPause:
                                                channel = 3;
                                                break;
                                            case off:
                                            case onPause:
                                                channel = 1;
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                    data[0] = (byte) (1 << (8 - channel));
                                    data[1] = (byte) (1 << (8 - channel));
                                    break;
                                default:
                                    if (currentDevice.getStatusEnum() != ConstantUtils.Status.on)
                                        data[0] = (byte) (1 << (8 - currentDevice.getChannel()));
                                    data[1] = (byte) (1 << (8 - currentDevice.getChannel()));
                            }
                        }
                        sendBytes(SerialCommand.makeCmdData(currentDevice.getKind() == ConstantUtils.DeviceKind.temperature ? SerialCommand.CODE_SND_TEMPERATURE_CONTROL : SerialCommand.CODE_SND_CONTROL, mac, type, data));
                        Message m = commHandler.obtainMessage(HANDLER_RESEND_SWITCH_PRESS);
                        m.obj = msg.obj;
                        m.arg1 = msg.arg1;
                        m.arg2 = msg.arg2;
                        commHandler.sendMessageDelayed(m, currentDevice.getKind() == ConstantUtils.DeviceKind.temperature ? ConstantUtils.RESEND_PANEL_TIMEOUT : currentDevice.getPower() == SerialCommand.POWER_LOW ? ConstantUtils.RESEND_LOW_POWER_TIMEOUT : ConstantUtils.RESEND_HIGH_POWER_TIMEOUT);
                    }
                    break;
                case HANDLER_SCENE_PRESS:
                    if (working) {
                        Message m = commHandler.obtainMessage(HANDLER_SCENE_PRESS);
                        m.obj = new SceneBean((SceneBean) msg.obj);
                        m.arg1 = msg.arg1;
                        m.arg2 = msg.arg2;
                        messageList.add(m);
                        break;
                    }
                    resendCount = 0;
                case HANDLER_RESEND_SCENE_PRESS:
                    receiveData = null;
                    if (msg.obj == null) {
                        checkMessageList();
                        break;
                    }
                    working = true;
                    currentScene = (SceneBean) msg.obj;
                    if (resendCount++ >= ConstantUtils.SEND_SCENE_TIMES) {
                        deviceOffline(currentScene.getMac());
                        if (cmdType == CommandType.task)
                            nextTask();
                        else {
                            working = false;
                            cmdType = CommandType.idle;
                            Log.d(TAG, "idle 7");
                            checkMessageList();
                        }
                    } else if (currentScene.getType() == ConstantUtils.SceneType.local) {
                        currentTask = new ArrayList<>();
                        addInCurrentTaskList(currentScene.getTaskBeans());
                        devIndex = 0;
                        nextTask();
                    } else if (currentScene.getMac() != null && currentScene.getMac().length() >= 12) {
                        cmdType = CommandType.values()[msg.arg1];
                        byte[] mac = new byte[6];
                        byte[] type = new byte[3];
                        for (int i = 0; i < 6; i++)
                            mac[i] = (byte) Integer.parseInt(currentScene.getMac().substring(currentScene.getMac().length() - 12 + i * 2, currentScene.getMac().length() - 10 + i * 2), 16);
                        type[0] = (byte) ((currentScene.getDevType() >> 8) & 0xFF);
                        type[1] = (byte) (currentScene.getDevType() & 0xFF);
                        type[2] = currentScene.getPower();
                        byte[] data;
                        byte action;
//                        if (currentScene.getKind() == ConstantUtils.DeviceKind.panel) {
                        data = new byte[1];
                        data[0] = (byte) (currentScene.getKind() == ConstantUtils.DeviceKind.panel ? currentScene.getChannel() : currentScene.getChannel() - 1);
                        action = SerialCommand.CODE_SND_SCENE_EXEC;
//                        } else {
//                            data = new byte[2];
//                            data[0] = (byte) (1 << (8 - currentScene.getChannel()));
//                            data[1] = (byte) (1 << (8 - currentScene.getChannel()));
//                            action = SerialCommand.CODE_SND_CONTROL;
//                        }
                        sendBytes(SerialCommand.makeCmdData(action, mac, type, data));
                        Message m = commHandler.obtainMessage(HANDLER_RESEND_SCENE_PRESS);
                        m.obj = msg.obj;
                        m.arg1 = msg.arg1;
                        commHandler.sendMessageDelayed(m, currentScene.getKind() == ConstantUtils.DeviceKind.panel ? ConstantUtils.RESEND_PANEL_TIMEOUT : ConstantUtils.RESEND_SCENE_TIMEOUT);
                    } else {
                        working = false;
                        cmdType = CommandType.idle;
                        Log.d(TAG, "idle 7");
                    }
                    break;
                case HANDLER_RECEIVE_CMD:
                    byte[] received = ((byte[]) msg.obj);
                    Log.d(TAG, "receiveData=" + (receiveData == null ? "null" : Arrays.toString(receiveData)));
                    if (receiveData == null) {
                        receiveData = received.clone();
                    } else {
                        byte[] bytes = new byte[receiveData.length + received.length];
                        System.arraycopy(receiveData, 0, bytes, 0, receiveData.length);
                        System.arraycopy(received, 0, bytes, receiveData.length, received.length);
                        receiveData = bytes;
                    }
                    Log.d(TAG, "Receive length:" + receiveData.length + getString(R.string.text_speech_split) + SerialCommand.checkData(receiveData) + "," + received.length);
                    if (SerialCommand.checkData(receiveData)) {
                        commHandler.removeMessages(HANDLER_SEND_CMD);
                        switch (cmdType) {
                            case waiting:
                                if (working)
                                    break;
                            case require:
                                StringBuilder mac = new StringBuilder();
                                Cursor cursor;
                                boolean exist = false;
                                for (int i = 3; i < 9; i++)
                                    mac.append(String.format("%02X", receiveData[i]));
                                int type = (receiveData[9] << 8) + Byte.toUnsignedInt(receiveData[10]);
                                db = myHelper.getReadableDatabase();
                                cursor = db.query(DatabaseStatic.TABLE_OWNED, null,
                                        DatabaseStatic.OwnedDevices.MAC + "=?", new String[]{mac.toString()}, null, null, null);
                                if (cursor.moveToNext()) {
                                    Log.d(TAG, mac + " Exist!");
                                    if (cmdType == CommandType.require) {
                                        cursor.close();
                                        cmdType = CommandType.idle;
                                        Log.d(TAG, "idle 8");
                                        break;
                                    }
                                    exist = true;
                                }
                                cursor.close();
                                int devID;
                                int btnAmount;
                                int pfAmount;
                                String picture;
                                String name;
                                ConstantUtils.DeviceKind kind = ConstantUtils.DeviceKind.none;
                                cursor = db.query(DatabaseStatic.TABLE_DEVICE, null,
                                        DatabaseStatic.Device.TYPE + "=?", new String[]{type + ""}, null, null, null);
                                if (cursor.moveToNext()) {
                                    devID = cursor.getInt(DatabaseStatic.Device.COL_ID);
                                    name = cursor.getString(DatabaseStatic.Device.COL_NAME);
                                    Log.d(TAG, name + getString(R.string.text_speech_split) + cursor.getString(DatabaseStatic.Device.COL_DESCRIPTION));
                                    btnAmount = cursor.getInt(DatabaseStatic.Device.COL_BUTTON_AMOUNT);
                                    pfAmount = cursor.getInt(DatabaseStatic.Device.COL_SCENE_AMOUNT);
                                    picture = cursor.getString(DatabaseStatic.Device.COL_PICTURE);
                                    int i = cursor.getInt(DatabaseStatic.Device.COL_KIND);
                                    if (i < ConstantUtils.DeviceKind.values().length)
                                        kind = ConstantUtils.DeviceKind.values()[i];
                                } else {
                                    if (addDeviceDialog == null || !addDeviceDialog.isShowing())
                                        myToast("未找到设备类型！");
                                    cursor.close();
                                    db.close();
                                    break;
                                }
                                cursor.close();
                                db.close();
                                ContentValues v = new ContentValues();
                                if (cmdType == CommandType.require) {
                                    db = myHelper.getWritableDatabase();
                                    db.beginTransaction();
                                    v.put(DatabaseStatic.OwnedDevices.DEVICE_ID, devID);
                                    v.put(DatabaseStatic.OwnedDevices.MAC, mac.toString());
                                    gwMac = mac.toString();
                                    v.put(DatabaseStatic.OwnedDevices.NAME, DatabaseStatic.DEFAULT_GATEWAY);
                                    v.put(DatabaseStatic.OwnedDevices.POWER, Byte.toUnsignedInt(receiveData[11]));
                                    db.insert(DatabaseStatic.TABLE_OWNED, null, v);
                                    db.setTransactionSuccessful();
                                    db.endTransaction();
                                    db.close();
                                    cmdType = CommandType.idle;
                                    Log.d(TAG, "idle 9");
                                } else if (addDeviceDialog != null && addDeviceDialog.isShowing()) {
                                    for (DeviceBean bean : discoveredDevices)
                                        if (bean.getMac().equals(mac.toString())) {
                                            return true;
                                        }
                                    if (currentDevice != null && currentDevice.getMac().equals(mac.toString()))
                                        break;
                                    currentDevice = new DeviceBean();
                                    currentDevice.setMac(mac.toString());
                                    currentDevice.setDevType(type);
                                    currentDevice.setPower(receiveData[11]);
                                    currentDevice.setDevId(devID);
                                    currentDevice.setName(name);
                                    currentDevice.setKind(kind);
                                    if (roomBeans.size() > 1) {
                                        addDeviceDialog.setRoom(roomBeans.subList(1, roomBeans.size()));
                                        addDeviceDialog.setDefaultRoom(binding.layoutDevices.layoutList.tlDevices.getSelectedTabPosition() - 1);
                                    }
                                    addDeviceDialog.setDeviceName(name);
                                    addDeviceDialog.setKind(kind);
                                    addDeviceDialog.setPicture(picture);
                                    if (btnAmount + pfAmount != 0) {
                                        addDeviceDialog.setButtonAmount(btnAmount);
                                        addDeviceDialog.setSceneAmount(pfAmount);
                                    }
                                    if (exist) {
                                        WarningDialog dialog = new WarningDialog(MainActivity.this);
                                        dialog.setOnPositiveClick(v1 -> {
                                            db = myHelper.getWritableDatabase();
                                            db.beginTransaction();
                                            db.delete(DatabaseStatic.TABLE_OWNED, DatabaseStatic.OwnedDevices.MAC + "=?", new String[]{mac.toString()});
                                            db.delete(DatabaseStatic.TABLE_SCENE, DatabaseStatic.OwnedDevices.MAC + "=?", new String[]{mac.toString()});
                                            db.setTransactionSuccessful();
                                            db.endTransaction();
                                            db.close();
                                            addDeviceDialog.getHandler().sendEmptyMessage(AddDeviceDialog.STEP_DETECTED);
                                            working = true;
                                            dialog.dismiss();
                                        });
                                        dialog.setMessage(R.string.text_device_exist);
                                        dialog.show();
                                    } else {
                                        addDeviceDialog.getHandler().sendEmptyMessage(AddDeviceDialog.STEP_DETECTED);
                                        working = true;
                                    }
                                }
                                break;
                            case search:
                                addDeviceDialog.getHandler().sendEmptyMessage(AddDeviceDialog.STEP_INDICATION);
                                cmdType = CommandType.waiting;
                                break;
                            case complete:
                                resendCount = 0;
                                devIndex = 0;
                                cmdType = CommandType.register;
                                commHandler.sendEmptyMessage(HANDLER_SEND_CMD);
                                break;
                            case exitSearch:
                                if (addDeviceDialog != null && addDeviceDialog.isShowing())
                                    addDeviceDialog.dismiss();
                                cmdType = CommandType.idle;
                                Log.d(TAG, "idle 10");
                                break;
                            case register: {
                                int channel = 1;
                                db = myHelper.getWritableDatabase();
                                db.beginTransaction();
                                resendCount = 0;
                                for (DevicePropsBean bean : devicePropsBeans.get(devIndex)) {
                                    ContentValues values;
                                    int roomId = -1, roomIndex = -1;
                                    if (bean.getRoom() != null && !bean.getRoom().isEmpty()) {
                                        for (int j = 1; j < roomBeans.size(); j++)
                                            if (roomBeans.get(j).getName().equals(bean.getRoom())) {
                                                roomId = roomBeans.get(j).getId();
                                                roomIndex = j;
                                                break;
                                            }
                                        if (roomId == -1) {
                                            values = new ContentValues();
                                            values.put(DatabaseStatic.Room.NAME, bean.getName());
                                            roomId = (int) db.insert(DatabaseStatic.TABLE_ROOM, null, values);
                                            roomBeans.add(new RoomBean(roomId, bean.getName()));
                                            roomOrder.add(roomId);
                                            roomIndex = deviceOrder.size();
                                            deviceOrder.add(new ArrayList<>());
                                            sceneOrder.add(new ArrayList<>());
                                            settings.setDeviceOrder(deviceOrder);
                                            settings.setSceneOrder(sceneOrder);
                                            settings.setRoomOrder(roomOrder);
                                            saveSettings();
                                            deviceChanged = true;
                                            sceneChanged = true;
                                        }
                                    }
                                    values = new ContentValues();
                                    if (bean.getType() == ConstantUtils.TYPE_DEVICE) {
                                        deviceChanged = true;
                                        values.put(DatabaseStatic.OwnedDevices.DEVICE_ID, discoveredDevices.get(devIndex).getDevId());
                                        values.put(DatabaseStatic.OwnedDevices.MAC, discoveredDevices.get(devIndex).getMac());
                                        values.put(DatabaseStatic.OwnedDevices.NAME, bean.getName());
                                        switch (discoveredDevices.get(devIndex).getDevType()) {
                                            case 0x0301:
                                            case 0x0311:
                                                values.put(DatabaseStatic.OwnedDevices.CHANNEL, 2);
                                                break;
                                            case 0x0302:
                                            case 0x0312:
                                                values.put(DatabaseStatic.OwnedDevices.CHANNEL, channel == 2 ? 3 : channel);
                                                break;
                                            case 0x0314:
                                                switch (channel) {
                                                    case 2:
                                                        values.put(DatabaseStatic.OwnedDevices.CHANNEL, 3);
                                                        break;
                                                    case 3:
                                                        values.put(DatabaseStatic.OwnedDevices.CHANNEL, 4);
                                                        break;
                                                    case 4:
                                                        values.put(DatabaseStatic.OwnedDevices.CHANNEL, 6);
                                                        break;
                                                    default:
                                                        values.put(DatabaseStatic.OwnedDevices.CHANNEL, channel);
                                                }
                                                break;
                                            default:
                                                values.put(DatabaseStatic.OwnedDevices.CHANNEL, channel);
                                        }
                                        values.put(DatabaseStatic.OwnedDevices.POWER, Byte.toUnsignedInt(discoveredDevices.get(devIndex).getPower()));
                                        if (roomId != -1)
                                            values.put(DatabaseStatic.OwnedDevices.ROOM_ID, roomId);
                                        int id = (int) db.insert(DatabaseStatic.TABLE_OWNED, null, values);
                                        deviceOrder.get(0).add(id);
                                        if (roomId != -1)
                                            deviceOrder.get(roomIndex).add(id);
                                        if (discoveredDevices.get(devIndex).getKind() == ConstantUtils.DeviceKind.panel) {
                                            sceneChanged = true;
                                            for (int i = 0; i < ConstantUtils.PANEL_SCENE_NAME[0].length; i++) {
                                                values = new ContentValues();
                                                values.put(DatabaseStatic.Scene.DEVICE_ID, discoveredDevices.get(devIndex).getDevId());
                                                values.put(DatabaseStatic.Scene.MAC, discoveredDevices.get(devIndex).getMac());
                                                values.put(DatabaseStatic.Scene.TYPE, ConstantUtils.SceneType.panel.ordinal());
                                                values.put(DatabaseStatic.Scene.NAME, ConstantUtils.PANEL_SCENE_NAME[0][i]);
                                                values.put(DatabaseStatic.Scene.KEYWORDS, ConstantUtils.PANEL_SCENE_NAME[1][i]);
                                                values.put(DatabaseStatic.Scene.CHANNEL, ConstantUtils.PANEL_SCENE_CHANNEL[i]);
                                                values.put(DatabaseStatic.Scene.PICTURE, i + "");
                                                values.put(DatabaseStatic.Scene.POWER, Byte.toUnsignedInt(discoveredDevices.get(devIndex).getPower()));
                                                if (roomId != -1)
                                                    values.put(DatabaseStatic.Scene.ROOM_ID, roomId);
                                                db.insert(DatabaseStatic.TABLE_SCENE, null, values);
                                            }
                                            for (int i = 0; i < ConstantUtils.PANEL_SWITCH_DEVICES.length; i++)
                                                for (int j = 0; j < ConstantUtils.PANEL_SWITCH_CONTROL.length; j++) {
                                                    values = new ContentValues();
                                                    values.put(DatabaseStatic.Scene.DEVICE_ID, discoveredDevices.get(devIndex).getDevId());
                                                    values.put(DatabaseStatic.Scene.MAC, discoveredDevices.get(devIndex).getMac());
                                                    values.put(DatabaseStatic.Scene.TYPE, ConstantUtils.SceneType.deviceInPanel.ordinal());
                                                    values.put(DatabaseStatic.Scene.NAME, ConstantUtils.PANEL_SWITCH_DEVICES[i]);
                                                    values.put(DatabaseStatic.Scene.CHANNEL, ConstantUtils.PANEL_SWITCH_CONTROL[j][i]);
                                                    values.put(DatabaseStatic.Scene.PICTURE, i + "");
                                                    values.put(DatabaseStatic.Scene.POWER, Byte.toUnsignedInt(discoveredDevices.get(devIndex).getPower()));
                                                    db.insert(DatabaseStatic.TABLE_SCENE, null, values);
                                                }
                                            for (int i = 0; i < ConstantUtils.PANEL_CURTAIN_DEVICES.length; i++)
                                                for (int j = 0; j < ConstantUtils.PANEL_CURTAIN_CONTROL.length; j++) {
                                                    values = new ContentValues();
                                                    values.put(DatabaseStatic.Scene.DEVICE_ID, discoveredDevices.get(devIndex).getDevId());
                                                    values.put(DatabaseStatic.Scene.MAC, discoveredDevices.get(devIndex).getMac());
                                                    values.put(DatabaseStatic.Scene.TYPE, ConstantUtils.SceneType.deviceInPanel.ordinal());
                                                    values.put(DatabaseStatic.Scene.NAME, ConstantUtils.PANEL_CURTAIN_DEVICES[i]);
                                                    values.put(DatabaseStatic.Scene.CHANNEL, ConstantUtils.PANEL_CURTAIN_CONTROL[j][i]);
                                                    values.put(DatabaseStatic.Scene.PICTURE, i + "");
                                                    values.put(DatabaseStatic.Scene.POWER, Byte.toUnsignedInt(discoveredDevices.get(devIndex).getPower()));
                                                    db.insert(DatabaseStatic.TABLE_SCENE, null, values);
                                                }
                                        }
                                    } else {
                                        sceneChanged = true;
                                        values.put(DatabaseStatic.Scene.DEVICE_ID, discoveredDevices.get(devIndex).getDevId());
                                        values.put(DatabaseStatic.Scene.MAC, discoveredDevices.get(devIndex).getMac());
                                        values.put(DatabaseStatic.Scene.TYPE, ConstantUtils.SceneType.switcher.ordinal());
                                        values.put(DatabaseStatic.Scene.NAME, bean.getName());
                                        switch (discoveredDevices.get(devIndex).getDevType()) {
                                            case 0x0311:
                                                values.put(DatabaseStatic.Scene.CHANNEL, 5);
                                                break;
                                            case 0x0312:
                                                values.put(DatabaseStatic.Scene.CHANNEL, channel == 4 ? 6 : 4);
                                                break;
                                            default:
                                                values.put(DatabaseStatic.Scene.CHANNEL, channel);
                                        }
                                        values.put(DatabaseStatic.Scene.PICTURE, String.format(Locale.CHINA, "%d", (int) (Math.random() * ConstantUtils.SCENE_PICTURE.length)));
                                        values.put(DatabaseStatic.Scene.POWER, Byte.toUnsignedInt(discoveredDevices.get(devIndex).getPower()));
                                        if (roomId != -1)
                                            values.put(DatabaseStatic.Scene.ROOM_ID, roomId);
                                        int id = (int) db.insert(DatabaseStatic.TABLE_SCENE, null, values);
                                        sceneOrder.get(0).add(id);
                                        if (roomId != -1)
                                            sceneOrder.get(roomIndex).add(id);
                                    }
                                    channel++;
                                }
                                db.setTransactionSuccessful();
                                db.endTransaction();
                                db.close();
                                if (++devIndex >= discoveredDevices.size()) {
                                    if (deviceChanged) {
                                        deviceChanged = false;
                                        initDevicePager();
                                    }
                                    if (sceneChanged) {
                                        sceneChanged = false;
                                        initScenePager();
                                    }
                                    devIndex = 0;
                                    currentDevice = new DeviceBean();
                                    nextDeviceStatus();
                                } else
                                    commHandler.sendEmptyMessage(HANDLER_SEND_CMD);
                            }
                            break;
                            case panelTask:
                            case setTask:
                                if (devIndex < currentTask.size()) {
                                    resendCount = 0;
                                    commHandler.sendEmptyMessage(HANDLER_SEND_CMD);
                                    break;
                                }
                                if (cmdType == CommandType.panelTask) {
                                    Log.d(TAG, "channelIndex:" + channelIndex + ",length" + currentScene.getAllChannel().length
                                            + ",taskBeans:" + taskBeans.size() + ",currentTask:" + currentTask.size());
                                    if (++channelIndex < currentScene.getAllChannel().length) {
                                        devIndex = 0;
                                        resendCount = 0;
                                        currentScene.setTaskBeans(new ArrayList<>(taskBeans));
                                        if (currentScene.getAllChannel().length == ConstantUtils.PANEL_SWITCH_CONTROL.length) {
                                            for (SceneBean.TaskBean bean : currentScene.getTaskBeans())
                                                bean.setAction(ConstantUtils.ActionType.off);
                                        } else {
                                            for (SceneBean.TaskBean bean : currentScene.getTaskBeans())
                                                bean.setAction(channelIndex == 2 ? ConstantUtils.ActionType.pause : ConstantUtils.ActionType.off);
                                        }
                                        commHandler.sendEmptyMessage(HANDLER_SEND_CMD);
                                        break;
                                    }
                                    taskBeans = null;
                                }
                                devIndex = 0;
                            case delete:
                                myToast(R.string.text_set_task_success);
                            case test:
                                working = false;
                                cmdType = CommandType.idle;
                                Log.d(TAG, "idle 11");
                                break;
                            case task:
                            case scene:
                                commHandler.removeMessages(HANDLER_RESEND_SCENE_PRESS);
                            case device:
                                commHandler.removeMessages(HANDLER_RESEND_SWITCH_PRESS);
                            default:
                                if (receiveData.length > SerialCommand.RETURN_COMMAND_CODE_AT) {
                                    StringBuilder m = new StringBuilder();
                                    for (int i = 3; i < 9; i++)
                                        m.append(String.format("%02X", receiveData[i]));
                                    switch (receiveData[SerialCommand.RETURN_COMMAND_CODE_AT]) {
                                        case SerialCommand.CODE_RCV_STATUS:
                                        case SerialCommand.CODE_RCV_CONTROL:
                                            resendCount = 0;
                                            int status = Byte.toUnsignedInt(receiveData[SerialCommand.RETURN_COMMAND_CODE_AT + 1]);
                                            Log.d(TAG, "Get status:" + status);
                                            for (DeviceBean bean : allDevices)
                                                if (m.toString().equals(bean.getMac())) {
                                                    Log.d(TAG, "device kind:" + bean.getKind());
                                                    switch (bean.getKind()) {
                                                        case switcher:
                                                            ConstantUtils.Status s = (status & (1 << (8 - bean.getChannel()))) == 0 ? ConstantUtils.Status.off : ConstantUtils.Status.on;
                                                            bean.setStatus(s);
                                                            break;
                                                        case curtain:
                                                            if ((status & (1 << (8 - 1))) != 0)
                                                                bean.setStatus(ConstantUtils.Status.on);
                                                            else if ((status & (1 << (8 - 3))) != 0)
                                                                bean.setStatus(ConstantUtils.Status.off);
                                                            else
                                                                bean.setStatus(bean.getStatusEnum() == ConstantUtils.Status.off ? ConstantUtils.Status.offPause : ConstantUtils.Status.onPause);
                                                            waitForDoublePress = false;
                                                            break;
                                                        default:
                                                            bean.setStatus(ConstantUtils.Status.on);
                                                    }
                                                    devicesFragments.get(0).updateDevice(bean);
                                                    if (devicesInRoom != null)
                                                        devicesInRoom.updateDevice(bean);
                                                    if (selectDeviceFragments != null)
                                                        selectDeviceFragments.get(0).updateDevice(bean);
                                                    for (HomeDeviceBean b : homeDeviceBeans)
                                                        if (b.getType() == ConstantUtils.TYPE_DEVICE && b.getId() == bean.getId()) {
                                                            refreshHomeItems();
                                                            break;
                                                        }
                                                }
                                            boolean refreshScenes = false;
                                            for (SceneBean bean : allScenes)
                                                if (m.toString().equals(bean.getMac()) && bean.getKind() == ConstantUtils.DeviceKind.switcher && bean.getStatusEnum() == ConstantUtils.Status.offline) {
                                                    bean.setStatus(ConstantUtils.Status.on);
                                                    if (scenesInRoom != null)
                                                        scenesInRoom.updateScene(bean);
                                                    scenesFragments.get(0).updateScene(bean);
                                                    refreshScenes = true;
                                                }
                                            if (refreshScenes) {
                                                refreshFragments(scenesFragments, scenePagerAdapter);
                                                refreshFragments(selectSceneFragments, selectSceneAdapter);
                                            }
                                            refreshFragments(devicesFragments, devicePagerAdapter);
                                            refreshFragments(selectDeviceFragments, selectDeviceAdapter);
                                            break;
                                        case SerialCommand.CODE_RCV_REQUIRE:
                                            for (DeviceBean bean : allDevices)
                                                if (m.toString().equals(bean.getMac())) {
                                                    bean.setStatus(ConstantUtils.Status.on);
                                                    devicesFragments.get(0).updateDevice(bean);
                                                    refreshFragments(devicesFragments, devicePagerAdapter);
                                                    break;
                                                }
                                            for (SceneBean bean : allScenes)
                                                if (m.toString().equals(bean.getMac())) {
                                                    bean.setStatus(ConstantUtils.Status.on);
                                                    scenesFragments.get(0).updateScene(bean);
                                                }
                                            refreshFragments(scenesFragments, scenePagerAdapter);
                                            break;
                                        case SerialCommand.CODE_RCV_SCENE_GET:
                                            if (currentScene == null || currentScene.getMac() == null)
                                                break;
                                            Log.d(TAG, "currentScene:" + currentScene.getMac() + ",mac:" + m + ",channel:" + currentScene.getChannel());
                                            if (m.toString().equals(currentScene.getMac()) &&
                                                    ((currentScene.getType() == ConstantUtils.SceneType.switcher && receiveData[SerialCommand.RETURN_COMMAND_CODE_AT + 1] + 1 == currentScene.getChannel())
                                                            || currentScene.getType() != ConstantUtils.SceneType.switcher && receiveData[SerialCommand.RETURN_COMMAND_CODE_AT + 1] == currentScene.getChannel())) {
                                                if (SerialCommand.RETURN_COMMAND_CODE_AT + (currentScene.getType() == ConstantUtils.SceneType.switcher ? 2 : 3) < receiveData.length - 4) {
                                                    byte[] data = Arrays.copyOfRange(receiveData, SerialCommand.RETURN_COMMAND_CODE_AT + (currentScene.getType() == ConstantUtils.SceneType.switcher ? 2 : 3), receiveData.length - 4);
                                                    devIndex = data.length / 7;
                                                    int index = 0;
                                                    Log.d(TAG, "data:" + data.length + ",dev:" + devIndex + ",type:" + currentScene.getType());
                                                    List<SceneBean.TaskBean> beans = new ArrayList<>();
                                                    try {
                                                        if (devIndex > 0)
                                                            while (index < data.length) {
                                                                StringBuilder m1 = new StringBuilder();
                                                                for (int i = index; i < index + 4; i++)
                                                                    m1.append(String.format("%02X", data[i]));
                                                                index += 4;
                                                                for (DeviceBean b : allDevices) {
                                                                    if (b.getMac().endsWith(m1.toString())
                                                                            && (((1 << (8 - b.getChannel())) & data[index + 2]) != 0)) {
                                                                        if (data[index] == SerialCommand.CODE_SND_TOGGLE) {
                                                                            beans.add(new SceneBean.TaskBean(b.getId(), ConstantUtils.ActionType.toggle));
                                                                            break;
                                                                        } else {
                                                                            beans.add(new SceneBean.TaskBean(b.getId(),
                                                                                    (((1 << (8 - b.getChannel())) & data[index + 1]) == 0) ? ConstantUtils.ActionType.off : ConstantUtils.ActionType.on));
                                                                        }
                                                                        Log.d(TAG, "task:" + beans.get(beans.size() - 1).getActionEnum() + ",id:" + beans.get(beans.size() - 1).getDeviceId());
                                                                    }
                                                                }
                                                                index += 3;
                                                            }
                                                        if (taskAdapter != null && beans.size() > 0) {
                                                            taskBeans = new ArrayList<>(beans);
                                                            taskAdapter.updateList(beans);
                                                            for (SceneBean bean : modifyScenes)
                                                                if (bean.getMac().equals(currentScene.getMac())) {
                                                                    bean.setTaskBeans(taskBeans);
                                                                    break;
                                                                }
                                                            binding.layoutScenes.layoutModify.tvNoTask.setVisibility(beans.size() > 0 ? View.INVISIBLE : View.VISIBLE);
                                                            changeModifyButton(beans);
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                break;
                                            }
                                            break;
                                        case SerialCommand.CODE_RCV_TEMPERATURE_CONTROL:
                                            int address = receiveData[SerialCommand.RETURN_COMMAND_CODE_AT + 1];
                                            int stat = 0;
                                            boolean refresh = false;
                                            Log.d(TAG, "Temp Ctrl:" + receiveData[SerialCommand.RETURN_COMMAND_CODE_AT + 2]);
                                            switch (receiveData[SerialCommand.RETURN_COMMAND_CODE_AT + 2]) {
                                                case SerialCommand.CODE_TEMPERATURE_READ:
                                                    if (receiveData[SerialCommand.RETURN_COMMAND_CODE_AT + 3] >= (byte) 0x0C) {
                                                        for (int i = 0; i < 12; i += 2) {
                                                            int temp = (Byte.toUnsignedInt(receiveData[SerialCommand.RETURN_COMMAND_CODE_AT + 4 + i]) << 8)
                                                                    + Byte.toUnsignedInt(receiveData[SerialCommand.RETURN_COMMAND_CODE_AT + 5 + i]);
                                                            switch (i) {
                                                                case 0:
                                                                    stat += (temp / 5) << 12;
                                                                    break;
                                                                case 2:
                                                                    stat += (((temp - 160) / 5) & 0x3F) << 6;
                                                                    break;
                                                                case 4:
                                                                    stat += temp & ConstantUtils.MASK_STATUS_SWITCH;
                                                                    break;
                                                                case 6:
                                                                    switch (temp) {
                                                                        case 1:
                                                                            stat += ConstantUtils.TemperatureMode.hot.ordinal() << 2;
                                                                            break;
                                                                        case 9:
                                                                            stat += ConstantUtils.TemperatureMode.fan.ordinal() << 2;
                                                                            break;
                                                                    }
                                                                    break;
                                                                case 10:
                                                                    stat += ((temp - 1) << 4) & ConstantUtils.MASK_STATUS_FAN;
                                                                    break;
                                                            }
                                                        }
                                                        refresh = true;
                                                    }
                                                    break;
                                                case SerialCommand.CODE_TEMPERATURE_WRITE:
                                                    int temp = (Byte.toUnsignedInt(receiveData[SerialCommand.RETURN_COMMAND_CODE_AT + 5]) << 8) + Byte.toUnsignedInt(receiveData[SerialCommand.RETURN_COMMAND_CODE_AT + 6]);
                                                    stat = currentDevice.getStatus();
                                                    switch ((Byte.toUnsignedInt(receiveData[SerialCommand.RETURN_COMMAND_CODE_AT + 3]) << 8) + Byte.toUnsignedInt(receiveData[SerialCommand.RETURN_COMMAND_CODE_AT + 4])) {
                                                        case SerialCommand.CODE_TEMPERATURE_SET:
                                                            stat = (stat & ~ConstantUtils.MASK_STATUS_TEMPERATURE) + (((temp - 160) / 5) << 6);
                                                            break;
                                                        case SerialCommand.CODE_TEMPERATURE_SWITCH:
                                                            stat = (stat & ~ConstantUtils.MASK_STATUS_SWITCH) + (temp & ConstantUtils.MASK_STATUS_SWITCH);
                                                            break;
                                                        case SerialCommand.CODE_TEMPERATURE_MODE:
                                                            stat &= ~ConstantUtils.MASK_STATUS_MODE;
                                                            switch (temp) {
                                                                case 1:
                                                                    stat += ConstantUtils.TemperatureMode.hot.ordinal() << 2;
                                                                    break;
                                                                case 9:
                                                                    stat += ConstantUtils.TemperatureMode.fan.ordinal() << 2;
                                                                    break;
                                                            }
                                                            break;
                                                        case SerialCommand.CODE_TEMPERATURE_FAN:
                                                            stat = (stat & ~ConstantUtils.MASK_STATUS_FAN) + (((temp - 1) << 4) & ConstantUtils.MASK_STATUS_FAN);
                                                            break;
                                                    }
                                                    Log.d(TAG, "change:" + (changeTemperatureStatus & ConstantUtils.MASK_STATUS_CLEAR_INDOOR_TEMPERATURE) + ",before:" + (currentDevice.getStatus() & ConstantUtils.MASK_STATUS_CLEAR_INDOOR_TEMPERATURE) + ",after:" + (stat & ConstantUtils.MASK_STATUS_CLEAR_INDOOR_TEMPERATURE) + getString(R.string.text_speech_split) + temp);
                                                    currentDevice.setStatus(stat);
                                                    if (!working || (stat & ConstantUtils.MASK_STATUS_CLEAR_INDOOR_TEMPERATURE) == (changeTemperatureStatus & ConstantUtils.MASK_STATUS_CLEAR_INDOOR_TEMPERATURE)) {
                                                        refresh = true;
                                                    } else {
                                                        working = false;
                                                        Message message = commHandler.obtainMessage(MainActivity.HANDLER_SWITCH_PRESS);
                                                        message.obj = currentDevice;
                                                        message.arg1 = cmdType.ordinal();
                                                        message.arg2 = changeTemperatureStatus;
                                                        commHandler.sendMessage(message);
                                                        return true;
                                                    }
                                            }
                                            if (refresh) {
                                                Log.d(TAG, "Temperature status:" + stat);
                                                int c = 0;
                                                for (DeviceBean bean : allDevices)
                                                    if (bean.getMac().equals(m.toString()) && ++c == address) {
                                                        bean.setStatus(stat);
                                                        devicesFragments.get(0).updateDevice(bean);
                                                        if (devicesInRoom != null)
                                                            devicesInRoom.updateDevice(bean);
                                                        if (selectDeviceFragments != null)
                                                            selectDeviceFragments.get(0).updateDevice(bean);
                                                        for (HomeDeviceBean b : homeDeviceBeans)
                                                            if (b.getType() == ConstantUtils.TYPE_DEVICE && b.getId() == bean.getId()) {
                                                                refreshHomeItems();
                                                                break;
                                                            }
                                                        if (ctrlDialog != null && ctrlDialog.isShowing())
                                                            ctrlDialog.setDeviceBean(bean);
                                                        refreshFragments(devicesFragments, devicePagerAdapter);
                                                        refreshFragments(selectDeviceFragments, selectDeviceAdapter);
                                                        break;
                                                    }
                                            }
                                            break;
                                    }
                                    switch (cmdType) {
                                        case allStatus:
                                            receiveData = null;
                                            nextDeviceStatus();
                                            return true;
                                        case task:
                                            nextTask();
                                            break;
                                        case getTask:
                                            if (devIndex >= ConstantUtils.MAX_TASK_COMMAND) {
                                                commHandler.sendEmptyMessage(HANDLER_SEND_CMD);
                                                return true;
                                            }
                                            break;
                                    }
                                    working = false;
                                    cmdType = CommandType.idle;
                                    Log.d(TAG, "idle 12");
                                }
                                break;
                        }
                        receiveData = null;
                        checkMessageList();
                    }
                    break;
                case HANDLER_ADD_DEVICES:
                    discoveredDevices.add(new DeviceBean(currentDevice));
                    devicePropsBeans.add(new ArrayList<>(addDeviceDialog.getPropsBeans()));
                    if (msg.arg1 == ConstantUtils.CONTINUE) {
                        cmdType = CommandType.waiting;
                        working = false;
                    }
                    break;
                case HANDLER_MODIFY_SCENE:
                    modifyScenes = new ArrayList<>();
                    tempRoomBeans = new ArrayList<>(roomBeans);
                    changeLists = new ArrayList<>();
                    modifyScenes.add(new SceneBean((SceneBean) msg.obj));
                    modifying = false;
                    modifyingScene = true;
                    changeLists.add(false);
                    enterModifyScene(modifyScenes.get(0));
                    break;
                case HANDLER_ADD_FINISHED:
                    cmdType = msg.arg1 == ConstantUtils.EXIT_SEARCH ? CommandType.exitSearch : CommandType.complete;
                    commHandler.sendEmptyMessage(HANDLER_SEND_CMD);
                    break;
                case HANDLER_ITEM_SELECTED:
                    if (msg.obj instanceof SceneBean)
                        refreshFragments(selectSceneFragments, selectSceneAdapter);
                    else {
                        if (singleSelect) {
                            currentButtonConfirm.setEnabled(((DeviceBean) msg.obj).isSelected());
                            for (DeviceBean bean : selectDeviceFragments.get(0).getDeviceBeans())
                                if (bean.getId() != ((DeviceBean) msg.obj).getId() && bean.isSelected())
                                    bean.setSelected(false);
                        }
                        refreshFragments(selectDeviceFragments, selectDeviceAdapter);
                    }
                    break;
                case HANDLER_DELETE_SCENE:
                    deviceChanged = true;
                    for (int i = 0; i < scenesFragments.size(); i++) {
                        scenesFragments.get(i).deleteScene(msg.arg1);
                        if (i != currentTabTitle.getSelectedTabPosition())
                            scenePagerAdapter.notifyItemChanged(i);
                    }
                    commHandler.sendMessageDelayed(commHandler.obtainMessage(HANDLER_FRESH_TAB, false), 200);
                    break;
                case HANDLER_DELETE_DEVICE:
                    deviceChanged = true;
                    for (int i = 0; i < devicesFragments.size(); i++) {
                        devicesFragments.get(i).deleteDevice(msg.arg1);
                        if (i != currentTabTitle.getSelectedTabPosition())
                            devicePagerAdapter.notifyItemChanged(i);
                    }
                    commHandler.sendMessageDelayed(commHandler.obtainMessage(HANDLER_FRESH_TAB, true), 200);
                    break;
                case HANDLER_FRESH_TAB:
                    for (int i = 0; i < roomBeans.size(); i++) {
                        TabLayout.Tab tab = currentTabTitle.getTabAt(i);
                        if (tab != null)
                            tab.setText(String.format(Locale.CHINA, "%s(%d)", roomBeans.get(i).getName(), (boolean) msg.obj ?
                                    (devicesFragments.get(i).getChangedDeviceBeans() == null ? devicesFragments.get(i).getDeviceBeans().size() - devicesFragments.get(i).getDeletedSize() : devicesFragments.get(i).getChangedDeviceBeans().size()) :
                                    (scenesFragments.get(i).getChangedSceneBeans() == null ? scenesFragments.get(i).getSceneBeans().size() - scenesFragments.get(i).getDeletedSize() : scenesFragments.get(i).getChangedSceneBeans().size())));
                    }
                    break;
                case HANDLER_RENAME_DEVICE:
                    deviceChanged = true;
                    refreshFragments(devicesFragments, devicePagerAdapter);
                    break;
                case HANDLER_DRAG:
                    deviceChanged = true;
                    if (msg.arg1 == ConstantUtils.TYPE_DEVICE)
                        binding.layoutDevices.layoutList.vpDevices.setUserInputEnabled(ConstantUtils.DRAG_STOP == msg.arg2);
                    else
                        binding.layoutScenes.layoutList.vpScenes.setUserInputEnabled(ConstantUtils.DRAG_STOP == msg.arg2);
                    break;
            }
            return true;
        }
    });

    private byte[] nextTemperatureCommand() {
        byte[] data = null;
        if ((currentDevice.getStatus() & ConstantUtils.MASK_STATUS_SWITCH) != (changeTemperatureStatus & ConstantUtils.MASK_STATUS_SWITCH))
            data = SerialCommand.makeTemperatureCmd(countTemperatureDevice(), false, SerialCommand.CODE_TEMPERATURE_SWITCH, changeTemperatureStatus & ConstantUtils.MASK_STATUS_SWITCH);
        else if ((currentDevice.getStatus() & ConstantUtils.MASK_STATUS_MODE) != (changeTemperatureStatus & ConstantUtils.MASK_STATUS_MODE)) {
            int[] mode = new int[]{3, 9, 1};
            data = SerialCommand.makeTemperatureCmd(countTemperatureDevice(), false, SerialCommand.CODE_TEMPERATURE_MODE, mode[(changeTemperatureStatus & ConstantUtils.MASK_STATUS_MODE) >> 2]);
        } else if ((currentDevice.getStatus() & ConstantUtils.MASK_STATUS_FAN) != (changeTemperatureStatus & ConstantUtils.MASK_STATUS_FAN))
            data = SerialCommand.makeTemperatureCmd(countTemperatureDevice(), false, SerialCommand.CODE_TEMPERATURE_FAN, ((changeTemperatureStatus & ConstantUtils.MASK_STATUS_FAN) >> 4) + 1);
        else if ((currentDevice.getStatus() & ConstantUtils.MASK_STATUS_TEMPERATURE) != (changeTemperatureStatus & ConstantUtils.MASK_STATUS_TEMPERATURE))
            data = SerialCommand.makeTemperatureCmd(countTemperatureDevice(), false, SerialCommand.CODE_TEMPERATURE_SET, ((changeTemperatureStatus & ConstantUtils.MASK_STATUS_TEMPERATURE) >> 6) * 5 + 160);
        return data;
    }

    private void checkMessageList() {
        Log.d(TAG, "MessageList:" + messageList.size());
        if (!working && messageList.size() > 0) {
            Message m = messageList.get(0);
            commHandler.sendMessage(m);
            messageList.remove(0);
        }
    }

    private byte countTemperatureDevice() {
        byte result = 0;
        for (DeviceBean bean : allDevices) {
            if (bean.getKind() == ConstantUtils.DeviceKind.temperature && bean.getMac().equals(currentDevice.getMac()) && bean.getId() <= currentDevice.getId())
                result++;
        }
        return result;
    }

    private byte[] makeTaskData() {
        currentTask = currentScene.getTaskBeans();
        byte[] result = new byte[200];
        int len = 0;
        for (int i = devIndex; devIndex < currentTask.size() && devIndex - i < ConstantUtils.MAX_TASK_COMMAND; devIndex++)
            for (DeviceBean bean : allDevices)
                if (bean.getId() == currentTask.get(devIndex).getDeviceId()) {
                    byte[] mac = new byte[4];
                    for (int j = 2; j < 6; j++)
                        mac[j - 2] = (byte) Integer.parseInt(bean.getMac().substring(bean.getMac().length() - 12 + j * 2, bean.getMac().length() - 10 + j * 2), 16);
                    byte[] control = new byte[2];
                    switch (bean.getKind()) {
                        case switcher: {
//                            control = findSameMacDevice(bean.getMac());
                            if (currentTask.get(devIndex).getActionEnum() == ConstantUtils.ActionType.on)
                                control[0] |= (byte) (1 << (8 - bean.getChannel()));
                            control[1] |= (byte) (1 << (8 - bean.getChannel()));
                        }
                        break;
                        case curtain:
                            int channel = 2;
                            switch (currentTask.get(devIndex).getActionEnum()) {
                                case on:
                                    channel = 1;
                                    break;
                                case off:
                                    channel = 3;
                                    break;
                            }
                            control[0] = (byte) (1 << (8 - channel));
                            control[1] = (byte) (1 << (8 - channel));
                            break;
                    }
                    System.arraycopy(mac, 0, result, len, mac.length);
                    result[len + mac.length] = bean.getPower() == SerialCommand.POWER_LOW ? SerialCommand.CODE_SND_CONTROL : SerialCommand.CODE_RCV_CONTROL;
                    System.arraycopy(control, 0, result, len + mac.length + 1, control.length);
                    len += mac.length + 3;
                }
        Log.d(TAG, "Task devIndex:" + devIndex + ",task size:" + currentTask.size());
        return Arrays.copyOfRange(result, 0, len);
    }

    private void addInCurrentTaskList(List<SceneBean.TaskBean> taskBeans) {
        for (SceneBean.TaskBean bean : taskBeans) {
            if (bean.getActionEnum() == ConstantUtils.ActionType.scene) {
                for (SceneBean b : allScenes)
                    if (b.getId() == bean.getDeviceId()) {
                        if (b.getType() == ConstantUtils.SceneType.local)
                            addInCurrentTaskList(b.getTaskBeans());
                        else
                            currentTask.add(new SceneBean.TaskBean(bean));
                        break;
                    }
            } else
                currentTask.add(new SceneBean.TaskBean(bean));
        }
    }

    private void nextTask() {
        resendCount = 0;
        if (devIndex >= currentTask.size()) {
            working = false;
            cmdType = CommandType.idle;
            Log.d(TAG, "idle 13");
            return;
        }
        Message message;
        byte[] control = new byte[2];
        Log.d(TAG, "index1:" + devIndex + ",task size:" + currentTask.size());
        for (SceneBean.TaskBean bean : currentTask) {
            Log.d(TAG, "task:" + bean.getActionEnum() + ",id:" + bean.getDeviceId());
        }
        message = commHandler.obtainMessage(HANDLER_RESEND_SWITCH_PRESS);
        if (currentTask.get(devIndex).getActionEnum() == ConstantUtils.ActionType.scene) {
            for (SceneBean bean : allScenes)
                if (bean.getId() == currentTask.get(devIndex).getDeviceId()) {
                    message.obj = new DeviceBean(bean);
                    if (bean.getKind() == ConstantUtils.DeviceKind.switcher) {
//                        control = findSameMacDevice(bean.getMac());
                        control[0] |= (byte) (1 << (8 - bean.getChannel()));
                        control[1] |= (byte) (1 << (8 - bean.getChannel()));
                    }
                    break;
                }
        } else {
            for (DeviceBean bean : allDevices)
                if (bean.getId() == currentTask.get(devIndex).getDeviceId()) {
                    message.obj = bean;
                    switch (bean.getKind()) {
                        case switcher:
//                            control = findSameMacDevice(bean.getMac());
                            if (currentTask.get(devIndex).getActionEnum() == ConstantUtils.ActionType.on)
                                control[0] |= (byte) (1 << (8 - bean.getChannel()));
                            control[1] |= (byte) (1 << (8 - bean.getChannel()));
                            break;
                        case curtain:
                            int channel = currentTask.get(devIndex).getActionEnum() == ConstantUtils.ActionType.pause ? 2 : currentTask.get(devIndex).getActionEnum() == ConstantUtils.ActionType.on ? 1 : 3;
                            control[0] = (byte) (1 << (8 - channel));
                            control[1] = (byte) (1 << (8 - channel));
                            break;
                        case temperature:
                            switch (currentTask.get(devIndex).getActionEnum()) {
                                case none:
                                    message.arg2 = currentTask.get(devIndex).getAction();
                                    if ((message.arg2 & ConstantUtils.MASK_SET_TEMPERATURE) == 0)
                                        message.arg2 = (message.arg2 & ~ConstantUtils.MASK_STATUS_TEMPERATURE) + (bean.getStatus() & ConstantUtils.MASK_STATUS_TEMPERATURE);
                                    if ((message.arg2 & ConstantUtils.MASK_SET_FAN) == 0)
                                        message.arg2 = (message.arg2 & ~ConstantUtils.MASK_STATUS_FAN) + (bean.getStatus() & ConstantUtils.MASK_STATUS_FAN);
                                    if ((message.arg2 & ConstantUtils.MASK_SET_MODE) == 0)
                                        message.arg2 = (message.arg2 & ~ConstantUtils.MASK_STATUS_MODE) + (bean.getStatus() & ConstantUtils.MASK_STATUS_MODE);
                                    break;
                                case on:
                                    message.arg2 = (bean.getStatus() & ~ConstantUtils.MASK_STATUS_SWITCH) + 1;
                                    break;
                                case off:
                                    message.arg2 = (bean.getStatus() & ~ConstantUtils.MASK_STATUS_SWITCH);
                                    break;
                            }
                            break;
                    }
                }
        }
        Log.d(TAG, "index2:" + devIndex + ",task size:" + currentTask.size() + getString(R.string.text_speech_split) + String.format("%02X,%02X", control[0], control[1]));
        message.arg1 = CommandType.task.ordinal();
        if (message.arg2 == 0)
            message.arg2 = (Byte.toUnsignedInt(control[0]) << 8) + Byte.toUnsignedInt(control[1]);
        commHandler.sendMessage(message);
        devIndex++;
    }

    private byte[] findSameMacDevice(String mac) {
        byte[] control = new byte[2];
        if (mac != null)
            for (int i = currentTask.size() - 1; i > devIndex; i--) {
                if (currentTask.get(i).getActionEnum() == ConstantUtils.ActionType.scene) {
                    for (SceneBean bean : allScenes)
                        if (bean.getId() == currentTask.get(i).getDeviceId() && bean.getKind() == ConstantUtils.DeviceKind.switcher && mac.equals(bean.getMac())) {
                            control[0] |= (byte) (1 << (8 - bean.getChannel()));
                            control[1] |= (byte) (1 << (8 - bean.getChannel()));
                            currentTask.remove(i);
                            break;
                        }
                } else {
                    for (DeviceBean bean : allDevices)
                        if (bean.getId() == currentTask.get(i).getDeviceId() && bean.getKind() == ConstantUtils.DeviceKind.switcher && mac.equals(bean.getMac())) {
                            if (currentTask.get(i).getActionEnum() == ConstantUtils.ActionType.on)
                                control[0] |= (byte) (1 << (8 - bean.getChannel()));
                            control[1] |= (byte) (1 << (8 - bean.getChannel()));
                            currentTask.remove(i);
                            break;
                        }
                }
            }
        return control;
    }

    private void initData(Module module) {
        db = myHelper.getReadableDatabase();
        Cursor cursor;
        switch (module) {
            case room:
                roomBeans = new ArrayList<>();
                List<RoomBean> beans = new ArrayList<>();
                beans.add(new RoomBean(getString(R.string.text_all)));
                cursor = db.query(DatabaseStatic.TABLE_ROOM, null, null, null, null, null, null);
                while (cursor.moveToNext())
                    beans.add(new RoomBean(cursor.getInt(DatabaseStatic.Room.COL_ID), cursor.getString(DatabaseStatic.Room.COL_NAME)));
                cursor.close();
                if (settings.getRoomOrder() == null || settings.getRoomOrder().size() == 0) {
                    roomOrder = new ArrayList<>();
                    for (int i = 1; i < beans.size(); i++)
                        roomOrder.add(beans.get(i).getId());
                    roomBeans = beans;
                    settings.setRoomOrder(roomOrder);
                    saveSettings();
                } else {
                    roomBeans.add(new RoomBean(getString(R.string.text_all)));
                    roomOrder = settings.getRoomOrder();
                    for (Integer i : roomOrder)
                        for (RoomBean bean : beans)
                            if (bean.getId() == i) {
                                roomBeans.add(bean);
                                break;
                            }
                }
                break;
            case device:
                cursor = db.query(DatabaseStatic.TABLE_OWNED, null, DatabaseStatic.OwnedDevices.NAME + "<>?", new String[]{DatabaseStatic.DEFAULT_GATEWAY}, null, null, null);
                List<DeviceBean> deviceBeans = new ArrayList<>();
                while (cursor.moveToNext()) {
                    DeviceBean device = new DeviceBean();
                    device.setId(cursor.getInt(DatabaseStatic.OwnedDevices.COL_ID));
                    device.setMac(cursor.getString(DatabaseStatic.OwnedDevices.COL_MAC));
                    device.setChannel(cursor.getInt(DatabaseStatic.OwnedDevices.COL_CHANNEL));
                    device.setPower((byte) cursor.getInt(DatabaseStatic.OwnedDevices.COL_POWER));
                    device.setRoomId(cursor.getInt(DatabaseStatic.OwnedDevices.COL_ROOM_ID));
                    device.setName(cursor.getString(DatabaseStatic.OwnedDevices.COL_NAME));
                    device.setPicture(cursor.getString(DatabaseStatic.OwnedDevices.COL_PICTURE));
                    device.setDevId(cursor.getInt(DatabaseStatic.OwnedDevices.COL_DEVICE_ID));
                    device.setStatus(ConstantUtils.Status.offline);
                    Cursor c = db.query(DatabaseStatic.TABLE_DEVICE, null, DatabaseStatic.Device.ID + "=?", new String[]{device.getDevId() + ""}, null, null, null);
                    if (c.moveToNext()) {
                        device.setDevType(c.getInt(DatabaseStatic.Device.COL_TYPE));
                        int kind = c.getInt(DatabaseStatic.Device.COL_KIND);
                        if (kind < ConstantUtils.DeviceKind.values().length)
                            device.setKind(ConstantUtils.DeviceKind.values()[kind]);
                    }
                    c.close();
                    if (device.getKind() == ConstantUtils.DeviceKind.curtain && device.getChannel() > 1 && device.getChannel() < 4)
                        continue;
                    for (int i = 1; i < roomBeans.size(); i++)
                        if (roomBeans.get(i).getId() == device.getRoomId())
                            device.setRoom(roomBeans.get(i).getName());
                    if (allDevices != null)
                        for (DeviceBean bean : allDevices)
                            if (bean.getId() == device.getId()) {
                                device.setStatus(bean.getStatus());
                                break;
                            }
                    deviceBeans.add(device);
                }
                cursor.close();
                allDevices = deviceBeans;
                break;
            case scene:
                cursor = db.query(DatabaseStatic.TABLE_SCENE, null, DatabaseStatic.Scene.TYPE + "<>?", new String[]{ConstantUtils.SceneType.deviceInPanel.ordinal() + ""}, null, null, null);
                List<SceneBean> sceneBeans = new ArrayList<>();
                while (cursor.moveToNext()) {
                    SceneBean scene = new SceneBean();
                    scene.setId(cursor.getInt(DatabaseStatic.Scene.COL_ID));
                    scene.setChannel(cursor.getInt(DatabaseStatic.Scene.COL_CHANNEL));
                    scene.setPower((byte) cursor.getInt(DatabaseStatic.Scene.COL_POWER));
                    scene.setRoomId(cursor.getInt(DatabaseStatic.Scene.COL_ROOM_ID));
                    scene.setName(cursor.getString(DatabaseStatic.Scene.COL_NAME));
                    scene.setMac(cursor.getString(DatabaseStatic.Scene.COL_MAC));
                    scene.setDevId(cursor.getInt(DatabaseStatic.Scene.COL_DEVICE_ID));
                    scene.setKeywords(cursor.getString(DatabaseStatic.Scene.COL_KEYWORDS));
                    scene.setPicture(cursor.getString(DatabaseStatic.Scene.COL_PICTURE));
                    scene.setType(ConstantUtils.SceneType.values()[cursor.getInt(DatabaseStatic.Scene.COL_TYPE)]);
                    Cursor c = db.query(DatabaseStatic.TABLE_DEVICE, null, DatabaseStatic.Device.ID + "=?", new String[]{scene.getDevId() + ""}, null, null, null);
                    if (c.moveToNext()) {
                        scene.setDevType(c.getInt(DatabaseStatic.Device.COL_TYPE));
                        int kind = c.getInt(DatabaseStatic.Device.COL_KIND);
                        if (kind < ConstantUtils.DeviceKind.values().length)
                            scene.setKind(ConstantUtils.DeviceKind.values()[kind]);
                    }
                    c.close();
                    for (int i = 1; i < roomBeans.size(); i++)
                        if (roomBeans.get(i).getId() == scene.getRoomId())
                            scene.setRoom(roomBeans.get(i).getName());
                    c = db.query(DatabaseStatic.TABLE_TASK, null, DatabaseStatic.Task.SCENE_ID + "=?", new String[]{scene.getId() + ""}, null, null, null);
                    while (c.moveToNext()) {
                        SceneBean.TaskBean taskBean = new SceneBean.TaskBean();
                        taskBean.setAction(c.getInt(DatabaseStatic.Task.COL_ACTION));
                        taskBean.setDeviceId(c.getInt(DatabaseStatic.Task.COL_DEVICE_ID));
                        scene.getTaskBeans().add(taskBean);
                    }
                    c.close();
                    if (allScenes != null)
                        for (SceneBean bean : allScenes)
                            if (bean.getId() == scene.getId()) {
                                scene.setStatus(bean.getStatus());
                                break;
                            }
                    sceneBeans.add(scene);
                }
                for (SceneBean bean : sceneBeans) {
                    if (bean.getTaskBeans() != null && bean.getTaskBeans().size() > 0) {
                        Iterator<SceneBean.TaskBean> iterator = bean.getTaskBeans().iterator();
                        while (iterator.hasNext()) {
                            SceneBean.TaskBean b = iterator.next();
                            boolean notFound = true;
                            if (b.getActionEnum() == ConstantUtils.ActionType.scene) {
                                for (SceneBean bean1 : sceneBeans)
                                    if (bean1.getId() == b.getDeviceId()) {
                                        notFound = false;
                                        break;
                                    }
                            } else {
                                for (DeviceBean bean1 : allDevices)
                                    if (bean1.getId() == b.getDeviceId()) {
                                        notFound = false;
                                        break;
                                    }
                            }
                            if (notFound)
                                iterator.remove();
                        }
                    }
                }
                allScenes = sceneBeans;
                cursor.close();
                break;
        }
        db.close();
    }

    private void nextDeviceStatus() {
        resendCount = 0;
        List<DeviceBean> deviceBeans;
        if (discoveredDevices != null)
            deviceBeans = discoveredDevices;
        else
            deviceBeans = allDevices;
        if (discoveredDevices != null)
            Log.d(TAG, "begin index:" + devIndex + ",size=" + deviceBeans.size() + getString(R.string.text_speech_split) + discoveredDevices.get(devIndex).getMac());
        while (devIndex < deviceBeans.size() && deviceBeans.get(devIndex).getMac().equals(currentDevice.getMac()))
            devIndex++;
        Log.d(TAG, "index:" + devIndex + ",size=" + deviceBeans.size() + getString(R.string.text_speech_split) + currentDevice.getMac());
        if (devIndex < deviceBeans.size()) {
            currentDevice = deviceBeans.get(devIndex);
            cmdType = CommandType.allStatus;
            commHandler.sendEmptyMessage(HANDLER_SEND_CMD);
            return;
        }
        working = false;
        cmdType = CommandType.idle;
        Log.d(TAG, "idle 15");
        currentDevice = new DeviceBean();
        if (discoveredDevices != null) {
            discoveredDevices = null;
            if (addDeviceDialog != null && addDeviceDialog.isShowing())
                addDeviceDialog.dismiss();
        }
    }

    private void initVoice() {
        Intent localIntent = new Intent();
        localIntent.setComponent(new ComponentName("com.yhk.rabbit.voice", "com.yhk.rabbit.voice.service.SpeechService"));
        startService(localIntent);
        NRMessage.init(getApplication());
        NRMessage.addObserver(APIMsgBean.com_sznaner_voice_callback, data -> {
            Log.d(TAG, "语音:data=" + data);

            if (data == null) return;
            if (data.length() == 0) return;

            APIMsgBean bean = JSONObject.parseObject(data, APIMsgBean.class);
            String msg = bean.getMsg();
            switch (msg) {
                case "sys.dialog.start": //对话开始
                    Log.d(TAG, "语音:" + "对话开始");
                    //你可以在此处显示语音动画
                    //dialog.show();
                    break;
                case "context.input.text": {//识别中
                    VoiceInputBean inputBean = JSONObject.parseObject(bean.getData(), VoiceInputBean.class);
                    String dialogText = ((inputBean.getText() != null) && inputBean.getText().length() > 0) ? inputBean.getText() : inputBean.getVar();

                    Log.d(TAG, "语音:" + "识别中," + dialogText);
                    break;
                }
                case "asr.speech.result": {//语音识别结果
                    //你可以在此处隐藏语音动画
                    //dialog.hide();
                    VoiceInputBean inputBean = JSONObject.parseObject(bean.getData(), VoiceInputBean.class);
                    String speechText = inputBean.getText();

                    Log.d(TAG, "识别结束/结果:" + speechText + getString(R.string.text_speech_split) + cmdType + getString(R.string.text_speech_split) + speechText.contains("开"));
//                    waitForVoiceDialog = !speechText.isEmpty();
//                    if (speechText.isEmpty())
//                        NRMessage.post(APIMsgBean.com_sznaner_voice_set, JSON.toJSONString(new APIMsgBean(APIMsgBean.stopDialog)));
                    if (!lastSpeech.equals(speechText)) {
                        int room;
                        for (room = 1; room < roomBeans.size(); room++)
                            if (speechText.contains(roomBeans.get(room).getName() + getString(R.string.text_speech_of)))
                                break;
                        if (speechText.contains("开") || speechText.contains("关") || speechText.contains("暂停") || speechText.contains("停止")) {
                            ConstantUtils.Status status = speechText.contains("暂停") || speechText.contains("停止") ? ConstantUtils.Status.onPause : speechText.contains("开") ? ConstantUtils.Status.off : ConstantUtils.Status.on;
                            currentTask = new ArrayList<>();
                            String[] operateDevices = speechText.split(getString(R.string.text_speech_and));
                            for (String device : operateDevices) {
                                List<String> match = new ArrayList<>();
                                List<Integer> id = new ArrayList<>();
                                int maxMatchLen = 0;
                                for (int i = 0; i < allDevices.size(); i++)
                                    if (device.contains(allDevices.get(i).getName()) && (room >= roomBeans.size() || device.contains(allDevices.get(i).getRoom()))
                                            && (status != ConstantUtils.Status.onPause || allDevices.get(i).getKind() == ConstantUtils.DeviceKind.curtain)) {
                                        match.add(allDevices.get(i).getName());
                                        id.add(i);
                                        maxMatchLen = Math.max(maxMatchLen, allDevices.get(i).getName().length());
                                    }
                                for (int i = match.size() - 1; i >= 0; i--)
                                    if (match.get(i).length() != maxMatchLen) {
                                        match.remove(i);
                                        id.remove(i);
                                    }
                                if (id.size() > 0)
                                    for (Integer i : id) {
                                        SceneBean.TaskBean taskBean = new SceneBean.TaskBean(allDevices.get(i).getId(), status == ConstantUtils.Status.onPause ? ConstantUtils.ActionType.pause : status == ConstantUtils.Status.off ? ConstantUtils.ActionType.on : ConstantUtils.ActionType.off);
                                        currentTask.add(taskBean);
                                        Log.d(TAG, "id:" + taskBean.getDeviceId() + ",ac:" + taskBean.getActionEnum());
                                    }
                            }
                            if (currentTask.size() > 0) {
                                if (cmdType != CommandType.task && (addDeviceDialog == null || !addDeviceDialog.isShowing())) {
                                    StringBuilder tts = new StringBuilder();
                                    tts.append(status == ConstantUtils.Status.onPause ? getString(R.string.tts_device_pause) : status == ConstantUtils.Status.off ? getString(R.string.tts_device_on) : getString(R.string.tts_device_off));
                                    if (currentTask.size() == 1) {
                                        for (DeviceBean b : allDevices)
                                            if (b.getId() == currentTask.get(0).getDeviceId()) {
                                                if (b.getRoom() != null && !b.getRoom().isEmpty())
                                                    tts.append(b.getRoom()).append(getString(R.string.text_speech_of));
                                                tts.append(b.getName());
                                                break;
                                            }
                                    } else {
                                        tts.append(getString(R.string.text_all));
                                        if (speechText.contains(getString(R.string.text_speech_and)))
                                            tts.append(getString(R.string.tts_appoint_devices));
                                        else if (room < roomBeans.size())
                                            tts.append(roomBeans.get(room).getName()).append(getString(R.string.text_speech_of)).append(speechText.substring(speechText.indexOf(getString(R.string.text_speech_of)) + 1));
                                        else {
                                            String[] keys = new String[]{"全部", "所有", "开", "关闭", "关掉", "关", "暂停", "停止"};
                                            boolean notFound = true;
                                            for (String key : keys) {
                                                final int i = speechText.indexOf(key) + key.length();
                                                if (speechText.contains(key) && i < speechText.length()) {
                                                    tts.append(speechText.substring(i));
                                                    notFound = false;
                                                    break;
                                                }
                                            }
                                            if (notFound)
                                                tts.append(getString(R.string.tts_appoint_devices));
                                        }
                                    }
                                    devIndex = 0;
                                    nextTask();
                                    speakTTS(tts.toString());
                                    lastSpeech = speechText;
                                } else
                                    speakTTS(getString(R.string.tts_busy));
                                return;
                            }
                        } else {
                            for (DeviceBean b : allDevices)
                                if (speechText.contains(b.getName()) && (room >= roomBeans.size() || speechText.contains(b.getRoom()))
                                        && b.getKind() == ConstantUtils.DeviceKind.temperature) {
                                    StringBuilder tts = new StringBuilder().append(String.format(getString(R.string.tts_device_temperature),
                                            b.getRoom() == null || b.getRoom().isEmpty() ? b.getName() : b.getRoom() + getString(R.string.text_speech_of) + b.getName()));
                                    int[] level = new int[]{R.string.text_speech_low_level, R.string.text_speech_mid_level, R.string.text_speech_high_level, R.string.text_speech_auto_level};
                                    for (int i = 0; i < level.length; i++) {
                                        String[] textLevel = getString(level[i]).split(getString(R.string.text_speech_split));
                                        for (String s : textLevel)
                                            if (speechText.contains(s)) {
                                                Message message = commHandler.obtainMessage(HANDLER_SWITCH_PRESS);
                                                message.obj = b;
                                                message.arg1 = CommandType.device.ordinal();
                                                message.arg2 = (b.getStatus() & ~ConstantUtils.MASK_STATUS_FAN) + (i << 4);
                                                commHandler.sendMessage(message);
                                                tts.append(textLevel[0]);
                                                tts.append(i == level.length - 1 ? "风量" : "速风量");
                                                lastSpeech = speechText;
                                                speakTTS(tts.toString());
                                                return;
                                            }
                                    }
                                    String[] textMode = getString(R.string.text_speech_temperature_mode).split(getString(R.string.text_speech_split));
                                    for (int i = 0; i < textMode.length; i++)
                                        if (speechText.contains(textMode[i])) {
                                            Message message = commHandler.obtainMessage(HANDLER_SWITCH_PRESS);
                                            message.obj = b;
                                            message.arg1 = CommandType.device.ordinal();
                                            message.arg2 = (b.getStatus() & ~ConstantUtils.MASK_STATUS_MODE) + (i << 2);
                                            commHandler.sendMessage(message);
                                            tts.append(textMode[i]).append("模式");
                                            lastSpeech = speechText;
                                            speakTTS(tts.toString());
                                            return;
                                        }

                                    Map<Character, Integer> textNumber = new HashMap<Character, Integer>() {
                                        {
                                            put('零', 0);
                                            put('一', 1);
                                            put('二', 2);
                                            put('三', 3);
                                            put('四', 4);
                                            put('五', 5);
                                            put('六', 6);
                                            put('七', 7);
                                            put('八', 8);
                                            put('九', 9);
                                            put('十', 10);
                                            put('百', 0);
                                            put('千', 0);
                                            put('万', 0);
                                            put('亿', 0);
                                            put('点', 0);
                                        }
                                    };
                                    int temp;
                                    int end = -1;
                                    int start = 0;
                                    for (int i = speechText.length() - 1; i >= 0; i--)
                                        if (textNumber.containsKey(speechText.charAt(i)) && end == -1) {
                                            end = i + 1;
                                        } else if (end != -1 && !textNumber.containsKey(speechText.charAt(i))) {
                                            start = i + 1;
                                            break;
                                        }
                                    String[] texts = speechText.substring(start, end).split("点");
                                    if (texts.length <= 2) {
                                        temp = TextToNumber.toInteger(texts[0]);
                                        if (temp >= 16 && temp <= 32) {
                                            temp = temp * 2 - 32;
                                            if (texts.length == 2 && TextToNumber.toNumber(texts[1]) == 5)
                                                temp++;
                                            Message message = commHandler.obtainMessage(HANDLER_SWITCH_PRESS);
                                            message.obj = b;
                                            message.arg1 = CommandType.device.ordinal();
                                            message.arg2 = (b.getStatus() & ~ConstantUtils.MASK_STATUS_TEMPERATURE) + (temp << 6);
                                            commHandler.sendMessage(message);
                                            tts.append(speechText.substring(start, end)).append("度");
                                            lastSpeech = speechText;
                                            speakTTS(tts.toString());
                                        }
                                    }
                                    return;
                                }
                        }
                        for (SceneBean b : allScenes) {
                            boolean match = speechText.contains("开") && speechText.contains(b.getName());
                            if (!match && b.getKeywords() != null && !b.getKeywords().isEmpty()) {
                                String[] keywords = b.getKeywords().split(" ");
                                for (String keyword : keywords) {
                                    if (keyword != null && !keyword.isEmpty() && speechText.contains(keyword)) {
                                        match = true;
                                        break;
                                    }
                                }
                            }
                            if (match) {
                                if (room < roomBeans.size() && !speechText.contains(b.getRoom()))
                                    continue;
                                if (cmdType != CommandType.task && (addDeviceDialog == null || !addDeviceDialog.isShowing())) {
                                    lastSpeech = speechText;
                                    speakTTS(getString(R.string.tts_scene_on) + (room < roomBeans.size() ? b.getRoom() + getString(R.string.text_speech_of) : "") +
                                            b.getName() + getString(R.string.text_scene));
                                    Message message = commHandler.obtainMessage(HANDLER_SCENE_PRESS);
                                    message.obj = new SceneBean(b);
                                    message.arg1 = CommandType.scene.ordinal();
                                    commHandler.sendMessage(message);
                                } else
                                    speakTTS(getString(R.string.tts_busy));
                            }
                        }
                    }
                    break;
                }
                case "sys.dialog.end": //对话结束(语音播报结束)
                    Log.d(TAG, "语音:" + "对话结束(语音播报结束");
//                    if (waitForVoiceDialog) {
//                        otherHandler.removeMessages(HANDLER_STOP_VOICE_DIALOG);
//                        NRMessage.post(APIMsgBean.com_sznaner_voice_set, JSON.toJSONString(new APIMsgBean(APIMsgBean.startDialog)));
//                        otherHandler.sendEmptyMessageDelayed(HANDLER_STOP_VOICE_DIALOG, 10000);
//                    }
                    break;
            }
        });
        if (!settings.isWakeupWord()) {
            new Handler(message -> {
                List<WakeupWordBean> wakeupWordList = new ArrayList<>();
                WakeupWordBean bean = new WakeupWordBean();
                bean.setPinyin("ni hao xiao yi");
                bean.setThreshold("0.171");
                bean.setWord("你好小易");
                wakeupWordList.add(bean);
                NRMessage.post(APIMsgBean.com_sznaner_voice_set, JSON.toJSONString(new APIMsgBean("updateMainWakeupWords", JSON.toJSONString(wakeupWordList))));
                settings.setWakeupWord(true);
                saveSettings();
                return false;
            }).sendEmptyMessageDelayed(1, 1000);
        }
    }

    private void speakTTS(String text) {
        Log.d(TAG, "语音播报：" + text);
        NRMessage.post(APIMsgBean.com_sznaner_voice_set, JSON.toJSONString(new APIMsgBean(APIMsgBean.stopDialog)));
        new Handler(msg -> {
            String m = APIMsgBean.speakTTS;
            String data = JSONObject.toJSONString(new TTSBean(text, 1));
            NRMessage.post(APIMsgBean.com_sznaner_voice_set, JSON.toJSONString(new APIMsgBean(m, data)));
            return false;
        }).sendEmptyMessageDelayed(1, 200);
    }

    private void initMusic() {
        NRMessage.addObserver(APIMsgBean.com_sznaner_music_callback_music, data -> {
            Log.d(TAG, "歌曲:" + data);
            APIMsgBean bean = JSONObject.parseObject(data, APIMsgBean.class);
            musicBean = JSONObject.parseObject(bean.getData(), APIMusicBean.class);
            if (null != musicBean.getCover() && !musicBean.getCover().isEmpty())
                new DownloadPictureTask().execute(musicBean.getCover(), "Album");
            otherHandler.sendEmptyMessage(HANDLER_MUSIC_SONG);
        });
        NRMessage.addObserver(APIMsgBean.com_sznaner_music_callback_progress, data -> {
            APIMsgBean bean = JSONObject.parseObject(data, APIMsgBean.class);
            progressBean = JSONObject.parseObject(bean.getData(), APIMusicProgressBean.class);
            otherHandler.sendEmptyMessage(HANDLER_MUSIC_ELAPSE);
        });
    }

    private void initDevicePager() {
        initData(Module.device);
        refreshHomeItems();
        deviceLists = new ArrayList<>();
        for (int i = 0; i < roomBeans.size(); i++) {
            deviceLists.add(new ArrayList<>());
        }
        if (settings.getDeviceOrder() == null || settings.getDeviceOrder().size() != roomBeans.size()) {
            deviceOrder = new ArrayList<>();
            for (int i = 0; i < roomBeans.size(); i++) {
                deviceOrder.add(new ArrayList<>());
            }
            for (DeviceBean bean : allDevices) {
                DeviceBean b = new DeviceBean(bean);
                deviceLists.get(0).add(b);
                deviceOrder.get(0).add(bean.getId());
                for (int i = 1; i < roomBeans.size(); i++)
                    if (b.getRoomId() == roomBeans.get(i).getId()) {
                        deviceLists.get(i).add(b);
                        deviceOrder.get(i).add(b.getId());
                        break;
                    }
            }
            settings.setDeviceOrder(deviceOrder);
            saveSettings();
        } else {
            deviceOrder = settings.getDeviceOrder();
            for (Integer j : deviceOrder.get(0))
                for (DeviceBean bean : allDevices)
                    if (bean.getId() == j) {
                        deviceLists.get(0).add(new DeviceBean(bean));
                        break;
                    }
            for (int i = 1; i < deviceOrder.size(); i++)
                for (Integer j : deviceOrder.get(i))
                    for (DeviceBean bean : deviceLists.get(0))
                        if (bean.getId() == j && bean.getRoomId() == roomBeans.get(i).getId()) {
                            deviceLists.get(i).add(bean);
                            break;
                        }
            boolean addNew = false;
            if (allDevices.size() != deviceLists.get(0).size())
                for (DeviceBean bean : allDevices)
                    if (!deviceLists.get(0).contains(bean)) {
                        deviceOrder.get(0).add(bean.getId());
                        deviceLists.get(0).add(new DeviceBean(bean));
                        addNew = true;
                    }
            for (DeviceBean bean : deviceLists.get(0))
                if (bean.getRoomId() != 0)
                    for (int i = 1; i < roomBeans.size(); i++)
                        if (roomBeans.get(i).getId() == bean.getRoomId() && !deviceLists.get(i).contains(bean)) {
                            deviceOrder.get(i).add(bean.getId());
                            deviceLists.get(i).add(bean);
                            addNew = true;
                        }
            if (addNew) {
                settings.setDeviceOrder(deviceOrder);
                saveSettings();
            }
        }
        devicesFragments = new ArrayList<>();
        binding.layoutDevices.layoutList.tvModify.setVisibility(allDevices.size() == 0 ? View.INVISIBLE : View.VISIBLE);
        int index = 0;
        if (currentTabTitle == binding.layoutScenes.layoutList.tlScenes)
            index = currentTabTitle.getSelectedTabPosition();
        binding.layoutDevices.layoutList.tlDevices.removeAllTabs();
        for (int i = 0; i < roomBeans.size(); i++) {
            binding.layoutDevices.layoutList.tlDevices.addTab(binding.layoutDevices.layoutList.tlDevices.newTab());
            devicesFragments.add(new DevicesFragment(deviceLists.get(i), commHandler));
        }
        devicePagerAdapter = new DevicePagerAdapter(MainActivity.this, devicesFragments);
        currentTabTitle = binding.layoutDevices.layoutList.tlDevices;
        fillDevicePager(binding.layoutDevices.layoutList.vpDevices, deviceLists, devicePagerAdapter, null);
        if (index != currentTabTitle.getSelectedTabPosition() && index < currentTabTitle.getTabCount())
            currentTabTitle.selectTab(currentTabTitle.getTabAt(index));
        refreshFragments(devicesFragments, devicePagerAdapter);
    }

    private void initScenePager() {
        initData(Module.scene);
        refreshHomeItems();
        sceneLists = new ArrayList<>();
        for (int i = 0; i < roomBeans.size(); i++) {
            sceneLists.add(new ArrayList<>());
        }
        if (settings.getSceneOrder() == null || settings.getSceneOrder().size() != roomBeans.size()) {
            sceneOrder = new ArrayList<>();
            for (int i = 0; i < roomBeans.size(); i++) {
                sceneOrder.add(new ArrayList<>());
            }
            for (SceneBean bean : allScenes) {
                SceneBean b = new SceneBean(bean);
                sceneLists.get(0).add(b);
                sceneOrder.get(0).add(bean.getId());
                for (int i = 1; i < roomBeans.size(); i++)
                    if (b.getRoomId() == roomBeans.get(i).getId()) {
                        sceneLists.get(i).add(b);
                        sceneOrder.get(i).add(b.getId());
                        break;
                    }
            }
            settings.setSceneOrder(sceneOrder);
            saveSettings();
        } else {
            sceneOrder = settings.getSceneOrder();
            for (Integer j : sceneOrder.get(0))
                for (SceneBean bean : allScenes)
                    if (bean.getId() == j) {
                        sceneLists.get(0).add(new SceneBean(bean));
                        break;
                    }
            for (int i = 1; i < sceneOrder.size(); i++)
                for (Integer j : sceneOrder.get(i))
                    for (SceneBean bean : sceneLists.get(0))
                        if (bean.getId() == j && bean.getRoomId() == roomBeans.get(i).getId()) {
                            sceneLists.get(i).add(bean);
                            break;
                        }
            boolean addNew = false;
            if (allScenes.size() != sceneLists.get(0).size())
                for (SceneBean bean : allScenes)
                    if (!sceneLists.get(0).contains(bean)) {
                        sceneOrder.get(0).add(bean.getId());
                        sceneLists.get(0).add(new SceneBean(bean));
                        addNew = true;
                    }
            for (SceneBean bean : sceneLists.get(0))
                if (bean.getRoomId() != 0)
                    for (int i = 1; i < roomBeans.size(); i++)
                        if (roomBeans.get(i).getId() == bean.getRoomId() && !sceneLists.get(i).contains(bean)) {
                            sceneLists.get(i).add(bean);
                            sceneOrder.get(i).add(bean.getId());
                            addNew = true;
                        }
            if (addNew) {
                settings.setSceneOrder(sceneOrder);
                saveSettings();
            }
        }
        binding.layoutScenes.layoutList.tvModify.setVisibility(allScenes.size() == 0 ? View.INVISIBLE : View.VISIBLE);
        int index = 0;
        if (currentTabTitle == binding.layoutScenes.layoutList.tlScenes)
            index = currentTabTitle.getSelectedTabPosition();
        binding.layoutScenes.layoutList.tlScenes.removeAllTabs();
        scenesFragments = new ArrayList<>();
        for (int i = 0; i < roomBeans.size(); i++) {
            binding.layoutScenes.layoutList.tlScenes.addTab(binding.layoutScenes.layoutList.tlScenes.newTab());
            scenesFragments.add(new ScenesFragment(sceneLists.get(i), commHandler));
        }
        scenePagerAdapter = new ScenePagerAdapter(MainActivity.this, scenesFragments);
        currentTabTitle = binding.layoutScenes.layoutList.tlScenes;
        fillScenePager(binding.layoutScenes.layoutList.vpScenes, sceneLists, scenePagerAdapter, null);
        if (index != currentTabTitle.getSelectedTabPosition() && index < currentTabTitle.getTabCount())
            currentTabTitle.selectTab(currentTabTitle.getTabAt(index));
        refreshFragments(scenesFragments, scenePagerAdapter);
    }

    private void changeSceneFrame(FrameType which) {
        switch (which) {
            case list:
                binding.layoutScenes.layoutTitle.rlContent.setVisibility(View.GONE);
                binding.layoutScenes.layoutModify.rlContent.setVisibility(View.GONE);
                binding.layoutScenes.layoutSelect.rlContent.setVisibility(View.GONE);
                binding.layoutScenes.layoutBackground.rlContent.setVisibility(View.GONE);
                binding.layoutScenes.layoutList.rlContent.setVisibility(View.VISIBLE);
                break;
            case select:
                binding.layoutScenes.layoutTitle.rlContent.setVisibility(View.VISIBLE);
                binding.layoutScenes.layoutTitle.tvSaveExit.setVisibility(View.GONE);
                binding.layoutScenes.layoutModify.rlContent.setVisibility(View.GONE);
                binding.layoutScenes.layoutSelect.rlContent.setVisibility(View.VISIBLE);
                binding.layoutScenes.layoutBackground.rlContent.setVisibility(View.GONE);
                binding.layoutScenes.layoutList.rlContent.setVisibility(View.GONE);
                break;
            case background:
                binding.layoutScenes.layoutTitle.rlContent.setVisibility(View.VISIBLE);
                binding.layoutScenes.layoutTitle.tvSaveExit.setVisibility(View.GONE);
                binding.layoutScenes.layoutModify.rlContent.setVisibility(View.GONE);
                binding.layoutScenes.layoutSelect.rlContent.setVisibility(View.GONE);
                binding.layoutScenes.layoutBackground.rlContent.setVisibility(View.VISIBLE);
                binding.layoutScenes.layoutList.rlContent.setVisibility(View.GONE);
                break;
            case modify:
                binding.layoutScenes.layoutTitle.rlContent.setVisibility(View.VISIBLE);
                binding.layoutScenes.layoutTitle.tvSaveExit.setVisibility(View.VISIBLE);
                binding.layoutScenes.layoutModify.rlContent.setVisibility(View.VISIBLE);
                binding.layoutScenes.layoutSelect.rlContent.setVisibility(View.GONE);
                binding.layoutScenes.layoutBackground.rlContent.setVisibility(View.GONE);
                binding.layoutScenes.layoutList.rlContent.setVisibility(View.GONE);
                break;
        }
    }

    private void changeDeviceFrame(FrameType which) {
        switch (which) {
            case list:
                binding.layoutDevices.layoutTitle.rlContent.setVisibility(View.GONE);
                binding.layoutDevices.layoutList.rlContent.setVisibility(View.VISIBLE);
                binding.layoutDevices.layoutPanel.rlContent.setVisibility(View.GONE);
                binding.layoutDevices.layoutSelect.rlContent.setVisibility(View.GONE);
                break;
            case select:
                binding.layoutDevices.layoutTitle.rlContent.setVisibility(View.VISIBLE);
                binding.layoutDevices.layoutList.rlContent.setVisibility(View.GONE);
                binding.layoutDevices.layoutPanel.rlContent.setVisibility(View.GONE);
                binding.layoutDevices.layoutSelect.rlContent.setVisibility(View.VISIBLE);
                break;
            case panelCurtain:
            case panelSwitch:
                selectMode = false;
                binding.layoutDevices.layoutTitle.rlContent.setVisibility(View.VISIBLE);
                binding.layoutDevices.layoutList.rlContent.setVisibility(View.GONE);
                binding.layoutDevices.layoutPanel.rlContent.setVisibility(View.VISIBLE);
                binding.layoutDevices.layoutSelect.rlContent.setVisibility(View.GONE);
                List<PanelEntryBean> panelEntryBeans = new ArrayList<>();
                binding.layoutDevices.layoutTitle.tvBack.setText(R.string.text_device);
                binding.layoutDevices.layoutTitle.tvBack.setOnClickListener(v -> changeDeviceFrame(FrameType.list));
                binding.layoutDevices.layoutTitle.tvTitle.setText(R.string.text_set_panel);
                binding.layoutDevices.layoutPanel.btnPanelSwitch.setOnClickListener(v -> {
                    if (binding.layoutDevices.layoutPanel.btnPanelCurtain.isSelected())
                        changeDeviceFrame(FrameType.panelSwitch);
                });
                binding.layoutDevices.layoutPanel.btnPanelCurtain.setOnClickListener(v -> {
                    if (binding.layoutDevices.layoutPanel.btnPanelSwitch.isSelected())
                        changeDeviceFrame(FrameType.panelCurtain);
                });
                switch (which) {
                    case panelSwitch:
                        binding.layoutDevices.layoutPanel.btnPanelSwitch.setSelected(true);
                        binding.layoutDevices.layoutPanel.btnPanelCurtain.setSelected(false);
                        for (String name : ConstantUtils.PANEL_SWITCH_DEVICES)
                            panelEntryBeans.add(new PanelEntryBean(name));
                        break;
                    case panelCurtain:
                        binding.layoutDevices.layoutPanel.btnPanelSwitch.setSelected(false);
                        binding.layoutDevices.layoutPanel.btnPanelCurtain.setSelected(true);
                        for (String name : ConstantUtils.PANEL_CURTAIN_DEVICES)
                            panelEntryBeans.add(new PanelEntryBean(name));
                        break;
                }
                PanelEntryAdapter adapter = new PanelEntryAdapter(MainActivity.this, panelEntryBeans);
                binding.layoutDevices.layoutPanel.lvPanel.setAdapter(adapter);
                binding.layoutDevices.layoutPanel.lvPanel.setOnItemClickListener((parent, view, position, id) -> {
                    if (position >= 0 && position < adapter.getPanelEntryBeans().size())
                        panelSelectDevice(position);
                });
        }
    }

    private void panelSelectDevice(int position) {
        changeDeviceFrame(FrameType.select);
        String name = binding.layoutDevices.layoutPanel.btnPanelCurtain.isSelected() ?
                ConstantUtils.PANEL_CURTAIN_DEVICES[position] : ConstantUtils.PANEL_SWITCH_DEVICES[position];
        binding.layoutDevices.layoutTitle.tvTitle.setText(String.format("%s:\"%s\"", getString(R.string.text_select_device), name));
        binding.layoutDevices.layoutTitle.tvBack.setText(String.format("%s:\"%s\"", getString(R.string.text_set_panel), currentDevice.getName()));
        binding.layoutDevices.layoutTitle.tvBack.setOnClickListener(v -> changeDeviceFrame(binding.layoutDevices.layoutPanel.btnPanelCurtain.isSelected() ? FrameType.panelCurtain : FrameType.panelSwitch));
        db = myHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseStatic.TABLE_SCENE, null, DatabaseStatic.Scene.MAC + "=? and " + DatabaseStatic.Scene.NAME + "=? and " + DatabaseStatic.Scene.TYPE + "=?",
                new String[]{currentDevice.getMac(), name, ConstantUtils.SceneType.deviceInPanel.ordinal() + ""}, null, null, null);
        currentScene = new SceneBean(currentDevice);
        if (cursor.moveToNext()) {
            int[] channels = new int[3];
            currentScene.setId(cursor.getInt(DatabaseStatic.Scene.COL_ID));
            currentScene.setRoomId(cursor.getInt(DatabaseStatic.Scene.COL_ROOM_ID));
            currentScene.setName(cursor.getString(DatabaseStatic.Scene.COL_NAME));
            currentScene.setMac(cursor.getString(DatabaseStatic.Scene.COL_MAC));
            currentScene.setDevId(cursor.getInt(DatabaseStatic.Scene.COL_DEVICE_ID));
            currentScene.setType(ConstantUtils.SceneType.deviceInPanel);
            currentScene.setKind(ConstantUtils.DeviceKind.panel);
            int i = 0;
            channels[i++] = cursor.getInt(DatabaseStatic.Scene.COL_CHANNEL);
            currentScene.setChannel(channels[0]);
            while (cursor.moveToNext())
                channels[i++] = cursor.getInt(DatabaseStatic.Scene.COL_CHANNEL);
            currentScene.setAllChannel(Arrays.copyOfRange(channels, 0, i));
            resendCount = 0;
            cmdType = CommandType.getTask;
            commHandler.sendEmptyMessage(HANDLER_SEND_CMD);
            cursor.close();
            cursor = db.query(DatabaseStatic.TABLE_TASK, null, DatabaseStatic.Task.SCENE_ID + "=?", new String[]{currentScene.getId() + ""}, null, null, null);
            while (cursor.moveToNext()) {
                SceneBean.TaskBean taskBean = new SceneBean.TaskBean();
                taskBean.setAction(ConstantUtils.ActionType.values()[cursor.getInt(DatabaseStatic.Task.COL_ACTION)]);
                taskBean.setDeviceId(cursor.getInt(DatabaseStatic.Task.COL_DEVICE_ID));
                currentScene.getTaskBeans().add(taskBean);
            }
        }
        cursor.close();
        db.close();
        selectDevices = new ArrayList<>();
        currentTabTitle.addTab(currentTabTitle.newTab());
        selectDevices.add(new ArrayList<>());
        for (DeviceBean b : deviceLists.get(0)) {
            if ((binding.layoutDevices.layoutPanel.btnPanelCurtain.isSelected() && b.getKind() == ConstantUtils.DeviceKind.curtain) ||
                    (binding.layoutDevices.layoutPanel.btnPanelSwitch.isSelected() && b.getKind() == ConstantUtils.DeviceKind.switcher)) {
                selectDevices.get(0).add(new DeviceBean(b));
                for (SceneBean.TaskBean bean : currentScene.getTaskBeans())
                    if (bean.getDeviceId() == b.getId()) {
                        selectDevices.get(0).get(selectDevices.get(0).size() - 1).setSelected(true);
                        break;
                    }
            }
        }
        initSelect(Module.device, (binding.layoutDevices.layoutPanel.btnPanelCurtain.isSelected() && position > 1) || (binding.layoutDevices.layoutPanel.btnPanelSwitch.isSelected() && position > 0), true, v -> {
            boolean diff = false;
            if (singleSelect) {
                for (DeviceBean bean : selectDevices.get(0))
                    if (bean.isSelected()) {
                        if (currentScene.getTaskBeans() != null && currentScene.getTaskBeans().size() > 0 && bean.getId() == currentScene.getTaskBeans().get(0).getDeviceId())
                            break;
                        SceneBean.TaskBean b = new SceneBean.TaskBean();
                        b.setAction(ConstantUtils.ActionType.on);
                        b.setDeviceId(bean.getId());
                        currentScene.setTaskBeans(new ArrayList<>());
                        currentScene.getTaskBeans().add(b);
                        diff = true;
                        break;
                    }
            } else {
                List<SceneBean.TaskBean> taskBeans = new ArrayList<>();
                for (DeviceBean bean : selectDevices.get(0))
                    if (bean.isSelected()) {
                        SceneBean.TaskBean b = new SceneBean.TaskBean();
                        b.setAction(ConstantUtils.ActionType.on);
                        b.setDeviceId(bean.getId());
                        taskBeans.add(b);
                    }
                if (currentScene.getTaskBeans() != null && currentScene.getTaskBeans().size() == taskBeans.size()) {
                    for (SceneBean.TaskBean bean : currentScene.getTaskBeans()) {
                        boolean notFound = true;
                        for (SceneBean.TaskBean bean1 : taskBeans)
                            if (bean.getDeviceId() == bean1.getDeviceId()) {
                                notFound = false;
                                break;
                            }
                        if (notFound) {
                            diff = true;
                            break;
                        }
                    }
                } else
                    diff = true;
                if (diff)
                    currentScene.setTaskBeans(taskBeans);
            }
            if (diff) {
                channelIndex = 0;
                resendCount = 0;
                devIndex = 0;
                taskBeans = new ArrayList<>(currentScene.getTaskBeans());
                cmdType = CommandType.panelTask;
                commHandler.sendEmptyMessage(HANDLER_SEND_CMD);
                db = myHelper.getWritableDatabase();
                db.beginTransaction();
                db.delete(DatabaseStatic.TABLE_TASK, DatabaseStatic.Task.SCENE_ID + "=?", new String[]{currentScene.getId() + ""});
                for (SceneBean.TaskBean bean : currentScene.getTaskBeans()) {
                    ContentValues values = new ContentValues();
                    values.put(DatabaseStatic.Task.ACTION, bean.getAction());
                    values.put(DatabaseStatic.Task.SCENE_ID, currentScene.getId());
                    values.put(DatabaseStatic.Task.DEVICE_ID, bean.getDeviceId());
                    db.insert(DatabaseStatic.TABLE_TASK, null, values);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();
            }
            singleSelect = false;
            selectMode = false;
            changeDeviceFrame(binding.layoutDevices.layoutPanel.btnPanelCurtain.isSelected() ? FrameType.panelCurtain : FrameType.panelSwitch);
        });
    }

    private void initSelect(Module module, boolean single, boolean isDevice, View.OnClickListener confirmClick) {
        ViewPager2 viewPager;
        Button btnSelect;
        Button btnSelectAll;
        switch (module) {
            case device:
                currentButtonConfirm = binding.layoutDevices.layoutSelect.btnConfirm;
                currentTabTitle = binding.layoutDevices.layoutSelect.tabTitle;
                viewPager = binding.layoutDevices.layoutSelect.viewPager;
                btnSelect = binding.layoutDevices.layoutSelect.btnSelect;
                btnSelectAll = binding.layoutDevices.layoutSelect.btnSelectAll;
                break;
            case scene:
                currentButtonConfirm = binding.layoutScenes.layoutSelect.btnConfirm;
                currentTabTitle = binding.layoutScenes.layoutSelect.tabTitle;
                viewPager = binding.layoutScenes.layoutSelect.viewPager;
                btnSelect = binding.layoutScenes.layoutSelect.btnSelect;
                btnSelectAll = binding.layoutScenes.layoutSelect.btnSelectAll;
                break;
            case room:
                selectDeviceAdapter = new DevicePagerAdapter(MainActivity.this, selectDeviceFragments);
                currentButtonConfirm = binding.layoutRoom.layoutSelect.btnConfirm;
                currentTabTitle = binding.layoutRoom.layoutSelect.tabTitle;
                viewPager = binding.layoutRoom.layoutSelect.viewPager;
                btnSelect = binding.layoutRoom.layoutSelect.btnSelect;
                btnSelectAll = binding.layoutRoom.layoutSelect.btnSelectAll;
                break;
            case home:
            default:
                currentButtonConfirm = binding.layoutHome.layoutSelect.btnConfirm;
                currentTabTitle = binding.layoutHome.layoutSelect.tabTitle;
                viewPager = binding.layoutHome.layoutSelect.viewPager;
                btnSelect = binding.layoutHome.layoutSelect.btnSelect;
                btnSelectAll = binding.layoutHome.layoutSelect.btnSelectAll;
                break;
        }
        singleSelect = single;
        selectMode = true;
        currentButtonConfirm.setEnabled(!single);
        btnSelect.setText(R.string.button_control);
        btnSelectAll.setVisibility(single ? View.GONE : View.VISIBLE);
        if (isDevice) {
            for (int i = 1; i < deviceLists.size(); i++) {
                currentTabTitle.addTab(currentTabTitle.newTab());
                selectDevices.add(new ArrayList<>());
                for (Integer j : deviceOrder.get(i))
                    for (DeviceBean b : selectDevices.get(0))
                        if (b.getId() == j) {
                            selectDevices.get(i).add(b);
                            break;
                        }
            }
            selectDeviceFragments = new ArrayList<>();
            for (int i = 0; i < roomBeans.size(); i++)
                selectDeviceFragments.add(new DevicesFragment(selectDevices.get(i), commHandler));
            selectDeviceAdapter = new DevicePagerAdapter(MainActivity.this, selectDeviceFragments);
            fillDevicePager(viewPager, selectDevices, selectDeviceAdapter, single ? null : btnSelectAll);
        } else {
            for (int i = 1; i < sceneLists.size(); i++) {
                currentTabTitle.addTab(currentTabTitle.newTab());
                selectScenes.add(new ArrayList<>());
                for (Integer j : sceneOrder.get(i))
                    for (SceneBean b : selectScenes.get(0))
                        if (b.getId() == j) {
                            selectScenes.get(i).add(b);
                            break;
                        }
            }
            selectSceneFragments = new ArrayList<>();
            for (int i = 0; i < roomBeans.size(); i++)
                selectSceneFragments.add(new ScenesFragment(selectScenes.get(i), commHandler));
            selectSceneAdapter = new ScenePagerAdapter(MainActivity.this, selectSceneFragments);
            fillScenePager(viewPager, selectScenes, selectSceneAdapter, single ? null : btnSelectAll);
        }
        initSelectButtonClickListener(isDevice, btnSelect, single ? null : btnSelectAll, currentTabTitle);
        currentButtonConfirm.setOnClickListener(confirmClick);
    }

    private void initRoom() {
        for (int i = 0; i < roomBeans.size(); i++) {
            roomBeans.get(i).setButtonAmount(deviceLists.get(i).size());
            roomBeans.get(i).setSceneAmount(sceneLists.get(i).size());
        }
        if (roomBeans.size() > 1) {
            changeRoomFrame(FrameType.list);
            binding.layoutRoom.layoutList.tvNoRoom.setVisibility(View.INVISIBLE);
            binding.layoutRoom.layoutList.lvRoom.setVisibility(View.VISIBLE);
            modifyMode(Module.room);
            if (null == roomAdapter) {
                roomAdapter = new RoomAdapter(MainActivity.this, roomBeans.subList(1, roomBeans.size()));
                binding.layoutRoom.layoutList.lvRoom.setAdapter(roomAdapter);
            } else {
                roomAdapter.updateList(roomBeans.subList(1, roomBeans.size()));
            }
            binding.layoutRoom.layoutList.tvModify.setOnClickListener(v -> {
                modifying = true;
                roomAdapter.notifyDataSetChanged();
                modifyMode(Module.room);
            });
            binding.layoutRoom.layoutList.tvCancel.setOnClickListener(v -> {
                modifying = false;
                selectMode = false;
                modifyingRoom = false;
                roomAdapter.updateList(roomBeans.subList(1, roomBeans.size()));
                modifyMode(Module.room);
            });
            binding.layoutRoom.layoutList.tvSave.setOnClickListener(v -> {
                boolean changed = false;
                db = myHelper.getWritableDatabase();
                db.beginTransaction();
                List<RoomBean> beans = roomAdapter.getRoomBeans();
                for (int i = 0; i < roomOrder.size(); i++)
                    if (roomOrder.get(i) != beans.get(i).getId()) {
                        changed = true;
                        break;
                    }
                for (int i = 1; i < roomBeans.size(); i++) {
                    boolean found = false;
                    for (RoomBean b2 : beans)
                        if (roomBeans.get(i).getId() == b2.getId()) {
                            found = true;
                            if (!roomBeans.get(i).getName().equals(b2.getName())) {
                                changed = true;
                                ContentValues values = new ContentValues();
                                values.put(DatabaseStatic.Room.NAME, b2.getName());
                                db.update(DatabaseStatic.TABLE_ROOM, values, DatabaseStatic.Room.ID + "=?", new String[]{roomBeans.get(i).getId() + ""});
                            }
                            break;
                        }
                    if (!found) {
                        int id = roomBeans.get(i).getId();
                        db.delete(DatabaseStatic.TABLE_ROOM, DatabaseStatic.Room.ID + "=?", new String[]{id + ""});
                        changed = true;
                    }
                }
                if (changed) {
                    roomOrder = new ArrayList<>();
                    for (RoomBean bean : beans)
                        roomOrder.add(bean.getId());
                    List<List<Integer>> tempDevice = new ArrayList<>();
                    List<List<Integer>> tempScene = new ArrayList<>();
                    tempDevice.add(new ArrayList<>(deviceOrder.get(0)));
                    tempScene.add(new ArrayList<>(sceneOrder.get(0)));
                    for (RoomBean b : beans)
                        for (int i = 0; i < roomOrder.size(); i++)
                            if (b.getId() == roomOrder.get(i)) {
                                tempDevice.add(new ArrayList<>(deviceOrder.get(i)));
                                tempScene.add(new ArrayList<>(sceneOrder.get(i)));
                                break;
                            }
                    deviceOrder = tempDevice;
                    sceneOrder = tempScene;
                }
                for (RoomBean b2 : beans)
                    if (b2.getId() == 0) {
                        ContentValues values = new ContentValues();
                        values.put(DatabaseStatic.Room.NAME, b2.getName());
                        b2.setId((int) db.insert(DatabaseStatic.TABLE_ROOM, null, values));
                        roomOrder.add(b2.getId());
                        deviceOrder.add(new ArrayList<>());
                        sceneOrder.add(new ArrayList<>());
                        changed = true;
                    }
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();
                if (changed) {
                    settings.setRoomOrder(roomOrder);
                    settings.setDeviceOrder(deviceOrder);
                    settings.setSceneOrder(sceneOrder);
                    saveSettings();
                    initData(Module.room);
                    initDevicePager();
                    initScenePager();
                    initRoom();
                    refreshHomeItems();
                }
                modifying = false;
                modifyingRoom = false;
                selectMode = false;
                roomAdapter.notifyDataSetChanged();
                modifyMode(Module.room);
            });
            binding.layoutRoom.layoutList.lvRoom.setOnItemClickListener((parent, view, position, id) -> {
                if (position >= 0 && position < roomAdapter.getRoomBeans().size()) {
                    if (modifying) {
                        InputTextDialog inputTextDialog = new InputTextDialog(MainActivity.this);
                        inputTextDialog.setText(roomAdapter.getRoomBeans().get(position).getName());
                        inputTextDialog.setHint(R.string.text_input_room_name);
                        inputTextDialog.setCancelable(false);
                        inputTextDialog.setCanceledOnTouchOutside(false);
                        inputTextDialog.setOnDismissListener(dialog -> {
                            if (validRoomName(inputTextDialog.getText(), roomAdapter.getRoomBeans())) {
                                roomAdapter.getRoomBeans().get(position).setName(inputTextDialog.getText());
                                roomAdapter.notifyDataSetChanged();
                            }
                        });
                        inputTextDialog.show();
                    } else
                        initRoomPager(position + 1, false);
                }
            });
        } else {
            binding.layoutRoom.layoutList.tvNoRoom.setVisibility(View.VISIBLE);
            binding.layoutRoom.layoutList.lvRoom.setVisibility(View.INVISIBLE);
            binding.layoutRoom.layoutList.tvModify.setVisibility(View.INVISIBLE);
            binding.layoutRoom.layoutList.tvSave.setVisibility(View.INVISIBLE);
            binding.layoutRoom.layoutList.tvCancel.setVisibility(View.INVISIBLE);
        }
    }

    private boolean validRoomName(String text, List<RoomBean> roomBeans) {
        if (text == null || text.isEmpty())
            return false;
        for (RoomBean bean : roomBeans)
            if (bean.getName().equals(text))
                return false;
        return true;
    }

    private void changeRoomFrame(FrameType which) {
        switch (which) {
            case detail:
                binding.layoutRoom.layoutTitle.rlContent.setVisibility(View.VISIBLE);
                binding.layoutRoom.layoutList.rlContent.setVisibility(View.GONE);
                binding.layoutRoom.layoutDetail.rlContent.setVisibility(View.VISIBLE);
                binding.layoutRoom.layoutSelect.rlContent.setVisibility(View.GONE);
                break;
            case select:
                binding.layoutRoom.layoutTitle.rlContent.setVisibility(View.VISIBLE);
                binding.layoutRoom.layoutList.rlContent.setVisibility(View.GONE);
                binding.layoutRoom.layoutDetail.rlContent.setVisibility(View.GONE);
                binding.layoutRoom.layoutSelect.rlContent.setVisibility(View.VISIBLE);
                break;
            case list:
                binding.layoutRoom.layoutTitle.rlContent.setVisibility(View.GONE);
                binding.layoutRoom.layoutList.rlContent.setVisibility(View.VISIBLE);
                binding.layoutRoom.layoutDetail.rlContent.setVisibility(View.GONE);
                binding.layoutRoom.layoutSelect.rlContent.setVisibility(View.GONE);
                break;
        }
    }

    private void initRoomPager(int position, boolean switchScene) {
        RoomBean bean = roomBeans.get(position);
        roomSelecting(bean.getName());
        binding.layoutRoom.layoutDetail.tlRoom.removeAllTabs();
        binding.layoutRoom.layoutDetail.tlRoom.addTab(binding.layoutRoom.layoutDetail.tlRoom.newTab());
        binding.layoutRoom.layoutDetail.tlRoom.addTab(binding.layoutRoom.layoutDetail.tlRoom.newTab());
        devicesInRoom = new DevicesFragment(deviceLists.get(position), commHandler);
        scenesInRoom = new ScenesFragment(sceneLists.get(position), commHandler);
        RoomPagerAdapter roomPagerAdapter = new RoomPagerAdapter(MainActivity.this, devicesInRoom, scenesInRoom);
        binding.layoutRoom.layoutDetail.vpRoom.setAdapter(roomPagerAdapter);
        binding.layoutRoom.layoutDetail.tlRoom.clearOnTabSelectedListeners();
        binding.layoutRoom.layoutDetail.tlRoom.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.layoutRoom.layoutDetail.vpRoom.setCurrentItem(tab.getPosition());
                binding.layoutRoom.layoutDetail.tvSelectDevice.setText(0 == tab.getPosition() ? R.string.text_select_device : R.string.text_select_scene);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        new TabLayoutMediator(binding.layoutRoom.layoutDetail.tlRoom, binding.layoutRoom.layoutDetail.vpRoom, (tab1, pos) ->
                tab1.setText(String.format(Locale.CHINA, "%s(%d)", getString(pos == 0 ? R.string.text_device : R.string.text_scene),
                        pos == 0 ? deviceLists.get(position).size() : sceneLists.get(position).size()))).attach();
        if (switchScene)
            binding.layoutRoom.layoutDetail.tlRoom.selectTab(binding.layoutRoom.layoutDetail.tlRoom.getTabAt(1));
        binding.layoutRoom.layoutTitle.tvBack.setOnClickListener(v -> {
            if (binding.layoutRoom.layoutSelect.rlContent.getVisibility() == View.VISIBLE) {
                selectDeviceFragments = null;
                selectDeviceAdapter = null;
                selectDevices = null;
                selectSceneFragments = null;
                selectSceneAdapter = null;
                selectScenes = null;
                modifyingRoom = false;
                selectMode = false;
                changeRoomFrame(FrameType.detail);
            } else
                changeRoomFrame(FrameType.list);
        });
        binding.layoutRoom.layoutDetail.tvSelectDevice.setOnClickListener(v -> {
            modifyingRoom = true;
            roomSelecting(bean.getName());
            if (binding.layoutRoom.layoutDetail.tlRoom.getSelectedTabPosition() == 0) {
                binding.layoutRoom.layoutTitle.tvTitle.setText(R.string.text_select_device);
                selectDevices = new ArrayList<>();
                selectDevices.add(new ArrayList<>());
                binding.layoutRoom.layoutSelect.tabTitle.addTab(binding.layoutRoom.layoutSelect.tabTitle.newTab());
                for (DeviceBean b : deviceLists.get(0)) {
                    DeviceBean b1 = new DeviceBean(b);
                    b1.setSelected(b1.getRoomId() == bean.getId());
                    selectDevices.get(0).add(b1);
                }
                initSelect(Module.room, false, true, v1 -> saveSelected(true, position));
            } else {
                binding.layoutRoom.layoutTitle.tvTitle.setText(R.string.text_select_scene);
                selectScenes = new ArrayList<>();
                selectScenes.add(new ArrayList<>());
                binding.layoutRoom.layoutSelect.tabTitle.addTab(binding.layoutRoom.layoutSelect.tabTitle.newTab());
                for (SceneBean b : sceneLists.get(0)) {
                    SceneBean b1 = new SceneBean(b);
                    b1.setSelected(b.getRoomId() == bean.getId());
                    selectScenes.get(0).add(b1);
                }
                initSelect(Module.room, false, false, v1 -> saveSelected(false, position));
            }
        });
    }

    private void saveSelected(boolean isDevice, int pos) {
        boolean changed = false;
        modifyingRoom = false;
        selectMode = false;
        db = myHelper.getWritableDatabase();
        db.beginTransaction();
        ContentValues values;
        if (isDevice) {
            for (DeviceBean b : selectDevices.get(pos)) {
                if (!b.isSelected()) {
                    values = new ContentValues();
                    values.put(DatabaseStatic.OwnedDevices.ROOM_ID, 0);
                    db.update(DatabaseStatic.TABLE_OWNED, values, DatabaseStatic.OwnedDevices.ID + "=?", new String[]{b.getId() + ""});
                    deviceOrder.get(pos).removeIf(i -> i == b.getId());
                    changed = true;
                }
            }
            for (DeviceBean b : selectDevices.get(0))
                if (b.isSelected() && b.getRoomId() != roomBeans.get(pos).getId()) {
                    values = new ContentValues();
                    values.put(DatabaseStatic.OwnedDevices.ROOM_ID, roomBeans.get(pos).getId());
                    db.update(DatabaseStatic.TABLE_OWNED, values, DatabaseStatic.OwnedDevices.ID + "=?", new String[]{b.getId() + ""});
                    for (int i = 1; i < deviceOrder.size(); i++)
                        if (i != pos)
                            deviceOrder.get(i).removeIf(integer -> integer == b.getId());
                    deviceOrder.get(pos).add(b.getId());
                    changed = true;
                }
            selectDevices = null;
            selectDeviceFragments = null;
            selectDeviceAdapter = null;
        } else {
            for (SceneBean b : selectScenes.get(pos)) {
                if (!b.isSelected()) {
                    values = new ContentValues();
                    values.put(DatabaseStatic.Scene.ROOM_ID, 0);
                    db.update(DatabaseStatic.TABLE_SCENE, values, DatabaseStatic.Scene.ID + "=?", new String[]{b.getId() + ""});
                    sceneOrder.get(pos).removeIf(i -> i == b.getId());
                    changed = true;
                }
            }
            for (SceneBean b : selectScenes.get(0))
                if (b.isSelected() && b.getRoomId() != roomBeans.get(pos).getId()) {
                    values = new ContentValues();
                    values.put(DatabaseStatic.Scene.ROOM_ID, roomBeans.get(pos).getId());
                    db.update(DatabaseStatic.TABLE_SCENE, values, DatabaseStatic.Scene.ID + "=?", new String[]{b.getId() + ""});
                    for (int i = 1; i < sceneOrder.size(); i++)
                        if (i != pos)
                            sceneOrder.get(i).removeIf(integer -> integer == b.getId());
                    sceneOrder.get(pos).add(b.getId());
                    changed = true;
                }
            selectScenes = null;
            selectSceneFragments = null;
            selectSceneAdapter = null;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        if (changed) {
            if (isDevice) {
                initDevicePager();
                for (int i = 0; i < roomBeans.size(); i++)
                    roomBeans.get(i).setButtonAmount(deviceLists.get(i).size());
                settings.setDeviceOrder(deviceOrder);
            } else {
                initScenePager();
                for (int i = 0; i < roomBeans.size(); i++)
                    roomBeans.get(i).setButtonAmount(sceneLists.get(i).size());
                settings.setSceneOrder(sceneOrder);
            }
            saveSettings();
            initRoom();
            initRoomPager(pos, !isDevice);
        } else
            roomSelecting(roomBeans.get(pos).getName());
    }

    private void roomSelecting(String name) {
        if (modifyingRoom) {
            changeRoomFrame(FrameType.select);
            singleSelect = false;
            selectMode = true;
            binding.layoutRoom.layoutTitle.tvBack.setText(String.format("%s\"%s\"", getString(R.string.text_room), name));
            binding.layoutRoom.layoutSelect.tabTitle.removeAllTabs();
        } else {
            changeRoomFrame(FrameType.detail);
            selectMode = false;
            binding.layoutRoom.layoutTitle.tvTitle.setText(name);
            binding.layoutRoom.layoutTitle.tvBack.setText(R.string.text_room);
        }
    }

    private void initHomeDevice() {
        homeDeviceBeans = settings.getHomeDeviceBeans();
        changeHomeFrame(FrameType.list);
        refreshHomeItems();
        homeDeviceAdapter = new HomeDeviceAdapter(MainActivity.this, homeDeviceBeans, homeDevices, homeScenes);
        binding.layoutHome.layoutWidget.gvDevice.setAdapter(homeDeviceAdapter);
        binding.layoutHome.layoutWidget.gvDevice.setVerticalSpacing(20);
        binding.layoutHome.layoutWidget.gvDevice.setOnItemClickListener((parent, view, position, id) -> {
            homeDeviceAdapter.notifyDataSetChanged();
            if (position >= 0 && position < homeDeviceBeans.size())
                switch (homeDeviceBeans.get(position).getType()) {
                    case ConstantUtils.TYPE_DEVICE:
                        for (DeviceBean bean : allDevices)
                            if (bean.getId() == homeDeviceBeans.get(position).getId()) {
                                Message m = commHandler.obtainMessage(HANDLER_SWITCH_PRESS);
                                m.obj = bean;
                                m.arg1 = CommandType.device.ordinal();
                                if (bean.getKind() == ConstantUtils.DeviceKind.temperature)
                                    m.arg2 = ConstantUtils.TEMPERATURE_SWITCH_ON_OFF;
                                commHandler.sendMessage(m);
                                break;
                            }
                        break;
                    case ConstantUtils.TYPE_SCENE:
                        for (SceneBean bean : allScenes)
                            if (bean.getId() == homeDeviceBeans.get(position).getId()) {
                                Message m = commHandler.obtainMessage(HANDLER_SCENE_PRESS);
                                m.obj = bean;
                                m.arg1 = CommandType.scene.ordinal();
                                commHandler.sendMessage(m);
                                break;
                            }
                        break;
                    default:
                        changeHomeFrame(FrameType.select);
                        binding.layoutHome.layoutTitle.tvTitle.setText(R.string.text_select_device);
                        binding.layoutHome.layoutTitle.tvBack.setText(R.string.text_home);
                        binding.layoutHome.layoutTitle.tvBack.setOnClickListener(v -> {
                            singleSelect = false;
                            selectMode = false;
                            changeHomeFrame(FrameType.list);
                        });
                        selectDevices = new ArrayList<>();
                        currentTabTitle.addTab(currentTabTitle.newTab());
                        selectDevices.add(new ArrayList<>());
                        for (DeviceBean b : deviceLists.get(0)) {
                            if (b.getKind() == ConstantUtils.DeviceKind.panel)
                                continue;
                            boolean found = false;
                            for (HomeDeviceBean b1 : homeDeviceBeans)
                                if (b1.getType() == ConstantUtils.TYPE_DEVICE && b1.getId() == b.getId()) {
                                    found = true;
                                    break;
                                }
                            if (!found)
                                selectDevices.get(0).add(new DeviceBean(b));
                        }
                        initSelect(Module.home, true, true, v1 -> {
                            for (DeviceBean bean : selectDeviceFragments.get(0).getDeviceBeans())
                                if (bean.isSelected()) {
                                    homeDeviceBeans.get(position).setId(bean.getId());
                                    homeDeviceBeans.get(position).setType(ConstantUtils.TYPE_DEVICE);
                                    refreshHomeItems();
                                    break;
                                }
                            changeHomeFrame(FrameType.list);
                            singleSelect = false;
                            selectMode = false;
                        });
                        break;
                }
        });
        binding.layoutHome.layoutWidget.gvDevice.setOnItemLongClickListener((parent, view, position, id) -> {
            homeDeviceAdapter.notifyDataSetChanged();
            if (position >= 0 && position < homeDeviceBeans.size() && homeDeviceBeans.get(position).getId() != 0) {
                if (homeDeviceBeans.get(position).getType() == ConstantUtils.TYPE_DEVICE) {
                    for (DeviceBean bean : allDevices)
                        if (bean.getId() == homeDeviceBeans.get(position).getId()) {
                            if (bean.getKind() == ConstantUtils.DeviceKind.temperature) {
                                ctrlDialog = new TemperatureCtrlDialog(MainActivity.this, bean, commHandler);
                                ctrlDialog.setEnableDisplayHome(true);
                                ctrlDialog.setCanceledOnTouchOutside(false);
                                ctrlDialog.setCancelable(false);
                                ctrlDialog.show();
                                ctrlDialog.setOnDismissListener(dialogInterface -> {
                                    if (ctrlDialog.isNotDisplayHome()) {
                                        homeDeviceBeans.set(position, new HomeDeviceBean());
                                        refreshHomeItems();
                                    }
                                    ctrlDialog = null;
                                });
                                return true;
                            }
                            break;
                        }
                }
                WarningDialog dialog = new WarningDialog(MainActivity.this);
                dialog.setOnPositiveClick(v1 -> {
                    homeDeviceBeans.set(position, new HomeDeviceBean());
                    refreshHomeItems();
                    dialog.dismiss();
                });
                dialog.setMessage(R.string.text_delete_shortcut);
                dialog.show();
                return true;
            }
            return false;
        });
    }

    private void refreshHomeItems() {
        if (homeDeviceBeans != null) {
            homeScenes = new ArrayList<>();
            homeDevices = new ArrayList<>();
            for (HomeDeviceBean bean : homeDeviceBeans) {
                boolean notFound = true;
                switch (bean.getType()) {
                    case ConstantUtils.TYPE_DEVICE:
                        for (DeviceBean b : allDevices)
                            if (b.getId() == bean.getId()) {
                                homeDevices.add(b);
                                notFound = false;
                                break;
                            }
                        break;
                    case ConstantUtils.TYPE_SCENE:
                        for (SceneBean b : allScenes)
                            if (b.getId() == bean.getId()) {
                                homeScenes.add(b);
                                notFound = false;
                                break;
                            }
                        break;
                }
                if (notFound) {
                    bean.setType(0);
                    bean.setId(0);
                }
            }
            settings.setHomeDeviceBeans(homeDeviceBeans);
            saveSettings();
            if (homeDeviceAdapter != null)
                homeDeviceAdapter.updateLists(homeDeviceBeans, homeDevices, homeScenes);
        }
    }

    private void changeHomeFrame(FrameType which) {
        if (which == FrameType.select) {
            binding.layoutHome.layoutWidget.rlContent.setVisibility(View.GONE);
            binding.layoutHome.layoutTitle.rlContent.setVisibility(View.VISIBLE);
            binding.layoutHome.layoutSelect.rlContent.setVisibility(View.VISIBLE);
        } else {
            binding.layoutHome.layoutWidget.rlContent.setVisibility(View.VISIBLE);
            binding.layoutHome.layoutTitle.rlContent.setVisibility(View.GONE);
            binding.layoutHome.layoutSelect.rlContent.setVisibility(View.GONE);
        }
    }

    private void deviceOffline(String mac) {
        resendCount = 0;
        if (mac == null || mac.isEmpty())
            return;
        for (DeviceBean bean : allDevices)
            if (mac.equals(bean.getMac()) && bean.getStatusEnum() != ConstantUtils.Status.offline) {
                if (bean.getKind() == ConstantUtils.DeviceKind.temperature)
                    bean.setStatus((bean.getStatus() & ~ConstantUtils.MASK_STATUS_SWITCH) + ConstantUtils.Status.offline.ordinal());
                else
                    bean.setStatus(ConstantUtils.Status.offline);
                devicesFragments.get(0).updateDevice(bean);
                refreshFragments(devicesFragments, devicePagerAdapter);
                if (devicesInRoom != null)
                    devicesInRoom.updateDevice(bean);
                if (selectDeviceFragments != null)
                    selectDeviceFragments.get(0).updateDevice(bean);
                for (HomeDeviceBean b : homeDeviceBeans)
                    if (b.getType() == ConstantUtils.TYPE_DEVICE && b.getId() == bean.getId()) {
                        refreshHomeItems();
                        break;
                    }
            }
        for (SceneBean bean : allScenes)
            if (mac.equals(bean.getMac()) && bean.getStatusEnum() != ConstantUtils.Status.offline) {
                bean.setStatus(ConstantUtils.Status.offline);
                scenesFragments.get(0).updateScene(bean);
                if (scenesInRoom != null)
                    scenesInRoom.updateScene(bean);
                if (selectSceneFragments != null)
                    selectSceneFragments.get(0).updateScene(bean);
                for (HomeDeviceBean b : homeDeviceBeans)
                    if (b.getType() == ConstantUtils.TYPE_SCENE && b.getId() == bean.getId()) {
                        refreshHomeItems();
                        break;
                    }
            }
        refreshFragments(devicesFragments, devicePagerAdapter);
        refreshFragments(scenesFragments, scenePagerAdapter);
        if (selectDeviceFragments != null)
            refreshFragments(selectDeviceFragments, selectDeviceAdapter);
        if (selectSceneFragments != null)
            refreshFragments(selectSceneFragments, selectSceneAdapter);
    }

    private void initSerial() {
        BufferedWriterUtil.write(path_485, "off");//切到接收状态
        mSerialPortManager = new SerialPortManager();
        mSerialPortManager.setOnSerialPortDataListener(new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {
                StringBuilder data = new StringBuilder();
                for (byte i : bytes)
                    data.append(String.format("%02X ", i));
                Log.d(TAG, "485接收到:" + data);
                writeToFile("收：" + data);
                Message msg = commHandler.obtainMessage(HANDLER_RECEIVE_CMD);
                msg.obj = bytes;
                commHandler.sendMessage(msg);
            }

            @Override
            public void onDataSent(byte[] bytes) {
                StringBuilder data = new StringBuilder();
                for (byte i : bytes)
                    data.append(String.format("%02X ", i));
                Log.d(TAG, "485已发送:" + data);
                writeToFile("发：" + data);
                new Handler().postDelayed(() -> BufferedWriterUtil.write(path_485, "off"), 10);
            }
        });
        mSerialPortManager.openSerialPort(new File("/dev/ttyS0"), 115200);//Z9和A8都是S0口
    }

    private void sendBytes(byte[] bytes) {
        BufferedWriterUtil.write(path_485, "on");//on切到发送状态
        mSerialPortManager.sendBytes(bytes);
    }

    private void initGateway() {
//        deleteDatabase(DatabaseStatic.DATABASE_NAME);
        File file = new File(ConstantUtils.FILE_BACKUP_DB);
        if (!file.exists()) {
            BackupTask backupTask = new BackupTask(this);
            backupTask.doInBackground(BackupTask.COMMAND_BACKUP);
        }
        myHelper = new MyHelper(this);
        db = myHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseStatic.TABLE_OWNED, null,
                DatabaseStatic.OwnedDevices.NAME + "=?", new String[]{DatabaseStatic.DEFAULT_GATEWAY}, null, null, null);
        if (cursor.moveToNext()) {
            gwMac = cursor.getString(DatabaseStatic.OwnedDevices.COL_MAC);
        } else {
            cmdType = CommandType.require;
            commHandler.sendEmptyMessage(HANDLER_SEND_CMD);
        }
        cursor.close();
        db.close();
    }

    private void initButton() {
        currentModule = Module.home;
        enterModule(Module.home);
        binding.btnHome.setOnClickListener(v -> changeModule(Module.home));
        binding.btnDevice.setOnClickListener(v -> changeModule(Module.device));
        binding.btnScene.setOnClickListener(v -> changeModule(Module.scene));
        binding.btnRoom.setOnClickListener(v -> changeModule(Module.room));
        binding.layoutDevices.layoutList.tvAddDevice.setOnClickListener(v -> {
            Log.d(TAG, "status1:" + cmdType);
            if (CommandType.idle == cmdType) {
                cmdType = CommandType.search;
                commHandler.sendEmptyMessage(HANDLER_SEND_CMD);
            } else
                myToast(R.string.text_working);
        });
        binding.layoutHome.layoutWidget.btnPause.setOnClickListener(v -> NRMessage.post(APIMsgBean.com_sznaner_music_control, JSONObject.toJSONString(new APIMsgBean(isPlaying ? "pause" : "play"))));
        binding.layoutHome.layoutWidget.btnPrev.setOnClickListener(v -> NRMessage.post(APIMsgBean.com_sznaner_music_control, JSONObject.toJSONString(new APIMsgBean("prev"))));
        binding.layoutHome.layoutWidget.btnNext.setOnClickListener(v -> NRMessage.post(APIMsgBean.com_sznaner_music_control, JSONObject.toJSONString(new APIMsgBean("next"))));
        binding.layoutHome.layoutWidget.btnMode.setOnClickListener(v -> {
            if (null != musicBean) {
                switch (musicBean.getMode()) {
                    case ConstantUtils.PLAY_MODE_RANDOM:
                        musicBean.setMode(ConstantUtils.PLAY_MODE_LIST);
                        break;
                    case ConstantUtils.PLAY_MODE_LIST:
                        musicBean.setMode(ConstantUtils.PLAY_MODE_SINGLE);
                        break;
                    case ConstantUtils.PLAY_MODE_SINGLE:
                        musicBean.setMode(ConstantUtils.PLAY_MODE_RANDOM);
                        break;
                }
                Integer mode = ConstantUtils.playModeImage.get(musicBean.getMode());
                if (null != mode)
                    binding.layoutHome.layoutWidget.btnMode.setImageResource(mode);
            }
            NRMessage.post(APIMsgBean.com_sznaner_music_control, JSONObject.toJSONString(new APIMsgBean("mode.change")));
        });
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(VOLUME_CHANGED_ACTION);
        volumeReceiver = new VolumeReceiver();
        registerReceiver(volumeReceiver, intentFilter);
        binding.layoutHome.layoutWidget.sbVolume.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        binding.layoutHome.layoutWidget.sbVolume.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
        binding.layoutHome.layoutWidget.sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        binding.layoutHome.layoutWidget.btnVoice.setOnClickListener(v -> {
            if (binding.layoutHome.layoutWidget.btnVoice.getContentDescription().toString().equals(getString(R.string.text_voice))) {
                binding.layoutHome.layoutWidget.btnVoice.setContentDescription(getString(R.string.text_no_voice));
                binding.layoutHome.layoutWidget.btnVoice.setImageResource(R.drawable.button_disable_voice_style);
                NRMessage.post(APIMsgBean.com_sznaner_voice_set, JSON.toJSONString(new APIMsgBean("disableWakeup")));
            } else {
                binding.layoutHome.layoutWidget.btnVoice.setContentDescription(getString(R.string.text_voice));
                binding.layoutHome.layoutWidget.btnVoice.setImageResource(R.drawable.button_enable_voice_style);
                NRMessage.post(APIMsgBean.com_sznaner_voice_set, JSON.toJSONString(new APIMsgBean("enableWakeup")));
            }
        });
        binding.layoutHome.layoutWidget.ivAlbum.setOnClickListener(v -> {
//            cmdType = CommandType.test;
//            commHandler.sendEmptyMessage(HANDLER_SEND_CMD);
            startApp("com.sznaner.onlinemusic", "com.sznaner.onlinemusic.MainActivity", false);
        });
        try {
            binding.layoutHome.layoutWidget.tvVersion.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.layoutHome.layoutWidget.btnDecrease.setOnClickListener(v -> {
            if (binding.layoutHome.layoutWidget.sbVolume.getProgress() > 0)
                binding.layoutHome.layoutWidget.sbVolume.incrementProgressBy(-1);
        });
        binding.layoutHome.layoutWidget.btnIncrease.setOnClickListener(v -> {
            if (binding.layoutHome.layoutWidget.sbVolume.getProgress() < binding.layoutHome.layoutWidget.sbVolume.getMax())
                binding.layoutHome.layoutWidget.sbVolume.incrementProgressBy(1);
        });
        binding.layoutHome.layoutWidget.btnWeather.setOnClickListener(v -> acquireWeatherByArea(lastLocation != null ? "" : "深圳"));
        binding.layoutRoom.layoutList.tvAddRoom.setOnClickListener(v -> {
            if (!modifying) {
                InputTextDialog inputTextDialog = new InputTextDialog(MainActivity.this);
                inputTextDialog.setHint(R.string.text_input_room_name);
                inputTextDialog.setCancelable(false);
                inputTextDialog.setCanceledOnTouchOutside(false);
                inputTextDialog.setOnDismissListener(dialog -> {
                    if (validRoomName(inputTextDialog.getText(), roomBeans)) {
                        ContentValues values = new ContentValues();
                        RoomBean bean = new RoomBean(inputTextDialog.getText());
                        values.put(DatabaseStatic.Room.NAME, inputTextDialog.getText());
                        db = myHelper.getWritableDatabase();
                        db.beginTransaction();
                        bean.setId((int) db.insert(DatabaseStatic.TABLE_ROOM, null, values));
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        db.close();
                        roomBeans.add(bean);
                        roomOrder.add(bean.getId());
                        deviceOrder.add(new ArrayList<>());
                        sceneOrder.add(new ArrayList<>());
                        settings.setDeviceOrder(deviceOrder);
                        settings.setSceneOrder(sceneOrder);
                        settings.setRoomOrder(roomOrder);
                        saveSettings();
                        initDevicePager();
                        initScenePager();
                        initRoom();
                    }
                });
                inputTextDialog.show();
            }
        });
        binding.layoutScenes.layoutList.tvAddScene.setOnClickListener(v -> {
            modifyScenes = new ArrayList<>();
            changeLists = new ArrayList<>();
            modifyScenes.add(new SceneBean());
            changeLists.add(false);
            tempRoomBeans = new ArrayList<>(roomBeans);
            enterModifyScene(modifyScenes.get(0));
        });
        binding.layoutDevices.layoutList.tvCancel.setVisibility(View.INVISIBLE);
        binding.layoutDevices.layoutList.tvSave.setVisibility(View.INVISIBLE);
        binding.layoutScenes.layoutList.tvCancel.setVisibility(View.INVISIBLE);
        binding.layoutScenes.layoutList.tvSave.setVisibility(View.INVISIBLE);
        binding.layoutDevices.layoutList.tvModify.setOnClickListener(v -> {
            deviceChanged = false;
            modifying = true;
            refreshFragments(devicesFragments, devicePagerAdapter);
            modifyMode(Module.device);
        });
        binding.layoutDevices.layoutList.tvCancel.setOnClickListener(v -> {
            modifying = false;
            selectMode = false;
            if (deviceChanged) {
                initDevicePager();
                deviceChanged = false;
            } else
                refreshFragments(devicesFragments, devicePagerAdapter);
            modifyMode(Module.device);
        });
        binding.layoutDevices.layoutList.tvSave.setOnClickListener(v -> {
            modifying = false;
            selectMode = false;
            List<List<Integer>> order = new ArrayList<>();
            for (int i = 0; i < devicesFragments.size(); i++) {
                if (devicesFragments.get(i).getChangedDeviceBeans() == null) {
                    order.add(new ArrayList<>(deviceOrder.get(i)));
                } else {
                    order.add(new ArrayList<>());
                    for (DeviceBean bean : devicesFragments.get(i).getChangedDeviceBeans())
                        order.get(i).add(bean.getId());
                }
            }
            deviceOrder = order;
            settings.setDeviceOrder(deviceOrder);
            saveSettings();
            if (deviceChanged) {
                db = myHelper.getWritableDatabase();
                db.beginTransaction();
                for (DeviceBean bean : allDevices) {
                    boolean found = false;
                    for (DeviceBean b : devicesFragments.get(0).getChangedDeviceBeans()) {
                        if (bean.getId() == b.getId()) {
                            found = true;
                            if (!bean.getName().equals(b.getName())) {
                                ContentValues values = new ContentValues();
                                values.put(DatabaseStatic.Device.NAME, b.getName());
                                db.update(DatabaseStatic.TABLE_OWNED, values, DatabaseStatic.Device.ID + "=?", new String[]{b.getId() + ""});
                            }
                            break;
                        }
                    }
                    if (!found) {
                        homeDeviceBeans.removeIf(homeDeviceBean -> homeDeviceBean.getType() == ConstantUtils.TYPE_DEVICE && homeDeviceBean.getType() == bean.getId());
                        settings.setHomeDeviceBeans(homeDeviceBeans);
                        saveSettings();
                        db.delete(DatabaseStatic.TABLE_OWNED, DatabaseStatic.Device.ID + "=?", new String[]{bean.getId() + ""});
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();
                initDevicePager();
                refreshHomeItems();
            } else
                refreshFragments(devicesFragments, devicePagerAdapter);
            modifyMode(Module.device);
        });
        binding.layoutScenes.layoutList.tvModify.setOnClickListener(v -> {
            modifying = true;
            deviceChanged = false;
            refreshFragments(scenesFragments, scenePagerAdapter);
            modifyMode(Module.scene);
        });
        binding.layoutScenes.layoutList.tvCancel.setOnClickListener(v -> {
            modifying = false;
            selectMode = false;
            modifyingScene = false;
            if (deviceChanged) {
                deviceChanged = false;
                initScenePager();
            } else
                refreshFragments(scenesFragments, scenePagerAdapter);
            modifyMode(Module.scene);
        });
        binding.layoutScenes.layoutList.tvSave.setOnClickListener(v -> {
            modifying = false;
            selectMode = false;
            modifyingScene = false;
            List<List<Integer>> order = new ArrayList<>();
            for (int i = 0; i < scenesFragments.size(); i++) {
                if (scenesFragments.get(i).getChangedSceneBeans() == null) {
                    order.add(new ArrayList<>(sceneOrder.get(i)));
                } else {
                    order.add(new ArrayList<>());
                    for (SceneBean bean : scenesFragments.get(i).getChangedSceneBeans())
                        order.get(i).add(bean.getId());
                }
            }
            sceneOrder = order;
            settings.setSceneOrder(sceneOrder);
            saveSettings();
            if (deviceChanged) {
                db = myHelper.getWritableDatabase();
                db.beginTransaction();
                for (SceneBean bean : allScenes) {
                    boolean found = false;
                    for (SceneBean b : scenesFragments.get(0).getChangedSceneBeans()) {
                        if (bean.getId() == b.getId()) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        homeDeviceBeans.removeIf(homeDeviceBean -> homeDeviceBean.getType() == ConstantUtils.TYPE_SCENE && homeDeviceBean.getType() == bean.getId());
                        settings.setHomeDeviceBeans(homeDeviceBeans);
                        saveSettings();
                        db.delete(DatabaseStatic.TABLE_SCENE, DatabaseStatic.Scene.ID + "=?", new String[]{bean.getId() + ""});
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();
                initScenePager();
                refreshHomeItems();
            } else
                refreshFragments(scenesFragments, scenePagerAdapter);
            modifyMode(Module.scene);
        });
        changeSceneFrame(FrameType.list);
    }

    private void modifyMode(Module module) {
        switch (module) {
            case device:
                if (modifying) {
                    binding.layoutDevices.layoutList.tvModify.setVisibility(View.GONE);
                    binding.layoutDevices.layoutList.tvCancel.setVisibility(View.VISIBLE);
                    binding.layoutDevices.layoutList.tvSave.setVisibility(View.VISIBLE);
                    binding.layoutDevices.layoutList.tvAddDevice.setVisibility(View.GONE);
                } else {
                    binding.layoutDevices.layoutList.tvModify.setVisibility(View.VISIBLE);
                    binding.layoutDevices.layoutList.tvCancel.setVisibility(View.INVISIBLE);
                    binding.layoutDevices.layoutList.tvSave.setVisibility(View.INVISIBLE);
                    binding.layoutDevices.layoutList.tvAddDevice.setVisibility(View.VISIBLE);
                }
                break;
            case scene:
                if (modifying) {
                    binding.layoutScenes.layoutList.tvModify.setVisibility(View.GONE);
                    binding.layoutScenes.layoutList.tvCancel.setVisibility(View.VISIBLE);
                    binding.layoutScenes.layoutList.tvSave.setVisibility(View.VISIBLE);
                    binding.layoutScenes.layoutList.tvAddScene.setVisibility(View.GONE);
                } else {
                    binding.layoutScenes.layoutList.tvModify.setVisibility(View.VISIBLE);
                    binding.layoutScenes.layoutList.tvCancel.setVisibility(View.INVISIBLE);
                    binding.layoutScenes.layoutList.tvSave.setVisibility(View.INVISIBLE);
                    binding.layoutScenes.layoutList.tvAddScene.setVisibility(View.VISIBLE);
                }
            case room:
                if (modifying) {
                    binding.layoutRoom.layoutList.tvModify.setVisibility(View.GONE);
                    binding.layoutRoom.layoutList.tvSave.setVisibility(View.VISIBLE);
                    binding.layoutRoom.layoutList.tvCancel.setVisibility(View.VISIBLE);
                    binding.layoutRoom.layoutList.tvAddRoom.setVisibility(View.GONE);
                } else {
                    binding.layoutRoom.layoutList.tvModify.setVisibility(View.VISIBLE);
                    binding.layoutRoom.layoutList.tvSave.setVisibility(View.INVISIBLE);
                    binding.layoutRoom.layoutList.tvCancel.setVisibility(View.INVISIBLE);
                    binding.layoutRoom.layoutList.tvAddRoom.setVisibility(View.VISIBLE);
                }
        }
    }

    private void enterModifyScene(@NonNull SceneBean sceneBean) {
        changeSceneFrame(FrameType.modify);
        sceneChanged = changeLists.get(changeLists.size() - 1);
        binding.layoutScenes.layoutModify.tvSaveTask.setVisibility(View.INVISIBLE);
        binding.layoutScenes.layoutModify.tvCancel.setVisibility(View.INVISIBLE);
        taskModifying(false);
        if (sceneBean.getTaskBeans().size() == 0 &&
                (sceneBean.getKind() == ConstantUtils.DeviceKind.switcher || sceneBean.getKind() == ConstantUtils.DeviceKind.panel)) {
            resendCount = 0;
            cmdType = CommandType.getTask;
            currentScene = sceneBean;
            commHandler.sendEmptyMessage(HANDLER_SEND_CMD);
        }
        setUpperLevelText();
        taskAdapter = new SceneTaskAdapter(MainActivity.this, sceneBean.getTaskBeans(), allScenes, allDevices);
        binding.layoutScenes.layoutModify.lvTasks.setTaskList(true);
        binding.layoutScenes.layoutModify.lvTasks.setAdapter(taskAdapter);
        binding.layoutScenes.layoutModify.lvTasks.setOnItemClickListener((parent, view, position, id) -> {
            if (!taskAdapter.isModifying() && position >= 0 && position < taskAdapter.getSceneTaskBeans().size()) {
                switch (taskAdapter.getSceneTaskBeans().get(position).getActionEnum()) {
                    case none:
                    case on:
                        for (DeviceBean bean : deviceLists.get(0))
                            if (taskAdapter.getSceneTaskBeans().get(position).getDeviceId() == bean.getId()) {
                                if (bean.getKind() == ConstantUtils.DeviceKind.temperature && modifyScenes.get(modifyScenes.size() - 1).getType() == ConstantUtils.SceneType.local) {
                                    if (taskAdapter.getSceneTaskBeans().get(position).getActionEnum() == ConstantUtils.ActionType.on)
                                        taskAdapter.getSceneTaskBeans().get(position).setAction(ConstantUtils.ActionType.none);
                                    DeviceBean b = new DeviceBean(bean);
                                    b.setStatus(taskAdapter.getSceneTaskBeans().get(position).getAction() == ConstantUtils.ActionType.none.ordinal()
                                            ? ((b.getStatus() & ~ConstantUtils.MASK_STATUS_SWITCH) + 1) : taskAdapter.getSceneTaskBeans().get(position).getAction());
                                    TemperatureCtrlDialog dialog = new TemperatureCtrlDialog(MainActivity.this, b, commHandler);
                                    dialog.setTask(true);
                                    dialog.setOnDismissListener(dialog1 -> {
                                        int status = dialog.getStatus();
                                        Log.d("MY_LOG", "dialog status:" + status);
                                        if ((status & ConstantUtils.MASK_STATUS_SWITCH) == 0) {
                                            taskAdapter.getSceneTaskBeans().get(position).setAction(ConstantUtils.ActionType.off);
                                        } else if (status == 1) {
                                            taskAdapter.getSceneTaskBeans().get(position).setAction(ConstantUtils.ActionType.on);
                                        } else {
                                            taskAdapter.getSceneTaskBeans().get(position).setAction(status);
                                        }
                                        taskAdapter.notifyDataSetChanged();
                                        binding.layoutScenes.layoutTitle.tvSaveExit.setEnabled(validSceneName(binding.layoutScenes.layoutModify.tvSceneName.getText().toString(), sceneBean));
                                    });
                                    dialog.show();
                                } else
                                    taskAdapter.getSceneTaskBeans().get(position).setAction(bean.getKind() == ConstantUtils.DeviceKind.curtain ? ConstantUtils.ActionType.pause : ConstantUtils.ActionType.off);
                                break;
                            }
                        taskAdapter.notifyDataSetChanged();
                        binding.layoutScenes.layoutTitle.tvSaveExit.setEnabled(validSceneName(binding.layoutScenes.layoutModify.tvSceneName.getText().toString(), sceneBean));
                        break;
                    case pause:
                        taskAdapter.getSceneTaskBeans().get(position).setAction(ConstantUtils.ActionType.off);
                        taskAdapter.notifyDataSetChanged();
                        binding.layoutScenes.layoutTitle.tvSaveExit.setEnabled(validSceneName(binding.layoutScenes.layoutModify.tvSceneName.getText().toString(), sceneBean));
                        break;
                    case off:
                        taskAdapter.getSceneTaskBeans().get(position).setAction(ConstantUtils.ActionType.on);
                        taskAdapter.notifyDataSetChanged();
                        binding.layoutScenes.layoutTitle.tvSaveExit.setEnabled(validSceneName(binding.layoutScenes.layoutModify.tvSceneName.getText().toString(), sceneBean));
                        break;
                    case scene:
                        for (SceneBean bean : allScenes)
                            if (bean.getId() == taskAdapter.getSceneTaskBeans().get(position).getDeviceId()) {
                                sceneBean.setTaskBeans(taskAdapter.getSceneTaskBeans());
                                sceneBean.setName(binding.layoutScenes.layoutModify.tvSceneName.getText().toString());
                                sceneBean.setRoom(binding.layoutScenes.layoutModify.tvRoomName.getText().toString());
                                sceneBean.setKeywords(binding.layoutScenes.layoutModify.tvKeywords.getText().toString());
                                if (binding.layoutScenes.layoutModify.ivThumbBg.getContentDescription() != null && binding.layoutScenes.layoutModify.ivThumbBg.getContentDescription().length() > 0)
                                    sceneBean.setPicture(binding.layoutScenes.layoutModify.ivThumbBg.getContentDescription().toString());
                                modifyScenes.set(modifyScenes.size() - 1, sceneBean);
                                modifyScenes.add(new SceneBean(bean));
                                changeLists.add(sceneChanged);
                                enterModifyScene(modifyScenes.get(modifyScenes.size() - 1));
                                break;
                            }
                        break;
                }
            }
        });
        changeModifyButton(sceneBean.getTaskBeans());
        binding.layoutScenes.layoutModify.tvReverse.setOnClickListener(v -> {
            boolean changed = false;
            for (SceneBean.TaskBean bean : taskAdapter.getSceneTaskBeans())
                if (bean.getActionEnum() == ConstantUtils.ActionType.on || bean.getActionEnum() == ConstantUtils.ActionType.off)
                    for (DeviceBean b : allDevices)
                        if (bean.getDeviceId() == b.getId()) {
                            bean.setAction(bean.getActionEnum() == ConstantUtils.ActionType.on ? ConstantUtils.ActionType.off : ConstantUtils.ActionType.on);
                            changed = true;
                            break;
                        }
            if (changed)
                taskAdapter.notifyDataSetChanged();
        });
        binding.layoutScenes.layoutTitle.tvBack.setOnClickListener(v -> {
            if (binding.layoutScenes.layoutBackground.rlContent.getVisibility() == View.VISIBLE) {
                selectingBackground(false, sceneBean.getId() == 0 ? null : "");
            } else if (binding.layoutScenes.layoutSelect.rlContent.getVisibility() == View.VISIBLE) {
                addingTask(Module.home, sceneBean.getId() == 0 ? null : "");
            } else if (modifyScenes.size() > 1) {
                modifyScenes.remove(modifyScenes.size() - 1);
                changeLists.remove(changeLists.size() - 1);
                enterModifyScene(modifyScenes.get(modifyScenes.size() - 1));
            } else if (sceneChanged) {
                WarningDialog dialog = new WarningDialog(MainActivity.this);
                dialog.setOnPositiveClick(v1 -> {
                    exitModifyScene();
                    dialog.dismiss();
                });
                dialog.setMessage(R.string.text_exit_no_save);
                dialog.show();
            } else {
                exitModifyScene();
            }
        });
        binding.layoutScenes.layoutModify.ivThumbBg.setContentDescription(null);
        binding.layoutScenes.layoutModify.ivThumbBg.setVisibility(View.INVISIBLE);
        binding.layoutScenes.layoutModify.tvChoose.setVisibility(View.VISIBLE);
        if (sceneBean.getId() == 0) {
            binding.layoutScenes.layoutTitle.tvTitle.setText(R.string.text_create_scene);
            binding.layoutScenes.layoutModify.tvSceneHint.setVisibility(View.VISIBLE);
            binding.layoutScenes.layoutModify.tvRoomHint.setVisibility(binding.layoutScenes.layoutList.vpScenes.getCurrentItem() == 0 ? View.VISIBLE : View.INVISIBLE);
            binding.layoutScenes.layoutModify.tvSceneName.setText("");
            if (binding.layoutScenes.layoutList.vpScenes.getCurrentItem() != 0 && binding.layoutScenes.layoutList.vpScenes.getCurrentItem() < roomBeans.size()) {
                sceneBean.setRoom(roomBeans.get(binding.layoutScenes.layoutList.vpScenes.getCurrentItem()).getName());
                sceneBean.setRoomId(roomBeans.get(binding.layoutScenes.layoutList.vpScenes.getCurrentItem()).getId());
            }
            binding.layoutScenes.layoutModify.tvRoomName.setText(sceneBean.getRoom());
            binding.layoutScenes.layoutModify.tvKeywords.setText("");
            binding.layoutScenes.layoutModify.tvSceneHint.setVisibility(View.VISIBLE);
        } else {
            binding.layoutScenes.layoutTitle.tvTitle.setText(R.string.text_modify_scene);
            if (sceneBean.getName() != null && !sceneBean.getName().isEmpty()) {
                binding.layoutScenes.layoutModify.tvSceneName.setText(sceneBean.getName());
                binding.layoutScenes.layoutModify.tvSceneHint.setVisibility(View.INVISIBLE);
            } else {
                binding.layoutScenes.layoutModify.tvSceneName.setText("");
                binding.layoutScenes.layoutModify.tvSceneHint.setVisibility(View.VISIBLE);
            }
            if (sceneBean.getRoom() != null && !sceneBean.getRoom().isEmpty()) {
                binding.layoutScenes.layoutModify.tvRoomName.setText(sceneBean.getRoom());
                binding.layoutScenes.layoutModify.tvRoomHint.setVisibility(View.INVISIBLE);
            } else {
                binding.layoutScenes.layoutModify.tvRoomName.setText("");
                binding.layoutScenes.layoutModify.tvRoomHint.setVisibility(View.VISIBLE);
            }
            if (sceneBean.getKeywords() != null && !sceneBean.getKeywords().isEmpty()) {
                binding.layoutScenes.layoutModify.tvKeywords.setText(sceneBean.getKeywords());
                binding.layoutScenes.layoutModify.tvKeywordsHint.setVisibility(View.INVISIBLE);
            } else {
                binding.layoutScenes.layoutModify.tvKeywords.setText("");
                binding.layoutScenes.layoutModify.tvKeywordsHint.setVisibility(View.VISIBLE);
            }
            if (sceneBean.getPicture() != null && !sceneBean.getPicture().isEmpty()) {
                binding.layoutScenes.layoutModify.ivThumbBg.setVisibility(View.VISIBLE);
                binding.layoutScenes.layoutModify.tvChoose.setVisibility(View.INVISIBLE);
                try {
                    binding.layoutScenes.layoutModify.ivThumbBg.setContentDescription(sceneBean.getPicture());
                    binding.layoutScenes.layoutModify.ivThumbBg.setImageResource(ConstantUtils.SCENE_PICTURE[Integer.parseInt(sceneBean.getPicture())]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        binding.layoutScenes.layoutModify.rlSceneName.setEnabled(sceneBean.getType() != ConstantUtils.SceneType.panel);
        binding.layoutScenes.layoutModify.rlSceneName.setOnClickListener(v -> {
            InputTextDialog inputTextDialog = new InputTextDialog(MainActivity.this);
            if (sceneBean.getName() != null && !sceneBean.getName().isEmpty())
                inputTextDialog.setText(sceneBean.getName());
            inputTextDialog.setHint(R.string.text_input_scene_name);
            inputTextDialog.setCancelable(false);
            inputTextDialog.setCanceledOnTouchOutside(false);
            inputTextDialog.setOnDismissListener(dialog -> {
                if (!binding.layoutScenes.layoutModify.tvSceneName.getText().equals(inputTextDialog.getText()) && validSceneName(inputTextDialog.getText(), sceneBean)) {
                    binding.layoutScenes.layoutModify.tvSceneName.setText(inputTextDialog.getText());
                    binding.layoutScenes.layoutModify.tvSceneHint.setVisibility(View.INVISIBLE);
                    binding.layoutScenes.layoutTitle.tvSaveExit.setEnabled(validSceneName(binding.layoutScenes.layoutModify.tvSceneName.getText().toString(), sceneBean));
                    sceneChanged = true;
                }
            });
            inputTextDialog.show();
        });
        binding.layoutScenes.layoutModify.rlRoom.setOnClickListener(v -> {
            for (RoomBean bean : tempRoomBeans)
                bean.setSelected(bean.getId() == sceneBean.getRoomId());
            SelectRoomDialog selectRoomDialog = new SelectRoomDialog(MainActivity.this, tempRoomBeans.subList(1, tempRoomBeans.size()));
            selectRoomDialog.setCancelable(false);
            selectRoomDialog.setCanceledOnTouchOutside(false);
            selectRoomDialog.setOnDismissListener(dialog -> {
                if (selectRoomDialog.getSelectedRoom() >= 0 && selectRoomDialog.getSelectedRoom() < roomBeans.size()
                        && !binding.layoutScenes.layoutModify.tvRoomName.getText().equals(tempRoomBeans.get(selectRoomDialog.getSelectedRoom() + 1).getName())) {
                    binding.layoutScenes.layoutModify.tvRoomName.setText(tempRoomBeans.get(selectRoomDialog.getSelectedRoom() + 1).getName());
                    binding.layoutScenes.layoutModify.tvRoomHint.setVisibility(View.INVISIBLE);
                    binding.layoutScenes.layoutTitle.tvSaveExit.setEnabled(validSceneName(binding.layoutScenes.layoutModify.tvSceneName.getText().toString(), sceneBean));
                    sceneChanged = true;
                } else if (selectRoomDialog.getText() != null && !selectRoomDialog.getText().isEmpty()
                        && !binding.layoutScenes.layoutModify.tvRoomName.getText().equals(selectRoomDialog.getText())) {
                    tempRoomBeans.add(new RoomBean(selectRoomDialog.getText()));
                    binding.layoutScenes.layoutModify.tvRoomName.setText(selectRoomDialog.getText());
                    binding.layoutScenes.layoutModify.tvRoomHint.setVisibility(View.INVISIBLE);
                    binding.layoutScenes.layoutTitle.tvSaveExit.setEnabled(validSceneName(binding.layoutScenes.layoutModify.tvSceneName.getText().toString(), sceneBean));
                    sceneChanged = true;
                }
            });
            selectRoomDialog.show();
        });
        binding.layoutScenes.layoutModify.rlKeywords.setEnabled(sceneBean.getType() != ConstantUtils.SceneType.panel);
        binding.layoutScenes.layoutModify.rlKeywords.setOnClickListener(v -> {
            InputTextDialog inputTextDialog = new InputTextDialog(MainActivity.this);
            if (sceneBean.getKeywords() != null && !sceneBean.getKeywords().isEmpty())
                inputTextDialog.setText(sceneBean.getKeywords());
            inputTextDialog.setHint(R.string.text_input_keywords_hint);
            inputTextDialog.setCancelable(false);
            inputTextDialog.setCanceledOnTouchOutside(false);
            inputTextDialog.setOnDismissListener(dialog -> {
                if (inputTextDialog.getText() != null && !inputTextDialog.getText().isEmpty()) {
                    binding.layoutScenes.layoutModify.tvKeywords.setText(inputTextDialog.getText());
                    binding.layoutScenes.layoutModify.tvKeywordsHint.setVisibility(View.INVISIBLE);
                    binding.layoutScenes.layoutTitle.tvSaveExit.setEnabled(validSceneName(binding.layoutScenes.layoutModify.tvSceneName.getText().toString(), sceneBean));
                    sceneChanged = true;
                }
            });
            inputTextDialog.show();
        });
        binding.layoutScenes.layoutModify.rlBackground.setOnClickListener(v -> {
            selectingBackground(true, sceneBean.getId() == 0 ? null : sceneBean.getName());
            List<SelectImageBean> selectImageBeans = new ArrayList<>();
            for (int i = 0; i < ConstantUtils.SCENE_PICTURE.length; i++) {
                selectImageBeans.add(new SelectImageBean(i + "", ConstantUtils.SCENE_PICTURE[i],
                        binding.layoutScenes.layoutModify.ivThumbBg.getContentDescription() != null &&
                                binding.layoutScenes.layoutModify.ivThumbBg.getContentDescription().toString().equals(i + "")));
            }
            SelectImageAdapter adapter = new SelectImageAdapter(MainActivity.this, selectImageBeans);
            binding.layoutScenes.layoutBackground.gvBackground.setAdapter(adapter);
            binding.layoutScenes.layoutBackground.gvBackground.setVerticalSpacing(30);
            binding.layoutScenes.layoutBackground.gvBackground.setNumColumns(6);
            binding.layoutScenes.layoutBackground.btnConfirm.setOnClickListener(v1 -> {
                for (SelectImageBean bean : adapter.getSelectImageList())
                    if (bean.isSelected()) {
                        if (binding.layoutScenes.layoutModify.ivThumbBg.getContentDescription() == null
                                || binding.layoutScenes.layoutModify.ivThumbBg.getContentDescription().length() == 0
                                || !bean.getName().equals(binding.layoutScenes.layoutModify.ivThumbBg.getContentDescription().toString())) {
                            binding.layoutScenes.layoutModify.ivThumbBg.setImageResource(bean.getResource());
                            binding.layoutScenes.layoutModify.ivThumbBg.setContentDescription(bean.getName());
                            binding.layoutScenes.layoutModify.ivThumbBg.setVisibility(View.VISIBLE);
                            binding.layoutScenes.layoutModify.tvChoose.setVisibility(View.INVISIBLE);
                            binding.layoutScenes.layoutTitle.tvSaveExit.setEnabled(validSceneName(binding.layoutScenes.layoutModify.tvSceneName.getText().toString(), sceneBean));
                            sceneChanged = true;
                        }
                        selectingBackground(false, sceneBean.getId() == 0 ? null : "");
                        break;
                    }
            });
        });
        binding.layoutScenes.layoutModify.tvModify.setOnClickListener(v -> {
            taskAdapter.setModifying(true);
            taskBeans = new ArrayList<>(taskAdapter.getSceneTaskBeans());
            taskModifying(true);
        });
        binding.layoutScenes.layoutModify.tvCancel.setOnClickListener(v -> {
            taskAdapter.setModifying(false);
            taskAdapter.updateList(taskBeans);
            taskBeans = null;
            taskModifying(false);
        });
        binding.layoutScenes.layoutModify.tvSaveTask.setOnClickListener(v -> {
            taskAdapter.setModifying(false);
            taskBeans = null;
            sceneBean.setTaskBeans(taskAdapter.getSceneTaskBeans());
            sceneChanged = true;
            binding.layoutScenes.layoutTitle.tvSaveExit.setEnabled(validSceneName(binding.layoutScenes.layoutModify.tvSceneName.getText().toString(), sceneBean));
            changeModifyButton(sceneBean.getTaskBeans());
            setAddTaskButton(sceneBean);
            taskModifying(false);
        });

        boolean notFull = false;
        for (HomeDeviceBean bean : homeDeviceBeans)
            if (bean.getId() == 0) {
                notFull = true;
                break;
            }
        binding.layoutScenes.layoutModify.ivDisplaySwitch.setContentDescription(ConstantUtils.OFF);
        binding.layoutScenes.layoutModify.ivDisplaySwitch.setImageResource(R.mipmap.ic_switch_off);
        for (HomeDeviceBean bean : homeDeviceBeans)
            if (bean.getId() == sceneBean.getId() && bean.getType() == ConstantUtils.TYPE_SCENE) {
                binding.layoutScenes.layoutModify.ivDisplaySwitch.setContentDescription(ConstantUtils.ON);
                binding.layoutScenes.layoutModify.ivDisplaySwitch.setImageResource(R.mipmap.ic_switch_on);
                notFull = true;
                break;
            }
        boolean finalNotFull = notFull;
        binding.layoutScenes.layoutModify.rlPutHome.setOnClickListener(v -> {
            if (finalNotFull) {
                if (binding.layoutScenes.layoutModify.ivDisplaySwitch.getContentDescription().toString().equals(ConstantUtils.ON)) {
                    binding.layoutScenes.layoutModify.ivDisplaySwitch.setContentDescription(ConstantUtils.OFF);
                    binding.layoutScenes.layoutModify.ivDisplaySwitch.setImageResource(R.mipmap.ic_switch_off);
                } else {
                    binding.layoutScenes.layoutModify.ivDisplaySwitch.setContentDescription(ConstantUtils.ON);
                    binding.layoutScenes.layoutModify.ivDisplaySwitch.setImageResource(R.mipmap.ic_switch_on);
                }
                sceneChanged = true;
                binding.layoutScenes.layoutTitle.tvSaveExit.setEnabled(validSceneName(binding.layoutScenes.layoutModify.tvSceneName.getText().toString(), sceneBean));
            }
        });
        binding.layoutScenes.layoutModify.tvAddSceneTask.setText(sceneBean.getType() == ConstantUtils.SceneType.switcher ? R.string.text_toggle_task : R.string.text_scene_task);
        setAddTaskButton(sceneBean);
        binding.layoutScenes.layoutModify.tvAddSceneTask.setOnClickListener(v -> {
            if (!taskAdapter.isModifying()) {
                if (sceneBean.getType() == ConstantUtils.SceneType.switcher) {
                    if (taskAdapter.getSceneTaskBeans().size() == 0) {
                        addingTask(Module.device, sceneBean.getId() == 0 ? null : sceneBean.getName());
                        selectDevices = new ArrayList<>();
                        binding.layoutScenes.layoutSelect.tabTitle.addTab(binding.layoutScenes.layoutSelect.tabTitle.newTab());
                        selectDevices.add(new ArrayList<>());
                        for (DeviceBean b : deviceLists.get(0)) {
                            if (b.getKind() == ConstantUtils.DeviceKind.switcher && !b.getMac().equals(sceneBean.getMac())) {
                                boolean found = false;
                                for (SceneBean bean : modifyScenes) {
                                    for (SceneBean.TaskBean b1 : bean.getTaskBeans()) {
                                        if (b1.getActionEnum() == ConstantUtils.ActionType.scene) {
                                            found = findDeviceInScene(b1.getDeviceId(), b.getId());
                                            if (found)
                                                break;
                                        } else if (b.getId() == b1.getDeviceId()) {
                                            found = true;
                                            break;
                                        }
                                    }
                                }
                                if (!found)
                                    selectDevices.get(0).add(new DeviceBean(b));
                            }
                        }
                        initSelect(Module.scene, true, true, v1 -> {
                            sceneBean.getTaskBeans().clear();
                            for (DeviceBean bean : selectDeviceFragments.get(0).getDeviceBeans())
                                if (bean.isSelected()) {
                                    sceneBean.getTaskBeans().add(new SceneBean.TaskBean(bean.getId(), ConstantUtils.ActionType.toggle));
                                    break;
                                }
                            taskAdapter.updateList(sceneBean.getTaskBeans());
                            changeModifyButton(sceneBean.getTaskBeans());
                            addingTask(Module.home, sceneBean.getId() == 0 ? null : "");
                            setAddTaskButton(sceneBean);
                            sceneChanged = true;
                            binding.layoutScenes.layoutTitle.tvSaveExit.setEnabled(validSceneName(binding.layoutScenes.layoutModify.tvSceneName.getText().toString(), sceneBean));
                        });
                    }
                } else {
                    addingTask(Module.scene, sceneBean.getId() == 0 ? null : sceneBean.getName());
                    selectScenes = new ArrayList<>();
                    binding.layoutScenes.layoutSelect.tabTitle.addTab(binding.layoutScenes.layoutSelect.tabTitle.newTab());
                    selectScenes.add(new ArrayList<>());
                    for (SceneBean b : sceneLists.get(0)) {
                        if ((sceneBean.getType() != ConstantUtils.SceneType.local && b.getKind() == ConstantUtils.DeviceKind.panel) || b.getId() == sceneBean.getId())
                            continue;
                        boolean found = false;
                        for (SceneBean b1 : modifyScenes) {
                            for (SceneBean.TaskBean bean : b1.getTaskBeans()) {
                                if (bean.getActionEnum() == ConstantUtils.ActionType.scene) {
                                    if (bean.getDeviceId() == b.getId()) {
                                        found = true;
                                        break;
                                    } else {
                                        found = findSceneInScene(bean.getDeviceId(), b.getId());
                                    }
                                }
                            }
                            if (found)
                                break;
                        }
                        if (!found)
                            selectScenes.get(0).add(new SceneBean(b));
                    }
                    initSelect(Module.scene, false, false, v1 -> {
                        for (SceneBean bean : selectScenes.get(0))
                            if (bean.isSelected())
                                sceneBean.getTaskBeans().add(new SceneBean.TaskBean(bean.getId(), ConstantUtils.ActionType.scene));
                        taskAdapter.updateList(sceneBean.getTaskBeans());
                        changeModifyButton(sceneBean.getTaskBeans());
                        addingTask(Module.home, sceneBean.getId() == 0 ? null : "");
                        setAddTaskButton(sceneBean);
                        sceneChanged = true;
                        binding.layoutScenes.layoutTitle.tvSaveExit.setEnabled(validSceneName(binding.layoutScenes.layoutModify.tvSceneName.getText().toString(), sceneBean));
                    });
                }
            }
        });
        binding.layoutScenes.layoutModify.tvAddDeviceTask.setOnClickListener(v -> {
            if (!taskAdapter.isModifying()) {
                addingTask(Module.device, sceneBean.getId() == 0 ? null : sceneBean.getName());
                selectDevices = new ArrayList<>();
                binding.layoutScenes.layoutSelect.tabTitle.addTab(binding.layoutScenes.layoutSelect.tabTitle.newTab());
                selectDevices.add(new ArrayList<>());
                for (DeviceBean b : deviceLists.get(0)) {
                    if (b.getKind() == ConstantUtils.DeviceKind.panel || (sceneBean.getMac() != null && b.getMac().equals(sceneBean.getMac()))
                            || (b.getKind() == ConstantUtils.DeviceKind.temperature && sceneBean.getType() != ConstantUtils.SceneType.local))
                        continue;
                    boolean found = false;
                    for (SceneBean bean : modifyScenes) {
                        for (SceneBean.TaskBean b1 : bean.getTaskBeans())
                            if (b1.getActionEnum() == ConstantUtils.ActionType.scene) {
                                found = findDeviceInScene(b1.getDeviceId(), b.getId());
                                if (found)
                                    break;
                            } else if (b.getId() == b1.getDeviceId()) {
                                found = true;
                                break;
                            }
                        if (found)
                            break;
                    }
                    if (!found)
                        selectDevices.get(0).add(new DeviceBean(b));
                }
                initSelect(Module.scene, false, true, v1 -> {
                    List<SceneBean.TaskBean> temp = new ArrayList<>(taskAdapter.getSceneTaskBeans());
                    for (DeviceBean bean : selectDevices.get(0))
                        if (bean.isSelected())
                            temp.add(new SceneBean.TaskBean(bean.getId(), ConstantUtils.ActionType.on));
                    Integer max = ConstantUtils.maxCommand.get(sceneBean.getType());
                    if (null != max) {
                        Log.d(TAG, "selected:" + countCommand(temp));
                        if (countCommand(temp) > max) {
                            myToast("选择设备数量不能超过" + max + "个！");
                            return;
                        }
                    }
                    sceneBean.setTaskBeans(temp);
                    taskAdapter.updateList(sceneBean.getTaskBeans());
                    changeModifyButton(sceneBean.getTaskBeans());
                    addingTask(Module.home, sceneBean.getId() == 0 ? null : "");
                    setAddTaskButton(sceneBean);
                    sceneChanged = true;
                    binding.layoutScenes.layoutTitle.tvSaveExit.setEnabled(validSceneName(binding.layoutScenes.layoutModify.tvSceneName.getText().toString(), sceneBean));
                });
            }
        });
        binding.layoutScenes.layoutTitle.tvSaveExit.setEnabled(sceneChanged);
        binding.layoutScenes.layoutTitle.tvSaveExit.setOnClickListener(v -> {
            Integer max = ConstantUtils.maxCommand.get(sceneBean.getType());
            if (null != max && countCommand(sceneBean.getTaskBeans()) > max) {
                myToast("设备数量不能超过" + max + "个！");
                return;
            }
            deviceChanged = true;
            sceneBean.setTaskBeans(sceneBean.getTaskBeans());
            sceneBean.setName(binding.layoutScenes.layoutModify.tvSceneName.getText().toString());
            sceneBean.setRoom(binding.layoutScenes.layoutModify.tvRoomName.getText().toString());
            sceneBean.setKeywords(binding.layoutScenes.layoutModify.tvKeywords.getText().toString());
            if (binding.layoutScenes.layoutModify.ivThumbBg.getContentDescription() != null && binding.layoutScenes.layoutModify.ivThumbBg.getContentDescription().length() > 0)
                sceneBean.setPicture(binding.layoutScenes.layoutModify.ivThumbBg.getContentDescription().toString());
            else
                sceneBean.setPicture(String.format(Locale.CHINA, "%d", (int) (Math.random() * ConstantUtils.SCENE_PICTURE.length)));

            db = myHelper.getWritableDatabase();
            db.beginTransaction();

            ContentValues values = new ContentValues();
            boolean roomChange = false;
            if (roomBeans.size() != tempRoomBeans.size()) {
                for (RoomBean bean : tempRoomBeans)
                    if (bean.getId() == 0) {
                        roomChange = true;
                        values.put(DatabaseStatic.Room.NAME, bean.getName());
                        bean.setId((int) db.insert(DatabaseStatic.TABLE_ROOM, null, values));
                        roomOrder.add(bean.getId());
                        deviceOrder.add(new ArrayList<>());
                        sceneOrder.add(new ArrayList<>());
                        values = new ContentValues();
                    }
                settings.setDeviceOrder(deviceOrder);
                settings.setSceneOrder(sceneOrder);
                settings.setRoomOrder(roomOrder);
                saveSettings();
                roomBeans = new ArrayList<>(tempRoomBeans);
                tempRoomBeans = null;
            }
            values.put(DatabaseStatic.Scene.TYPE, sceneBean.getType().ordinal());
            values.put(DatabaseStatic.Scene.NAME, sceneBean.getName());
            values.put(DatabaseStatic.Scene.PICTURE, sceneBean.getPicture());
            int index = 0;
            for (RoomBean bean : roomBeans)
                if (bean.getName().equals(sceneBean.getRoom())) {
                    values.put(DatabaseStatic.Scene.ROOM_ID, bean.getId());
                    break;
                } else
                    index++;
            values.put(DatabaseStatic.Scene.KEYWORDS, sceneBean.getKeywords());
            if (sceneBean.getId() == 0) {
                sceneBean.setId((int) db.insert(DatabaseStatic.TABLE_SCENE, null, values));
                sceneOrder.get(0).add(sceneBean.getId());
                if (index < sceneOrder.size())
                    sceneOrder.get(index).add(sceneBean.getId());
            } else {
                db.update(DatabaseStatic.TABLE_SCENE, values, DatabaseStatic.Scene.ID + "=?", new String[]{sceneBean.getId() + ""});
                db.delete(DatabaseStatic.TABLE_TASK, DatabaseStatic.Task.SCENE_ID + "=?", new String[]{sceneBean.getId() + ""});
            }
            for (SceneBean.TaskBean bean : sceneBean.getTaskBeans()) {
                values = new ContentValues();
                values.put(DatabaseStatic.Task.ACTION, bean.getAction());
                values.put(DatabaseStatic.Task.SCENE_ID, sceneBean.getId());
                values.put(DatabaseStatic.Task.DEVICE_ID, bean.getDeviceId());
                db.insert(DatabaseStatic.TABLE_TASK, null, values);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            if (sceneBean.getKind() == ConstantUtils.DeviceKind.switcher || sceneBean.getKind() == ConstantUtils.DeviceKind.panel) {
                devIndex = 0;
                resendCount = 0;
                cmdType = CommandType.setTask;
                currentScene = sceneBean;
                commHandler.sendEmptyMessage(HANDLER_SEND_CMD);
            }

            if (roomChange)
                initDevicePager();

            initScenePager();
            boolean refreshed = false;
            for (HomeDeviceBean bean : homeDeviceBeans)
                if (bean.getId() == sceneBean.getId()) {
                    if (binding.layoutScenes.layoutModify.ivDisplaySwitch.getContentDescription().toString().equals(ConstantUtils.OFF)) {
                        bean.setId(0);
                        bean.setType(0);
                        refreshHomeItems();
                    } else {
                        refreshed = true;
                        break;
                    }
                }
            if (!refreshed && binding.layoutScenes.layoutModify.ivDisplaySwitch.getContentDescription().toString().equals(ConstantUtils.ON))
                for (HomeDeviceBean bean : homeDeviceBeans)
                    if (bean.getId() == 0) {
                        bean.setId(sceneBean.getId());
                        bean.setType(ConstantUtils.TYPE_SCENE);
                        refreshHomeItems();
                        break;
                    }
            if (modifyingScene) {
                modifyingScene = false;
                selectMode = false;
                modifying = true;
                refreshFragments(scenesFragments, scenePagerAdapter);
            }
            changeSceneFrame(FrameType.list);
        });
    }

    private void changeModifyButton(List<SceneBean.TaskBean> taskBeans) {
        binding.layoutScenes.layoutModify.tvModify.setEnabled(taskBeans.size() > 0);
        if (taskBeans.size() > 0) {
            for (SceneBean.TaskBean bean : taskBeans)
                if (bean.getActionEnum() == ConstantUtils.ActionType.on || bean.getActionEnum() == ConstantUtils.ActionType.off) {
                    binding.layoutScenes.layoutModify.tvReverse.setEnabled(true);
                    return;
                }
        } else
            binding.layoutScenes.layoutModify.tvReverse.setEnabled(false);
    }

    private int countCommand(List<SceneBean.TaskBean> list) {
        List<SceneBean.TaskBean> beans = new ArrayList<>(list);
        for (int i = 0; i < beans.size(); i++) {
            if (beans.get(i).getActionEnum() != ConstantUtils.ActionType.scene) {
                for (DeviceBean bean : allDevices)
                    if (bean.getId() == beans.get(i).getDeviceId()) {
                        for (int j = beans.size() - 1; j > i; j--) {
                            if (beans.get(j).getActionEnum() != ConstantUtils.ActionType.scene) {
                                for (DeviceBean bean1 : allDevices)
                                    if (bean1.getId() == beans.get(j).getDeviceId()) {
                                        if (bean1.getMac().equals(bean.getMac()))
                                            beans.remove(j);
                                        break;
                                    }
                            }
                        }
                        break;
                    }
            }
        }
        return beans.size();
    }

    private void setAddTaskButton(SceneBean sceneBean) {
        if (sceneBean.getType() == ConstantUtils.SceneType.switcher) {
            binding.layoutScenes.layoutModify.tvAddSceneTask.setEnabled(sceneBean.getTaskBeans().size() == 0);
            Integer max = ConstantUtils.maxCommand.get(sceneBean.getType());
            binding.layoutScenes.layoutModify.tvAddDeviceTask.setEnabled(null == max || sceneBean.getTaskBeans().size() <= max);
        }
        binding.layoutScenes.layoutModify.tvNoTask.setVisibility(sceneBean.getTaskBeans().size() > 0 ? View.INVISIBLE : View.VISIBLE);
    }

    private void exitModifyScene() {
        if (modifyingScene) {
            modifyingScene = false;
            selectMode = false;
            modifying = true;
            refreshFragments(scenesFragments, scenePagerAdapter);
        }
        changeSceneFrame(FrameType.list);
    }

    private void addingTask(Module module, String name) {
        if (module == Module.home) {
            singleSelect = false;
            selectMode = false;
            binding.layoutScenes.layoutTitle.tvTitle.setText(name == null ? R.string.text_create_scene : R.string.text_modify_scene);
            changeSceneFrame(FrameType.modify);
            if (selectDeviceAdapter != null)
                selectDeviceAdapter = null;
            if (selectDeviceFragments != null)
                selectDeviceFragments = null;
            if (selectSceneFragments != null)
                selectSceneFragments = null;
            if (selectScenes != null)
                selectScenes = null;
            if (selectDevices != null)
                selectDevices = null;
            setUpperLevelText();
        } else {
            if (name == null)
                binding.layoutScenes.layoutTitle.tvBack.setText(R.string.text_create_scene);
            else
                binding.layoutScenes.layoutTitle.tvBack.setText(String.format("%s\"%s\"", getString(R.string.text_modify_scene), name));
            changeSceneFrame(FrameType.select);
            binding.layoutScenes.layoutSelect.btnSelect.setVisibility(View.VISIBLE);
            binding.layoutScenes.layoutSelect.btnSelectAll.setVisibility(singleSelect ? View.GONE : View.VISIBLE);
            binding.layoutScenes.layoutSelect.btnConfirm.setVisibility(View.VISIBLE);
            binding.layoutScenes.layoutTitle.tvTitle.setText(module == Module.scene ? R.string.text_select_scene : R.string.text_select_device);
            binding.layoutScenes.layoutSelect.tabTitle.removeAllTabs();
        }
    }

    private void selectingBackground(boolean selecting, String name) {
        if (selecting) {
            if (name == null)
                binding.layoutScenes.layoutTitle.tvBack.setText(R.string.text_create_scene);
            else
                binding.layoutScenes.layoutTitle.tvBack.setText(String.format("%s\"%s\"", getString(R.string.text_modify_scene), name));
            binding.layoutScenes.layoutTitle.tvTitle.setText(R.string.text_select_background);
            changeSceneFrame(FrameType.background);
        } else {
            changeSceneFrame(FrameType.modify);
            binding.layoutScenes.layoutTitle.tvTitle.setText(name == null ? R.string.text_create_scene : R.string.text_modify_scene);
            setUpperLevelText();
        }
    }

    private void taskModifying(boolean modifying) {
        if (modifying) {
            binding.layoutScenes.layoutModify.tvModify.setVisibility(View.GONE);
            binding.layoutScenes.layoutModify.tvSaveTask.setVisibility(View.VISIBLE);
            binding.layoutScenes.layoutModify.tvCancel.setVisibility(View.VISIBLE);
            binding.layoutScenes.layoutModify.tvReverse.setVisibility(View.GONE);
            binding.layoutScenes.layoutModify.tvAddSceneTask.setVisibility(View.GONE);
            binding.layoutScenes.layoutModify.tvAddDeviceTask.setVisibility(View.GONE);
        } else {
            binding.layoutScenes.layoutModify.tvModify.setVisibility(View.VISIBLE);
            binding.layoutScenes.layoutModify.tvSaveTask.setVisibility(View.INVISIBLE);
            binding.layoutScenes.layoutModify.tvCancel.setVisibility(View.INVISIBLE);
            binding.layoutScenes.layoutModify.tvReverse.setVisibility(View.VISIBLE);
            binding.layoutScenes.layoutModify.tvAddSceneTask.setVisibility(View.VISIBLE);
            binding.layoutScenes.layoutModify.tvAddDeviceTask.setVisibility(View.VISIBLE);
        }
    }

    private boolean findSceneInScene(int sceneId, int id) {
        for (SceneBean bean : allScenes)
            if (bean.getId() == sceneId)
                for (SceneBean.TaskBean b1 : bean.getTaskBeans()) {
                    if (b1.getActionEnum() == ConstantUtils.ActionType.scene) {
                        if (b1.getDeviceId() == id)
                            return true;
                        if (findSceneInScene(b1.getDeviceId(), id))
                            return true;
                    }
                }
        return false;
    }

    private void initSelectButtonClickListener(boolean isDevice, Button buttonMode, Button buttonAll, TabLayout tabLayout) {
        buttonMode.setOnClickListener(v1 -> {
            if (buttonMode.getText().equals(getString(R.string.button_select))) {
                buttonMode.setText(R.string.button_control);
                selectMode = true;
                if (null != buttonAll)
                    buttonAll.setVisibility(View.VISIBLE);
            } else {
                selectMode = false;
                buttonMode.setText(R.string.button_select);
                if (null != buttonAll)
                    buttonAll.setVisibility(View.INVISIBLE);
            }
            if (isDevice)
                refreshFragments(selectDeviceFragments, selectDeviceAdapter);
            else
                refreshFragments(selectSceneFragments, selectSceneAdapter);
        });
        if (null != buttonAll) {
            buttonAll.setOnClickListener(v1 -> {
                if (isDevice) {
                    for (DeviceBean bean : selectDeviceFragments.get(tabLayout.getSelectedTabPosition()).getDeviceBeans())
                        bean.setSelected(buttonAll.getText().equals(getString(R.string.button_select_all)));
                    selectDeviceFragments.get(tabLayout.getSelectedTabPosition()).updateDevice(null);
                } else {
                    for (SceneBean bean : selectSceneFragments.get(tabLayout.getSelectedTabPosition()).getSceneBeans())
                        bean.setSelected(buttonAll.getText().equals(getString(R.string.button_select_all)));
                    selectSceneFragments.get(tabLayout.getSelectedTabPosition()).updateScene(null);
                }
                buttonAll.setText(buttonAll.getText().equals(getString(R.string.button_select_all)) ? R.string.button_cancel_select_all : R.string.button_select_all);
            });
        }
        if (isDevice)
            refreshFragments(selectDeviceFragments, selectDeviceAdapter);
        else
            refreshFragments(selectSceneFragments, selectSceneAdapter);
    }

    private void setUpperLevelText() {
        if (modifyScenes.size() > 1) {
            if (modifyScenes.get(modifyScenes.size() - 2).getId() == 0)
                binding.layoutScenes.layoutTitle.tvBack.setText(R.string.text_create_scene);
            else
                binding.layoutScenes.layoutTitle.tvBack.setText(String.format("%s\"%s\"", getString(R.string.text_modify_scene), modifyScenes.get(modifyScenes.size() - 2).getName()));
        } else
            binding.layoutScenes.layoutTitle.tvBack.setText(R.string.text_scene);
    }

    private void fillDevicePager(ViewPager2 viewPager, List<List<DeviceBean>> deviceBeans, DevicePagerAdapter adapter, Button btnSelectAll) {
        viewPager.setAdapter(adapter);
        currentTabTitle.clearOnTabSelectedListeners();
        currentTabTitle.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (btnSelectAll != null) {
                    btnSelectAll.setText(R.string.button_cancel_select_all);
                    for (DeviceBean bean : deviceBeans.get(tab.getPosition()))
                        if (!bean.isSelected()) {
                            btnSelectAll.setText(R.string.button_select_all);
                            break;
                        }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        new TabLayoutMediator(currentTabTitle, viewPager, (tab, position) ->
                tab.setText(String.format(Locale.CHINA, "%s(%d)", roomBeans.get(position).getName(), deviceBeans.get(position).size()))).attach();
    }

    private void fillScenePager(ViewPager2 viewPager, List<List<SceneBean>> sceneBeans, ScenePagerAdapter adapter, Button btnSelectAll) {
        viewPager.setAdapter(adapter);
        currentTabTitle.clearOnTabSelectedListeners();
        currentTabTitle.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (btnSelectAll != null) {
                    btnSelectAll.setText(R.string.button_cancel_select_all);
                    for (SceneBean bean : sceneBeans.get(tab.getPosition()))
                        if (!bean.isSelected()) {
                            btnSelectAll.setText(R.string.button_select_all);
                            break;
                        }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        new TabLayoutMediator(currentTabTitle, viewPager, (tab1, position) ->
                tab1.setText(String.format(Locale.CHINA, "%s(%d)", roomBeans.get(position).getName(), sceneBeans.get(position).size()))).attach();
    }

    private boolean findDeviceInScene(int sceneId, int id) {
        for (SceneBean bean : allScenes)
            if (bean.getId() == sceneId) {
                for (SceneBean.TaskBean b1 : bean.getTaskBeans()) {
                    Log.d(TAG, "action:" + b1.getActionEnum() + ",id" + b1.getDeviceId());
                    if (b1.getActionEnum() == ConstantUtils.ActionType.scene && findDeviceInScene(b1.getDeviceId(), id))
                        return true;
                    else if (b1.getActionEnum() != ConstantUtils.ActionType.toggle && id == b1.getDeviceId())
                        return true;
                }
                break;
            }
        return false;
    }

    private void refreshFragments(List<?> list, Object adapter) {
        if (adapter != null && list != null) {
            if (adapter instanceof DevicePagerAdapter) {
                for (int i = 0; i < list.size(); i++) {
                    if (i != currentTabTitle.getSelectedTabPosition())
                        ((DevicePagerAdapter) adapter).notifyItemChanged(i);
                    else
                        ((DevicesFragment) list.get(i)).updateDevice(null);
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    if (i != currentTabTitle.getSelectedTabPosition())
                        ((ScenePagerAdapter) adapter).notifyItemChanged(i);
                    else
                        ((ScenesFragment) list.get(i)).updateScene(null);
                }
            }
        }
    }

    private boolean validSceneName(String text, SceneBean sceneBean) {
        if (text == null || text.isEmpty())
            return false;
        if (sceneBean.getId() != 0 && sceneBean.getType() == ConstantUtils.SceneType.local)
            for (SceneBean bean : allScenes)
                if (bean.getId() != sceneBean.getId() && bean.getName().equals(text)) {
                    myToast(getString(R.string.text_duplicate_scene));
                    return false;
                }
        return true;
    }

    private void changeModule(Module module) {
        if (module != currentModule) {
            Log.d(TAG, "modifying:" + modifying + ",selectMode:" + selectMode + ",modifyingScene:" + modifyingScene + ",modifyingRoom:" + modifyingRoom);
            if (modifying || selectMode || modifyingScene || modifyingRoom) {
                WarningDialog dialog = new WarningDialog(MainActivity.this);
                dialog.setOnPositiveClick(v1 -> {
                    modifying = false;
                    selectMode = false;
                    modifyingScene = false;
                    modifyingRoom = false;
                    if (currentModule != Module.home)
                        modifyMode(currentModule);
                    switch (currentModule) {
                        case device:
                            if (deviceChanged) {
                                initDevicePager();
                                deviceChanged = false;
                            } else
                                refreshFragments(devicesFragments, devicePagerAdapter);
                            break;
                        case scene:
                            if (deviceChanged) {
                                initScenePager();
                                deviceChanged = false;
                            } else
                                refreshFragments(scenesFragments, scenePagerAdapter);
                            break;
                    }
                    enterModule(module);
                    dialog.dismiss();
                });
                dialog.setMessage(R.string.text_exit_no_save);
                dialog.show();
            } else
                enterModule(module);
        }
    }

    private void enterModule(Module module) {
        binding.layoutHome.rlContent.setVisibility(module == Module.home ? View.VISIBLE : View.GONE);
        binding.layoutDevices.rlContent.setVisibility(module == Module.device ? View.VISIBLE : View.GONE);
        binding.layoutScenes.rlContent.setVisibility(module == Module.scene ? View.VISIBLE : View.GONE);
        binding.layoutRoom.rlContent.setVisibility(module == Module.room ? View.VISIBLE : View.GONE);
        binding.btnHome.setSelected(module == Module.home);
        binding.btnDevice.setSelected(module == Module.device);
        binding.btnScene.setSelected(module == Module.scene);
        binding.btnRoom.setSelected(module == Module.room);
        currentModule = module;
        selectDeviceFragments = null;
        selectDeviceAdapter = null;
        selectDevices = null;
        selectSceneFragments = null;
        selectSceneAdapter = null;
        selectScenes = null;
        switch (module) {
            case home:
                changeHomeFrame(FrameType.list);
            case device:
                currentTabTitle = binding.layoutDevices.layoutList.tlDevices;
                changeDeviceFrame(FrameType.list);
                break;
            case scene:
                currentTabTitle = binding.layoutScenes.layoutList.tlScenes;
                changeSceneFrame(FrameType.list);
                break;
            case room:
                changeRoomFrame(FrameType.list);
                initRoom();
                break;
        }
    }

    private void SaveWeather(WeatherBean weatherBean) {
        try {
            FileWriter fw = new FileWriter(ConstantUtils.FILE_WEATHER);
            fw.append(new Gson().toJson(weatherBean));
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WeatherBean ReadWeather() {
        File dataFile = new File(ConstantUtils.FILE_WEATHER);
        WeatherBean weatherBean = new WeatherBean();
        if (dataFile.exists()) {
            try {
                FileReader fr = new FileReader(dataFile);
                BufferedReader br = new BufferedReader(fr);
                String content;
                StringBuilder temp = new StringBuilder();
                while ((content = br.readLine()) != null) {
                    temp.append(content);
                }
                br.close();
                fr.close();
                weatherBean = new Gson().fromJson(temp.toString(), WeatherBean.class);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return weatherBean;
    }

    private void initLocation() {
        try {
            rssiBroadcast = new RssiBroadcast();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
            intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            registerReceiver(rssiBroadcast, intentFilter);

            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connMgr.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                LocationClient.setAgreePrivacy(true);
                SDKInitializer.setAgreePrivacy(getApplicationContext(), true);
                // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
                SDKInitializer.initialize(getApplicationContext());
                LocationClient locationClient = new LocationClient(MainActivity.this);
                //通过LocationClientOption设置LocationClient相关参数
                LocationClientOption option = new LocationClientOption();
                option.setOpenGps(true); // 打开gps
                option.setCoorType("bd09ll"); // 设置坐标类型
                option.setScanSpan(1000);

                //设置locationClientOption
                locationClient.setLocOption(option);

                //注册LocationListener监听器
                MyLocationListener myLocationListener = new MyLocationListener();
                locationClient.registerLocationListener(myLocationListener);
                //开启地图定位图层
                locationClient.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location != null && 0 != (int) location.getLatitude() && 0 != (int) location.getLongitude()) {
                lastLocation = location;
//                Log.d("ZBEST", "坐标:" + lastLocation.getLatitude() + getString(R.string.text_speech_split) + lastLocation.getLongitude());
            }
        }
    }

    public void startApp(String packageName, String className, boolean isService) {
        PackageManager packageManager = getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent != null) {
            intent.setClassName(packageName, className);
            if (isService)
                startService(intent);
            else
                startActivity(intent);
        }
    }

    private void initWeather() {
        WeatherBean weatherBean = ReadWeather();
        if (!weatherBean.isSuccess()) {
            acquireWeatherByArea(lastLocation != null ? "" : "深圳");
        } else {
            binding.layoutHome.layoutWidget.tvTemperature.setText(String.format("%s%s", weatherBean.getData().getNow().getTemperature(), getString(R.string.text_temperature_degree)));
            binding.layoutHome.layoutWidget.tvWeather.setText(weatherBean.getData().getNow().getWeather());
            File file = new File(ConstantUtils.FILE_WEATHER_PIC);
            if (file.exists()) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    binding.layoutHome.layoutWidget.ivWeather.setImageBitmap(BitmapFactory.decodeStream(fileInputStream));
                    fileInputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connMgr.getActiveNetworkInfo();
                if (info != null && info.isAvailable())
                    new DownloadPictureTask().execute(weatherBean.getData().getNow().getWeather_pic(), "Weather");
            }
        }
    }

    private void acquireWeatherByArea(String area) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connMgr.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String url = "https://api.jumdata.com/weather/query/by-area";

            long timestamp = System.currentTimeMillis();
            String appId = "JlY9CGRZZRgxoMZv";
            String appSecret = "JlY9CGRZZRgxoMZvCs3Z5hkD4p2AjMJK";
            String sign = Sha256.getSHA256(appId + appSecret + timestamp);
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody body;
                if (area.isEmpty()) {
                    url = "https://api.jumdata.com/weather/query/by-longitudeLatitude";
                    body = new FormBody.Builder()
                            .add("appId", appId)  // 聚美智数分配的appId
                            .add("timestamp", timestamp + "")
                            .add("sign", sign)
                            .add("productCode", "weather_query")// 固定值
                            .add("longitude", lastLocation.getLongitude() + "") // 经度
                            .add("latitude", lastLocation.getLatitude() + "") // 纬度  .add("weaid", "1")
                            .build();
                } else
                    body = new FormBody.Builder()
                            .add("appId", appId)  // 聚美智数分配的appId
                            .add("timestamp", timestamp + "")
                            .add("sign", sign)
                            .add("productCode", "weather_query")// 固定值
                            .add("area", area)
//                    .add("longitude", "") // 经度
//                    .add("latitude", "") // 纬度  .add("weaid", "1")
                            .build();

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) {
                        ResponseBody body = response.body();
                        if (body != null) {
                            try {
                                String respond = body.string();
                                Log.d("LEON", respond);
                                WeatherBean bean = new Gson().fromJson(respond, WeatherBean.class);
                                if (bean.isSuccess()) {
                                    runOnUiThread(() -> {
                                        binding.layoutHome.layoutWidget.tvTemperature.setText(String.format("%s%s", bean.getData().getNow().getTemperature(), getString(R.string.text_temperature_degree)));
                                        binding.layoutHome.layoutWidget.tvWeather.setText(bean.getData().getNow().getWeather());
                                        new DownloadPictureTask().execute(bean.getData().getNow().getWeather_pic(), "Weather");
                                    });
                                    SaveWeather(bean);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveSettings() {
        try {
            FileWriter fw = new FileWriter(ConstantUtils.FILE_LOCAL_SETTINGS);
            fw.append(new Gson().toJson(settings));
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readSettings() {
        File dataFile = new File(ConstantUtils.FILE_LOCAL_SETTINGS);
        settings = new SettingsBean();
        if (dataFile.exists()) {
            try {
                FileReader fr = new FileReader(dataFile);
                BufferedReader br = new BufferedReader(fr);
                String content;
                StringBuilder temp = new StringBuilder();
                while ((content = br.readLine()) != null) {
                    temp.append(content);
                }
                br.close();
                fr.close();
                settings = new Gson().fromJson(temp.toString(), SettingsBean.class);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private void initScreenSaver() {
        File file = new File(ConstantUtils.FILE_SCREEN_SAVER_APK);
        if (!file.exists() || file.length() != 5805805)
            new DownloadApkTask().execute(NRSign.isYHK() ? ConstantUtils.OLD_SCREEN_SAVER_URL : ConstantUtils.SCREEN_SAVER_URL, ConstantUtils.FILE_SCREEN_SAVER_APK);
        else
            startApp("com.leon.screensaver", "com.leon.screensaver.ScreenSaverService", true);
    }

    private void writeToFile(String log) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
            BufferedWriter bfw = new BufferedWriter(new FileWriter(ConstantUtils.FILE_DEBUG_LOG, true));
            bfw.write(String.format(Locale.CHINA, "%s\n%s\n", df.format(new Date()), log));
            bfw.flush();
            bfw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class VolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(VOLUME_CHANGED_ACTION)
                    && intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1) == AudioManager.STREAM_MUSIC) {
                runOnUiThread(() -> {
                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    binding.layoutHome.layoutWidget.sbVolume.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
                });
            }
        }
    }


    private class RssiBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                // Wifi的连接速度及信号强度：
                int strength;
                final int[] wifiSignal = {R.mipmap.ic_wifi1,
                        R.mipmap.ic_wifi2,
                        R.mipmap.ic_wifi3,
                        R.mipmap.ic_wifi4,
                        R.mipmap.ic_wifi0,
                        R.mipmap.ic_ethernet};
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isAvailable()
                        && networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                    strength = wifiSignal.length - 1;
                } else {
                    WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
                    if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                        WifiInfo info = wifiManager.getConnectionInfo();
                        String ssid = info.getSSID();
                        if (ssid.startsWith("\"") && ssid.endsWith("\""))
                            ssid = ssid.substring(1, ssid.length() - 1);
                        if (ssid.length() > 0 && !"0x".equals(ssid) && !"<unknown ssid>".equals(ssid))
                            strength = WifiManager.calculateSignalLevel(info.getRssi(), wifiSignal.length - 2);
                        else
                            strength = 0;
                    } else {
                        strength = wifiSignal.length - 2;
                    }
                }
                runOnUiThread(() -> binding.layoutHome.layoutWidget.ivWifi.setImageResource(wifiSignal[strength]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetVersion extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(ConstantUtils.VERSION_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                    InputStream is = connection.getInputStream();

                    int len;
                    byte[] buf = new byte[1024];
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((len = is.read(buf)) != -1) {
                        String s = new String(buf, 0, len);
                        stringBuilder.append(s);
                    }
                    Log.d(TAG, "version:" + stringBuilder);
                    versionBean = new Gson().fromJson(stringBuilder.toString(), UpdateVersionBean.class);
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "success";
        }

        private boolean newVersion() {
            if (versionBean != null && versionBean.getVersion() != null) {
                String[] version = versionBean.getVersion().split("\\.");
                if (version.length == 3) {
                    try {
                        int code = Integer.parseInt(version[0]) * 1000 * 1000 + Integer.parseInt(version[1]) * 1000 + Integer.parseInt(version[2]);
                        version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName.split("\\.");
                        if (version.length == 3 && code > Integer.parseInt(version[0]) * 1000 * 1000 + Integer.parseInt(version[1]) * 1000 + Integer.parseInt(version[2])) {
                            return true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(String result) {
            if (newVersion())
                otherHandler.sendEmptyMessage(HANDLER_UPDATE);
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadApkTask extends AsyncTask<String, Integer, String> {
        private String which;

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            File file;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                //设置请求方式（一般提交数据使用POST，获取数据使用GET）
                connection.setRequestMethod("GET");
                //设置请求时间
                connection.setConnectTimeout(5000);
                // expect HTTP 200 OK, so we don't mistakenly save error
                // report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    myToast(connection.getResponseCode() + " " + connection.getResponseMessage());
                    return "Server returned HTTP "
                            + connection.getResponseCode() + " "
                            + connection.getResponseMessage();
                }
                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();
                which = sUrl[1];
                file = new File(sUrl[1]);
                if (file.exists()) {
                    file.delete();
                }
                input = connection.getInputStream();
                output = Files.newOutputStream(file.toPath());
                byte[] data = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
//                myApp.myToast(UpdateAppActivity.this, "您未打开SD卡权限！");
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            Message msg = otherHandler.obtainMessage(HANDLER_UPDATE_PROGRESS);
            msg.arg1 = progress[0];
            otherHandler.sendMessage(msg);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                otherHandler.sendEmptyMessage(HANDLER_UPDATE_FAIL);
            } else {
                if (updateDialog != null && updateDialog.isShowing())
                    updateDialog.dismiss();
                File file = new File(which);
                if (file.exists()) {
                    //安装应用
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = FileProvider.getUriForFile(MainActivity.this, getPackageName() + ".provider", file);
                    Log.d(TAG, "APK uri:" + uri);
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                    new Handler(msg -> {
                        initScreenSaver();
                        return false;
                    }).sendEmptyMessageDelayed(1, 1000 * 180);
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadPictureTask extends AsyncTask<String, Void, Bitmap> {
        private String whichView;

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            try {
                whichView = strings[1];
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(strings[0]).build();
                ResponseBody body = client.newCall(request).execute().body();
                if (body != null) {
                    InputStream in = body.byteStream();
                    bitmap = BitmapFactory.decodeStream(in);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (null != bitmap && whichView != null) {
                runOnUiThread(() -> {
                    switch (whichView) {
                        case "Album":
                            binding.layoutHome.layoutWidget.ivAlbum.setImageBitmap(bitmap);
                            break;
                        case "Weather":
                            binding.layoutHome.layoutWidget.ivWeather.setImageBitmap(bitmap);
                            try {
                                File filePic = new File(ConstantUtils.FILE_WEATHER_PIC);
                                if (filePic.exists() && !filePic.delete())
                                    myToast("创建删除失败！");
                                FileOutputStream fos = new FileOutputStream(filePic);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                                fos.flush();
                                fos.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                });
            }
            super.onPostExecute(bitmap);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        File file = new File(ConstantUtils.APP_PATH);
        if (!file.exists() && !file.mkdirs())
            myToast("创建目录失败！");

        readSettings();
        initSerial();
        initGateway();
        initData(Module.room);
        initDevicePager();
        initScenePager();
        initHomeDevice();
        initButton();
        initLocation();
        initVoice();
        initMusic();
        initWeather();
        NRSign.init(getApplication());
        initScreenSaver();
        if (cmdType == CommandType.idle)
            nextDeviceStatus();
        otherHandler.sendEmptyMessage(HANDLER_CHECK_VERSION);
        otherHandler.sendEmptyMessageDelayed(HANDLER_CHANGE_HINTS, 15000);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onDestroy() {
        otherHandler.removeCallbacksAndMessages(null);
        commHandler.removeCallbacksAndMessages(null);
        otherHandler = null;
        commHandler = null;
        if (db.isOpen())
            db.close();
        myHelper.close();
        unregisterReceiver(volumeReceiver);
        unregisterReceiver(rssiBroadcast);
        mSerialPortManager.closeSerialPort();
        super.onDestroy();
    }
}