package com.shinemo.wangge.test.web;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.utils.EncryptUtil;
import com.shinemo.stallup.domain.utils.Md5Util;
import com.shinemo.todo.domain.TodoDO;
import com.shinemo.todo.domain.TodoLogDO;
import com.shinemo.todo.enums.ThirdTodoTypeEnum;
import com.shinemo.todo.query.TodoLogQuery;
import com.shinemo.todo.query.TodoQuery;
import com.shinemo.todo.query.TodoUrlQuery;
import com.shinemo.wangge.core.service.todo.TodoLogService;
import com.shinemo.wangge.core.service.todo.TodoService;
import com.shinemo.wangge.core.service.todo.impl.TodoRedirectUrlServiceImpl;
import com.shinemo.wangge.dal.mapper.ThirdTodoMapper;
import com.shinemo.wangge.web.MainApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Author shangkaihui
 * @Date 2020/6/17 13:58
 * @Desc
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainApplication.class)
@Slf4j
public class TodoTest {

    @Resource
    private TodoLogService todoLogService;

    @Resource
    private TodoRedirectUrlServiceImpl todoRedirectUrlService;

    @Resource
    private TodoService todoService;



    @Test
    public void testGetRedirectUrl() {
        String seed = "ffd40e661eb946f48fd3c759e6b8ef0b";
        String mobile = "13588039023";
        String token = "xxxx";
        String thirdId = "6c5127a3-9f1c-11ea-a34d-5254001a0735";
        Integer thirdType=1;
        Integer redirectPage=1;
        long timestamp = System.currentTimeMillis();
        Map<String, Object> formData = new HashMap<>();
        formData.put("mobile", mobile);
        formData.put("thirdId", thirdId);
        formData.put("timestamp", timestamp);
        formData.put("token", token);
        formData.put("thirdType", thirdType);
        formData.put("redirectPage", redirectPage);
        String paramStr = EncryptUtil.buildParameterString(formData);
        //1、加密
        String encryptData = EncryptUtil.encrypt(paramStr, seed);
        System.out.println("encrypt:"+encryptData);
        //2、生成签名
        String sign = Md5Util.getMD5Str(encryptData+","+seed+","+timestamp);
        System.out.println("sign:"+sign);

        String url = "https://developer.e.uban360.com/cmgr-gx-smartgrid/todo/redirectPage?";
        StringBuilder sb = new StringBuilder(url);
        url = sb.append("paramData=").append(encryptData)
                .append("&timestamp=").append(timestamp)
                .append("&sign=").append(sign)
                .append("&thirdType=").append(thirdType)
                .toString();

        System.out.println("url = " + url);
    }

    @Test
    public void testGetZhuangYiAppUrl() {
        TodoUrlQuery todoUrlQuery = new TodoUrlQuery();
        todoUrlQuery.setThirdType(ThirdTodoTypeEnum.ZHUANG_YI_ORDER.getId());
        todoUrlQuery.setOperatorMobile("13617712720");
        todoUrlQuery.setThirdId("1-15890-13617712720");
        todoRedirectUrlService.getRedirectUrl(todoUrlQuery);
    }

    @Test
    public void testGetChannelVisitUrl() {
        TodoUrlQuery todoUrlQuery = new TodoUrlQuery();
        todoUrlQuery.setThirdType(ThirdTodoTypeEnum.CHANNEL_VISIT.getId());
        todoUrlQuery.setOperatorMobile("13617712720");
        todoUrlQuery.setThirdId("1-15890-13617712720");
        todoRedirectUrlService.getRedirectUrl(todoUrlQuery);
    }

    @Test
    public void testGetYujingUrl() {
        TodoUrlQuery todoUrlQuery = new TodoUrlQuery();
        todoUrlQuery.setThirdType(5);
        todoUrlQuery.setOperatorMobile("13617712720");
        todoUrlQuery.setThirdId("DK202006240707281");
        todoRedirectUrlService.getRedirectUrl(todoUrlQuery);
    }

