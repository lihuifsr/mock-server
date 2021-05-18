package com.madai.mock.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MoConfig implements Serializable {

    @JsonProperty("request")
    private RequestConfig req;

    @JsonProperty("response")
    private ResponseConfig res;

    @JsonProperty("cache")
    private CacheConfig cache;

    @JsonProperty("callback")
    private CallbackConfig callbackConfig;

    public RequestConfig getReq() {
        return req;
    }

    public void setReq(RequestConfig req) {
        this.req = req;
    }

    public ResponseConfig getRes() {
        return res;
    }

    public void setRes(ResponseConfig res) {
        this.res = res;
    }

    public CacheConfig getCache() {
        return cache;
    }

    public void setCache(CacheConfig cache) {
        this.cache = cache;
    }

    public CallbackConfig getCallbackConfig() {
        return callbackConfig;
    }

    public void setCallbackConfig(CallbackConfig callbackConfig) {
        this.callbackConfig = callbackConfig;
    }
}
