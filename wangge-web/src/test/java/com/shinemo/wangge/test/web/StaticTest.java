package com.shinemo.wangge.test.web;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.utils.EncryptUtil;
import com.shinemo.stallup.domain.utils.Md5Util;
import com.shinemo.todo.domain.TodoDO;
import com.shinemo.todo.dto.TodoRedirectDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Author shangkaihui
 * @Date 2020/6/11 10:25
 * @Desc
 */
@Slf4j
public class StaticTest {

    @Test
    public void testSubTable() {
        //String tableIndexByMonth = SubTableUtils.getTableIndexByOnlyMonth(LocalDate.of(2020, 7, 1).minusDays(1));
        //System.out.println("tableIndexByMonth = " + tableIndexByMonth);
        //
        //tableIndexByMonth = SubTableUtils.getTableIndexByOnlyMonth(LocalDate.of(2020, 6, 29).plusDays(2));
        //System.out.println("tableIndexByMonth = " + tableIndexByMonth);

    }

    @Test
    public void testDecode() {
        Map<String, String> stringStringMap = HttpUtil.decodeParamMap("communitylocation=108.245974%2C22.835926&redirectpage=1&address=%E5%8D%97%E5%AE%81%E8%A5%BF%E4%B9%A1%E5%A1%98%E5%8C%BA%E8%A5%BF%E4%B9%A1%E5%A1%98%E5%8C%BA%E5%9F%8E%E5%8C%BA%E5%A4%A7%E5%AD%A6%E4%B8%9C%E8%B7%AF%E5%B9%BF%E8%A5%BF%E7%BB%8F%E6%B5%8E%E7%AE%A1%E7%90%86%E5%B9%B2%E9%83%A8%E5%AD%A6%E9%99%A2%28%E5%B0%8F%E5%BE%AE%29105%E5%8F%B7&thirdid=DK202006240707281&communityname=%E5%B9%BF%E8%A5%BF%E7%BB%8F%E6%B5%8E%E7%AE%A1%E7%90%86%E5%B9%B2%E9%83%A8%E5%AD%A6%E9%99%A2%28%E5%B0%8F%E5%BE%AE%29&mobile=13588039023&communityaddress=%E5%8D%97%E5%AE%81%E8%A5%BF%E4%B9%A1%E5%A1%98%E5%8C%BA%E8%A5%BF%E4%B9%A1%E5%A1%98%E5%8C%BA%E5%9F%8E%E5%8C%BA%E5%A4%A7%E5%AD%A6%E4%B8%9C%E8%B7%AF%E5%B9%BF%E8%A5%BF%E7%BB%8F%E6%B5%8E%E7%AE%A1%E7%90%86%E5%B9%B2%E9%83%A8%E5%AD%A6%E9%99%A2%28%E5%B0%8F%E5%BE%AE%29105%E5%8F%B7&thirdtype=5&communityid=BUSINESS_COMMUNITY-ff8080815fa666f0015fc7ca4dd633bd&title=163%E5%8F%B7%E6%9C%BA%E8%80%95%E9%98%9F%E5%B0%8F%E5%8C%BA%EF%BC%88%E9%AB%98%E4%BB%B7%E4%BD%8E%E5%8D%A0%EF%BC%89%E5%AE%BD%E5%B8%A6%E7%9B%AE%E6%A0%87%E7%94%A8%E6%88%B7%E9%A2%84%E8%AD%A6&timestamp=1599120604573&token=3cc201d4-f8b7-44ca-8852-2c5855cee6d9","UTF-8");
        System.out.println("stringListMap = " + stringStringMap);
    }

    @Test
    public void getYaxinSign() {
        String seed = "ffd40e661eb946f48fd3c759e6b8ef0b";
        long timestamp = System.currentTimeMillis();
        Map<String, Object> formData = new HashMap<>();
        formData.put("mobile", "13588039023");
        formData.put("busicode", "DDZP");
        String paramData = EncryptUtil.buildParameterString(formData, Boolean.FALSE);
        String encryptData = EncryptUtil.encrypt(paramData, seed);

        //2、生成签名
        String sign = Md5Util.getMD5Str(encryptData + "," + seed + "," + timestamp);
        String url = "http://xx?";
        StringBuilder sb = new StringBuilder(url);
        url = sb.append("paramData=").append(encryptData)
                .append("&timestamp=").append(timestamp)
                .append("&sign=").append(sign).toString();

        System.out.println("url = " + url);
    }

