package com.madai.mock.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.madai.mock.cache.CacheHandler;
import com.madai.mock.crypt.CryptHandler;
import com.madai.mock.model.MoConfig;
import com.madai.mock.parse.ReqParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    private static final Map<String, List<MoConfig>> dataMap = new HashMap<>();

    public static Map<String, List<MoConfig>> getDataMap() {
        return dataMap;
    }

    private static final Map<String, CryptHandler> cryptMap = new HashMap<>();

    private static final Map<String, ReqParser> parseMap = new HashMap<>();

    private static final Map<String, CacheHandler> cacheMap = new HashMap<>();

    private static String configPath = null;

    public static void setConfigPath(String path) {
        configPath = path;
    }

    public static String getConfigPath() {
        return configPath;
    }

    //    static {
//        cryptMap.put("baofoo", new BaoFooCryptHandler());
//        cryptMap.put("china", new ChinaCryptHandler());
//        cryptMap.put("mop", new MopCryptHandler());
//
//        parseMap.put("baofoo", new BaofooReqParser());
//        parseMap.put(CONSTANT.REQ_PARSER_DEFAULT, new DefaultReqParser());
//
//        cacheMap.put(CONSTANT.CACHE_DEFAULT, new DefaultCacheHandler());
//    }

    public static Map<String, CryptHandler> getCryptMap() {
        return cryptMap;
    }

    public static Map<String, ReqParser> getParseMap() {
        return parseMap;
    }

    public static Map<String, CacheHandler> getCacheMap() {
        return cacheMap;
    }

    public static void initConfig() {
        dataMap.putAll(loadConfigFiles());
    }

    private static Map loadConfigFiles() {
        Map<String, List<MoConfig>> map = new HashMap();
        Map<String, String> files = FileUtil.readFilesToMap(configPath);
        for (Map.Entry<String, String> entry : files.entrySet()) {
            if(entry.getKey().endsWith(".json")){
                loadConfigFile(entry.getKey(), entry.getValue(), map);
            }
        }
        return map;
    }

    private static void loadConfigFile(String fileName, String data, Map<String, List<MoConfig>> map) {
        List<MoConfig> moConfigs = null;
        try {
            moConfigs = JsonHelper.getObjectMapper().readValue(data, new TypeReference<List<MoConfig>>() {
            });
            for (MoConfig moConfig : moConfigs) {
                String uri = moConfig.getReq().getUri();
                List<MoConfig> mos = map.get(uri);
                if (mos == null) {
                    mos = new ArrayList<>();
                    map.put(uri, mos);
                }
                mos.add(moConfig);
            }
        } catch (IOException e) {
            LOGGER.error("------------- " + fileName + " 炸了！-------------", e);
            e.printStackTrace();
        }
    }

    private static void loadConfigFile(String data, Map<String, List<MoConfig>> map) {
        List<MoConfig> moConfigs = null;
        try {
            moConfigs = JsonHelper.getObjectMapper().readValue(data, new TypeReference<List<MoConfig>>() {
            });
            for (MoConfig moConfig : moConfigs) {
                String uri = moConfig.getReq().getUri();
                List<MoConfig> mos = map.get(uri);
                if (mos == null) {
                    mos = new ArrayList<>();
                    map.put(uri, mos);
                }
                mos.add(moConfig);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void resetDataConfig() {
        Map<String, List<MoConfig>> map = loadConfigFiles();
        dataMap.clear();
        dataMap.putAll(map);
    }
}
