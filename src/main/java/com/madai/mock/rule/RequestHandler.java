package com.madai.mock.rule;

import com.alibaba.fastjson.JSON;
import com.madai.mock.cache.CacheHandler;
import com.madai.mock.callback.CallbackPool;
import com.madai.mock.callback.HttpClient;
import com.madai.mock.crypt.CryptHandler;
import com.madai.mock.model.*;
import com.madai.mock.parse.ReqParser;
import com.madai.mock.utils.*;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import org.json.JSONException;
import org.mockserver.mock.action.ExpectationCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.json.JSONObject;

/**
 * mock-server处理主流程
 */
public class RequestHandler implements ExpectationCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpectationCallback.class);

    public RequestHandler() {
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {

        // 获取URI对应的配置
        List<MoConfig> moConfigs = findMoConfigs(httpRequest);

        String reqBody = httpRequest.getBodyAsString();

        // 相同uri的解密,解析方式应该是一致的
        MoConfig moConfig = moConfigs.get(0);

        // 解密
        reqBody = handleReqCrypt(moConfig.getReq(), reqBody);

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("mockCU", new CollectionUtil());
        bodyMap.put("now", new NowMethod());
        bodyMap.put("random", new RandomMethod());


        // request基本信息存入map(uri等信息)
        handleReqBaseInfo(moConfig.getReq(), bodyMap);

        // request body解析并存入map
        handleReqBody(moConfig.getReq(), reqBody, bodyMap);

        // 根据params参数过滤配置信息
        moConfig = filterByParams(moConfigs, bodyMap);

        // 缓存处理
        handleCache(moConfig, bodyMap);

        // 模版填充
        String responseStr = TemplateParser.buildResponse(moConfig, bodyMap);

        // 加密
        responseStr = handleResCrypt(moConfig.getRes(), responseStr);

        // 延迟处理(mock超时等场景)
        handleDelay(moConfig.getRes());

        // 异步回调
        try {
            callback(moConfig.getCallbackConfig(), bodyMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpResponse httpResponse = HttpResponse.response();
        // 处理response Content-Type
        handleResponseType(httpResponse, moConfig.getRes());
        return httpResponse.withBody(responseStr, Charset.forName("UTF-8"));
    }

    private String handleReqCrypt(RequestConfig req, String reqBody) {
        CryptHandler cryptHandler = Config.getCryptMap().get(req.getCrypt());
        if (cryptHandler == null) {
            return reqBody;
        }
        return cryptHandler.decode(reqBody);
    }

    private String handleResCrypt(ResponseConfig res, String responseBody) {
        CryptHandler cryptHandler = Config.getCryptMap().get(res.getCrypt());
        if (cryptHandler != null) {
            responseBody = cryptHandler.encode(responseBody);
        }
        return responseBody;
    }

    public void handleReqBody(RequestConfig req, String reqBody, Map map) {
        ReqParser reqParser = Config.getParseMap().get(req.getParse());
        if (reqParser == null) {
            String type = req.getType();
            reqParser = Config.getParseMap().get(CONSTANT.REQ_PARSER_DEFAULT);
            map.put(CONSTANT.PARSE_TYPE_KEY, type == null ? "" : type);
        }
        reqParser.parse(reqBody, map);
    }

    private List<MoConfig> findMoConfigs(HttpRequest httpRequest) {
        String uri = httpRequest.getPath().getValue();
        return Config.getDataMap().get(uri);
    }

    private void handleReqBaseInfo(RequestConfig requestConfig, Map map) {
        map.put("req", requestConfig);
    }

    private void handleCache(MoConfig moConfig, Map map) {
        CacheConfig cacheConfig = moConfig.getCache();
        if (cacheConfig == null) {
            return;
        }
        String type = cacheConfig.getType();
        CacheHandler cacheHandler = Config.getCacheMap().get(type);
        if (cacheHandler == null) {
            cacheHandler = Config.getCacheMap().get(CONSTANT.CACHE_DEFAULT);
        }
        // 填充key模版
        String writeKey = TemplateParser.buildByTemplate(cacheConfig.getWriteKey(), map);
        String readKey = TemplateParser.buildByTemplate(cacheConfig.getReadKey(), map);

        //读取缓存并放入map中
        if (!StringUtils.isEmpty(writeKey)) {
            LOGGER.info("writeKey:" + writeKey);
            cacheHandler.write(writeKey, map);
        }

        // 将map存入缓存
        if (!StringUtils.isEmpty(readKey)) {
            LOGGER.info("readKey:" + readKey);
            cacheHandler.read(readKey, map);
        }
    }

    private MoConfig filterByParams(List<MoConfig> moConfigs, Map map) {
        // 默认先取第一个
        MoConfig result = moConfigs.get(0);

        // 匹配params中都match的configs
        for (MoConfig moConfig : moConfigs) {
            Map params = moConfig.getReq().getParams();
            if (CollectionUtils.isEmpty(params)) {
                continue;
            }
            boolean is = true;
            for (Object entry : params.entrySet()) {
                Map.Entry<String, String> e = (Map.Entry<String, String>) entry;
                String key = e.getKey();
                key = TemplateParser.buildByTemplate(key, map);
                String value = e.getValue();
                if (!value.equals(key)) {
                    is = false;
                    break;
                }
            }
            if (is) {
                result = moConfig;
                break;
            }
        }
        return result;
    }

    private void handleDelay(ResponseConfig responseConfig) {
        int delay = responseConfig.getDelay();
        if (delay > 0) {
            try {
                Thread.sleep(delay * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void callback(final CallbackConfig callbackConfig, final Map map) throws JSONException {
        if (callbackConfig == null) {
            return;
        }
        Map params = callbackConfig.getParams();

        String jsonUrl = "{'url':'" + callbackConfig.getUrl() + "'}";

        String realUrlJson = TemplateParser.buildByTemplate(jsonUrl, map);
        JSONObject jsonObj = new JSONObject(realUrlJson);
        final String realUrl = jsonObj.getString("url");

        final String realJson = handleCallbackCtypt(callbackConfig.getCrypt(), TemplateParser.buildByTemplate(JSON.toJSONString(params), map));
        final Map realParams = JsonHelper.readValue(realJson, Map.class);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("callback run. callbackConfig = " + JSON.toJSON(callbackConfig) + ", realParams = " + JSON.toJSON(realParams));
                LOGGER.info("callback run. callbackConfig = " + JSON.toJSON(callbackConfig) + ", realParams = " + JSON.toJSON(realParams));
//                HttpClient.sendPostRequest(callbackConfig.getUrl(), realJson);
                HttpClient.sendPostRequest(realUrl, realParams);
            }
        };
        CallbackPool.execute(runnable);
    }

    private String handleCallbackCtypt(String crypt, String str) {
        CryptHandler cryptHandler = Config.getCryptMap().get(crypt);
        if (cryptHandler == null) {
            return str;
        }
        return cryptHandler.encode(str);
    }

    private void handleResponseType(HttpResponse httpResponse, ResponseConfig responseConfig) {
        String type = responseConfig.getType();
        String value = CONSTANT.RES_TYPE_MAP.get(type);
        if (!StringUtils.isEmpty(value)) {
            httpResponse.withHeader(CONSTANT.RES_TYPE_KEY, value);
        }
    }

    private static class NowMethod implements TemplateMethodModelEx {
        @Override
        public Object exec(final List arguments) {
            if (arguments.size() < 1) {
                throw new IllegalArgumentException("Date format is required");
            }

            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat(arguments.get(0).toString());
            return format.format(date);
        }
    }


    private static class RandomMethod implements TemplateMethodModelEx {
        @Override
        public Object exec(final List arguments) {
            Optional<Long> range = getRange(arguments);
            Optional<? extends NumberFormat> format = getFormat(arguments);
            double result = new Random().nextDouble() * range.orElse(1L);

            if (format.isPresent()) {
                return format.get().format(result);
            }

            return result;
        }

        private Optional<? extends NumberFormat> getFormat(final List<?> arguments) {
            if (arguments.size() > 0) {
                Object last = arguments.get(arguments.size() - 1);
                if (last instanceof SimpleScalar) {
                    SimpleScalar lastArgument = (SimpleScalar) last;
                    return Optional.of(new DecimalFormat(lastArgument.toString()));
                }
            }

            return Optional.empty();
        }

        private Optional<Long> getRange(final List<?> arguments) {
            if (arguments.size() > 0) {
                Object range = arguments.get(0);
                if (range instanceof SimpleNumber) {
                    return getRange((SimpleNumber) range);
                }
            }

            return Optional.empty();
        }

        private Optional<Long> getRange(final SimpleNumber range) {
            long reference = range.getAsNumber().longValue();
            if (reference <= 0) {
                throw new IllegalArgumentException("Random range should be greater than 0");
            }

            return Optional.of(reference);
        }
    }

    public static void main(String[] args) throws JSONException {
        String url = "${url}";
        Map map = new HashMap();
//            map.put("url","${url}");
//        map.put("a", "${ghi}");
        map.put("b", "${poi}");
        CallbackConfig callbackConfig = new CallbackConfig();
        callbackConfig.setUrl(url);
        callbackConfig.setParams(map);

        Map data = new HashMap();
        data.put("url", "http://www.baidu.com");
        data.put("poi", "bbb");
        new RequestHandler().callback(callbackConfig, data);
    }
}
