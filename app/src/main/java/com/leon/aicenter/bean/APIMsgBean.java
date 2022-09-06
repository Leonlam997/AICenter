package com.leon.aicenter.bean;

public class APIMsgBean {


    public static String com_sznaner_voice_callback = "com.sznaner.voice.callback";
    public static String com_sznaner_voice_set = "com.sznaner.voice.set";
    public static String stopDialog = "stopDialog";
    public static String startDialog = "startDialog";
    public static String speakTTS = "speakTTS";
    public static String stopTTS = "stopTTS";
    public static String enableWakeup = "enableWakeup";
    public static String disableWakeup = "disableWakeup";


    public static String com_sznaner_music = "com.sznaner.music";
    public static String com_sznaner_music_source = "com.sznaner.music.source";
    public static String com_sznaner_music_control = "com.sznaner.music.control";
    public static String com_sznaner_music_callback_progress = "com.sznaner.music.callback.progress";
    public static String com_sznaner_music_callback_music = "com.sznaner.music.callback.music";

    private String msg;
    private String data;


    public APIMsgBean() {
    }

    public APIMsgBean(String msg) {
        this.msg = msg;
    }

    public APIMsgBean(String msg, String data) {
        this.msg = msg;
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
