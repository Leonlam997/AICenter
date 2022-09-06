package com.leon.aicenter.component;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.leon.aicenter.R;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class InputTextDialog extends Dialog {
    private int hint = 0;
    private String text;
    private EditText editText;

    public InputTextDialog(@NonNull Context context) {
        super(context, R.style.Dialog_style);
    }

    public void setHint(@StringRes int hint) {
        this.hint = hint;
        if (null != editText)
            editText.setHint(hint);
    }

    public void setText(String text) {
        this.text = text;
        if (null != editText) {
            editText.setText(text);
            editText.setSelection(text.length());
        }
    }

    public String getText() {
        return text;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_input_text_dialog);
        editText = findViewById(R.id.et_input);
        if (hint != 0)
            editText.setHint(hint);
        if (null != text && !text.isEmpty()) {
            editText.setText(text);
            editText.setSelection(text.length());
        }
        if (getWindow() != null) {
            final WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = 600;
            params.height = 180;
            params.y = -120;
            getWindow().setAttributes(params);
        }
        findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            text = editText.getText().toString();
            ((InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
            dismiss();
        });
        findViewById(R.id.btn_close).setOnClickListener(v -> {
            text = null;
            ((InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
            dismiss();
        });
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
