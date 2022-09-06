package com.leon.aicenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leon.aicenter.R;
import com.leon.aicenter.bean.DevicePropsBean;
import com.leon.aicenter.bean.RoomBean;
import com.leon.aicenter.component.InputTextDialog;
import com.leon.aicenter.component.SelectRoomDialog;
import com.leon.aicenter.util.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

public class DevicePropsAdapter extends BaseAdapter {
    private final List<DevicePropsBean> devicePropsBeans;
    private final LayoutInflater inflater;
    private final List<RoomBean> roomBeans;

    public DevicePropsAdapter(Context context, List<DevicePropsBean> devicePropsBeans, List<RoomBean> roomBeans) {
        inflater = LayoutInflater.from(context);
        this.devicePropsBeans = devicePropsBeans;
        this.roomBeans = new ArrayList<>(roomBeans);
    }

    public List<DevicePropsBean> getDevicePropsBeans() {
        return devicePropsBeans;
    }

    @Override
    public int getCount() {
        return devicePropsBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return devicePropsBeans.get(position);
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
            convertView = inflater.inflate(R.layout.layout_device_props, parent, false);
            viewHolder.which = convertView.findViewById(R.id.tv_which);
            viewHolder.switchName = convertView.findViewById(R.id.tv_switch_name);
            viewHolder.roomName = convertView.findViewById(R.id.tv_room_name);
            viewHolder.deviceName = convertView.findViewById(R.id.rl_device_name);
            viewHolder.room = convertView.findViewById(R.id.rl_room);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.which.setText(devicePropsBeans.get(position).getWhich());
        if (devicePropsBeans.get(position).getName() == null || devicePropsBeans.get(position).getName().isEmpty()) {
            viewHolder.switchName.setEnabled(false);
            viewHolder.switchName.setText(inflater.getContext().getString(R.string.text_input));
        } else {
            viewHolder.switchName.setEnabled(true);
            viewHolder.switchName.setText(devicePropsBeans.get(position).getName());
        }
        viewHolder.deviceName.setOnClickListener(v -> {
            InputTextDialog inputTextDialog = new InputTextDialog(inflater.getContext());
            if (devicePropsBeans.get(position).getType() == ConstantUtils.TYPE_DEVICE)
                inputTextDialog.setHint(R.string.text_input_device_name);
            else
                inputTextDialog.setHint(R.string.text_input_scene_name);
            inputTextDialog.setText(devicePropsBeans.get(position).getName());
            inputTextDialog.setCancelable(false);
            inputTextDialog.setCanceledOnTouchOutside(false);
            inputTextDialog.setOnDismissListener(dialog -> {
                if (inputTextDialog.getText() != null && !inputTextDialog.getText().equals(devicePropsBeans.get(position).getName())) {
                    devicePropsBeans.get(position).setName(inputTextDialog.getText());
                    notifyDataSetChanged();
                }
            });
            inputTextDialog.show();
        });
        if (devicePropsBeans.get(position).getRoom() == null || devicePropsBeans.get(position).getRoom().isEmpty()) {
            viewHolder.roomName.setEnabled(false);
            viewHolder.roomName.setText(inflater.getContext().getString(R.string.text_input));
        } else {
            viewHolder.roomName.setEnabled(true);
            viewHolder.roomName.setText(devicePropsBeans.get(position).getRoom());
        }
        viewHolder.room.setOnClickListener(v -> {
            for (RoomBean bean : roomBeans)
                bean.setSelected(devicePropsBeans.get(position).getRoom().equals(bean.getName()));
            SelectRoomDialog selectRoomDialog = new SelectRoomDialog(inflater.getContext(), roomBeans);
            selectRoomDialog.setCancelable(false);
            selectRoomDialog.setCanceledOnTouchOutside(false);
            selectRoomDialog.setOnDismissListener(dialog -> {
                if (selectRoomDialog.getSelectedRoom() != -1 && selectRoomDialog.getSelectedRoom() < roomBeans.size()) {
                    roomBeans.get(selectRoomDialog.getSelectedRoom()).setSelected(true);
                    devicePropsBeans.get(position).setRoom(roomBeans.get(selectRoomDialog.getSelectedRoom()).getName());
                } else if (selectRoomDialog.getText() != null && !selectRoomDialog.getText().isEmpty()) {
                    roomBeans.add(new RoomBean(selectRoomDialog.getText()));
                    devicePropsBeans.get(position).setRoom(selectRoomDialog.getText());
                } else
                    return;
                notifyDataSetChanged();
            });
            selectRoomDialog.show();
        });
        return convertView;
    }

    private static class ViewHolder {
        RelativeLayout deviceName;
        RelativeLayout room;
        TextView which;
        TextView switchName;
        TextView roomName;
    }
}
