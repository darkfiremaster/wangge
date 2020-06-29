package com.shinemo.wangge.test.web;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.utils.EncryptUtil;
import com.shinemo.stallup.domain.utils.Md5Util;
import com.shinemo.stallup.domain.utils.SubTableUtils;
import com.shinemo.todo.domain.TodoDO;
import com.shinemo.todo.dto.TodoRedirectDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Author shangkaihui
 * @Date 2020/6/11 10:25
 *@Desc
 */
@Slf4j
public class StaticTest {

    @Test
    public void testSubTable() {
        String tableIndexByMonth = SubTableUtils.getTableIndexByOnlyMonth(LocalDate.of(2020, 7, 1).minusDays(1));
        System.out.println("tableIndexByMonth = " + tableIndexByMonth);

        tableIndexByMonth = SubTableUtils.getTableIndexByOnlyMonth(LocalDate.of(2020, 6, 29).plusDays(2));
        System.out.println("tableIndexByMonth = " + tableIndexByMonth);
    }

    @Test
    public void parseSign() {

        String encryptData = "Q-Rf5TNa26Vpa4uoiOX-cxE6H04otjluNgGSL54ptZyPp1aIoioM9PNM3OD8P6aIHZMjdtQI-9XBOKDOEbfIBQi7_9xSx_b1dUGIQBwaCp53xhCZM2aJOLGqJj21EPab0MLHLTqtiARqPJHPVhGHkg";
        Long time = 1593308926934L;
        String seed = "87a4b2e679304f4bbe4da9bb935ffd9f";
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
        Map<String,Object> postBody = new HashMap<>();
        postBody.put("thirdId", "13588039023");
        postBody.put("thirdType", 4);
        postBody.put("operateType",1);
        postBody.put("title", "测试装维");
        postBody.put("remark", "测试装维");
        postBody.put("status", 0);
        postBody.put("label", "未返单");
        postBody.put("operatorMobile", "13588039023");
        postBody.put("operatorTime", "2020-06-24 00:00:00");
        postBody.put("startTime", "2020-07-04 00:00:00");
        //注意:参数顺序按照首字母正序排列
        Map<String,Object> map =  new TreeMap<>();
        map.put("timeStamp",System.currentTimeMillis());
        map.put("postBody",postBody);
        map.put("method","operateTodoThing");
        //key为双方约定，参数不传递
        map.put("key",key);

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
        map.put("sign",sign);

        String request = GsonUtils.toJson(map);
        System.out.println("request = " + request);
    }

    @Test
    public void sign1() throws NoSuchAlgorithmException {
        //key为双方约定的key
        String key = "7095f283-4d05-4c5d-ad7c-0bb8879ce506";
        //请求参数
        Map<String,Object> postBody = new HashMap<>();
        postBody.put("thirdId", "6c5127a3-9f1c-11ea-a34d-5254001a0735");
        postBody.put("thirdType", 1);
        postBody.put("operateType",1);
        postBody.put("title", "申诉测试001");
        postBody.put("remark", "申诉测试001");
        postBody.put("status", 0);
        postBody.put("label", "处理中");
        postBody.put("operatorMobile", "15800000002");
        postBody.put("operatorTime", "2020-06-18 17:30:00");
        postBody.put("startTime", "2020-06-18 17:30:00");
        //注意:参数顺序按照首字母正序排列
        Map<String,Object> map =  new TreeMap<>();
        map.put("timeStamp",1592989502350L);
        map.put("postBody",postBody);
        map.put("method","operateTodoThing");
        //key为双方约定，参数不传递
        map.put("key",key);

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
        map.put("sign",sign);

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
}
