package com.madai.mock.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.Serializable;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ResponseConfig implements Serializable {

    private String body;

    private String crypt;

    private int delay;

    private String type;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCrypt() {
        return crypt;
    }

    public void setCrypt(String crypt) {
        this.crypt = crypt;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
