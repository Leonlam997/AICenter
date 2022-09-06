package com.leon.aicenter.bean;

public class RoomBean {
    private int id;
    private String name;
    private int buttonAmount;
    private int sceneAmount;
    private boolean selected;

    public RoomBean(String name) {
        this.name = name;
        selected = false;
        id = 0;
    }

    public RoomBean(int id, String name) {
        this.id = id;
        this.name = name;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getButtonAmount() {
        return buttonAmount;
    }

    public void setButtonAmount(int buttonAmount) {
        this.buttonAmount = buttonAmount;
    }

    public int getSceneAmount() {
        return sceneAmount;
    }

    public void setSceneAmount(int sceneAmount) {
        this.sceneAmount = sceneAmount;
    }
}
