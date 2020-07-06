package com.shinemo.wangge.web.controller.todo;

import com.shinemo.client.common.ListVO;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.todo.query.TodoQuery;
import com.shinemo.todo.query.TodoUrlQuery;
import com.shinemo.todo.vo.TodoIndexVO;
import com.shinemo.todo.vo.TodoTypeVO;
import com.shinemo.todo.vo.TodoVO;
import com.shinemo.wangge.core.service.todo.TodoRedirectUrlService;
import com.shinemo.wangge.core.service.todo.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author shangkaihui
 * @Date 2020/6/13 18:04
 * @Desc
 */
@RestController
@RequestMapping("/todo/thing/")
@Slf4j
public class TodoController {

    @Autowired
    private TodoService todoService;

    @Autowired
    private TodoRedirectUrlService todoRedirectUrlService;


    /**
     * 获取全部类别
     *
     * @return
     */
    @PostMapping("/getTypeList")
    public ApiResult<TodoTypeVO> getTypeList() {
        return todoService.getTypeList();
    }

    /**
     * 清空全部类别缓存
     *
     * @return
     */
    @GetMapping("/clearTypeListCache")
    public ApiResult<Void> clearTypeListCache() {
        return todoService.clearTypeListCache();
    }


    /**
     * 查询代办事项列表
     *
     * @param todoQuery
     * @return
     */
    @PostMapping("/getTodoList")
    public ApiResult<ListVO<TodoVO>> getTodoList(@RequestBody TodoQuery todoQuery) {
        return todoService.getTodoList(todoQuery);
    }

    /**
     * 获取全部代办事项列表
     *
     * @return
     */
    @PostMapping("/getAllTodoTypeList")
    public ApiResult<TodoTypeVO> getAllTodoTypeList() {
        return todoService.getAllTodoTypeList();
    }

    /**
     * 首页展示
     *
     * @return
     */
    @PostMapping("/getIndexInfo")
    public ApiResult<TodoIndexVO> getIndexInfo() {
        return todoService.getIndexInfo();
    }

    /**
     * 获取代办事项或工单跳转url参数
     *
     * @return
     */
    @GetMapping("/getRedirectUrl")
    public ApiResult<String> getRedirectUrl(TodoUrlQuery todoUrlQuery) {
        return todoRedirectUrlService.getRedirectUrl(todoUrlQuery);
    }

    /**
     * 点击消息推送中的消息,重定向到工单详情
     */
    @GetMapping("/messageRedirect")
    public ApiResult<Void> messageRedirect(TodoUrlQuery todoUrlQuery, HttpServletRequest request, HttpServletResponse response) {
        log.info("[messageRedirect] 请求参数:{}", todoUrlQuery);
        Assert.notNull(todoUrlQuery, "request is null");
        Assert.notNull(todoUrlQuery.getThirdType(), "thirdType is null");
        Assert.notNull(todoUrlQuery.getThirdId(), "thirdId is null");
        log.info("[messageRedirect] 消息通知第三方跳转请求, request:{}", todoUrlQuery);
        ApiResult<String> result = todoRedirectUrlService.getRedirectUrl(todoUrlQuery);
        if (!result.isSuccess()) {
            return ApiResult.fail(result.getMsg(), result.getCode());
        }
        String url = result.getData();
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            log.error("[messageRedirect] 跳转失败,request:{},result:{}", todoUrlQuery, result);
            return ApiResult.fail("跳转失败", 500);
        }
        return ApiResult.of(0);
    }
}
