package com.madai.mock.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CacheConfig {

    @JsonProperty("type")
    private String type;

    @JsonProperty("r")
    private String readKey;

    @JsonProperty("w")
    private String writeKey;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReadKey() {
        return readKey;
    }

    public void setReadKey(String readKey) {
        this.readKey = readKey;
    }

    public String getWriteKey() {
        return writeKey;
    }

    public void setWriteKey(String writeKey) {
        this.writeKey = writeKey;
    }
}
