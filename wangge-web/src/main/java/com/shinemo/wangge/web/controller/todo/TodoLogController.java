package com.shinemo.wangge.web.controller.todo;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.todo.domain.TodoDO;
import com.shinemo.todo.domain.TodoLogDO;
import com.shinemo.todo.query.TodoLogQuery;
import com.shinemo.todo.query.TodoQuery;
import com.shinemo.wangge.core.service.todo.TodoLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/6/17 13:47
 * @Desc
 */
@RestController
@RequestMapping("/todo/thing/log")
@Slf4j
public class TodoLogController {

    @Resource
    private TodoLogService todoLogService;


    @PostMapping("/getTodoLogList")
    public ApiResult<List<TodoLogDO>> getTodoLogList(@RequestBody TodoLogQuery todoLogQuery) {
        return todoLogService.getTodoLogList(todoLogQuery);
    }

    @PostMapping("/getTodoList")
    public ApiResult<List<TodoDO>> getTodoList(@RequestBody TodoQuery todoQuery) {
        return todoLogService.getTodoList(todoQuery);
    }

}
