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
        todoDTO.setThirdType(1);
        todoDTO.setOperateType(1);
        todoDTO.setTitle("测试");
        todoDTO.setRemark("备注");
        todoDTO.setStatus(0);
        todoDTO.setLabel("待开始");
        todoDTO.setOperatorMobile("13588039023");
        todoDTO.setOperatorTime(DateUtil.format(new Date()));
        todoDTO.setStartTime(DateUtil.format(new Date()));
        todoThirdRequest.setPostBody(todoDTO);

        Map<String, Object> map = new TreeMap<>();
        map.put("timeStamp", time);
        map.put("postBody", todoDTO);
        map.put("method", method);
        map.put("key", "34b18faa-0424-41ad-b73b-80fc02d4be55");
        String sign = SmartGridUtils.genSign(map);
        System.out.println(sign);
        todoThirdRequest.setSign(sign);

        String result = GsonUtils.toJson(todoThirdRequest);
        System.out.println("result = " + result);

    }
}
