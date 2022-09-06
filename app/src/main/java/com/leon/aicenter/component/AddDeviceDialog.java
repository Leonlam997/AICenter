package com.leon.aicenter.component;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.leon.aicenter.MainActivity;
import com.leon.aicenter.R;
import com.leon.aicenter.adapter.DevicePropsAdapter;
import com.leon.aicenter.bean.DevicePropsBean;
import com.leon.aicenter.bean.RoomBean;
import com.leon.aicenter.util.ConstantUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class AddDeviceDialog extends Dialog {
    public static final int STEP_INDICATION = 1;
    public static final int STEP_DETECTED = 2;
    public static final int STEP_EXIT = 3;
    public static final int STEP_PROGRESS = 4;
    private final Context context;
    private List<RoomBean> room;
    private String deviceName;
    private String picture;
    private int defaultRoom;
    private int deviceCount;
    private ConstantUtils.DeviceKind kind;
    private long startTime;
    private boolean exitOnly;
    private Handler mainHandler;
    private List<DevicePropsBean> propsBeans;
    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case STEP_INDICATION:
                    handler.removeMessages(STEP_PROGRESS);
                    ((TextView) findViewById(R.id.ll_step1).findViewById(R.id.tv_progress)).setText("");
                    findViewById(R.id.ll_step1).setVisibility(View.INVISIBLE);
                    findViewById(R.id.ll_step2).setVisibility(View.VISIBLE);
                    findViewById(R.id.ll_step3).setVisibility(View.INVISIBLE);
                    findViewById(R.id.ll_step4).setVisibility(View.INVISIBLE);
                    break;
                case STEP_DETECTED:
                    findViewById(R.id.ll_step1).setVisibility(View.INVISIBLE);
                    findViewById(R.id.ll_step2).setVisibility(View.INVISIBLE);
                    findViewById(R.id.ll_step3).setVisibility(View.VISIBLE);
                    findViewById(R.id.ll_step4).setVisibility(View.INVISIBLE);
                    if (null != deviceName)
                        ((TextView) findViewById(R.id.ll_step3).findViewById(R.id.tv_detected_device)).setText(deviceName);
                    if (picture != null && Pattern.matches(ConstantUtils.NUMBER_PATTERN, picture))
                        ((ImageView) findViewById(R.id.ll_step3).findViewById(R.id.iv_example)).setImageResource(ConstantUtils.DEVICE_DEMO_PICTURE[Integer.parseInt(picture)]);
                    break;
                case STEP_EXIT:
                    startTime = System.currentTimeMillis();
                    handler.sendEmptyMessageDelayed(STEP_PROGRESS, 200);
                    Message message = mainHandler.obtainMessage(MainActivity.HANDLER_ADD_FINISHED);
                    message.arg1 = exitOnly ? ConstantUtils.EXIT_SEARCH : ConstantUtils.COMPLETE;
                    mainHandler.sendMessage(message);
                    findViewById(R.id.ll_step1).setVisibility(View.VISIBLE);
                    findViewById(R.id.ll_step2).setVisibility(View.INVISIBLE);
                    findViewById(R.id.ll_step3).setVisibility(View.INVISIBLE);
                    findViewById(R.id.ll_step4).setVisibility(View.INVISIBLE);
                    ((TextView) findViewById(R.id.ll_step1).findViewById(R.id.tv_title)).setText(exitOnly ? R.string.text_exiting : R.string.text_completing);
                    break;
                case STEP_PROGRESS:
                    handler.removeMessages(STEP_PROGRESS);
                    float progress = 95f * (System.currentTimeMillis() - startTime)
                            / (((TextView) findViewById(R.id.ll_step1).findViewById(R.id.tv_title)).getText().equals(getContext().getString(R.string.text_initial)) || exitOnly ? ConstantUtils.GATEWAY_TIME : ConstantUtils.GATEWAY_TIME + deviceCount * 1000);
                    if (progress >= 95)
                        ((TextView) findViewById(R.id.ll_step1).findViewById(R.id.tv_progress)).setText("95%");
                    else {
                        ((TextView) findViewById(R.id.ll_step1).findViewById(R.id.tv_progress)).setText(String.format(Locale.CHINA, "%d%%", (int) progress));
                        handler.sendEmptyMessageDelayed(STEP_PROGRESS, 100);
                    }
                    break;
            }
            return false;
        }
    });

    public void setMainHandler(Handler mainHandler) {
        this.mainHandler = mainHandler;
    }

    public List<DevicePropsBean> getPropsBeans() {
        return propsBeans;
    }

    public void setDefaultRoom(int defaultRoom) {
        this.defaultRoom = defaultRoom;
    }

    public void setButtonAmount(int buttonAmount) {
        DevicePropsBean bean = new DevicePropsBean();
        switch (buttonAmount) {
            case 1:
                bean.setWhich(getContext().getString(R.string.text_button));
                if (defaultRoom >= 0 && room != null && room.size() > defaultRoom)
                    bean.setRoom(room.get(defaultRoom).getName());
                bean.setType(ConstantUtils.TYPE_DEVICE);
                propsBeans.add(bean);
                break;
            case 2:
                bean.setWhich(getContext().getString(R.string.text_button_left));
                if (defaultRoom >= 0 && room != null && room.size() > defaultRoom)
                    bean.setRoom(room.get(defaultRoom).getName());
                bean.setType(ConstantUtils.TYPE_DEVICE);
                propsBeans.add(bean);
                bean = new DevicePropsBean();
                bean.setWhich(getContext().getString(R.string.text_button_right));
                if (defaultRoom >= 0 && room != null && room.size() > defaultRoom)
                    bean.setRoom(room.get(defaultRoom).getName());
                bean.setType(ConstantUtils.TYPE_DEVICE);
                propsBeans.add(bean);
                break;
            case 3:
                if (kind == ConstantUtils.DeviceKind.curtain) {
                    bean.setWhich(getContext().getString(R.string.text_button_control));
                    bean.setName(getContext().getString(R.string.text_curtain));
                } else {
                    bean.setWhich(getContext().getString(R.string.text_button_left));
                    if (defaultRoom >= 0 && room != null && room.size() > defaultRoom)
                        bean.setRoom(room.get(defaultRoom).getName());
                    bean.setType(ConstantUtils.TYPE_DEVICE);
                    propsBeans.add(bean);
                    bean = new DevicePropsBean();
                    bean.setWhich(getContext().getString(R.string.text_button_middle));
                    if (defaultRoom >= 0 && room != null && room.size() > defaultRoom)
                        bean.setRoom(room.get(defaultRoom).getName());
                    bean.setType(ConstantUtils.TYPE_DEVICE);
                    propsBeans.add(bean);
                    bean = new DevicePropsBean();
                    bean.setWhich(getContext().getString(R.string.text_button_right));
                }
                if (defaultRoom >= 0 && room != null && room.size() > defaultRoom)
                    bean.setRoom(room.get(defaultRoom).getName());
                bean.setType(ConstantUtils.TYPE_DEVICE);
                propsBeans.add(bean);
                break;
            default:
                for (int i = 1; i <= buttonAmount; i++) {
                    bean = new DevicePropsBean();
                    bean.setWhich(getContext().getString(R.string.text_button) + i);
                    if (defaultRoom >= 0 && room != null && room.size() > defaultRoom)
                        bean.setRoom(room.get(defaultRoom).getName());
                    bean.setType(ConstantUtils.TYPE_DEVICE);
                    propsBeans.add(bean);
                }
                break;
        }
    }

    public void setSceneAmount(int sceneAmount) {
        DevicePropsBean bean = new DevicePropsBean();
        switch (sceneAmount) {
            case 1:
                bean.setWhich(getContext().getString(R.string.text_scene));
                if (defaultRoom >= 0 && room != null && room.size() > defaultRoom)
                    bean.setRoom(room.get(defaultRoom).getName());
                bean.setType(ConstantUtils.TYPE_SCENE);
                propsBeans.add(bean);
                break;
            case 2:
                bean.setWhich(getContext().getString(R.string.text_scene_left));
                if (defaultRoom >= 0 && room != null && room.size() > defaultRoom)
                    bean.setRoom(room.get(defaultRoom).getName());
                bean.setType(ConstantUtils.TYPE_SCENE);
                propsBeans.add(bean);
                bean = new DevicePropsBean();
                bean.setWhich(getContext().getString(R.string.text_scene_right));
                if (defaultRoom >= 0 && room != null && room.size() > defaultRoom)
                    bean.setRoom(room.get(defaultRoom).getName());
                bean.setType(ConstantUtils.TYPE_SCENE);
                propsBeans.add(bean);
                break;
            case 3:
                bean.setWhich(getContext().getString(R.string.text_scene_left));
                if (defaultRoom >= 0 && room != null && room.size() > defaultRoom)
                    bean.setRoom(room.get(defaultRoom).getName());
                bean.setType(ConstantUtils.TYPE_SCENE);
                propsBeans.add(bean);
                bean = new DevicePropsBean();
                bean.setWhich(getContext().getString(R.string.text_scene_middle));
                if (defaultRoom >= 0 && room != null && room.size() > defaultRoom)
                    bean.setRoom(room.get(defaultRoom).getName());
                bean.setType(ConstantUtils.TYPE_SCENE);
                propsBeans.add(bean);
                bean = new DevicePropsBean();
                bean.setWhich(getContext().getString(R.string.text_scene_right));
                if (defaultRoom >= 0 && room != null && room.size() > defaultRoom)
                    bean.setRoom(room.get(defaultRoom).getName());
                bean.setType(ConstantUtils.TYPE_SCENE);
                propsBeans.add(bean);
                break;
            default:
                for (int i = 1; i <= sceneAmount; i++) {
                    bean = new DevicePropsBean();
                    bean.setWhich(getContext().getString(R.string.text_scene) + i);
                    if (defaultRoom >= 0 && room != null && room.size() > defaultRoom)
                        bean.setRoom(room.get(defaultRoom).getName());
                    bean.setType(ConstantUtils.TYPE_SCENE);
                    propsBeans.add(bean);
                }
                break;
        }
    }

    public void setKind(ConstantUtils.DeviceKind kind) {
        propsBeans = new ArrayList<>();
        DevicePropsBean bean = new DevicePropsBean();
        switch (kind) {
            case panel:
                bean.setWhich(getContext().getString(R.string.text_panel));
                if (defaultRoom >= 0 && room != null && room.size() > defaultRoom)
                    bean.setRoom(room.get(defaultRoom).getName());
                bean.setType(ConstantUtils.TYPE_DEVICE);
                bean.setName(getContext().getString(R.string.text_panel));
                propsBeans.add(bean);
                break;
            case temperature:
                bean.setWhich(getContext().getString(R.string.text_temperature));
                if (defaultRoom >= 0 && room != null && room.size() > defaultRoom)
                    bean.setRoom(room.get(defaultRoom).getName());
                bean.setType(ConstantUtils.TYPE_DEVICE);
                bean.setName(getContext().getString(R.string.text_temperature));
                propsBeans.add(bean);
                break;
        }
        this.kind = kind;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setRoom(List<RoomBean> room) {
        this.room = new ArrayList<>(room);
    }

    public Handler getHandler() {
        return handler;
    }

    public AddDeviceDialog(@NonNull Context context) {
        super(context, R.style.Dialog_style);
        this.context = context;
        startTime = System.currentTimeMillis();
        room = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_device_dialog);
        if (getWindow() != null) {
            final WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = 700;
            params.height = 500;
            getWindow().setAttributes(params);
        }
        handler.sendEmptyMessageDelayed(STEP_PROGRESS, 100);
        exitOnly = true;
        deviceCount = 0;

        findViewById(R.id.btn_close).setOnClickListener(v -> {
            if (findViewById(R.id.ll_step1).getVisibility() == View.INVISIBLE)
                handler.sendEmptyMessage(STEP_EXIT);
            else if (ConstantUtils.GATEWAY_TIME < System.currentTimeMillis() - startTime)
                dismiss();
        });
        findViewById(R.id.ll_step3).findViewById(R.id.btn_finish).setOnClickListener(v -> {
            findViewById(R.id.ll_step1).setVisibility(View.INVISIBLE);
            findViewById(R.id.ll_step2).setVisibility(View.INVISIBLE);
            findViewById(R.id.ll_step3).setVisibility(View.INVISIBLE);
            findViewById(R.id.ll_step4).setVisibility(View.VISIBLE);
            DevicePropsAdapter adapter = new DevicePropsAdapter(getContext(), propsBeans, room);
            ((ListView) findViewById(R.id.ll_step4).findViewById(R.id.lv_switches)).setAdapter(adapter);
            findViewById(R.id.ll_step4).findViewById(R.id.btn_finish).setOnClickListener(v1 -> {
                for (DevicePropsBean bean : adapter.getDevicePropsBeans())
                    if (bean.getName() == null || bean.getName().isEmpty()) {
                        ((MainActivity) context).myToast(getContext().getString(R.string.text_field_input_error));
                        return;
                    }
                propsBeans = adapter.getDevicePropsBeans();
                deviceCount++;
                mainHandler.sendEmptyMessage(MainActivity.HANDLER_ADD_DEVICES);
                exitOnly = false;
                handler.sendEmptyMessage(STEP_EXIT);
            });
            findViewById(R.id.ll_step4).findViewById(R.id.btn_continue).setOnClickListener(v1 -> {
                for (DevicePropsBean bean : adapter.getDevicePropsBeans())
                    if (bean.getName() == null || bean.getName().isEmpty()) {
                        ((MainActivity) context).myToast(getContext().getString(R.string.text_field_input_error));
                        return;
                    }
                propsBeans = adapter.getDevicePropsBeans();
                deviceCount++;
                Message message = mainHandler.obtainMessage(MainActivity.HANDLER_ADD_DEVICES);
                message.arg1 = ConstantUtils.CONTINUE;
                mainHandler.sendMessage(message);
                handler.sendEmptyMessage(STEP_INDICATION);
                exitOnly = false;
            });
        });
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
}