    @Test
    public void parseSign() {

        String encryptData = "Hi2XXYKx8YJhlUKChcXLBCj9twnOTqs6B5c5n_zn8hfTDJVYqfxpKZfCMswF7q0lYZCnKfnlmqe-14gM4w-BKQ";

        Long time = 1593308926934L;
        String seed = "a8537aaefdd489789c07ae8a9760203";
        String sign = Md5Util.getMD5Str(encryptData + "," + seed + "," + time);
        System.out.println("sign = " + sign);

        String decryptData = EncryptUtil.decrypt(encryptData, seed);
        log.info("[redirectPage]解密后的参数decryptData:{}", decryptData);
        Map<String, String> map = HttpUtil.decodeParamMap(decryptData, CharsetUtil.charset("UTF-8"));
        TodoRedirectDetailDTO todoRedirectDetailDTO = BeanUtil.mapToBean(map, TodoRedirectDetailDTO.class, false);
        log.info("[redirectPage]map转化为bean后的的参数todoRedirectDetailDTO:{}", todoRedirectDetailDTO);


        //String decode = URLUtil.decode("%250A");
        //System.out.println("decode = " + decode);
        //String encode = URLUtil.encode("%250A", "utf-8");
        //System.out.println("encode = " + encode);
    }

    @Test
    public void sign() throws NoSuchAlgorithmException {
        //key为双方约定的key
        String key = "e0d349cb-e98f-45e5-bf98-3a89d46d0671";
        //请求参数
        Map<String, Object> postBody = new HashMap<>();
        postBody.put("thirdId", "C-771-20200602-680001");
        postBody.put("thirdType", 4);
        postBody.put("operateType", 1);
        postBody.put("title", "家庭宽带-1000M-FTTH-终端更换");
        postBody.put("remark", "2020-06-05 20:00:00.0;2020-06-24 08:00:00 - 2020-06-24 12:00:00;苏泳琮;广西南宁武鸣县城厢镇灵源路灵水三区3号民房");
        postBody.put("status", 0);
        postBody.put("label", null);
        postBody.put("operatorMobile", "18277406772");
        postBody.put("operatorTime", null);
        //注意:参数顺序按照首字母正序排列
        Map<String, Object> map = new TreeMap<>();
        map.put("timeStamp", 1593427008995L);
        map.put("postBody", postBody);
        map.put("method", "operateTodoThing");
        //key为双方约定，参数不传递
        map.put("key", key);

        //将参数转化为json字符串进行md5加密
        ObjectMapper objectMapper = new ObjectMapper();
        String source = null;
        try {
            source = objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] bytes = MessageDigest.getInstance("MD5").digest(source.getBytes(Charset.forName("UTF-8")));
        char[] result = new char[bytes.length * 2];
        int c = 0;
        char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        for (byte b : bytes) {
            result[c++] = HEX_DIGITS[(b >> 4) & 0xf];
            result[c++] = HEX_DIGITS[b & 0xf];
        }

        //将加密后的字符串转化为小写
        String sign = new String(result).toLowerCase();
        map.remove("key");
        map.put("sign", sign);

        String request = GsonUtils.toJson(map);
        System.out.println("request = " + request);
    }

    @Test
    public void ts() {
        String seed = IdUtil.simpleUUID();
        System.out.println("seed = " + seed);
        int length = seed.length();
        System.out.println("length = " + length);
        //String encrypt = AESUtil.encrypt("15000001171", "71a25f582a266454");
        //System.out.println("encrypt = " + encrypt);
    }

    @Test
    public void sign1() throws NoSuchAlgorithmException {
        //key为双方约定的key
        String key = "7095f283-4d05-4c5d-ad7c-0bb8879ce506";
        //请求参数
        Map<String, Object> postBody = new HashMap<>();
        postBody.put("thirdId", "6c5127a3-9f1c-11ea-a34d-5254001a0735");
        postBody.put("thirdType", 1);
        postBody.put("operateType", 1);
        postBody.put("title", "申诉测试001");
        postBody.put("remark", "申诉测试001");
        postBody.put("status", 0);
        postBody.put("label", "处理中");
        postBody.put("operatorMobile", "15800000002");
        postBody.put("operatorTime", "2020-06-18 17:30:00");
        postBody.put("startTime", "2020-06-18 17:30:00");
        //注意:参数顺序按照首字母正序排列
        Map<String, Object> map = new TreeMap<>();
        map.put("timeStamp", 1592989502350L);
        map.put("postBody", postBody);
        map.put("method", "operateTodoThing");
        //key为双方约定，参数不传递
        map.put("key", key);

        //将参数转化为json字符串进行md5加密
        ObjectMapper objectMapper = new ObjectMapper();
        String source = null;
        try {
            source = objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] bytes = MessageDigest.getInstance("MD5").digest(source.getBytes(Charset.forName("UTF-8")));
        char[] result = new char[bytes.length * 2];
        int c = 0;
        char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        for (byte b : bytes) {
            result[c++] = HEX_DIGITS[(b >> 4) & 0xf];
            result[c++] = HEX_DIGITS[b & 0xf];
        }

        //将加密后的字符串转化为小写
        String sign = new String(result).toLowerCase();
        map.remove("key");
        map.put("sign", sign);

        String request = GsonUtils.toJson(map);
        System.out.println("request = " + request);

    }

