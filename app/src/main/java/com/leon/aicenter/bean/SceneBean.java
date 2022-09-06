package com.leon.aicenter.bean;

import androidx.annotation.Nullable;

import com.leon.aicenter.util.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

public class SceneBean extends DeviceBean {
    private String keywords;
    private int[] allChannel;
    private ConstantUtils.SceneType type;
    private List<TaskBean> taskBeans;

    public SceneBean() {
        super();
        keywords = "";
        type = ConstantUtils.SceneType.local;
        taskBeans = new ArrayList<>();
    }

    public SceneBean(DeviceBean deviceBean) {
        super(deviceBean);
        keywords = "";
        type = ConstantUtils.SceneType.local;
        taskBeans = new ArrayList<>();
    }

    public SceneBean(SceneBean sceneBean) {
        super(sceneBean);
        keywords = sceneBean.getKeywords();
        allChannel = sceneBean.allChannel != null ? sceneBean.allChannel.clone() : null;
        type = sceneBean.getType();
        taskBeans = new ArrayList<>(sceneBean.getTaskBeans());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof SceneBean)
            return ((SceneBean) obj).getId() == getId();
        return false;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public int[] getAllChannel() {
        return allChannel;
    }

    public void setAllChannel(int[] allChannel) {
        this.allChannel = allChannel;
    }

    public ConstantUtils.SceneType getType() {
        return type;
    }

    public void setType(ConstantUtils.SceneType type) {
        this.type = type;
    }

    public List<TaskBean> getTaskBeans() {
        return taskBeans;
    }

    public void setTaskBeans(List<TaskBean> taskBeans) {
        this.taskBeans = new ArrayList<>(taskBeans);
    }

    public static class TaskBean {
        private int deviceId;
        private int action;

        public TaskBean() {
        }

        public TaskBean(int deviceId, ConstantUtils.ActionType action) {
            this.deviceId = deviceId;
            this.action = action.ordinal();
        }

        public TaskBean(int deviceId, int action) {
            this.deviceId = deviceId;
            this.action = action;
        }

        public TaskBean(TaskBean bean) {
            this.deviceId = bean.deviceId;
            this.action = bean.action;
        }

        public int getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(int deviceId) {
            this.deviceId = deviceId;
        }

        public ConstantUtils.ActionType getActionEnum() {
            if (action < ConstantUtils.ActionType.values().length)
                return ConstantUtils.ActionType.values()[action];
            else
                return ConstantUtils.ActionType.none;
        }

        public void setAction(ConstantUtils.ActionType action) {
            this.action = action.ordinal();
        }

        public int getAction() {
            return action;
        }

        public void setAction(int action) {
            this.action = action;
        }
    }
}
