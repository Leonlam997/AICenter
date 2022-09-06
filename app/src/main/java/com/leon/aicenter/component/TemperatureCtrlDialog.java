package com.leon.aicenter.component;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.leon.aicenter.MainActivity;
import com.leon.aicenter.R;
import com.leon.aicenter.bean.DeviceBean;
import com.leon.aicenter.util.ConstantUtils;

import java.util.Locale;

public class TemperatureCtrlDialog extends Dialog {
    private DeviceBean deviceBean;
    private TextView tvTemperature;
    private final Handler mainHandler;
    private int status;
    private boolean isTask;
    private boolean enableDisplayHome;
    private boolean displayHome;
    private TextView tvDisplayHome;
    private TextView tvTitle;
    private CheckBox cbTemperature;
    private CheckBox cbFan;
    private CheckBox cbMode;

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            handler.removeMessages(1);
            if (MainActivity.working) {
                handler.sendEmptyMessageDelayed(1, 500);
            } else if (msg.what == 1 && mainHandler != null) {
                Message message = mainHandler.obtainMessage(MainActivity.HANDLER_SWITCH_PRESS);
                message.obj = deviceBean;
                message.arg1 = MainActivity.CommandType.device.ordinal();
                message.arg2 = status;
                mainHandler.sendMessage(message);
            }
            return false;
        }
    });

    public TemperatureCtrlDialog(@NonNull Context context, DeviceBean deviceBean, Handler mainHandler) {
        super(context, R.style.Dialog_style);
        this.deviceBean = deviceBean;
        this.mainHandler = mainHandler;
    }

    public void setEnableDisplayHome(boolean enableDisplayHome) {
        this.enableDisplayHome = enableDisplayHome;
        if (enableDisplayHome && tvDisplayHome != null)
            initDisplayHome();
    }

    public boolean isNotDisplayHome() {
        return !displayHome;
    }

    private void initDisplayHome() {
        displayHome = true;
        tvDisplayHome.setVisibility(View.VISIBLE);
        tvDisplayHome.setOnClickListener(view -> {
            displayHome = !displayHome;
            tvDisplayHome.setCompoundDrawablesRelativeWithIntrinsicBounds(displayHome ? R.mipmap.ic_switch_on : R.mipmap.ic_switch_off, 0, 0, 0);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_temperature_control_dialog);
        if (getWindow() != null) {
            final WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = 700;
            params.height = 500;
            getWindow().setAttributes(params);
        }
        Log.d("MY_LOG", "Status:" + deviceBean.getStatus());
        status = deviceBean.getStatus() & ConstantUtils.MASK_STATUS_CLEAR_INDOOR_TEMPERATURE;
        tvTitle = findViewById(R.id.tv_room_temperature);
        cbTemperature = findViewById(R.id.cb_temperature);
        cbFan = findViewById(R.id.cb_fan);
        cbMode = findViewById(R.id.cb_mode);
        setTask(isTask);
        tvTemperature = findViewById(R.id.tv_temperature);
        float t1 = ((status >> 6) * 5 + 160) / 10f;
        tvTemperature.setText(String.format(Locale.CHINA, "%.1f%s", t1, getContext().getString(R.string.text_temperature_degree)));
        findViewById(R.id.btn_decrease).setEnabled(t1 > 16);
        findViewById(R.id.btn_increase).setEnabled(t1 < 32);

        int fan = (deviceBean.getStatus() & ConstantUtils.MASK_STATUS_FAN) >> 4;
        TextView[] tvFans = new TextView[]{findViewById(R.id.tv_fan_low),
                findViewById(R.id.tv_fan_mid),
                findViewById(R.id.tv_fan_high),
                findViewById(R.id.tv_fan_auto)};
        for (int i = 0; i < tvFans.length; i++) {
            tvFans[i].setBackground(fan == i ? ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.temperature_fan_control_selected_bg, getContext().getTheme()) : null);
            int finalI = i;
            tvFans[i].setOnClickListener(v -> {
                for (int j = 0; j < tvFans.length; j++)
                    tvFans[j].setBackground(j == finalI ? ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.temperature_fan_control_selected_bg, getContext().getTheme()) : null);
                status = (status & ~ConstantUtils.MASK_STATUS_FAN) + (finalI << 4);
                sendMessage();
            });
        }
        int mode = (deviceBean.getStatus() & ConstantUtils.MASK_STATUS_MODE) >> 2;
        TextView[] tvMode = new TextView[]{findViewById(R.id.tv_cool),
                findViewById(R.id.tv_fan),
                findViewById(R.id.tv_hot)};
        int[][] drawable = new int[][]{
                {R.drawable.button_temperature_cool_style, R.drawable.button_temperature_fan_style, R.drawable.button_temperature_hot_style},
                {R.drawable.button_temperature_cool_off_style, R.drawable.button_temperature_fan_off_style, R.drawable.button_temperature_hot_off_style}
        };
        for (int i = 0; i < tvMode.length; i++) {
            tvMode[i].setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                    ResourcesCompat.getDrawable(getContext().getResources(), drawable[mode == i ? 0 : 1][i], getContext().getTheme()), null, null);
            int finalI = i;
            tvMode[i].setOnClickListener(v -> {
                for (int j = 0; j < tvMode.length; j++)
                    tvMode[j].setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                            ResourcesCompat.getDrawable(getContext().getResources(), drawable[j == finalI ? 0 : 1][j], getContext().getTheme()), null, null);
                status = (status & ~ConstantUtils.MASK_STATUS_MODE) + (finalI << 2);
                sendMessage();
            });
        }
        TextView tvSwitch = findViewById(R.id.tv_switch);
        tvSwitch.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                ResourcesCompat.getDrawable(getContext().getResources(), (deviceBean.getStatus() & 0x01) == 1 ? R.drawable.button_temperature_switch_style : R.drawable.button_temperature_switch_off_style, getContext().getTheme()), null, null);
        tvSwitch.setText((deviceBean.getStatus() & 0x01) == 1 ? R.string.text_temperature_on : R.string.text_temperature_off);
        tvSwitch.setOnClickListener(v -> {
            tvSwitch.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                    ResourcesCompat.getDrawable(getContext().getResources(), tvSwitch.getText().toString().equals(getContext().getString(R.string.text_temperature_off)) ? R.drawable.button_temperature_switch_style : R.drawable.button_temperature_switch_off_style, getContext().getTheme()), null, null);
            tvSwitch.setText(tvSwitch.getText().toString().equals(getContext().getString(R.string.text_temperature_off)) ? R.string.text_temperature_on : R.string.text_temperature_off);
            status = (status & ~ConstantUtils.MASK_STATUS_SWITCH) + (tvSwitch.getText().toString().equals(getContext().getString(R.string.text_temperature_off)) ? 0 : 1);
            sendMessage();
        });
        findViewById(R.id.btn_increase).setOnClickListener(v -> {
            try {
                float t = Float.parseFloat(tvTemperature.getText().toString().substring(0, tvTemperature.getText().length() - 1));
                if (t < 32)
                    t += 0.5;
                setTemperature(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.btn_decrease).setOnClickListener(v -> {
            try {
                float t = Float.parseFloat(tvTemperature.getText().toString().substring(0, tvTemperature.getText().length() - 1));
                if (t > 16)
                    t -= 0.5;
                setTemperature(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());
        tvDisplayHome = findViewById(R.id.tv_display_home);
        tvDisplayHome.setVisibility(View.INVISIBLE);
        if (enableDisplayHome)
            initDisplayHome();
    }

    public int getStatus() {
        if (isTask) {
            int temp = status & ConstantUtils.MASK_STATUS_CLEAR_INDOOR_TEMPERATURE;
            if (cbTemperature.isChecked())
                temp |= ConstantUtils.MASK_SET_TEMPERATURE;
            else
                temp &= ~ConstantUtils.MASK_STATUS_TEMPERATURE;
            if (cbFan.isChecked())
                temp |= ConstantUtils.MASK_SET_FAN;
            else
                temp &= ~ConstantUtils.MASK_STATUS_FAN;
            if (cbMode.isChecked())
                temp |= ConstantUtils.MASK_SET_MODE;
            else
                temp &= ~ConstantUtils.MASK_STATUS_MODE;
            return temp;
        }
        return status;
    }

    public void setTask(boolean task) {
        isTask = task;
        if (tvTitle != null) {
            if (isTask) {
                tvTitle.setText(R.string.text_task_temperature);
                cbTemperature.setVisibility(View.VISIBLE);
                cbFan.setVisibility(View.VISIBLE);
                cbMode.setVisibility(View.VISIBLE);
                cbTemperature.setChecked((deviceBean.getStatus() & ConstantUtils.MASK_SET_TEMPERATURE) != 0);
                cbFan.setChecked((deviceBean.getStatus() & ConstantUtils.MASK_SET_FAN) != 0);
                cbMode.setChecked((deviceBean.getStatus() & ConstantUtils.MASK_SET_MODE) != 0);
            } else {
                tvTitle.setText(String.format(Locale.CHINA, "%s%.1f%s", getContext().getString(R.string.text_room_temperature), (deviceBean.getStatus() >> 12) / 2f, getContext().getString(R.string.text_temperature_degree)));
                cbTemperature.setVisibility(View.INVISIBLE);
                cbFan.setVisibility(View.INVISIBLE);
                cbMode.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setDeviceBean(DeviceBean deviceBean) {
        this.deviceBean = deviceBean;
    }

    private void sendMessage() {
        Log.d("MY_LOG", "new status:" + status);
        if (!isTask) {
            handler.removeMessages(1);
            handler.sendEmptyMessageDelayed(1, 1000);
        }
    }

    private void setTemperature(float t) {
        tvTemperature.setText(String.format(Locale.CHINA, "%.1f%s", t, getContext().getString(R.string.text_temperature_degree)));
        status = ((int) (((t * 10) - 160) / 5) << 6) + (status & ~ConstantUtils.MASK_STATUS_TEMPERATURE);
        findViewById(R.id.btn_decrease).setEnabled(t > 16);
        findViewById(R.id.btn_increase).setEnabled(t < 32);
        sendMessage();
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
