package com.shinemo.wangge.test.web;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.utils.EncryptUtil;
import com.shinemo.stallup.domain.utils.Md5Util;
import com.shinemo.stallup.domain.utils.SubTableUtils;
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

        String encryptData = "Q81wxKR1TghpKNmUKflyyyPjsN8fNWba4HSA54E6BWt3EPTYQOrDP8txaxgvc2Aee7rjgkvff4zcDXuy_5MIt31WVAW6xdb3RWAzsY0cDBWpyXK-7BfZEvOFHBAW5aN6ifLrJGiSLz4-F_jxpEQu5Q";
        Long time = 1592996798155L;
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
        String key = "34b18faa-0424-41ad-b73b-80fc02d4be55";
        //请求参数
        Map<String,Object> postBody = new HashMap<>();
        postBody.put("thirdId", "DK202006240707281");
        postBody.put("thirdType", 5);
        postBody.put("operateType",1);
        postBody.put("title", "中直村委吴屋村端口数预警");
        postBody.put("remark", "您负责的@中直村委吴屋村-@，在20200622的@宽带端口剩余数为22个-@，@端口可利用率为55%-@，请及时组织人员开展实地摆台营销，小区相关信息如下，营销完成后，请填写营销日志并进行返单。");
        postBody.put("status", 0);
        postBody.put("label", "未返单");
        postBody.put("operatorMobile", "13557710513");
        postBody.put("operatorTime", "2020-06-24 00:00:00");
        postBody.put("startTime", "2020-07-04 00:00:00");
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
}
