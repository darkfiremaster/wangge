package com.shinemo.wangge.test.web;

import cn.hutool.core.date.DateUtil;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.smartgrid.utils.SmartGridUtils;
import com.shinemo.stallup.domain.utils.SubTableUtils;
import com.shinemo.todo.vo.TodoDTO;
import com.shinemo.todo.vo.TodoThirdRequest;
import org.junit.Test;

import java.time.LocalDate;
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
        todoThirdRequest.setPostBody(request);
        todoThirdRequest.setIgnoreCheckSign(false);

        String result = GsonUtils.toJson(todoThirdRequest);
        System.out.println("result = " + result);
    }


}
