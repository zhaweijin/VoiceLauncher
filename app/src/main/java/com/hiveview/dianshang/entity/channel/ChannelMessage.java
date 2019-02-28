package com.hiveview.dianshang.entity.channel;

public class ChannelMessage {

    private int code;
    private String message;
    private ChannelResult result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ChannelResult getResult() {
        return result;
    }

    public void setResult(ChannelResult result) {
        this.result = result;
    }
}
