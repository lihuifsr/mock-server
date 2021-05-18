package com.madai.mock.utils;

import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Lists.newArrayList;

public class CryptUtil {

    /** */
    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";

    /** */
    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /** */
    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";

    /** */
    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /** */
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /** */
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /** */
    /**
     * <p>
     * 生成密钥对(公钥和私钥)
     * </p>
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Object> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /** */
    /**
     * <p>
     * 用私钥对信息生成数字签名
     * </p>
     *
     * @param data       已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
//        byte[] keyBytes = Base64Util.decodeString(privateKey);

        byte[] keyBytes = Base64Utils.decodeFromString(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
//        return Base64Util.encodeByte(signature.sign());
        return Base64Utils.encodeToString(signature.sign());
    }

    /** */
    /**
     * <p>
     * 校验数字签名
     * </p>
     *
     * @param data      已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign      数字签名
     * @return
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKey, String sign)
            throws Exception {
//        byte[] keyBytes = Base64Util.decodeString(publicKey);
        byte[] keyBytes = Base64Utils.decodeFromString(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
//        return signature.verify(Base64Util.decodeString(sign));
        return signature.verify(Base64Utils.decodeFromString(sign));
    }

    public static String getPublicKeyStr(String publicKeyPath) {
        FileInputStream pubKeyStream = null;
        try {
            pubKeyStream = new FileInputStream(publicKeyPath);
            byte[] reads = new byte[pubKeyStream.available()];
            pubKeyStream.read(reads);
            return new String(reads);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (pubKeyStream != null) {
                try {
                    pubKeyStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 根据Cer文件读取公钥
     *
     * @param pubCerPath
     * @return
     */
    public PublicKey getPublicKeyFromFile(String pubCerPath) {
        FileInputStream pubKeyStream = null;
        try {
            pubKeyStream = new FileInputStream(pubCerPath);
            byte[] reads = new byte[pubKeyStream.available()];
            pubKeyStream.read(reads);
            return getPublicKeyByText(new String(reads));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
//            logger.error("公钥文件不存在:{}", pubCerPath, e);
        } catch (IOException e) {
            e.printStackTrace();
//            logger.error("公钥文件读取失败:{}", pubCerPath, e);
        } finally {
            if (pubKeyStream != null) {
                try {
                    pubKeyStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
//                    logger.error("从文件获取公钥异常:{}", pubCerPath, e);
                }
            }
        }
        return null;
    }

    /**
     * 根据公钥Cer文本串读取公钥
     *
     * @param pubKeyText
     * @return
     */
    public static PublicKey getPublicKeyByText(String pubKeyText) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
            BufferedReader br = new BufferedReader(new StringReader(pubKeyText));
            String line = null;
            StringBuilder keyBuffer = new StringBuilder();

            while ((line = br.readLine()) != null) {
                if (!line.startsWith("-")) {
                    keyBuffer.append(line);
                }
            }

            java.security.cert.Certificate certificate = certificateFactory.generateCertificate(new ByteArrayInputStream(Base64Utils.decode(keyBuffer.toString().getBytes())));

            return certificate.getPublicKey();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ======================================================================================
    // 公私钥算法
    // ======================================================================================

