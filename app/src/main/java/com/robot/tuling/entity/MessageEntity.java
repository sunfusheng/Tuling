package com.robot.tuling.entity;

import com.orm.SugarRecord;

import java.io.Serializable;

public class MessageEntity extends SugarRecord<MessageEntity> implements Serializable {

    private int type;
    private long time;
    private int code;
    private String text;
    private String url;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
