package com.leon.aicenter.bean;

public class APIMusicProgressBean {
    private int maxProgress;
    private int progress;
    private String source;

    public APIMusicProgressBean() {
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
