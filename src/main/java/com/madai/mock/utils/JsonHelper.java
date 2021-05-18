package com.madai.mock.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JsonHelper {

    private JsonHelper() {

    }

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static <T> T readValue(String content, Class<T> t) {
        T result = null;
        try {
            result = objectMapper.readValue(content, t);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String writeValueAsString(Map map) {
        String ret = "";
        try {
            String result = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        ret = ret.replaceAll("\\\\", "").
                replaceAll("\"\\[", "\\[").
                replaceAll("\\]\"", "\\]");
        return ret;
    }

}
