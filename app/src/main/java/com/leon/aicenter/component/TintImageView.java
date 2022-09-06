package com.leon.aicenter.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.leon.aicenter.R;

public class TintImageView extends AppCompatImageView {
    private int tintColor = Color.argb(20, 255, 255, 255);

    public TintImageView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public TintImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TintImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TintImageView);
            if (typedArray != null) {
                tintColor = typedArray.getColor(R.styleable.TintImageView_tintColor, Color.argb(20, 255, 255, 255));
                typedArray.recycle();
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.setImageTintList(ColorStateList.valueOf(tintColor));
                this.setImageTintMode(getImageTintMode() == null ? PorterDuff.Mode.ADD : getImageTintMode());
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                this.setImageTintList(null);
                break;
        }
        return super.onTouchEvent(event);
    }
}
