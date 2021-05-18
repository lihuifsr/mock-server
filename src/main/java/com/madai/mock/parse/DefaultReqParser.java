package com.madai.mock.parse;

import com.madai.mock.Annotation.MockResource;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import com.madai.mock.utils.CONSTANT;
import com.madai.mock.utils.JsonHelper;
import com.madai.mock.utils.XmlUtil;

import java.util.Map;

//@MockResource(name = CONSTANT.REQ_PARSER_DEFAULT)
public class DefaultReqParser implements ReqParser {

    public static final String PARAMS_SEPARATOR = "&";

    public static final String PARAMS_CONNECTOR = "=";

    public static void parse(String body, Map map, String type) {
        if (CONSTANT.REQ_PARAMS_TYPE_XML.equalsIgnoreCase(type)) {
            parseXml(body, map);
        } else if (CONSTANT.REQ_PARAMS_TYPE_JSON.equalsIgnoreCase(type)) {
            parseJson(body, map);
        } else {
            parseQuery(body, map);
        }
    }

    public static void parseXml(String body, Map map) {
        if (body == null) {
            return;
        }
        Document document = null;
        try {
            document = DocumentHelper.parseText(body);
            XmlUtil.element2map(document.getRootElement(), map);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static void parseJson(String body, Map map) {
        if (body == null) {
            return;
        }
        Map bodyMap = JsonHelper.readValue(body, Map.class);
        if (bodyMap == null) {
            return;
        }
        map.putAll(JsonHelper.readValue(body, Map.class));
    }

    public static void parseQuery(String body, Map map) {
        if (body == null) {
            return;
        }
        String[] params = body.split(PARAMS_SEPARATOR);
        for (String param : params) {
            String[] keyValue = param.split(PARAMS_CONNECTOR, 2);
            map.put(keyValue[0], keyValue[1]);
        }
    }

    @Override
    public void parse(String reqBody, Map map) {
        String type = map.get(CONSTANT.PARSE_TYPE_KEY).toString();
        parse(reqBody, map, type);
    }
}
