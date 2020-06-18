package com.shinemo.wangge.test.web;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.smartgrid.utils.SmartGridUtils;
import com.shinemo.stallup.domain.utils.SubTableUtils;
import com.shinemo.todo.vo.TodoDTO;
import com.shinemo.todo.vo.TodoThirdRequest;
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
 * @Desc
 */
public class StaticTest {

    @Test
    public void testSubTable() {
        String tableIndexByMonth = SubTableUtils.getTableIndexByOnlyMonth(LocalDate.of(2020, 7, 1).minusDays(1));
        System.out.println("tableIndexByMonth = " + tableIndexByMonth);

        tableIndexByMonth = SubTableUtils.getTableIndexByOnlyMonth(LocalDate.of(2020, 6, 29).plusDays(2));
        System.out.println("tableIndexByMonth = " + tableIndexByMonth);
    }

    @Test
    public void sign() throws NoSuchAlgorithmException {
        //key为双方约定的key
        String key = "7095f283-4d05-4c5d-ad7c-0bb8879ce506";
        //请求参数
        Map<String,Object> postBody = new HashMap<>();
        postBody.put("operateType", 1);
        postBody.put("status", 0);
        postBody.put("thirdId", "xxx");
        postBody.put("thirdType", 1);
        postBody.put("title", "测试");
        postBody.put("remark", "测试");
        postBody.put("label", "待开始");
        postBody.put("operatorMobile", "13588039023");
        postBody.put("operatorTime", "2020-06-17 12:00:00");
        postBody.put("startTime", "2020-06-15 12:00:00");
        //注意:参数顺序按照首字母正序排列
        Map<String,Object> map =  new TreeMap<>();
        map.put("timeStamp",System.currentTimeMillis());
        map.put("postBody",postBody);
        map.put("key",key);
        map.put("method","operateTodoThing");

        //将参数转化为json字符串进行md5加密
        ObjectMapper objectMapper = new ObjectMapper();
        String source = null;
        try {
            source = objectMapper.writeValueAsString(map);
            System.out.println("source = " + source);
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
    public void generateTodoRequest() {
        //生成代办事项的请求参数
        Map<String,Object> map = new TreeMap<>();
        TodoDTO request = new TodoDTO();
        request.setThirdId("1");
        request.setThirdType(1);
        request.setOperateType(2);
        request.setTitle("测试");
        request.setRemark("测试");
        request.setStatus(0);
        request.setLabel("待开始");
        request.setOperatorMobile("13588039023");
        request.setOperatorTime(DateUtil.now());
        request.setStartTime(DateUtil.now());

        long l = System.currentTimeMillis();
        map.put("timeStamp",l);
        map.put("postBody",request);
        map.put("method","operateTodo");
        map.put("key", "34b18faa-0424-41ad-b73b-80fc02d4be55");
        String sign = SmartGridUtils.genSign(map);
        System.out.println(sign);


        TodoThirdRequest todoThirdRequest = new TodoThirdRequest();
        todoThirdRequest.setTimeStamp((Long) map.get("timeStamp"));
        todoThirdRequest.setMethod((String) map.get("method"));
        todoThirdRequest.setSign(sign);
        //todoThirdRequest.setPostBody(request);
        todoThirdRequest.setIgnoreCheckSign(false);

        String result = GsonUtils.toJson(todoThirdRequest);
        System.out.println("result = " + result);
    }


}
