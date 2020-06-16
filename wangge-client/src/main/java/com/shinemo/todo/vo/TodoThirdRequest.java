package com.shinemo.todo.vo;

import com.shinemo.client.util.DateUtil;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.smartgrid.utils.SmartGridUtils;
import lombok.Data;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 11:43
 * @Desc
 */
@Data
public class TodoThirdRequest {

    private Long timeStamp;

    private String method;

    private String sign;

    private TodoDTO postBody;

    private Boolean ignoreCheckSign = false;

    public static void main(String[] args) {

        long time = System.currentTimeMillis();
        String method = "operateTodoThing";
        TodoThirdRequest todoThirdRequest = new TodoThirdRequest();
        todoThirdRequest.setTimeStamp(time);
        todoThirdRequest.setMethod(method);

        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setThirdId("123");
        todoDTO.setThirdType(7);
        todoDTO.setOperateType(1);
        todoDTO.setTitle("测试");
        todoDTO.setRemark("备注");
        todoDTO.setStatus(0);
        todoDTO.setLabel("待开始");
        todoDTO.setOperatorMobile("13617712720");
        todoDTO.setOperatorTime(DateUtil.format(new Date()));
        todoDTO.setStartTime(DateUtil.format(new Date()));
        todoThirdRequest.setPostBody(todoDTO);

        Map<String, Object> map = new TreeMap<>();
        map.put("timeStamp", time);
        map.put("postBody", todoDTO);
        map.put("method", method);
        //注意:这里要换成相应thirdType的公司的秘钥,不然的话,签名会报错
        map.put("key", "4313e55f-8234-4a0c-8cc6-6ed084a61df9");
        String sign = SmartGridUtils.genSign(map);
        System.out.println(sign);
        todoThirdRequest.setSign(sign);

        String result = GsonUtils.toJson(todoThirdRequest);
        System.out.println("result = " + result);

    }
}
