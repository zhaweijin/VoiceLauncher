package com.hiveview.dianshang.entity.content;

public class ContentMessage {

    private int code;
    private String message;
    private ContentResult result;

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

    public ContentResult getResult() {
        return result;
    }

    public void setResult(ContentResult result) {
        this.result = result;
    }
}
