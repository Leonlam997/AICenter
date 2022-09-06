package com.leon.aicenter.component;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.leon.aicenter.R;
import com.leon.aicenter.adapter.SelectRoomListAdapter;
import com.leon.aicenter.bean.RoomBean;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SelectRoomDialog extends Dialog {
    private String text;
    private final List<RoomBean> room;
    private final EditText editText;
    private final SelectRoomListAdapter adapter;

    public String getText() {
        return text;
    }

    public int getSelectedRoom() {
        int i = 0;
        for (RoomBean bean : room)
            if (bean.isSelected())
                return i;
            else
                i++;
        return -1;
    }

    public SelectRoomDialog(@NonNull Context context, List<RoomBean> room) {
        super(context, R.style.Dialog_style);
        setContentView(R.layout.layout_select_room);
        if (getWindow() != null) {
            final WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = 500;
            params.height = 500;
            getWindow().setAttributes(params);
        }
        editText = findViewById(R.id.et_input);
        this.room = new ArrayList<>(room);
        ListView listView = findViewById(R.id.lv_room);
        adapter = new SelectRoomListAdapter(getContext(), room);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < room.size()) {
                for (RoomBean bean : room)
                    bean.setSelected(false);
                room.get(position).setSelected(true);
                adapter.updateList(room);
                editText.clearFocus();
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean changed = false;
                for (RoomBean bean : room)
                    if (editText.getText().toString().equals(bean.getName())) {
                        bean.setSelected(true);
                        changed = true;
                    } else if (bean.isSelected()) {
                        changed = true;
                        bean.setSelected(false);
                    }
                if (changed)
                    adapter.updateList(room);
            }
        });
        findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            ((InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
            dismiss();
        });
        findViewById(R.id.btn_close).setOnClickListener(v -> {
            text = null;
            ((InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
            dismiss();
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
