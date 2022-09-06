package com.leon.aicenter.bean;

import androidx.annotation.Nullable;

import com.leon.aicenter.util.ConstantUtils;

public class DeviceBean {
    private int id;
    private String name;
    private String room;
    private String mac;
    private String picture;
    private int channel;
    private byte power;
    private int roomId;
    private int devId;
    private int devType;
    private int status;
    private ConstantUtils.DeviceKind kind;
    private boolean selected;

    public DeviceBean() {
        name = "";
        room = "";
        mac = "";
        picture = "";
        kind = ConstantUtils.DeviceKind.none;
    }

    public DeviceBean(DeviceBean device) {
        id = device.getId();
        name = device.getName();
        room = device.getRoom();
        mac = device.getMac();
        picture = device.getPicture();
        channel = device.getChannel();
        power = device.getPower();
        roomId = device.getRoomId();
        devId = device.getDevId();
        devType = device.getDevType();
        status = device.getStatus();
        kind = device.getKind();
        selected = device.isSelected();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof SceneBean)
            return false;
        if (obj instanceof DeviceBean)
            return ((DeviceBean) obj).getId() == getId();
        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public byte getPower() {
        return power;
    }

    public void setPower(byte power) {
        this.power = power;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getDevId() {
        return devId;
    }

    public void setDevId(int devId) {
        this.devId = devId;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getDevType() {
        return devType;
    }

    public void setDevType(int devType) {
        this.devType = devType;
    }

    public ConstantUtils.Status getStatusEnum() {
        if (kind == ConstantUtils.DeviceKind.temperature)
            return ConstantUtils.Status.values()[status & ConstantUtils.MASK_STATUS_SWITCH];
        return ConstantUtils.Status.values()[status];
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(ConstantUtils.Status status) {
        this.status = status.ordinal();
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public ConstantUtils.DeviceKind getKind() {
        return kind;
    }

    public void setKind(ConstantUtils.DeviceKind kind) {
        this.kind = kind;
    }
}
