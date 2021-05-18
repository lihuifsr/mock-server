//package com.madai.mock.parse;
//
//import com.madai.mock.Annotation.MockResource;
//import com.madai.mock.utils.JsonHelper;
//
//import java.util.Map;
//
//@MockResource(name = "baofoo")
//public class BaofooReqParser implements  ReqParser {
//
//    @Override
//    public void parse(String reqBody, Map map) {
////        DefaultReqParser.parseQuery(reqBody, map);
////        reqBody = map.get("data_content").toString();
//        Map paramMap = JsonHelper.readValue(reqBody, Map.class);
//        map.putAll(paramMap);
//    }
//
//}
