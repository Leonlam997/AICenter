package com.leon.aicenter.bean;

/**
 * Created by Administrator on 2021/12/18/018.
 */

public class TTSBean {
    private String text;
    //①优先级0：保留，与DDS语音交互同级，仅限内部使用；
    //②优先级1：正常，默认选项，同级按序播放；
    //③优先级2：重要，可以插话<优先级1>，同级按序播放，播报完毕后继续播报刚才被插话的<优先级1>；
    //④优先级3：紧急，可以打断当前正在播放的<优先级1|优先级2>，同级按序播放，播报完毕后不再继续播报刚才被打断的<优先级1｜优先级2>。
    private int priority;

    public TTSBean() {
    }

    public TTSBean(String text, int priority) {
        this.text = text;
        this.priority = priority;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