    @Test
    public void t() {
        String json = "{\n" +
                "            \"id\": 32,\n" +
                "            \"gmtCreate\": 1592991344000,\n" +
                "            \"gmtModified\": 1593230403000,\n" +
                "            \"thirdId\": \"DK202006240707281\",\n" +
                "            \"thirdType\": 5,\n" +
                "            \"title\": \"中直村委吴屋村端口数预警\",\n" +
                "            \"remark\": \"您负责的@中直村委吴屋村-@，在20200622的@宽带端口剩余数为22个-@，@端口可利用率为55%-@，请及时组织人员开展实地摆台营销，小区相关信息如下，营销完成后，请填写营销日志并进行返单。\",\n" +
                "            \"status\": 0,\n" +
                "            \"label\": \"未返单\",\n" +
                "            \"operatorTime\": 1592928000000,\n" +
                "            \"operatorMobile\": \"13557710513\",\n" +
                "            \"startTime\": 1593792000000\n" +
                "        }";
        TodoDO todoDO = GsonUtils.fromGson2Obj(json, TodoDO.class);
        System.out.println("todoDO = " + todoDO);

    }


    @Test
    public void parseCookie() {
        String s = "eyJncmlkR\n" +
                "GV0YWlsIjp7ImlkIjoiNzcxX0EyMTA3XzMwIiwibmFtZSI6IuS4nOWMuuaxn-WNl-e9keagvCIsImNpdHlOYW1lIjoi5Y2X5a6BIiwiY2l0eUNvZGUiOiI3NzEiLCJjb3VudHlOYW1lIjoi5Lic5Yy6IiwiY291bnR5Q29kZSI6IkEyMTA3Iiwicm9sZUxpc3QiOlt7ImlkIjoiMSIsIm5hbWUiOiLnvZHmoLzplb8ifV0sIn\n" +
                "R5cGUiOjB9fQ";
        String result = Base64.decodeStr(s);
        System.out.println("result = " + result);
    }


    @Test
    public void getZhuangyiUrl() {
        String seed = "8b0f0fdfa4504925b963aa9df415de71";
        String domain = "http://211.138.252.146:27001";
        String path = "/hello-mui/ossIntegratedSchedulingWeb/noticeboard/index.html";
        long timestamp = System.currentTimeMillis();
        Map<String, Object> formData = new HashMap<>();
        formData.put("mobileTel", "18776892034");
        formData.put("gridName", "智慧网格");
        formData.put("areaName", "南宁");
        formData.put("countyName", "县级");
        //String roleId = SmartGridContext.getSelectGridUserRoleDetail().getRoleList().get(0).getId();
        ////将我们的角色转化为装移那边的角色名称
        //String roleName = "";
        //if (roleId.equals(SmartGridRoleEnum.GRID_CAPTAIN.getId())
        //        || roleId.equals(SmartGridRoleEnum.GRID_MANAGER.getId())
        //        || roleId.equals(SmartGridRoleEnum.BUSINESS_HALL.getId())) {
        //    roleName = "网格长";
        //} else if (roleId.equals(SmartGridRoleEnum.DECORATOR.getId())) {
        //    roleName = "一线装维";
        //} else {
        //    throw new ApiException("角色错误");
        //}
        formData.put("roleName", "一线装维");
        formData.put("timestamp", timestamp);
        String paramData = EncryptUtil.buildParameterString(formData, Boolean.FALSE);
        try {
            log.info("[getZhuangyiDataBroadUrl] 加密前参数paramData:{}", URLDecoder.decode(paramData, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //1、加密
        String encryptData = EncryptUtil.encrypt(paramData, seed);

        //2、生成签名
        String sign = Md5Util.getMD5Str(encryptData + "," + seed + "," + timestamp);

        String url = domain + path + "?";
        StringBuilder sb = new StringBuilder(url);
        url = sb.append("paramData=").append(encryptData)
                .append("&timestamp=").append(timestamp)
                .append("&sign=").append(sign).toString();


        log.info("[getZhuangyiDataBroadUrl] 生成装移数据看板跳转url:{}", url);
    }

    @Test
    public void testObj() {
    }
}
