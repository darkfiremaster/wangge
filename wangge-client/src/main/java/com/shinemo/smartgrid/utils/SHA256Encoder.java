package com.shinemo.smartgrid.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;

public class SHA256Encoder {

    /**
     * 编码
     *
     * @param source source
     * @return messageDigest
     */
    public static byte[] encode(String source) {
        String sha256 = PropertiesUtil.getString("SHA256");
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance(sha256);
            messageDigest.update(source.getBytes("UTF-8"));
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * HmacSHA256消息摘要
     *
     * @param data 待做摘要处理的数据
     * @param key 密钥
     * @return 加密后字符串
     */
    public static String encodeHmacSHA256(String data, String key) {
        // 还原密钥
        SecretKey secretKey;
        try {
            secretKey = new SecretKeySpec(Hex.decodeHex(key.toCharArray()), "HmacSHA256");
            // 实例化Mac
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            // 初始化Mac
            mac.init(secretKey);
            // 执行消息摘要
            byte[] message = mac.doFinal(data.getBytes("UTF-8"));
            return Hex.encodeHexString(message, false);
        } catch (DecoderException | NoSuchAlgorithmException | InvalidKeyException | IllegalStateException
                | UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * 生成加密盐值
     *
     * @return String 盐值
     */
    public static String getSalt() {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return Hex.encodeHexString(salt).toUpperCase(Locale.US);
    }

    public static void main(String[] args) throws Exception {
        String salt = getSalt();
        System.out.println(salt);
        //String indexRuleId = HWRandomizer.getRandomGUID();
        UUID indexRuleId = UUID.randomUUID();
        System.out.println(indexRuleId);
        System.out.println(new SimpleDateFormat("yyyyMMdd").parse("20200730").getTime());
        String sss = encodeHmacSHA256("oa+1596038400000", "A2FE3AD385EACE05CD177C35B76A1661");
        System.out.println(sss);
        System.out.println(sss.equals("9E223FBAC5D73F8E6AB7595398B1BD092B6B6CF80FB5AD373A467F8E1837DAC5"));
    }

}

