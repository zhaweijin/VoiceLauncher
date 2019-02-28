package com.hiveview.dianshang.entity.channel;

public class ChannelData {
    private String sn;
    private String name;
    private String guide;
    private int intervals;
    private int type;
    private int relationCarousel;

    private SpeechCarousel speechCarousel;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGuide() {
        return guide;
    }

    public void setGuide(String guide) {
        this.guide = guide;
    }

    public int getIntervals() {
        return intervals;
    }

    public void setIntervals(int intervals) {
        this.intervals = intervals;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRelationCarousel() {
        return relationCarousel;
    }

    public void setRelationCarousel(int relationCarousel) {
        this.relationCarousel = relationCarousel;
    }

    public SpeechCarousel getSpeechCarousel() {
        return speechCarousel;
    }

    public void setSpeechCarousel(SpeechCarousel speechCarousel) {
        this.speechCarousel = speechCarousel;
    }
}
