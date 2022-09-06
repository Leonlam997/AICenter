package com.leon.aicenter.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.leon.aicenter.MainActivity;
import com.leon.aicenter.R;
import com.leon.aicenter.adapter.DeviceAdapter;
import com.leon.aicenter.adapter.SceneAdapter;

/***
 * 自定义拖拽GridView
 *
 * @author Leon
 *
 */
public class DragGridView extends GridView {
    private WindowManager windowManager;// windows窗口控制类
    private WindowManager.LayoutParams windowParams;// 用于控制拖拽项的显示的参数
    private ImageView dragImageView;// 被拖拽的项(item)，其实就是一个ImageView
    private int dragPosition;// 手指点击准备拖动的时候,当前拖动项在列表中的位置.
    private int dragPointX;// 在当前数据项中的位置
    private int dragPointY;// 在当前数据项中的位置
    private int dragOffsetX;// 当前视图和屏幕的距离(这里只使用了x方向上)
    private int dragOffsetY;// 当前视图和屏幕的距离(这里只使用了y方向上)
    private int upScrollBounce;// 拖动的时候，开始向上滚动的边界
    private int downScrollBounce;// 拖动的时候，开始向下滚动的边界
    private int temChangId;// 临时交换id
    private boolean isDevice;
    private final static int step = 3;// GridView 滑动步伐.
    private long timeCount;

    public DragGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && MainActivity.modifying) {
            if (dragImageView != null) {
                stopDrag();// 删除映像
                onDrop((int) ev.getX(), (int) ev.getY());// 松开
            }
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            temChangId = dragPosition = pointToPosition(x, y);
            if (dragPosition == AdapterView.INVALID_POSITION) {
                return super.onInterceptTouchEvent(ev);
            }
            ViewGroup itemView = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());
            dragPointX = x - itemView.getLeft();
            dragPointY = y - itemView.getTop();
            dragOffsetX = (int) (ev.getRawX() - x);
            dragOffsetY = (int) (ev.getRawY() - y);
            View dragger = itemView.findViewById(R.id.iv_drag);
            if (dragger != null && dragPointX > dragger.getLeft() - 20
                    && dragPointX < dragger.getRight() + 20
                    && dragPointY > dragger.getTop() - 20
                    && dragPointY < dragger.getBottom() + 20) {
                upScrollBounce = getHeight() / 3;
                downScrollBounce = getHeight() * 2 / 3;
                itemView.setDrawingCacheEnabled(true);
                Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
                startDrag(bm, x, y);// 初始话映像
                itemView.destroyDrawingCache();
                onHide(x, y);// 隐藏该项
                timeCount = System.currentTimeMillis();
            }
        } else if ((ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_UP) && dragImageView != null) {
            stopDrag();// 删除映像
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            onDrop(x, y);// 松开
            ViewGroup itemView = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());
            x -= itemView.getLeft();
            y -= itemView.getTop();
            if (ev.getAction() == MotionEvent.ACTION_UP && System.currentTimeMillis() - timeCount < 300 && Math.abs(dragPointX - x) < 5 && Math.abs(dragPointY - y) < 5) {
                performClick();
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (dragImageView != null && dragPosition != INVALID_POSITION && MainActivity.modifying) {
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_UP:
                    stopDrag();// 删除映像
                    onDrop(x, y);// 松开
                    ViewGroup itemView = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());
                    x -= itemView.getLeft();
                    y -= itemView.getTop();
                    if (System.currentTimeMillis() - timeCount < 300 && Math.abs(dragPointX - x) < 5 && Math.abs(dragPointY - y) < 5) {
                        performClick();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    onDrag(x, y);// 拖拽
                    break;
                default:
                    break;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean performClick() {
        if (dragPosition != INVALID_POSITION) {
            if (isDevice)
                ((DeviceAdapter) getAdapter()).clickItem(dragPosition);
            else
                ((SceneAdapter) getAdapter()).clickItem(dragPosition);
        }
        return super.performClick();
    }

    public void setDevice(boolean device) {
        this.isDevice = device;
    }

    /**
     * 准备拖动，初始化拖动项的图像
     */
    public void startDrag(Bitmap bm, int x, int y) {
        windowParams = new WindowManager.LayoutParams();
        windowParams.gravity = Gravity.TOP | Gravity.START;
        windowParams.x = x - dragPointX + dragOffsetX;
        windowParams.y = y - dragPointY + dragOffsetY;
        windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.windowAnimations = 0;
        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(bm);
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(imageView, windowParams);
        dragImageView = imageView;
    }

    /***
     * 拖动时时change
     */
    private void onChange(int x, int y) {
// 数据交换
        if (dragPosition < getAdapter().getCount()) {
// 不相等的情况下要进行换位,相等的情况下说明正在移动
            if (dragPosition != temChangId) {
                if (isDevice)
                    ((DeviceAdapter) getAdapter()).update(temChangId, dragPosition);
                else
                    ((SceneAdapter) getAdapter()).update(temChangId, dragPosition);// 进行换位
                temChangId = dragPosition;// 将点击最初所在位置position付给临时的，用于判断是否换位.
            }
        }
// 为了避免滑动到分割线的时候，返回-1的问题
        int tempPosition = pointToPosition(x, y);
        if (tempPosition != INVALID_POSITION) {
            dragPosition = tempPosition;
        }
    }

    /***
     * 拖动执行，在Move方法中执行
     */
    public void onDrag(int x, int y) {
// 移动
        if (dragImageView != null) {
            windowParams.alpha = 0.8f;
            windowParams.x = x - dragPointX + dragOffsetX;
            windowParams.y = y - dragPointY + dragOffsetY;
            windowManager.updateViewLayout(dragImageView, windowParams);
        }
        onChange(x, y);// 时时交换
        doScroller(y);
    }

    /***
     * ListView的移动.
     * 要明白移动原理：当我移动到下端的时候，ListView向上滑动，当我移动到上端的时候，ListView要向下滑动。正好和实际的相反.
     *
     */
    public void doScroller(int y) {
// ListView需要下滑
        // 当前步伐.
        int current_Step;
        if (y < upScrollBounce) {
            current_Step = step + (upScrollBounce - y);// 时时步伐
        }// ListView需要上滑
        else if (y > downScrollBounce) {
            current_Step = -(step + (y - downScrollBounce));// 时时步伐
        } else {
            current_Step = 0;
        }
// 获取你拖拽滑动到位置及显示item相应的view上（注：可显示部分）（position）
        View view = getChildAt(dragPosition - getFirstVisiblePosition());
// 真正滚动的方法setSelectionFromTop()
        if (view != null)
            smoothScrollToPositionFromTop(dragPosition, view.getTop() + current_Step);
    }

    /***
     * 隐藏该选项
     */
    private void onHide(int x, int y) {
// 获取适配器
// 为了避免滑动到分割线的时候，返回-1的问题
        int tempPosition = pointToPosition(x, y);
        if (tempPosition != INVALID_POSITION) {
            dragPosition = tempPosition;
        }
        if (isDevice)
            ((DeviceAdapter) getAdapter()).startDrag(dragPosition);
        else
            ((SceneAdapter) getAdapter()).startDrag(dragPosition);
    }

    /**
     * 停止拖动，删除影像
     */
    public void stopDrag() {
        if (dragImageView != null) {
            windowManager.removeView(dragImageView);
            dragImageView = null;
        }
    }

    /***
     * 拖动放下的时候
     */
    public void onDrop(int x, int y) {
        if (isDevice)
            ((DeviceAdapter) getAdapter()).stopDrag();
        else
            ((SceneAdapter) getAdapter()).stopDrag();
    }
}