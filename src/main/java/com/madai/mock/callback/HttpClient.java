package com.madai.mock.callback;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

    private static final int CONNECT_TIMEOUT = 5000;

    private static final int SOCKET_TIMEOUT = 10000;

    //禁用重试
    private static final org.apache.http.client.HttpClient CLIENT = HttpClients.custom()
            .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
            .build();

    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom()
            .setConnectTimeout(CONNECT_TIMEOUT)
            .setSocketTimeout(SOCKET_TIMEOUT)
            .build();

    public static HttpResponse sendPostRequest(String url, Map<String, String> params) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(REQUEST_CONFIG);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));
        HttpResponse httpResponse = null;
        try {
            System.out.println("sendPostRequest ... " + url);
            LOGGER.info("sendPostRequest ... " + url);
            httpResponse = CLIENT.execute(httpPost);
            System.out.println("httpResponse ... " + httpResponse.getStatusLine().getStatusCode());
            LOGGER.info("httpResponse ... " + httpResponse.getStatusLine().getStatusCode());
            return httpResponse;
        } catch (Exception e) {
            System.out.println("sendPostRequest error.");
            LOGGER.error("sendPostRequest error.", e);
            e.printStackTrace();
        } finally {
            if (httpResponse != null) {
                try {
                    EntityUtils.consume(httpResponse.getEntity()); //会自动释放连接
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static HttpResponse sendPostRequest(String url, String json) {
        StringEntity entity = new StringEntity(json, Consts.UTF_8);
        entity.setContentEncoding(ContentType.APPLICATION_JSON.getCharset().name());
        entity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(REQUEST_CONFIG);
        httpPost.setEntity(entity);
        try {
            return CLIENT.execute(httpPost);
        } catch (IOException e) {
            System.out.println(e.toString());
            LOGGER.info("error", e);
            e.printStackTrace();
        }
        return null;
    }

}
