package com.hiveview.dianshang.entity.content;

public class ContentData {
    private String channelSn;
    private String title;
    private String content;
    private int resType;
    private String packageName;

    /**
     * 增加的频道名称
     */
    private String typeName="";

    /**
     * 增加的向导语言提示
     */
    private String guide="";

    /**
     * 用于随机数复位
     */
    private boolean showed = false;

    public boolean isShowed() {
        return showed;
    }

    public void setShowed(boolean showed) {
        this.showed = showed;
    }

    public String getChannelSn() {
        return channelSn;
    }

    public void setChannelSn(String channelSn) {
        this.channelSn = channelSn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getResType() {
        return resType;
    }

    public void setResType(int resType) {
        this.resType = resType;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getGuide() {
        return guide;
    }

    public void setGuide(String guide) {
        this.guide = guide;
    }
}
