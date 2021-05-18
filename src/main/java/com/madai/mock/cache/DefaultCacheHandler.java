package com.madai.mock.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.madai.mock.Annotation.MockResource;
import com.madai.mock.utils.CONSTANT;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 默认内存级别cache
 * 淘汰机制：过期时间1天
 */
//@MockResource(name= CONSTANT.CACHE_DEFAULT)
public class DefaultCacheHandler implements CacheHandler{

    private static final String CACHE_TEMPLATE_PREFIX = "mockCache";

    private static LoadingCache<String, Map> loadingCache = CacheBuilder.newBuilder().
            expireAfterWrite(1, TimeUnit.DAYS).
            build(new CacheLoader<String, Map>() {
                @Override
                public Map load(String key) throws Exception {
                    return Collections.emptyMap();
                }
            });

    private void put(String key, Map value) {
        loadingCache.put(key, value);
    }

    private Map get(String key) {
        Map map = null;
        try {
            map = loadingCache.get(key);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public void read(String key, Map map) {
        Map cacheData = get(key);
        if (!CollectionUtils.isEmpty(cacheData)) {
            map.put(CACHE_TEMPLATE_PREFIX,cacheData);
        }
    }

    @Override
    public void write(String key, Map map) {
        put(key,map);
    }
}
