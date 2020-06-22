package com.shinemo.wangge.test.web;

import cn.hutool.core.date.DateUtil;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.todo.domain.TodoDO;
import com.shinemo.todo.domain.TodoLogDO;
import com.shinemo.todo.enums.ThirdTodoTypeEnum;
import com.shinemo.todo.query.TodoLogQuery;
import com.shinemo.todo.query.TodoQuery;
import com.shinemo.todo.query.TodoUrlQuery;
import com.shinemo.wangge.core.service.todo.TodoLogService;
import com.shinemo.wangge.core.service.todo.impl.TodoRedirectUrlServiceImpl;
import com.shinemo.wangge.web.MainApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/6/17 13:58
 * @Desc
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainApplication.class)
public class TodoTest {

    @Resource
    private TodoLogService todoLogService;

    @Resource
    private TodoRedirectUrlServiceImpl todoRedirectUrlService;

    @Test
    public void testGetUrl() {
        TodoUrlQuery todoUrlQuery = new TodoUrlQuery();
        todoUrlQuery.setThirdType(ThirdTodoTypeEnum.DAO_SAN_JIAO_ORDER.getId());
        todoUrlQuery.setOperatorMobile("15800000002");
        todoUrlQuery.setThirdId("6c5127a3-9f1c-11ea-a34d-5254001a0735");

        ApiResult<String> result = todoRedirectUrlService.getDetailRedirectUrl(todoUrlQuery);
        System.out.println("result = " + result);
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
}
