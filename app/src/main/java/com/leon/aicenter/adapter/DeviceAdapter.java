package com.leon.aicenter.adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leon.aicenter.MainActivity;
import com.leon.aicenter.R;
import com.leon.aicenter.bean.DeviceBean;
import com.leon.aicenter.component.InputTextDialog;
import com.leon.aicenter.component.TemperatureCtrlDialog;
import com.leon.aicenter.util.ConstantUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeviceAdapter extends BaseAdapter {
    private List<DeviceBean> deviceBeans;
    private final LayoutInflater inflater;
    private final Handler handler;
    private DeviceBean draggingBean;
    private TemperatureCtrlDialog dialog;
    private ImageView ivSelect;
    private int index;
    private boolean notDragging = true;

    public DeviceAdapter(Context context, List<DeviceBean> deviceBeans, Handler handler) {
        this.deviceBeans = new ArrayList<>(deviceBeans);
        inflater = LayoutInflater.from(context);
        this.handler = handler;
    }

    public List<DeviceBean> getDeviceBeans() {
        return deviceBeans;
    }

    public void updateList(List<DeviceBean> deviceBeans) {
        if (null != deviceBeans)
            this.deviceBeans = new ArrayList<>(deviceBeans);
        notifyDataSetChanged();
        if (dialog != null && dialog.isShowing() && deviceBeans != null && index < deviceBeans.size())
            dialog.setDeviceBean(deviceBeans.get(index));
    }

    public void deleteDevice(int id) {
        deviceBeans.removeIf(deviceBean -> deviceBean.getId() == id);
        notifyDataSetChanged();
    }

    public void startDrag(int start) {
        notDragging = false;
        handler.sendMessage(handler.obtainMessage(MainActivity.HANDLER_DRAG, ConstantUtils.TYPE_DEVICE, ConstantUtils.DRAG_START));
        draggingBean = deviceBeans.get(start);
        deviceBeans.remove(start);
        deviceBeans.add(start, null);
        notifyDataSetChanged();
    }

    public void update(int start, int down) {
        DeviceBean bean = deviceBeans.get(start);
        deviceBeans.remove(start);
        deviceBeans.add(down, bean);
        notifyDataSetChanged();
    }

    public void stopDrag() {
        notDragging = true;
        handler.sendMessage(handler.obtainMessage(MainActivity.HANDLER_DRAG, ConstantUtils.TYPE_DEVICE, ConstantUtils.DRAG_STOP));
        for (int i = 0; i < deviceBeans.size(); i++) {
            if (deviceBeans.get(i) == null) {
                deviceBeans.set(i, draggingBean);
                notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public int getCount() {
        return deviceBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_device, parent, false);
            viewHolder.name = convertView.findViewById(R.id.tv_device_name);
            viewHolder.room = convertView.findViewById(R.id.tv_room_name);
            viewHolder.background = convertView.findViewById(R.id.rl_my_dev);
            viewHolder.status = convertView.findViewById(R.id.iv_switch);
            viewHolder.connection = convertView.findViewById(R.id.iv_connection);
            viewHolder.logo = convertView.findViewById(R.id.iv_device_logo);
            viewHolder.delete = convertView.findViewById(R.id.iv_delete);
            viewHolder.select = convertView.findViewById(R.id.iv_selected);
            viewHolder.detail = convertView.findViewById(R.id.tv_detail);
            viewHolder.pause = convertView.findViewById(R.id.iv_pause);
            viewHolder.drag = convertView.findViewById(R.id.iv_drag);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        ivSelect = viewHolder.select;
        if (deviceBeans.get(position) == null) {
            viewHolder.background.setBackgroundResource(R.drawable.common_background);
            viewHolder.name.setVisibility(View.INVISIBLE);
            viewHolder.room.setVisibility(View.INVISIBLE);
            viewHolder.status.setVisibility(View.INVISIBLE);
            viewHolder.connection.setVisibility(View.INVISIBLE);
            viewHolder.logo.setVisibility(View.INVISIBLE);
            viewHolder.delete.setVisibility(View.INVISIBLE);
            viewHolder.select.setVisibility(View.INVISIBLE);
            viewHolder.detail.setVisibility(View.INVISIBLE);
            viewHolder.pause.setVisibility(View.INVISIBLE);
            viewHolder.drag.setVisibility(View.INVISIBLE);
        } else {
            //Log.d("MY_LOG", "bean:" + deviceBeans.get(position).getName() + ",select:" + deviceBeans.get(position).isSelected());
            viewHolder.logo.setVisibility(View.VISIBLE);
            viewHolder.name.setVisibility(View.VISIBLE);
            viewHolder.room.setVisibility(View.VISIBLE);
            viewHolder.background.setBackgroundResource(MainActivity.selectMode ? R.drawable.common_background : (deviceBeans.get(position).getStatusEnum() == ConstantUtils.Status.on || deviceBeans.get(position).getStatusEnum() == ConstantUtils.Status.onPause ? R.drawable.device_on_bg_style : R.drawable.common_bg_style));
            viewHolder.status.setVisibility(deviceBeans.get(position).getKind() == ConstantUtils.DeviceKind.panel ? View.INVISIBLE : View.VISIBLE);
            viewHolder.status.setImageResource(deviceBeans.get(position).getStatusEnum() == ConstantUtils.Status.on || deviceBeans.get(position).getStatusEnum() == ConstantUtils.Status.onPause ? R.mipmap.ic_on : R.mipmap.ic_off);
            viewHolder.connection.setVisibility(deviceBeans.get(position).getStatusEnum() == ConstantUtils.Status.offline ? View.VISIBLE : View.INVISIBLE);
            viewHolder.name.setText(deviceBeans.get(position).getName());
            viewHolder.room.setText(deviceBeans.get(position).getRoom());
            viewHolder.pause.setVisibility(View.INVISIBLE);
            viewHolder.detail.setVisibility(deviceBeans.get(position).getKind() == ConstantUtils.DeviceKind.temperature ? View.VISIBLE : View.INVISIBLE);
            viewHolder.delete.setVisibility(MainActivity.modifying ? View.VISIBLE : View.INVISIBLE);
            viewHolder.drag.setVisibility(MainActivity.modifying ? View.VISIBLE : View.INVISIBLE);
            viewHolder.delete.setOnClickListener(v -> {
                if (notDragging) {
                    handler.sendMessage(handler.obtainMessage(MainActivity.HANDLER_DELETE_DEVICE, deviceBeans.get(position).getId(), 0));

                }
            });
            viewHolder.select.setVisibility(MainActivity.selectMode ? (deviceBeans.get(position).isSelected() ? View.VISIBLE : View.INVISIBLE) : View.INVISIBLE);
            viewHolder.background.setOnClickListener(v -> clickItem(position));
            viewHolder.background.setOnLongClickListener(view -> {
                if (notDragging && !MainActivity.modifying && !MainActivity.selectMode && deviceBeans.get(position).getKind() == ConstantUtils.DeviceKind.temperature) {
                    index = position;
                    dialog = new TemperatureCtrlDialog(inflater.getContext(), deviceBeans.get(position), handler);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
                return false;
            });
            if (null != deviceBeans.get(position).getKind())
                switch (deviceBeans.get(position).getKind()) {
                    case curtain:
                        viewHolder.logo.setImageResource(R.mipmap.dev_curtain);
                        switch (deviceBeans.get(position).getStatusEnum()) {
                            case on:
                                viewHolder.status.setImageResource(R.mipmap.ic_curtain_on);
                                break;
                            case off:
                                viewHolder.status.setImageResource(R.mipmap.ic_curtain_off);
                                break;
                            case offPause:
                                viewHolder.status.setImageResource(R.mipmap.ic_curtain_off_pause);
                                break;
                            default:
                                viewHolder.status.setImageResource(R.mipmap.ic_curtain_on_pause);
                                break;
                        }
                        break;
                    case panel:
                        viewHolder.logo.setImageResource(R.mipmap.dev_panel);
                        break;
                    case temperature:
                        viewHolder.logo.setImageResource(R.mipmap.dev_air_conditioning);
                        viewHolder.detail.setText(String.format(Locale.CHINA, "%.1f%s", ((deviceBeans.get(position).getStatus() & ConstantUtils.MASK_STATUS_CLEAR_INDOOR_TEMPERATURE) >> 6) / 2f + 16, inflater.getContext().getString(R.string.text_temperature_degree)));
                        break;
                    default:
                        viewHolder.logo.setImageResource(R.mipmap.dev_lamp);
                        break;
                }
            else
                viewHolder.logo.setImageResource(R.mipmap.dev_lamp);
        }
        return convertView;
    }

    public void clickItem(int position) {
        if (notDragging) {
            if (MainActivity.modifying) {
                InputTextDialog inputTextDialog = new InputTextDialog(inflater.getContext());
                inputTextDialog.setHint(R.string.text_input_device_name);
                inputTextDialog.setCancelable(false);
                inputTextDialog.setCanceledOnTouchOutside(false);
                inputTextDialog.setOnDismissListener(dialog -> {
                    if (inputTextDialog.getText() != null && !inputTextDialog.getText().isEmpty()) {
                        deviceBeans.get(position).setName(inputTextDialog.getText());
                        handler.sendEmptyMessage(MainActivity.HANDLER_RENAME_DEVICE);
                    }
                });
                inputTextDialog.show();
            } else if (MainActivity.selectMode) {
                deviceBeans.get(position).setSelected(!deviceBeans.get(position).isSelected());
                handler.sendMessage(handler.obtainMessage(MainActivity.HANDLER_ITEM_SELECTED, deviceBeans.get(position)));
                ivSelect.setVisibility(deviceBeans.get(position).isSelected() ? View.VISIBLE : View.INVISIBLE);
            } else {
                handler.sendMessage(handler.obtainMessage(MainActivity.HANDLER_SWITCH_PRESS,
                        MainActivity.CommandType.device.ordinal(), deviceBeans.get(position).getKind() == ConstantUtils.DeviceKind.temperature ? ConstantUtils.TEMPERATURE_SWITCH_ON_OFF : 0, deviceBeans.get(position)));
            }
        }
    }

    private static class ViewHolder {
        TextView name;
        TextView room;
        TextView detail;
        RelativeLayout background;
        ImageView status;
        ImageView connection;
        ImageView logo;
        ImageView delete;
        ImageView select;
        ImageView pause;
        ImageView drag;
    }
}
