//package com.madai.mock.crypt;
//
//import com.madai.mock.Annotation.MockResource;
//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;
//import com.madai.mock.utils.CONSTANT;
//import com.madai.mock.utils.CryptUtil;
//import com.madai.mock.parse.DefaultReqParser;
//
//import javax.crypto.Cipher;
//import java.io.UnsupportedEncodingException;
//import java.security.PrivateKey;
//import java.security.PublicKey;
//import java.util.HashMap;
//import java.util.Map;
//
//@MockResource(name = "baofoo")
//public class BaoFooCryptHandler implements CryptHandler {
//
//
//    @Override
//    public String decode(String oriData) {
//        String result = null;
//        oriData = parseReqBody(oriData);
//        try {
//            byte[] oriDatas = CryptUtil.hex2Bytes(oriData);
//            String publicKeyPath = CONSTANT.KEY_PATH + "public-rsa.cer";
//            String publicKeyStr = CryptUtil.getPublicKeyStr(publicKeyPath);
//            PublicKey publicKey = CryptUtil.getPublicKeyByText(publicKeyStr);
//            byte[] datas = CryptUtil.rsaByPublicKey(oriDatas, publicKey, Cipher.DECRYPT_MODE);
//            result = new String(new BASE64Decoder().decodeBuffer(new String(datas)));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//
//    @Override
//    public String encode(String oriData) {
//        String result = null;
//        try {
//            oriData = new BASE64Encoder().encode(oriData.getBytes());
//            String privateKeyPath = CONSTANT.KEY_PATH + "mo-rsa.pfx";
//            PrivateKey privateKey = CryptUtil.getPrivateKeyFromFile(privateKeyPath, "123456");
//            byte[] datas = CryptUtil.rsaByPrivateKey(oriData.getBytes(), privateKey, Cipher.ENCRYPT_MODE);
//            oriData = new BASE64Encoder().encode(oriData.getBytes("UTF-8"));
//            result = CryptUtil.byte2Hex(datas);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//
//    public String parseReqBody(String reqBody) {
//        Map map = new HashMap();
//        DefaultReqParser.parseQuery(reqBody, map);
//        return map.get("data_content").toString();
//    }
//
//}
