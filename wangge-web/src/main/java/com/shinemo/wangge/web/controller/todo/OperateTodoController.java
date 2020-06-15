package com.shinemo.wangge.web.controller.todo;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.todo.vo.TodoThirdRequest;
import com.shinemo.wangge.core.service.todo.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 10:07
 * @Desc
 */
@RestController
@RequestMapping("/todo")
@Slf4j
public class OperateTodoController {

    @Autowired
    private TodoService todoService;


    @PostMapping("/operateTodoThing")
    public ApiResult<Void> operateTodoThing(@RequestBody TodoThirdRequest todoThirdRequest) {
        log.info("[operateTodoThing] invoke, reuquest:{}", todoThirdRequest);
        return todoService.operateTodoThing(todoThirdRequest);
    }



}