    /**
     * 公钥算法
     *
     * @param srcData   源字节
     * @param publicKey 公钥
     * @param mode      加密 OR 解密
     * @return
     */
    public static byte[] rsaByPublicKey(byte[] srcData, PublicKey publicKey, int mode) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(mode, publicKey);
            // 分段加密
            int blockSize = (mode == Cipher.ENCRYPT_MODE) ? 117 : 128;
            byte[] encryptedData = null;
            for (int i = 0; i < srcData.length; i += blockSize) {
                // 注意要使用2的倍数，否则会出现加密后的内容再解密时为乱码
                byte[] doFinal = cipher.doFinal(subarray(srcData, i, i + blockSize));
                encryptedData = addAll(encryptedData, doFinal);
            }
            return encryptedData;

        } catch (NoSuchAlgorithmException e) {
//			//log.error("公钥算法-不存在的解密算法:", e);
        } catch (NoSuchPaddingException e) {
//			//log.error("公钥算法-无效的补位算法:", e);
        } catch (IllegalBlockSizeException e) {
//			//log.error("公钥算法-无效的块大小:", e);
        } catch (BadPaddingException e) {
//			//log.error("公钥算法-补位算法异常:", e);
        } catch (InvalidKeyException e) {
//			//log.error("公钥算法-无效的私钥:", e);
        }
        return null;
    }

    public static byte[] decrypt(byte[] encryptedData, PublicKey publicKey)
            throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    public static byte[] subarray(byte[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;

        if (newSize <= 0) {
            return new byte[0];
        }

        byte[] subarray = new byte[newSize];

        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);

        return subarray;
    }

    private static byte[] addAll(byte[] array1, byte[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        byte[] joinedArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static byte[] clone(byte[] array) {
        if (array == null) {
            return null;
        }
        return (byte[]) array.clone();
    }

    public static byte[] hexStr2Bytes(String src) {
        int m = 0, n = 0;
        int l = src.length() / 2;
        System.out.println(l);
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = Byte.decode("0x" + src.substring(i * 2, m) + src.substring(m, n));
        }
        return ret;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (toByte(hexChars[pos]) << 4 | toByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte toByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static byte[] hex2Bytes(String source) {
        byte[] sourceBytes = new byte[source.length() / 2];
        for (int i = 0; i < sourceBytes.length; i++) {
            sourceBytes[i] = (byte) Integer.parseInt(source.substring(i * 2, i * 2 + 2), 16);
        }
        return sourceBytes;
    }

    public static PrivateKey getPrivateKeyFromFile(String pfxPath, String priKeyPass) {
        InputStream priKeyStream = null;
        try {
            priKeyStream = new FileInputStream(pfxPath);
            byte[] reads = new byte[priKeyStream.available()];
            priKeyStream.read(reads);
            return getPrivateKeyByStream(reads, priKeyPass);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (priKeyStream != null) {
                try {
                    priKeyStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 根据PFX私钥字节流读取私钥
     *
     * @param pfxBytes
     * @param priKeyPass
     * @return
     */
    private static PrivateKey getPrivateKeyByStream(byte[] pfxBytes, String priKeyPass) {
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            char[] charPriKeyPass = priKeyPass.toCharArray();
            ks.load(new ByteArrayInputStream(pfxBytes), charPriKeyPass);
            Enumeration<String> aliasEnum = ks.aliases();
            String keyAlias = null;
            if (aliasEnum.hasMoreElements()) {
                keyAlias = (String) aliasEnum.nextElement();
            }
            return (PrivateKey) ks.getKey(keyAlias, charPriKeyPass);
        } catch (IOException e) {
            // 加密失败
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据私钥加密
     *
     * @param src
     * @param privateKey
     */
    private String encryptByPrivateKey(String src, PrivateKey privateKey) {

        byte[] destBytes = rsaByPrivateKey(src.getBytes(), privateKey, Cipher.ENCRYPT_MODE);

        if (destBytes == null) {
            return null;
        }
        return byte2Hex(destBytes);

    }

    /**
     * 私钥算法
     *
     * @param srcData    源字节
     * @param privateKey 私钥
     * @param mode       加密 OR 解密
     * @return
     */
    public static byte[] rsaByPrivateKey(byte[] srcData, PrivateKey privateKey, int mode) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(mode, privateKey);
            // 分段加密
            int blockSize = (mode == Cipher.ENCRYPT_MODE) ? 117 : 128;
            byte[] decryptData = null;

            for (int i = 0; i < srcData.length; i += blockSize) {
                byte[] doFinal = cipher.doFinal(subarray(srcData, i, i + blockSize));

                decryptData = addAll(decryptData, doFinal);
            }
            return decryptData;
        } catch (NoSuchAlgorithmException e) {
//			//log.error("私钥算法-不存在的解密算法:", e);
        } catch (NoSuchPaddingException e) {
            //log.error("私钥算法-无效的补位算法:", e);
        } catch (IllegalBlockSizeException e) {
            //log.error("私钥算法-无效的块大小:", e);
        } catch (BadPaddingException e) {
            //log.error("私钥算法-补位算法异常:", e);
        } catch (InvalidKeyException e) {
            //log.error("私钥算法-无效的私钥:", e);
        }
        return null;
    }

    /**
     * 将byte[] 转换成字符串
     */
    public static String byte2Hex(byte[] srcBytes) {
        StringBuilder hexRetSB = new StringBuilder();
        for (byte b : srcBytes) {
            String hexString = Integer.toHexString(0x00ff & b);
            hexRetSB.append(hexString.length() == 1 ? 0 : "").append(hexString);
        }
        return hexRetSB.toString();
    }

    /**
     * 给TOP请求签名。
     *
     * @param params 所有字符型的TOP请求参数
     * @param secret 签名密钥
     * @return 签名
     * @throws IOException
     */
    public static String signTopRequest(Map<String, String> params, String secret)
            throws IOException {
        // 第一步：把字典按Key的字母顺序排序
        Map<String, String> sortedParams = new TreeMap<String, String>(params);
        Set<Map.Entry<String, String>> paramSet = sortedParams.entrySet();

        // 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder(secret);
        for (Map.Entry<String, String> param : paramSet) {
            if (!StringUtils.isEmpty(param.getKey()) && !StringUtils.isEmpty(param.getValue())) {
                query.append(param.getKey()).append(param.getValue());
            }
        }
        query.append(secret);

        // 第三步：使用MD5加密
        MessageDigest md5 = getMd5MessageDigest();
        byte[] bytes = md5.digest(query.toString().getBytes("UTF-8"));

        // 第四步：把二进制转化为大写的十六进制
        return byte2Hex(bytes).toUpperCase();
    }

    private static MessageDigest getMd5MessageDigest() throws IOException {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e.getMessage());
        }
    }

    public static String signTopResponse(String code, String msg, String bodyStr, String appSecret) {
        try {
            String result = buildResponseToSign(code, msg, bodyStr, appSecret);
            MessageDigest md5 = getMd5MessageDigest();
            byte[] bytes = md5.digest(result.getBytes("UTF-8"));
            return byte2Hex(bytes).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String buildResponseToSign(String code, String msg, String bodyStr, String appSecret) {
        //把所有参数名和参数值串在一起, secret+code+msg+body+secret
        List<String> joinerParams = newArrayList();
        joinerParams.add(appSecret);
        joinerParams.add(code);
        joinerParams.add(msg);
        joinerParams.add(bodyStr);
        joinerParams.add(appSecret);
        return on("").skipNulls().join(joinerParams);
    }

}
