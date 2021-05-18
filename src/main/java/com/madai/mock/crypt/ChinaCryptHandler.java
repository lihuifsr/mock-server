//package com.madai.mock.crypt;
//
//import com.gnete.security.crypt.Crypt;
//import com.gnete.security.crypt.CryptException;
//import com.madai.mock.Annotation.MockResource;
//import org.springframework.util.StringUtils;
//import com.madai.mock.utils.CONSTANT;
//
//@MockResource(name = "china")
//public class ChinaCryptHandler implements CryptHandler {
//
//    private static Crypt crypt = new Crypt("GBK");
//
//    @Override
//    public String decode(String oriData) {
//        return oriData;
//    }
//
//    @Override
//    public String encode(String oriData) {
//        return sign(oriData);
//    }
//
//    private String sign(String oriData) {
//        String signedMsg = null;
//        try {
//            String moRsaPfx = "/Users/admin/git/mock-server/security/" + "mo-rsa.pfx";
//            signedMsg = crypt.sign(oriData, moRsaPfx, "123456");
//        } catch (CryptException e) {
//            e.printStackTrace();
//        }
//        return StringUtils.replace(oriData, "</INFO>", "<SIGNED_MSG>" + signedMsg + "</SIGNED_MSG></INFO>");
//    }
//}
