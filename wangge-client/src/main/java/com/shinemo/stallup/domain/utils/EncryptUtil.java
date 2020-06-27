package com.shinemo.stallup.domain.utils;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.*;

public class EncryptUtil {

    private static final Logger log = LoggerFactory.getLogger(EncryptUtil.class);

    private static final int KEYSIZE = 128;
    private static final String CHARSET = "UTF-8";
    private static final String ALGORITHM_AES = "AES";
    private static final String AES_ECB_PKCS5 = "AES/ECB/PKCS5Padding";
    private static final String seed = "ffd40e661eb946f48fd3c759e6b8ef0b";

    public static String encrypt(String encryptString, String seed)  {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM_AES);
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(seed.getBytes(CHARSET));
            kgen.init(KEYSIZE, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, ALGORITHM_AES);
            Cipher cipher = Cipher.getInstance(AES_ECB_PKCS5);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.encodeBase64URLSafeString(cipher.doFinal(encryptString.getBytes(CHARSET)));
        }catch (Exception e) {
            log.error("[encrypt] exception,encryptString:"+encryptString+",seed:"+seed,e);
        }
        return null;
    }

    public static String decrypt(String decryptString, String seed){
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM_AES);
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(seed.getBytes(CHARSET));
            kgen.init(KEYSIZE, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, ALGORITHM_AES);
            Cipher cipher = Cipher.getInstance(AES_ECB_PKCS5);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.decodeBase64(decryptString)));
        }catch (Exception e) {
            log.error("[decrypt] exception,decryptString:"+decryptString+",seed:"+seed,e);
        }
        return null;
    }

    public static String sortString(Map<String, Object> params,boolean toLower){
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for(String key:keys){
            if(toLower){
                sb.append(key.toLowerCase());
            }else{
                sb.append(key);
            }
            sb.append("=").append(URLEncoder.encode(String.valueOf(params.get(key)))).append("&");
        }
        String str = sb.substring(0, sb.length()-1);
        return str;
    }

    public static String buildParameterString(Map<String, Object> param) {
        String paramString = sortString(param, true);
        return paramString;
    }

    public static String buildParameterString(Map<String, Object> param,Boolean toLower) {
        String paramString = sortString(param, toLower);
        return paramString;
    }

    public static void daosanjiao() {
        String mobile = "13557710513";
        int sid = 2;
        long timestamp = System.currentTimeMillis();
        Map<String, Object> formData = new HashMap<>();
        formData.put("mobile", mobile);
        formData.put("sid", sid);
        formData.put("timestamp", timestamp);

        String paramStr = buildParameterString(formData);

        //1、加密
        String encryptData = encrypt(paramStr, seed);
        System.out.println("encrypt:"+encryptData);

        //2、生成签名
        String sign = Md5Util.getMD5Str(encryptData+","+seed+","+timestamp);
        System.out.println("sign:"+sign);

        //3、解密
        String decryptData = decrypt(encryptData, seed);
        System.out.println("decrypt:"+decryptData);

        String url = "http://10.182.34.21:13000/grid-sop/index.html#/palmccLogin?";

        StringBuilder sb = new StringBuilder(url);
        sb.append("paramData=").append(encryptData)
            .append("&timestamp=").append(timestamp)
            .append("&sign=").append(sign);

        System.out.println(sb.toString());
    }

    public static void dudao1() {
        String seed = "87a4b2e679304f4bbe4da9bb935ffd9f";
        String mobile = "13617712720";
        String url = "http://10.185.18.2:8080/css_manager/appVistUrl.action?"; //html
        long timestamp = System.currentTimeMillis();
        Map<String, Object> formData = new HashMap<>();
        formData.put("mobile", mobile);
        formData.put("urlType", "bgcy_app");
        formData.put("timestamp", timestamp);
        //TODO 单一功能，需要添加resId这个参数
//        formData.put("resId", 7);

        String paramStr = buildParameterString(formData);
        //1、加密
        String encryptData = encrypt(paramStr, seed);
        System.out.println("encrypt:"+encryptData);

        //2、生成签名
        String sign = Md5Util.getMD5Str(encryptData+","+seed+","+timestamp);
        System.out.println("sign:"+sign);

        //3、解密
        String decryptData = decrypt(encryptData, seed);
        System.out.println("decrypt:"+decryptData);

        StringBuilder sb = new StringBuilder(url);
        sb.append("paramData=").append(encryptData)
            .append("&timestamp=").append(timestamp)
            .append("&sign=").append(sign);

        System.out.println(sb.toString());
    }

    public static void dudao2() {
        String seed = "87a4b2e679304f4bbe4da9bb935ffd9f";
        String mobile = "13617712720";
        String url = "http://10.185.18.2:8080/css_manager/appQueryData.action?";//接口
        long timestamp = System.currentTimeMillis();
        Map<String, Object> formData = new HashMap<>();
        formData.put("mobile", mobile);
        formData.put("urlType", "bgcy_app");
        formData.put("timestamp", timestamp);
        formData.put("methodName", "queryTaskTotal"); //接口url使用

        String paramStr = buildParameterString(formData);
        //1、加密
        String encryptData = encrypt(paramStr, seed);
        System.out.println("encrypt:"+encryptData);

        //2、生成签名
        String sign = Md5Util.getMD5Str(encryptData+","+seed+","+timestamp);
        System.out.println("sign:"+sign);

        //3、解密
        String decryptData = decrypt(encryptData, seed);
        System.out.println("decrypt:"+decryptData);

        StringBuilder sb = new StringBuilder(url);
        sb.append("paramData=").append(encryptData)
            .append("&timestamp=").append(timestamp)
            .append("&sign=").append(sign);

        System.out.println(sb.toString());
    }

    public static void dudao3() {
        String seed = "87a4b2e679304f4bbe4da9bb935ffd9f";
        String mobile = "13617712720";
        String url = "http://hwt.4kb.cn/css_manager/appVistUrl.action?"; //html
        long timestamp = System.currentTimeMillis();
        Map<String, Object> formData = new HashMap<>();
        formData.put("mobile", mobile);
        formData.put("urlType", "bgcy_app");
        formData.put("timestamp", timestamp);
        formData.put("resId",18);

        String paramStr = buildParameterString(formData);
        //1、加密
        String encryptData = encrypt(paramStr, seed);
        System.out.println("encrypt:"+encryptData);

        //2、生成签名
        String sign = Md5Util.getMD5Str(encryptData+","+seed+","+timestamp);
        System.out.println("sign:"+sign);

        //3、解密
        String decryptData = decrypt(encryptData, seed);
        System.out.println("decrypt:"+decryptData);

        StringBuilder sb = new StringBuilder(url);
        sb.append("paramData=").append(encryptData)
            .append("&timestamp=").append(timestamp)
            .append("&sign=").append(sign);

        System.out.println(sb.toString());
    }

    public static void main(String[] args) {
        dudao1();

    }

}