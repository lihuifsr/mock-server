package com.madai.mock.utils.autoinject;

import com.madai.mock.Annotation.MockResource;
import com.madai.mock.cache.CacheHandler;
import com.madai.mock.cache.DefaultCacheHandler;
import com.madai.mock.callback.HttpClient;
import com.madai.mock.crypt.CryptHandler;
import com.madai.mock.parse.DefaultReqParser;
import com.madai.mock.parse.ReqParser;
import com.madai.mock.utils.CONSTANT;
import com.madai.mock.utils.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.List;

public class HandlerInjector {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandlerInjector.class);

    public static void inject() {
        injectDefaultHandler();
        injectCustomizedHandler();
    }

    /**
     * 扫描有MockResource标注的类
     */
    private static void injectCustomizedHandler() {

        ClassFilter filter = new ClassFilter() {
            @Override
            public boolean accept(Class clazz) {
                Annotation an = clazz.getAnnotation(MockResource.class);
                return null != an;
            }
        };

        for (Class clazz : ClassUtils.scanPackage(filter)) {
            LOGGER.info(clazz.toString());
            injectToConfig(clazz);
        }

        PkgScanner scanner = new PkgScanner("", MockResource.class);
        List<String> list = scanner.scan();

        for (String name : list) {
            try {
                LOGGER.info("inject handler : " + name);
                Class clazz = Class.forName(name);
                injectToConfig(clazz);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static void injectToConfig(Class clazz) {
        MockResource annotation = (MockResource) clazz.getAnnotation(MockResource.class);
        String key = annotation.name();
        try {
            Object obj = clazz.newInstance();
            if (CacheHandler.class.isAssignableFrom(clazz)) {
                Config.getCacheMap().put(key, (CacheHandler) obj);
            }

            if (CryptHandler.class.isAssignableFrom(clazz)) {
                Config.getCryptMap().put(key, (CryptHandler) obj);
            }

            if (ReqParser.class.isAssignableFrom(clazz)) {
                Config.getParseMap().put(key, (ReqParser) obj);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void injectDefaultHandler() {
        Config.getParseMap().put(CONSTANT.REQ_PARSER_DEFAULT, new DefaultReqParser());
        Config.getCacheMap().put(CONSTANT.CACHE_DEFAULT, new DefaultCacheHandler());
    }
}
