package com.madai.mock.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.Serializable;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CallbackConfig implements Serializable {

    private String url;

    private Map params;

    private String crypt;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map getParams() {
        return params;
    }

    public void setParams(Map params) {
        this.params = params;
    }

    public String getCrypt() {
        return crypt;
    }

    public void setCrypt(String crypt) {
        this.crypt = crypt;
    }
}
