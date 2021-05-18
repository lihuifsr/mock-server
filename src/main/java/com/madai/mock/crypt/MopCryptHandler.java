//package com.madai.mock.crypt;
//
//import com.madai.mock.Annotation.MockResource;
//import com.madai.mock.parse.DefaultReqParser;
//import com.madai.mock.utils.CryptUtil;
//import com.madai.mock.utils.JsonHelper;
//
//import java.net.URLDecoder;
//import java.util.HashMap;
//import java.util.Map;
//
//@MockResource(name = "mop")
//public class MopCryptHandler implements CryptHandler {
//
//    private String appSecret = "b4e6278acb47be81b6a15152acf8fd20";
//
//    @Override
//    public String decode(String oriData) {
//        String result = "";
//        try {
//            oriData = URLDecoder.decode(oriData, "UTF-8");
//            Map map = new HashMap();
//            DefaultReqParser.parseQuery(oriData, map);
//            result = JsonHelper.getObjectMapper().writeValueAsString(map).
//                    replaceAll("\\\\", "").
//                    replaceAll("\"\\[", "\\[").
//                    replaceAll("\\]\"", "\\]");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//
//    @Override
//    public String encode(String oriData) {
//        return sign(oriData);
//    }
//
//    private String sign(String oriData) {
//        Map<String, String> resMap = JsonHelper.readValue(oriData, Map.class);
//        String bodyStr = "";
//        bodyStr = oriData.substring(1, oriData.length() - 1);//去头尾花括弧,剩下的就是body的花括弧
//        bodyStr = bodyStr.substring(bodyStr.indexOf("{"), bodyStr.lastIndexOf("}") + 1);
//        String sign = CryptUtil.signTopResponse(resMap.get("code"), resMap.get("msg"), bodyStr, appSecret);
//        oriData = oriData.replace("\"body\"", "\"sign\":\"" + sign + "\",\"body\"");
//        return oriData;
//    }
//
//}
