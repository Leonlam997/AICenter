package com.leon.aicenter.bean;

public class VoiceInputBean {
    private String pinyin;
    private int eof;
    private String text;
    private String var;
    private String type;

    public VoiceInputBean() {
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public int getEof() {
        return eof;
    }

    public void setEof(int eof) {
        this.eof = eof;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
