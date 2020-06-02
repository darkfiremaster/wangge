package com.shinemo.smartgrid.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinemo.stallup.domain.huawei.SearchMapRequest;
import com.shinemo.util.GsonUtil;
import com.shinemo.util.MD5Util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class SmartGridUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }


    /**
     * 生成签名
     * @param map
     * @return
     */
    public static String genSign(Map<String,Object> map) {

        try {
            return MD5Util.encodeMD5(objectMapper.writeValueAsString(map)).toLowerCase();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 拼装参数
     * @param method
     * @param o
     * @return
     */
    public static  String buildRequestParam(String method,Object o,String signkey) {
        TreeMap<String,Object> map = new TreeMap<>();
        map.put("timeStamp",System.currentTimeMillis());
        map.put("method",method);
        map.put("postBody",o);
        map.put("key",signkey);
        String sign = genSign(map);
        map.put("sign",sign);
        map.remove("key");

        return GsonUtil.toJson(map);
    }


    /**
     * 生成uuid
     * @return
     */
    public static String genUuid() {
        return UUID.randomUUID().toString().replace("-","");
    }

    public static void main(String[] args) {
        //System.out.println(genUuid());
        Map<String,Object> map = new HashMap<>();
        SearchMapRequest request = new SearchMapRequest();
        request.setCellName("广西");
        map.put("timeStamp",1588161226756L);
        map.put("postBody",request);
        map.put("method","queryCellList");
        String s = genSign(map);
        System.out.println(s);

    }

}
