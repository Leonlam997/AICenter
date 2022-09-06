package com.leon.aicenter.bean;

public class APIMusicBean {
    private String source;
    private String name;
    private String singer;
    private int maxProgress;
    private String cover;
    private String state;
    private String path;
    private String mode;

    public APIMusicBean() {
    }


    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


//
//
//    public static APIMusicBean getCurBeanForDB(){
//        Context context = MyApplication.getInstance().getApplicationContext();
//        String json = BaseDBTool.getString("com.sznaner.music.cur",null);
//        if(json == null){
//            Mp3Info mp3Info = MusicService.getCurMp3Info();
//
//            APIMusicBean bean = new APIMusicBean();
//            bean.setSource("local");
//            bean.setName(mp3Info.getTitle());
//            bean.setSinger(mp3Info.getArtist());
//            bean.setMaxProgress((int)mp3Info.getDuration());
//            bean.setState(MusicService.isPlaying()?"play":"pause");
//            bean.setPath(mp3Info.getUrl());
//            return bean;
//        }else {
//            APIMusicBean bean = JSONObject.parseObject(json,APIMusicBean.class);
//            return bean;
//        }
//    }
//
//    public static void setCurBeanToDB(APIMusicBean bean){
//        if(bean == null) return;
//        try {
//            BaseDBTool.putString("com.sznaner.music.cur", JSON.toJSONString(bean));
//        }catch (Exception e){}
//    }
}
