package com.madai.mock.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.Serializable;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RequestConfig implements Serializable {

    private String uri;

    private Map params;

    private String crypt;

    private String type;

    private String parse;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParse() {
        return parse;
    }

    public void setParse(String parse) {
        this.parse = parse;
    }
}
