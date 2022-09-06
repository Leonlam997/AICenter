package com.leon.aicenter.bean;

import java.util.ArrayList;
import java.util.List;

public class SettingsBean {
    private List<HomeDeviceBean> homeDeviceBeans;
    private List<Integer> roomOrder;
    private List<List<Integer>> deviceOrder;
    private List<List<Integer>> sceneOrder;
    private boolean wakeupWord;

    public SettingsBean() {
        roomOrder = new ArrayList<>();
        deviceOrder = new ArrayList<>();
        sceneOrder = new ArrayList<>();
        homeDeviceBeans = new ArrayList<>();
        for (int i = 0; i < 8; i++)
            homeDeviceBeans.add(new HomeDeviceBean());
    }

    public List<HomeDeviceBean> getHomeDeviceBeans() {
        if (homeDeviceBeans.size() < 8) {
            for (int i = homeDeviceBeans.size(); i < 8; i++)
                homeDeviceBeans.add(new HomeDeviceBean());
        }
        return new ArrayList<>(homeDeviceBeans);
    }

    public void setHomeDeviceBeans(List<HomeDeviceBean> homeDeviceBeans) {
        this.homeDeviceBeans = new ArrayList<>(homeDeviceBeans);
    }

    public List<Integer> getRoomOrder() {
        return new ArrayList<>(roomOrder);
    }

    public void setRoomOrder(List<Integer> roomOrder) {
        this.roomOrder = new ArrayList<>(roomOrder);
    }

    public List<List<Integer>> getDeviceOrder() {
        return new ArrayList<>(deviceOrder);
    }

    public void setDeviceOrder(List<List<Integer>> deviceOrder) {
        this.deviceOrder = new ArrayList<>(deviceOrder);
    }

    public List<List<Integer>> getSceneOrder() {
        return new ArrayList<>(sceneOrder);
    }

    public void setSceneOrder(List<List<Integer>> sceneOrder) {
        this.sceneOrder = new ArrayList<>(sceneOrder);
    }

    public boolean isWakeupWord() {
        return wakeupWord;
    }

    public void setWakeupWord(boolean wakeupWord) {
        this.wakeupWord = wakeupWord;
    }
}
