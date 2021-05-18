package com.madai.mock.cache;

import java.util.Map;

public interface CacheHandler {

    /**
     * 从cache把数据读到map中
     * @param map
     */
    public void read(String key, Map map);

    /**
     * 把map中的数据写到cache中
     * @param map
     */
    public void write(String key, Map map);
}
