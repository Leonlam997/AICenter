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
import com.leon.aicenter.bean.SceneBean;
import com.leon.aicenter.util.ConstantUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SceneTaskAdapter extends BaseAdapter {
    private List<SceneBean.TaskBean> taskBeans;
    private final List<SceneBean> sceneBeans;
    private final List<DeviceBean> deviceBeans;
    private final LayoutInflater inflater;
    private SceneBean.TaskBean draggingBean;
    private boolean modifying;

    public SceneTaskAdapter(Context context, List<SceneBean.TaskBean> taskBeans, List<SceneBean> sceneBeans, List<DeviceBean> deviceBeans) {
        if (taskBeans != null)
            this.taskBeans = new ArrayList<>(taskBeans);
        else
            this.taskBeans = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        this.sceneBeans = sceneBeans;
        this.deviceBeans = deviceBeans;
    }

    @Override
    public int getCount() {
        return taskBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return taskBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<SceneBean.TaskBean> getSceneTaskBeans() {
        return taskBeans;
    }

    public void updateList(List<SceneBean.TaskBean> taskBeans) {
        this.taskBeans = new ArrayList<>(taskBeans);
        notifyDataSetChanged();
    }

    public void startDrag(int start) {
        draggingBean = taskBeans.get(start);
        taskBeans.remove(start);
        taskBeans.add(start, null);
        notifyDataSetChanged();
    }

    public void update(int start, int down) {
        SceneBean.TaskBean bean = taskBeans.get(start);
        taskBeans.remove(start);
        taskBeans.add(down, bean);
        notifyDataSetChanged();
    }

    public void stopDrag() {
        for (int i = 0; i < taskBeans.size(); i++) {
            if (taskBeans.get(i) == null) {
                taskBeans.set(i, draggingBean);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public boolean isModifying() {
        return modifying;
    }

    public void setModifying(boolean modifying) {
        this.modifying = modifying;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_scene_task_list, parent, false);
            viewHolder.room = convertView.findViewById(R.id.tv_room_name);
            viewHolder.device = convertView.findViewById(R.id.tv_device_name);
            viewHolder.delete = convertView.findViewById(R.id.iv_delete);
            viewHolder.more = convertView.findViewById(R.id.iv_more);
            viewHolder.logo = convertView.findViewById(R.id.iv_device_logo);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        if (taskBeans.get(position) == null) {
            viewHolder.room.setVisibility(View.INVISIBLE);
            viewHolder.device.setVisibility(View.INVISIBLE);
            viewHolder.delete.setVisibility(View.INVISIBLE);
            viewHolder.more.setVisibility(View.INVISIBLE);
            viewHolder.logo.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.room.setVisibility(View.VISIBLE);
            viewHolder.device.setVisibility(View.VISIBLE);
            viewHolder.more.setVisibility(View.VISIBLE);
            viewHolder.logo.setVisibility(View.VISIBLE);
            if (modifying) {
                viewHolder.delete.setVisibility(View.VISIBLE);
                viewHolder.delete.setOnClickListener(v -> {
                    taskBeans.remove(position);
                    notifyDataSetChanged();
                });
                viewHolder.more.setImageResource(R.mipmap.ic_drag);
            } else
                viewHolder.delete.setVisibility(View.GONE);
            if (taskBeans.get(position).getActionEnum() == ConstantUtils.ActionType.scene) {
                viewHolder.logo.setImageResource(R.mipmap.task_scene);
                for (SceneBean bean : sceneBeans)
                    if (bean.getId() == taskBeans.get(position).getDeviceId()) {
                        viewHolder.room.setText(String.format("%s:\"%s\"", inflater.getContext().getString(R.string.text_room), bean.getRoom()));
                        viewHolder.device.setText(bean.getName());
                    }
                if (!modifying)
                    viewHolder.more.setImageResource(R.mipmap.ic_more);
            } else {
                viewHolder.logo.setImageResource(R.mipmap.task_device);
                for (DeviceBean bean : deviceBeans)
                    if (bean.getId() == taskBeans.get(position).getDeviceId()) {
                        viewHolder.room.setText(String.format("%s:\"%s\"", inflater.getContext().getString(R.string.text_room), bean.getRoom()));
                        viewHolder.device.setText(bean.getName());
                        if (!modifying)
                            switch (taskBeans.get(position).getActionEnum()) {
                                case toggle:
                                    viewHolder.more.setImageResource(R.mipmap.ic_reverse);
                                    break;
                                case on:
                                    viewHolder.more.setImageResource(R.mipmap.ic_switch_on);
                                    break;
                                case off:
                                    viewHolder.more.setImageResource(R.mipmap.ic_switch_off);
                                    break;
                                case pause:
                                    viewHolder.more.setImageResource(R.mipmap.ic_pause);
                                    break;
                                default:
                                    StringBuilder detail = new StringBuilder();
                                    int status = taskBeans.get(position).getAction();
                                    detail.append(inflater.getContext().getString(R.string.text_room))
                                            .append(":\"").append(bean.getRoom()).append("\", 设置");
                                    if ((status & ConstantUtils.MASK_SET_TEMPERATURE) != 0)
                                        detail.append(String.format(Locale.CHINA, "温度:%.1f℃  ", (((status & ConstantUtils.MASK_STATUS_TEMPERATURE) >> 6) * 5 + 160) / 10f));
                                    if ((status & ConstantUtils.MASK_SET_FAN) != 0) {
                                        int[] text = new int[]{
                                                R.string.text_fan_low,
                                                R.string.text_fan_mid,
                                                R.string.text_fan_high,
                                                R.string.text_fan_auto};
                                        detail.append(String.format(Locale.CHINA, "风量:%s  ",
                                                inflater.getContext().getString(text[(status & ConstantUtils.MASK_STATUS_FAN) >> 4])));
                                    }
                                    if ((status & ConstantUtils.MASK_SET_MODE) != 0) {
                                        int[] text = new int[]{
                                                R.string.text_temperature_cool,
                                                R.string.text_temperature_fan,
                                                R.string.text_temperature_hot,
                                                R.string.text_fan_auto};
                                        detail.append(String.format(Locale.CHINA, "模式:%s",
                                                inflater.getContext().getString(text[(status & ConstantUtils.MASK_STATUS_MODE) >> 4])));
                                    }
                                    viewHolder.room.setText(detail);
                                    viewHolder.more.setImageResource(R.mipmap.ic_more);
                                    break;
                            }
                    }
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView delete;
        ImageView logo;
        TextView device;
        TextView room;
        ImageView more;
    }
}
