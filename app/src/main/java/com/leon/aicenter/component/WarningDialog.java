package com.leon.aicenter.component;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.leon.aicenter.R;

public class WarningDialog extends Dialog {
    private View.OnClickListener onPositiveClick;
    private View.OnClickListener onNegativeClick;
    private String message;
    private int messageRes;

    public WarningDialog(@NonNull Context context) {
        super(context, R.style.Dialog_style);
    }

    public void setOnPositiveClick(View.OnClickListener onPositiveClick) {
        this.onPositiveClick = onPositiveClick;
        if (null != findViewById(R.id.btn_confirm))
            findViewById(R.id.btn_confirm).setOnClickListener(onPositiveClick);
    }

    public void setOnNegativeClick(View.OnClickListener onNegativeClick) {
        this.onNegativeClick = onNegativeClick;
        if (null != findViewById(R.id.btn_cancel))
            findViewById(R.id.btn_cancel).setOnClickListener(onNegativeClick);
    }

    public void setMessage(String message) {
        this.message = message;
        if (null != findViewById(R.id.tv_message))
            ((TextView) findViewById(R.id.tv_message)).setText(message);
    }

    public void setMessage(@StringRes int message) {
        messageRes = message;
        if (null != findViewById(R.id.tv_message))
            ((TextView) findViewById(R.id.tv_message)).setText(message);
    }

    public void setProgress(int progress) {
        if (null != findViewById(R.id.progress_bar)) {
            findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
            ((ProgressBar) findViewById(R.id.progress_bar)).setProgress(progress);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_warning_dialog);
        if (getWindow() != null) {
            final WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = 280;
            params.height = 180;
            getWindow().setAttributes(params);
        }
        if (onPositiveClick != null)
            findViewById(R.id.btn_confirm).setOnClickListener(onPositiveClick);
        if (onNegativeClick != null)
            findViewById(R.id.btn_cancel).setOnClickListener(onNegativeClick);
        else
            findViewById(R.id.btn_cancel).setOnClickListener(v -> dismiss());
        if (message != null)
            ((TextView) findViewById(R.id.tv_message)).setText(message);
        if (messageRes != 0)
            ((TextView) findViewById(R.id.tv_message)).setText(messageRes);
        findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
}
