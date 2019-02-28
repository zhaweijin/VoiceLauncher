package com.hiveview.dianshang.entity.background;

public class BackgroundMessage {

    private int code;
    private String message;
    private BackgroundResult result;

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

    public BackgroundResult getResult() {
        return result;
    }

    public void setResult(BackgroundResult result) {
        this.result = result;
    }
}
