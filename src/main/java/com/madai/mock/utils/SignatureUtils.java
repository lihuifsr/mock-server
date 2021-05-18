package com.madai.mock.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SignatureUtils {

    private static final String RSA = "RSA";

    private static final Charset DEFAULT_CHARSET = Charset.forName("utf-8");

    public static KeyPair generateRsaKeyPair(int keySize)
            throws NoSuchAlgorithmException {

        KeyPairGenerator keyPairGen = null;
        keyPairGen = KeyPairGenerator.getInstance(RSA);

        keyPairGen.initialize(keySize, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();

        return keyPair;
    }

    public static PublicKey getRsaX509PublicKey(byte[] encodedKey)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
    }

    public static PrivateKey getRsaPkcs8PrivateKey(byte[] encodedKey)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
    }

    public static String sign(SignatureAlgorithm algorithm, String privateKeyStr, String plainText)
            throws GeneralSecurityException {
        PrivateKey privateKey = SignatureUtils.getRsaPkcs8PrivateKey(Base64.decodeBase64(privateKeyStr));
        byte[] sign = sign(algorithm, privateKey, plainText);
        return Base64.encodeBase64String(sign);
    }

    public static byte[] sign(SignatureAlgorithm algorithm, PrivateKey privateKey,
                              String data)
            throws GeneralSecurityException {
        return sign(algorithm, privateKey, data, DEFAULT_CHARSET);
    }

    public static byte[] sign(SignatureAlgorithm algorithm, PrivateKey privateKey,
                              String data, String charset)
            throws GeneralSecurityException {
        return sign(algorithm, privateKey, data.getBytes(Charset.forName(charset)));
    }

    public static byte[] sign(SignatureAlgorithm algorithm, PrivateKey privateKey,
                              String data, Charset charset)
            throws GeneralSecurityException {
        return sign(algorithm, privateKey, data.getBytes(charset));
    }

    public static byte[] sign(SignatureAlgorithm algorithm, PrivateKey privateKey,
                              byte[] data) throws GeneralSecurityException {
        ByteArrayInputStream input = new ByteArrayInputStream(data);
        try {
            return sign(algorithm, privateKey, input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    public static byte[] sign(SignatureAlgorithm algorithm, PrivateKey privateKey,
                              InputStream data) throws GeneralSecurityException,
            IOException {

        Signature signature = Signature.getInstance(algorithm
                .getSignAlgorithm());

        signature.initSign(privateKey);
        doUpdate(signature, data);

        return signature.sign();
    }

    public static byte[] sign(SignatureAlgorithm algorithm, String privateKeyStr,
                              InputStream data) throws GeneralSecurityException,
            IOException {
        PrivateKey privateKey = SignatureUtils.getRsaPkcs8PrivateKey(Base64.decodeBase64(privateKeyStr));

        Signature signature = Signature.getInstance(algorithm
                .getSignAlgorithm());

        signature.initSign(privateKey);
        doUpdate(signature, data);

        return signature.sign();
    }

    public static boolean verify(SignatureAlgorithm algorithm, String publicKeyStr, String plainText, String sign)
            throws GeneralSecurityException, UnsupportedEncodingException {
        PublicKey publicKey = SignatureUtils.getRsaX509PublicKey(Base64.decodeBase64(publicKeyStr));
        byte[] bytes = Base64.decodeBase64(sign);
        return verify(algorithm, publicKey, plainText, bytes);
    }

    public static boolean verify(SignatureAlgorithm algorithm, PublicKey publicKey,
                                 String data, byte[] sign)
            throws GeneralSecurityException, UnsupportedEncodingException {
        return verify(algorithm, publicKey,
                data, DEFAULT_CHARSET, sign);
    }

    public static boolean verify(SignatureAlgorithm algorithm, PublicKey publicKey,
                                 String data, String charset, byte[] sign)
            throws GeneralSecurityException, UnsupportedEncodingException {
        return verify(algorithm, publicKey,
                data.getBytes(Charset.forName(charset)), sign);
    }

    public static boolean verify(SignatureAlgorithm algorithm, PublicKey publicKey,
                                 String data, Charset charset, byte[] sign)
            throws GeneralSecurityException {
        return verify(algorithm, publicKey,
                data.getBytes(charset), sign);
    }

    public static boolean verify(SignatureAlgorithm algorithm, PublicKey publicKey, byte[] data,
                                 byte[] sign) throws GeneralSecurityException {
        ByteArrayInputStream input = new ByteArrayInputStream(data);
        try {
            return verify(algorithm, publicKey, input, sign);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    public static boolean verify(SignatureAlgorithm algorithm, PublicKey publicKey,
                                 InputStream input, byte[] sign)
            throws GeneralSecurityException, IOException {

        Signature signature = Signature.getInstance(algorithm.getSignAlgorithm());

        signature.initVerify(publicKey);
        doUpdate(signature, input);

        return signature.verify(sign);
    }

    private static void doUpdate(Signature signature, InputStream input)
            throws IOException, SignatureException {

        byte[] buf = new byte[4096];

        int c = 0;
        do {
            c = input.read(buf);

            if (c > 0) {
                signature.update(buf, 0, c);
            }
        } while (c != -1);
    }

}
