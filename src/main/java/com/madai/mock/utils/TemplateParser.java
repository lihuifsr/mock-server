package com.madai.mock.utils;

import com.google.common.collect.ImmutableMap;
import com.madai.mock.utils.autoinject.ClassUtils;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.*;
import com.madai.mock.model.MoConfig;
import com.madai.mock.model.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;


public class TemplateParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateParser.class);

    private static final Version CURRENT_VERSION = Configuration.getVersion();
    private static final String TEMPLATE_NAME = "template";

    public static String buildResponse(MoConfig moConfig, Map map) {
        LOGGER.info(map.toString());
        System.out.println(map.toString());
        String body = moConfig.getRes().getBody();
        return buildByTemplate(body, map);
    }

    public static String buildByTemplate(String template, Map map) {
        if (StringUtils.isEmpty(template) || CollectionUtils.isEmpty(map)) {
            return template;
        }
        String result = template;
        try {
            Template targetTemplate = createTemplate(template);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(stream);
            targetTemplate.process(map, writer);
            return stream.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static Template createTemplate(String content) throws IOException {
        TemplateLoader templateLoader = createTemplateLoader(content);
        Configuration cfg = createConfiguration(templateLoader, Charset.forName("UTF-8"));
        return cfg.getTemplate(TEMPLATE_NAME);
    }

    private static StringTemplateLoader createTemplateLoader(String content) {
        StringTemplateLoader templateLoader = new StringTemplateLoader();
        templateLoader.putTemplate(TEMPLATE_NAME, content.toString());
        return templateLoader;
    }

    private static Configuration createConfiguration(final TemplateLoader templateLoader, final Charset charset) {
        Configuration cfg = new Configuration(CURRENT_VERSION);
        cfg.setObjectWrapper(new DefaultObjectWrapperBuilder(CURRENT_VERSION).build());
        cfg.setDefaultEncoding(charset.name());
        cfg.setTemplateLoader(templateLoader);
        return cfg;
    }

//    private static Map<String, Object> variables(final MoConfig moConfig, Map map) {
//        RequestConfig requestConfig = moConfig.getReq();
//        return ImmutableMap.<String, Object>builder()
//                .putAll(map)
//                .put("req", requestConfig)
//                .build();
//    }

}