    @Test
    public void testGetDaosanjiaoUrl() {
        TodoUrlQuery todoUrlQuery = new TodoUrlQuery();
        todoUrlQuery.setThirdType(ThirdTodoTypeEnum.DAO_SAN_JIAO_ORDER.getId());
        todoUrlQuery.setThirdId("6c5127a3-9f1c-11ea-a34d-5254001a0735");
        todoUrlQuery.setOperatorMobile("17380571067");
        ApiResult<String> result = todoRedirectUrlService.getRedirectUrl(todoUrlQuery);
        System.out.println("result = " + result);
    }

    @Test
    public void testOperateTodoThing() {
        TreeMap<String, Object> treeMap = new TreeMap<>();
        treeMap.put("key","34b18faa-0424-41ad-b73b-80fc02d4be55");
        treeMap.put("method","operateTodoThing");
        treeMap.put("postBody","34b18faa-0424-41ad-b73b-80fc02d4be55");
        treeMap.put("sign","34b18faa-0424-41ad-b73b-80fc02d4be55");

    }

    @Test
    public void testGetTodoLogList() {
        TodoLogQuery todoLogQuery = new TodoLogQuery();

        //todoLogQuery.setId();
        todoLogQuery.setCompany("讯盟");
        todoLogQuery.setThirdType(8);
        todoLogQuery.setThirdId(String.valueOf(294));
        todoLogQuery.setOperatorMobile("13107701611");
        todoLogQuery.setStatus(1);
        todoLogQuery.setStartTime("2020-06-01 00:00:00");
        todoLogQuery.setEndTime("2020-06-30 23:59:59");
        todoLogQuery.setCurrentPage(1);
        todoLogQuery.setPageSize(100);
        todoLogQuery.setOrderBy("id desc");
        String request = GsonUtils.toJson(todoLogQuery);
        System.out.println("request = " + request);
        ApiResult<List<TodoLogDO>> result = todoLogService.getTodoLogList(todoLogQuery);
        System.out.println("result = " + result);
    }

    @Test
    public void testGetTodoList() {
        TodoQuery todoQuery = new TodoQuery();
        todoQuery.setThirdId("294");
        todoQuery.setThirdType(8);
        todoQuery.setTitle("哈哈");
        todoQuery.setMobile("13107701611");
        todoQuery.setStatus(1);
        todoQuery.setStartTime(DateUtil.parseDateTime("2020-06-01 00:00:00"));
        todoQuery.setEndTime(DateUtil.parseDateTime("2020-06-30 23:59:59"));
        todoQuery.setCurrentPage(1);
        todoQuery.setPageSize(100);
        String request = GsonUtils.toJson(todoQuery);
        System.out.println("request = " + request);
        ApiResult<List<TodoDO>> result = todoLogService.getTodoList(todoQuery);
        System.out.println("result = " + result);
    }



    @Resource
    private ThirdTodoMapper thirdTodoMapper;
    @Test
    public void insertTodo() {
        String json = " {\n" +
                "            \"id\": 38,\n" +
                "            \"gmtCreate\": 1593412724000,\n" +
                "            \"gmtModified\": 1593412724000,\n" +
                "            \"thirdId\": \"13588039023\",\n" +
                "            \"thirdType\": 4,\n" +
                "            \"title\": \"测试装维\",\n" +
                "            \"remark\": \"测试装维\",\n" +
                "            \"status\": 0,\n" +
                "            \"label\": \"未返单\",\n" +
                "            \"operatorTime\": 1592928000000,\n" +
                "            \"operatorMobile\": \"13588039023\",\n" +
                "            \"startTime\": 1593792000000\n" +
                "        }";
        TodoDO todoDO = GsonUtils.fromGson2Obj(json, TodoDO.class);
        System.out.println("todoDO = " + todoDO);
        thirdTodoMapper.insert(todoDO);
    }

