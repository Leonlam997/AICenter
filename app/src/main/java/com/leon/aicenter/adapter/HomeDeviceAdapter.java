package com.leon.aicenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.leon.aicenter.R;
import com.leon.aicenter.bean.DeviceBean;
import com.leon.aicenter.bean.HomeDeviceBean;
import com.leon.aicenter.bean.SceneBean;
import com.leon.aicenter.util.ConstantUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeDeviceAdapter extends BaseAdapter {
    private List<HomeDeviceBean> homeDeviceBeans;
    private List<DeviceBean> deviceBeans;
    private List<SceneBean> sceneBeans;
    private final LayoutInflater inflater;
    private final int[] bgColor;
    private static final int[] deviceBg = new int[]{
            R.mipmap.home_device_back0,
            R.mipmap.home_device_back1,
            R.mipmap.home_device_back2,
            R.mipmap.home_device_back3,
            R.mipmap.home_device_back4,
            R.mipmap.home_device_back5
    };

    public HomeDeviceAdapter(Context context, List<HomeDeviceBean> homeDeviceBeans, List<DeviceBean> deviceBeans, List<SceneBean> sceneBeans) {
        this.homeDeviceBeans = new ArrayList<>(homeDeviceBeans);
        this.deviceBeans = new ArrayList<>(deviceBeans);
        this.sceneBeans = new ArrayList<>(sceneBeans);
        bgColor = new int[8];
        for (int i = 0; i < bgColor.length; i++) {
            int random;
            while (true) {
                random = (int) (Math.random() * 4) + 1;
                bgColor[i] = random;
                if (i == 0)
                    break;
                else if (i == bgColor.length - 1) {
                    if (bgColor[i] != bgColor[i - 1])
                        break;
                } else if (i > 3) {
                    if (bgColor[i] != bgColor[i - 1] && bgColor[i] != bgColor[i - 4])
                        break;
                } else if (bgColor[i] != bgColor[i - 1])
                    break;
            }
        }
        inflater = LayoutInflater.from(context);
    }

    public void updateLists(List<HomeDeviceBean> homeDeviceBeans, List<DeviceBean> deviceBeans, List<SceneBean> sceneBeans) {
        this.homeDeviceBeans = new ArrayList<>(homeDeviceBeans);
        this.deviceBeans = new ArrayList<>(deviceBeans);
        this.sceneBeans = new ArrayList<>(sceneBeans);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return homeDeviceBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return homeDeviceBeans.get(position);
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
            convertView = inflater.inflate(R.layout.layout_home_device, parent, false);
            viewHolder.background = convertView.findViewById(R.id.iv_background);
            viewHolder.textViewAdd = convertView.findViewById(R.id.tv_add_shortcut);
            viewHolder.device = convertView.findViewById(R.id.tv_device_name);
            viewHolder.room = convertView.findViewById(R.id.tv_room_name);
            viewHolder.connection = convertView.findViewById(R.id.iv_connection);
            viewHolder.logo = convertView.findViewById(R.id.iv_device_logo);
            viewHolder.scene = convertView.findViewById(R.id.tv_scene_name);
            viewHolder.type = convertView.findViewById(R.id.tv_scene_type);
            viewHolder.sceneRoom = convertView.findViewById(R.id.tv_scene_room_name);
            viewHolder.detail = convertView.findViewById(R.id.tv_detail);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.connection.setVisibility(View.INVISIBLE);
        viewHolder.type.setVisibility(View.INVISIBLE);
        viewHolder.background.setImageTintList(null);
        viewHolder.detail.setVisibility(View.INVISIBLE);
        switch (homeDeviceBeans.get(position).getType()) {
            case ConstantUtils.TYPE_DEVICE:
                for (DeviceBean bean : deviceBeans) {
                    if (bean.getId() == homeDeviceBeans.get(position).getId()) {
                        viewHolder.connection.setVisibility(bean.getStatusEnum() == ConstantUtils.Status.offline ? View.VISIBLE : View.INVISIBLE);
                        viewHolder.device.setText(bean.getName());
                        viewHolder.room.setText(bean.getRoom());
                        viewHolder.background.setVisibility(View.VISIBLE);
                        if (bean.getStatusEnum() == ConstantUtils.Status.on)
                            viewHolder.background.setImageResource(deviceBg[bgColor[position]]);
                        else
                            viewHolder.background.setImageResource(deviceBg[0]);
                        switch (bean.getKind()) {
                            case curtain:
                                viewHolder.logo.setImageResource(R.mipmap.dev_curtain);
                                break;
                            case temperature:
                                viewHolder.detail.setVisibility(View.VISIBLE);
                                viewHolder.detail.setText(String.format(Locale.CHINA, " %.1f%s", ((bean.getStatus() & ConstantUtils.MASK_STATUS_CLEAR_INDOOR_TEMPERATURE) >> 6) / 2f + 16, inflater.getContext().getString(R.string.text_temperature_degree)));
                                viewHolder.logo.setImageResource(R.mipmap.dev_air_conditioning);
                                break;
                            default:
                                viewHolder.logo.setImageResource(R.mipmap.dev_lamp);
                        }
                        break;
                    }
                }
                viewHolder.textViewAdd.setVisibility(View.INVISIBLE);
                viewHolder.device.setVisibility(View.VISIBLE);
                viewHolder.room.setVisibility(View.VISIBLE);
                viewHolder.logo.setVisibility(View.VISIBLE);
                viewHolder.scene.setVisibility(View.INVISIBLE);
                viewHolder.sceneRoom.setVisibility(View.INVISIBLE);
                break;
            case ConstantUtils.TYPE_SCENE:
                for (SceneBean bean : sceneBeans) {
                    if (bean.getId() == homeDeviceBeans.get(position).getId()) {
                        viewHolder.connection.setVisibility(bean.getStatusEnum() == ConstantUtils.Status.offline ? View.VISIBLE : View.INVISIBLE);
                        viewHolder.scene.setText(bean.getName());
                        viewHolder.sceneRoom.setText(bean.getRoom());
                        switch (bean.getType()) {
                            case switcher:
                                viewHolder.type.setVisibility(View.VISIBLE);
                                viewHolder.type.setText(R.string.text_button);
                                break;
                            case panel:
                                viewHolder.type.setVisibility(View.VISIBLE);
                                viewHolder.type.setText(R.string.text_voice);
                                break;
                        }
                        if (bean.getPicture() != null && !bean.getPicture().isEmpty()) {
                            try {
                                viewHolder.background.setVisibility(View.VISIBLE);
                                viewHolder.background.setImageResource(ConstantUtils.SCENE_PICTURE[Integer.parseInt(bean.getPicture())]);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            viewHolder.logo.setVisibility(View.INVISIBLE);
                        }
                        break;
                    }
                }
                viewHolder.textViewAdd.setVisibility(View.INVISIBLE);
                viewHolder.device.setVisibility(View.INVISIBLE);
                viewHolder.room.setVisibility(View.INVISIBLE);
                viewHolder.scene.setVisibility(View.VISIBLE);
                viewHolder.sceneRoom.setVisibility(View.VISIBLE);
                break;
            default:
                viewHolder.background.setVisibility(View.INVISIBLE);
                viewHolder.textViewAdd.setVisibility(View.VISIBLE);
                viewHolder.device.setVisibility(View.INVISIBLE);
                viewHolder.room.setVisibility(View.INVISIBLE);
                viewHolder.connection.setVisibility(View.INVISIBLE);
                viewHolder.logo.setVisibility(View.INVISIBLE);
                viewHolder.scene.setVisibility(View.INVISIBLE);
                viewHolder.sceneRoom.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView background;
        TextView textViewAdd;
        TextView detail;
        ImageView connection;
        ImageView logo;
        TextView room;
        TextView device;
        TextView scene;
        TextView sceneRoom;
        TextView type;
    }
}
