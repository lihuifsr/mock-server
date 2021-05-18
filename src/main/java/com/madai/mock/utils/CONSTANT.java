package com.madai.mock.utils;

import java.util.HashMap;
import java.util.Map;

public class CONSTANT {

    public static final String REQ_PARSER_DEFAULT = "parser_default";

    public static final String CACHE_DEFAULT = "cache_default";

    public static final String PARSE_TYPE_KEY = "parse_type_key";

    public static final String REQ_PARAMS_TYPE_XML = "xml";

    public static final String REQ_PARAMS_TYPE_JSON = "json";

    public static final String RES_TYPE_KEY = "Content-Type";

    public static final Map<String, String> RES_TYPE_MAP = new HashMap<>();

    static {
        RES_TYPE_MAP.put("html","text/html");
    }

//    public static final String KEY_PATH = "/Users/admin/git/mock-server/security/";
//
//    public static final String CONFIG_PATH = "/Users/admin/mock-config/";

//    public static final String KEY_PATH = "/opt/mock-server/security/";

//    public static final String CONFIG_PATH = "/opt/mock-server/config/";
}
