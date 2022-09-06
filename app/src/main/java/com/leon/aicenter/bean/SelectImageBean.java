package com.leon.aicenter.bean;

public class SelectImageBean {
    private String name;
    private int resource;
    private boolean selected;

    public SelectImageBean(String name, int resource) {
        this.name = name;
        this.resource = resource;
    }

    public SelectImageBean(String name, int resource, boolean selected) {
        this.name = name;
        this.resource = resource;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