    @Test
    public void abc() {
        String response = HttpUtil.get("https://api-gx.uban360.com/cmgr-test//todo/thing/log/getTodoList?thirdType=4&currentPage=1&pageSize=100");
        System.out.println("response = " + response);

    }

    @Test
    public void insertTodoList() {
        String json = "[\n" +
                "        {\n" +
                "            \"id\": 58,\n" +
                "            \"gmtCreate\": 1594116776000,\n" +
                "            \"gmtModified\": 1594116777000,\n" +
                "            \"thirdId\": \"C-771-20200707-188515\",\n" +
                "            \"thirdType\": 4,\n" +
                "            \"title\": \"魔百盒-null-null-新装\",\n" +
                "            \"remark\": \"工单时限：2020-07-08 20:00:00.0\\n预约时间：2020-07-07 18:00:00 - 2020-07-07 19:00:00\\n装机地址：广西南宁良庆区良庆区城区宋厢路18号盛科城小区A1栋1单元17楼竖井\",\n" +
                "            \"status\": 0,\n" +
                "            \"label\": \"待施工\",\n" +
                "            \"operatorTime\": 1594116777000,\n" +
                "            \"operatorMobile\": \"18776892034\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 57,\n" +
                "            \"gmtCreate\": 1594116776000,\n" +
                "            \"gmtModified\": 1594116776000,\n" +
                "            \"thirdId\": \"C-771-20200707-188485\",\n" +
                "            \"thirdType\": 4,\n" +
                "            \"title\": \"家庭宽带-50M-FTTH-新装-正装机\",\n" +
                "            \"remark\": \"工单时限：2020-07-09 12:00:00.0\\n预约时间： - \\n装机地址：广西南宁武鸣县城厢镇定罗路农机厂宿舍(B改H)B栋2单元3楼301\",\n" +
                "            \"status\": 0,\n" +
                "            \"label\": \"待预约\",\n" +
                "            \"operatorTime\": 1594116776000,\n" +
                "            \"operatorMobile\": \"18776892034\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 56,\n" +
                "            \"gmtCreate\": 1594116776000,\n" +
                "            \"gmtModified\": 1594117870000,\n" +
                "            \"thirdId\": \"C-771-20200707-188488\",\n" +
                "            \"thirdType\": 4,\n" +
                "            \"title\": \"家庭宽带-100M-FTTB-新装-正装机\",\n" +
                "            \"remark\": \"工单时限：2020-07-09 12:00:00.0\\n预约时间： - \\n装机地址：广西南宁青秀区青秀区城区仙葫大道121号观澜溪谷小区(B改H含小微)13栋1单元1楼24号商铺\",\n" +
                "            \"status\": 0,\n" +
                "            \"label\": \"待预约\",\n" +
                "            \"operatorTime\": 1594117870000,\n" +
                "            \"operatorMobile\": \"18776892034\"\n" +
                "        }\n" +
                "    ]";
        List<TodoDO> todoDOList = GsonUtils.fromJsonToList(json, TodoDO[].class);
        for (TodoDO todoDO : todoDOList) {
            TodoQuery todoQuery = new TodoQuery();
            todoQuery.setThirdId(todoDO.getThirdId());
            todoQuery.setThirdType(todoDO.getThirdType());
            todoQuery.setMobile(todoDO.getOperatorMobile());
            TodoDO todoDB = thirdTodoMapper.get(todoQuery);
            if (todoDB == null) {
                //新增
                todoDO.setId(null);
                thirdTodoMapper.insert(todoDO);
                log.info("新增成功");
            } else {
                //更新
                TodoDO todoDO1 = new TodoDO();
                BeanUtil.copyProperties(todoDO, todoDO1);
                todoDO1.setId(todoDB.getId());
                thirdTodoMapper.update(todoDO);
                log.info("修改成功,id:{}", todoDO1.getId());
            }
        }
    }
}
